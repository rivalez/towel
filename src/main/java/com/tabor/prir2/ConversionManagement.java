package com.tabor.prir2;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ConversionManagement implements ConversionManagementInterface {
    private ConverterInterface converter;
    private ConversionReceiverInterface receiver;
    private DataPortionReceiver dataPortionReceiver;

    private Computation computation;

    ConversionManagement() {
        this.dataPortionReceiver = new DataPortionReceiver();
    }

    @Override
    public void setCores(int cores) {
        if (computation != null) {
            computation.handleWorkersChange();
        }
        computation = new Computation(cores, converter);
    }

    @Override
    public void setConverter(ConverterInterface converter) {
        this.converter = converter;
    }

    @Override
    public void setConversionReceiver(ConversionReceiverInterface receiver) {
        this.receiver = receiver;
    }

    @Override
    public void addDataPortion(ConverterInterface.DataPortionInterface data) {
        dataPortionReceiver.addDataPortion(data);
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

        ConverterInterface.DataPortionInterface take() throws InterruptedException {
            return portions.take();
        }

    }

    public DataPortionReceiver getDataPortionReceiver() {
        return dataPortionReceiver;
    }

    class Computation {
        private final Logger logger = Logger.getLogger(Computation.class.getName());
        private ExecutorService workers;
        private ConverterInterface converter;

        Computation(int cores, ConverterInterface converter) {
            this.workers = Executors.newFixedThreadPool(cores);
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

        public long compute(ConverterInterface.DataPortionInterface data) {
            return converter.convert(data);
        }
    }
}
