package com.tabor.prir2;

import java.util.Comparator;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class ConversionManagement implements ConversionManagementInterface {
    private ConversionReceiverInterface receiver;
    private ConverterInterface converter;

    private Computation computation = new Computation();
    private ExecutorService computationService;

    private PairMatcher pairMatcher = new PairMatcher();
    private ExecutorService pairMatcherService = Executors.newSingleThreadScheduledExecutor();

    private DataPortionReceiver dataPortionReceiver;

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
        this.receiver = receiver;
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
                    receiver.result(new ConversionResult(foundValue.data, newResult.data, foundValue.result, newResult.result));
                } else {
                    receiver.result(new ConversionResult(newResult.data, foundValue.data, newResult.result, foundValue.result));
                }
        }

        private Predicate<ComputeResult> foundPair(ComputeResult newResult) {
            return computeResult -> newResult.id() == computeResult.id();
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
