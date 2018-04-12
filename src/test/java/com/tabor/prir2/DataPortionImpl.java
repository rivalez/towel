package com.tabor.prir2;

public class DataPortionImpl implements ConverterInterface.DataPortionInterface {
    private int id;
    private int[] data;
    private ConverterInterface.Channel channel;

    DataPortionImpl(int id, int[] data, ConverterInterface.Channel channel) {
        this.id = id;
        this.data = data;
        this.channel = channel;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public int[] data() {
        return data;
    }

    @Override
    public ConverterInterface.Channel channel() {
        return channel;
    }
}
