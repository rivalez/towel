package com.tabor.prir2;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConversionManagementTest {

    @Test
    public void priorityqueueTest() throws InterruptedException {
        ConversionManagement conversionManagement = new ConversionManagement();
        ExecutorService adders = Executors.newFixedThreadPool(4);
        adders.execute(() -> IntStream.range(0, 50).forEach(populate(conversionManagement)));
        adders.execute(() -> IntStream.range(0, 100).forEach(populate(conversionManagement)));
        adders.execute(() -> IntStream.range(1, 151).forEach(populate(conversionManagement)));
        adders.execute(() -> IntStream.range(1, 201).forEach(populate(conversionManagement)));
        Thread.sleep(100L);
        int size = conversionManagement.getDataPortionReceiver().getPortions().size();
        int first = conversionManagement.getDataPortionReceiver().getPortions().take().id();
        int second = conversionManagement.getDataPortionReceiver().getPortions().take().id();
        int third = conversionManagement.getDataPortionReceiver().getPortions().take().id();
        assertEquals(500, size);
        assertEquals(0, first);
        assertEquals(0, second);
        assertEquals(1, third);

    }

    private IntConsumer populate(ConversionManagement conversionManagement) {
        return id -> conversionManagement.addDataPortion(new DataPortionImpl(id, new int[]{0, 0, 0}, ConverterInterface.Channel.LEFT_CHANNEL));
    }

}