package com.tabor.prir2;

import java.util.Comparator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class ConversionManagement implements ConversionManagementInterface {
    private ConverterInterface converter;

    private Computation computation = new Computation();
    private ExecutorService computationService;

    private PairMatcher pairMatcher = new PairMatcher();
    private ExecutorService pairMatcherService = Executors.newSingleThreadScheduledExecutor();

    private DataPortionReceiver dataPortionReceiver;

    private ConversionResultToSend toSend;

    ConversionManagement() {
        this.dataPortionReceiver = new DataPortionReceiver();
    }

    @Override
    public void setCores(int cores) {
        if (computation != null) {
            computation.handleWorkersChange();
        }
        computationService = Executors.newFixedThreadPool(cores);
        if (computation.converter == null) {
            computation.setConverter(converter);
        }
    }

    @Override
    public void setConverter(ConverterInterface converter) {
        this.converter = converter;
        computation.setConverter(converter);
    }

    @Override
    public void setConversionReceiver(ConversionReceiverInterface receiver) {
        this.toSend = new ConversionResultToSend(receiver);
        new Thread(() -> toSend.send()).start();
    }

    @Override
    public void addDataPortion(ConverterInterface.DataPortionInterface data) {
        dataPortionReceiver.addDataPortion(data);
        computationService.execute(() -> computation.compute(dataPortionReceiver.take()));
    }

    class DataPortionReceiver {
        private final PriorityBlockingQueue<ConverterInterface.DataPortionInterface> portions =
                new PriorityBlockingQueue<>(20,
                        getDataPortionInterfaceComparator());

        private Comparator<ConverterInterface.DataPortionInterface> getDataPortionInterfaceComparator() {
            return Comparator.comparing(ConverterInterface.DataPortionInterface::id)
                    .thenComparing(ConverterInterface.DataPortionInterface::channel);
        }

        void addDataPortion(ConverterInterface.DataPortionInterface data) {
            portions.put(data);
        }

        ConverterInterface.DataPortionInterface take() {
            try {
                return portions.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //todo think about it
            return null;
        }

    }

    class Computation {
        private final Logger logger = Logger.getLogger(Computation.class.getName());
        private ExecutorService workers;
        private ConverterInterface converter;

        Computation() {
            this.workers = Executors.newCachedThreadPool();
        }

        void setConverter(ConverterInterface converter) {
            this.converter = converter;
        }

        void handleWorkersChange() {
            try {
                workers.awaitTermination(40, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                logger.warning(e.getMessage());
                workers.shutdown();
            }
        }

        long compute(ConverterInterface.DataPortionInterface data) {
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
    class PairMatcher {
        private ConcurrentSkipListSet<ComputeResult> computedElements = new ConcurrentSkipListSet<>(
                Comparator.comparing((ComputeResult data) -> data.getData().id())
                        .thenComparing((ComputeResult result) -> result.data.channel()));

        void tryFindPair(ComputeResult result) {
            findPair(result);
            computedElements.add(result);
        }

        void findPair(ComputeResult newResult) {
            computedElements.stream()
                    .filter(foundPair(newResult))
                    .findFirst()
                    .ifPresent((foundValue) -> matchChannel(newResult, foundValue));
        }

        private void matchChannel(ComputeResult newResult, ComputeResult foundValue) {
            if (foundValue.data.channel() == ConverterInterface.Channel.LEFT_CHANNEL) {
                toSend.add(new ConversionResult(foundValue.data, newResult.data, foundValue.result, newResult.result));
            } else {
                toSend.add(new ConversionResult(newResult.data, foundValue.data, newResult.result, foundValue.result));
            }
        }

        private Predicate<ComputeResult> foundPair(ComputeResult newResult) {
            return computeResult -> newResult.id() == computeResult.id();
        }
    }

    class ConversionResultToSend {
        private PriorityBlockingQueue<ConversionResult> computedElements =
                new PriorityBlockingQueue<>(20,
                        Comparator.comparing((ConversionResult result) -> result.leftChannelData.id()));
        private AtomicInteger nextIdToSend = new AtomicInteger(1);
        private ConversionReceiverInterface receiver;

        ConversionResultToSend(ConversionReceiverInterface receiver) {
            this.receiver = receiver;
        }

        void add(ConversionResult conversionResult) {
            this.computedElements.add(conversionResult);
        }

        void send() {
            while (true) {
                if(computedElements.peek() != null){
                    if (nextIdToSend.get() == computedElements.peek().leftChannelData.id()) {
                        try {
                            receiver.result(computedElements.take());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        nextIdToSend.incrementAndGet();
                    }
                }
            }
        }


    }

    class ComputeResult {
        private ConverterInterface.DataPortionInterface data;
        private long result;

        ComputeResult(ConverterInterface.DataPortionInterface data, long result) {
            this.data = data;
            this.result = result;
        }

        ConverterInterface.DataPortionInterface getData() {
            return data;
        }

        int id() {
            return data.id();
        }
    }
}
