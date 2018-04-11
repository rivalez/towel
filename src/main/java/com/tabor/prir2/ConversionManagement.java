package com.tabor.prir2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConversionManagement implements ConversionManagementInterface {
    private ConverterInterface converter;
    private ConversionReceiverInterface receiver;

    private Computation computation;

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

    }

    class Computation {
        private ExecutorService workers;
        private ConverterInterface converter;

        Computation(int cores, ConverterInterface converter) {
            this.workers = Executors.newFixedThreadPool(cores);
            this.converter = converter;
        }

        void handleWorkersChange() {
            try {
                workers.awaitTermination(100, TimeUnit.MILLISECONDS);
                workers.shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public long compute(ConverterInterface.DataPortionInterface data) {
            return converter.convert(data);
        }
    }
}
