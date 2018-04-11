package com.tabor.prir2;

import java.util.Comparator;
import java.util.concurrent.*;
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
        if(computation != null) {
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
        //todo not sure this structure will work
        private final PriorityBlockingQueue<ConverterInterface.DataPortionInterface> portions = new PriorityBlockingQueue<>(20, new DataPortionComparator());

        class DataPortionComparator implements Comparator<ConverterInterface.DataPortionInterface> {
            @Override
            public int compare(ConverterInterface.DataPortionInterface o1, ConverterInterface.DataPortionInterface o2) {
                return Integer.compare(o1.id(), o2.id());
            }
        }

        void addDataPortion(ConverterInterface.DataPortionInterface data){
            portions.put(data);
        }

        PriorityBlockingQueue<ConverterInterface.DataPortionInterface> getPortions() {
            return portions;
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
