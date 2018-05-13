package com.tabor.prir2;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class ConversionManagement implements ConversionManagementInterface {
    private ConverterInterface converter;

    private Computation computation = new Computation();
    private ThreadPoolExecutor computationService = new ThreadPoolExecutor(10, 10, 0, TimeUnit.MICROSECONDS,
            new LinkedBlockingDeque<>());

    private PairMatcher pairMatcher = new PairMatcher();
    private ExecutorService pairMatcherService = Executors.newSingleThreadScheduledExecutor();

    private DataPortionReceiver dataPortionReceiver;

    private ConversionResultToSend sender;
    private Lock lock = new ReentrantLock();

    ConversionManagement() {
        this.dataPortionReceiver = new DataPortionReceiver();
    }

    @Override
    public void setCores(int cores) {
        lock.lock();
        if (cores > 0 && cores != computationService.getMaximumPoolSize()) {
            if (cores < computationService.getMaximumPoolSize()) {
                computationService.setCorePoolSize(cores);
                computationService.setMaximumPoolSize(cores);
                this.computation.setCorePoolFirst(cores);

            } else {
                computationService.setMaximumPoolSize(cores);
                computationService.setCorePoolSize(cores);
                this.computation.setMaxPoolFirst(cores);
            }
        }

        if (computation.converter == null) {
            computation.setConverter(converter);
        }
        lock.unlock();
    }

    @Override
    public void setConverter(ConverterInterface converter) {
        this.converter = converter;
        computation.setConverter(converter);
    }

    @Override
    public void setConversionReceiver(ConversionReceiverInterface receiver) {
        this.sender = new ConversionResultToSend(receiver);
        new Thread(() -> sender.send()).start();
    }

    @Override
    public void addDataPortion(ConverterInterface.DataPortionInterface data) {
        dataPortionReceiver.addDataPortion(data);
        computationService.execute(() -> computation.compute(dataPortionReceiver.take()));
    }

    private class DataPortionReceiver {
        private final PriorityBlockingQueue<ConverterInterface.DataPortionInterface> portions =
                new PriorityBlockingQueue<>(20,
                        getDataPortionInterfaceComparator());

        private Comparator<ConverterInterface.DataPortionInterface> getDataPortionInterfaceComparator() {
            return Comparator.comparing(ConverterInterface.DataPortionInterface::id)
                    .thenComparing(ConverterInterface.DataPortionInterface::channel);
        }

        private void addDataPortion(ConverterInterface.DataPortionInterface data) {
            portions.put(data);
        }

        private ConverterInterface.DataPortionInterface take() {
            try {
                return portions.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //todo think about it
            return null;
        }

    }

    private class Computation {
        private final Logger logger = Logger.getLogger(Computation.class.getName());
        private ThreadPoolExecutor workers = new ThreadPoolExecutor(10, 10, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
        private ConverterInterface converter;

        private void setConverter(ConverterInterface converter) {
            this.converter = converter;
        }

        private void setCorePoolFirst(int cores) {
            this.workers.setCorePoolSize(cores);
            this.workers.setMaximumPoolSize(cores);
        }

        private void setMaxPoolFirst(int cores) {
            this.workers.setMaximumPoolSize(cores);
            this.workers.setCorePoolSize(cores);
        }

        private long compute(ConverterInterface.DataPortionInterface data) {
            Future<Long> result = workers.submit(() -> converter.convert(data));
            try {
                long comResult = result.get();
                pairMatcherService.execute(() -> pairMatcher.tryFindPair(new ComputeResult(data, comResult)));
                return comResult;
            } catch (InterruptedException | ExecutionException e) {
                logger.warning(e.getMessage());
            }
            //todo think about it
            return 0;
        }
    }

    /**
     * matching of elements can use multi threads but sending should be single threading
     */
    private class PairMatcher {
        private ConcurrentSkipListSet<ComputeResult> computedElements = new ConcurrentSkipListSet<>(
                Comparator.comparing(ComputeResult::id)
                        .thenComparing(ComputeResult::channel));

        private void tryFindPair(ComputeResult result) {
            findPair(result);
            computedElements.add(result);
        }

        private void findPair(ComputeResult newResult) {
            computedElements.stream()
                    .filter(foundPair(newResult))
                    .findFirst()
                    .ifPresent((foundValue) -> matchChannel(newResult, foundValue));
        }

        private void matchChannel(ComputeResult newResult, ComputeResult foundValue) {
            if (foundValue.data.channel() == ConverterInterface.Channel.LEFT_CHANNEL) {
                sender.add(new ConversionResult(foundValue.data, newResult.data, foundValue.result, newResult.result));
            } else {
                sender.add(new ConversionResult(newResult.data, foundValue.data, newResult.result, foundValue.result));
            }
        }

        private Predicate<ComputeResult> foundPair(ComputeResult newResult) {
            return computeResult -> newResult.id() == computeResult.id();
        }
    }

    private class ConversionResultToSend {
        private PriorityBlockingQueue<ConversionResult> computedElements =
                new PriorityBlockingQueue<>(20,
                        Comparator.comparing((ConversionResult result) -> result.leftChannelData.id()));
        private AtomicInteger nextIdToSend = new AtomicInteger(1);
        private ConversionReceiverInterface receiver;

        private ConversionResultToSend(ConversionReceiverInterface receiver) {
            this.receiver = receiver;
        }

        private void add(ConversionResult conversionResult) {
            this.computedElements.add(conversionResult);
        }

        private void send() {
            while (true) {
                Optional.ofNullable(computedElements.peek()).ifPresent((a) -> {
                    if (nextIdToSend.get() == computedElements.peek().leftChannelData.id()) {
                        try {
                            receiver.result(computedElements.take());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        nextIdToSend.incrementAndGet();
                    }
                });
            }
        }
    }

    private class ComputeResult {
        private ConverterInterface.DataPortionInterface data;
        private long result;

        private ComputeResult(ConverterInterface.DataPortionInterface data, long result) {
            this.data = data;
            this.result = result;
        }

        private int id() {
            return data.id();
        }

        private ConverterInterface.Channel channel() {
            return data.channel();
        }
    }
}
