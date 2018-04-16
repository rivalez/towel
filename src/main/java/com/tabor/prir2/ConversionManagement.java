package com.tabor.prir2;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class ConversionManagement implements ConversionManagementInterface {
    private ConversionReceiverInterface receiver;
    private DataPortionReceiver dataPortionReceiver;
    private ConverterInterface converter;

    private Computation computation;

    //todo consider usage of more threads?
    private ExecutorService computationService = Executors.newSingleThreadExecutor();

    private PairMatcher pairMatcher = new PairMatcher();
    private ExecutorService pairMatcherService = Executors.newSingleThreadExecutor();

    ConversionManagement() {
        this.dataPortionReceiver = new DataPortionReceiver();
    }

    @Override
    public void setCores(int cores) {
        if (computation != null) {
            computation.handleWorkersChange();
        }
        computation = new Computation(cores);
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

        PriorityBlockingQueue<ConverterInterface.DataPortionInterface> getPortions() {
            return portions;
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

    DataPortionReceiver getDataPortionReceiver() {
        return dataPortionReceiver;
    }

    class Computation {
        private final Logger logger = Logger.getLogger(Computation.class.getName());
        private ExecutorService workers;
        private ConverterInterface converter;

        Computation(int cores) {
            this.workers = Executors.newFixedThreadPool(cores);
        }

        void setConverter(ConverterInterface converter) {
            this.converter = converter;
        }

        void handleWorkersChange() {
            try {
                workers.awaitTermination(100, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                logger.warning(e.getMessage());
                workers.shutdown();
            }
        }

        long compute(ConverterInterface.DataPortionInterface data) {
            Future<Long> result = workers.submit(() -> converter.convert(data));
            try {
                long comResult = result.get();
                pairMatcherService.execute(() -> pairMatcher.add(new ComputeResult(data, comResult)));
                return comResult;
            } catch (InterruptedException | ExecutionException e) {
                logger.warning(e.getMessage());
            }
            //todo think about it
            return 0;
        }
    }

    class PairMatcher {
        private ConcurrentSkipListSet<ComputeResult> computedElements = new ConcurrentSkipListSet<>(Comparator.comparing(data -> data.getData().id()));

        void add(ComputeResult result) {
            findPair(result);
            computedElements.add(result);
        }

        void findPair(ComputeResult newResult) {
            Optional<ComputeResult> found = computedElements.stream().filter(foundPair(newResult)).findFirst();
            if (found.isPresent()) {
                ComputeResult foundValue = found.get();
                receiver.result(new ConversionResult(foundValue.data, newResult.data, foundValue.result, newResult.result));
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

        public long getResult() {
            return result;
        }

        int id() {
            return data.id();
        }
    }
}
