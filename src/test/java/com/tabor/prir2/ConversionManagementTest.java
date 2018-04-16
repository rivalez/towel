package com.tabor.prir2;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

class ConversionManagementTest {
    @Test
    public void simulation1() throws InterruptedException {
        //given
        List<ConverterInterface.DataPortionInterface> portions = Arrays.asList(
                new DataPortionImpl(2, new int[]{0, 0, 0}, ConverterInterface.Channel.RIGHT_CHANNEL),
                new DataPortionImpl(3, new int[]{0, 0, 0}, ConverterInterface.Channel.LEFT_CHANNEL),
                new DataPortionImpl(4, new int[]{0, 0, 0}, ConverterInterface.Channel.RIGHT_CHANNEL),
                new DataPortionImpl(5, new int[]{0, 0, 0}, ConverterInterface.Channel.LEFT_CHANNEL),
                new DataPortionImpl(10, new int[]{0, 0, 0}, ConverterInterface.Channel.RIGHT_CHANNEL),
                new DataPortionImpl(2, new int[]{0, 0, 0}, ConverterInterface.Channel.LEFT_CHANNEL),
                new DataPortionImpl(1, new int[]{0, 0, 0}, ConverterInterface.Channel.RIGHT_CHANNEL),
                new DataPortionImpl(3, new int[]{0, 0, 0}, ConverterInterface.Channel.RIGHT_CHANNEL),
                new DataPortionImpl(1, new int[]{0, 0, 0}, ConverterInterface.Channel.LEFT_CHANNEL),
                new DataPortionImpl(11, new int[]{0, 0, 0}, ConverterInterface.Channel.RIGHT_CHANNEL)
        );
        ConversionManagement conversionManagement = new ConversionManagement();
        ConversionReceiverImpl receiver = new ConversionReceiverImpl();
        ConverterInterface converter = Mockito.mock(ConverterInterface.class);
        given(converter.convert(any(ConverterInterface.DataPortionInterface.class))).willReturn(100L);
        conversionManagement.setCores(2);
        conversionManagement.setConverter(converter);
        conversionManagement.setConversionReceiver(receiver);
        portions.forEach(conversionManagement::addDataPortion);

        Thread.sleep(1000L);

        System.out.println(receiver.results);
    }
}