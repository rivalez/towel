package com.tabor.prir2;

import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static com.jayway.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.testng.Assert.assertEquals;

public class ConversionManagementTest {

    @Test(invocationCount = 1000)
    public void simulation1Test() {
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


        await().until(() -> receiver.results.size(), equalTo(3));
        assertEquals(receiver.results.get(0).leftChannelData.id(), 1);
        assertEquals(receiver.results.get(0).rightChannelData.id(), 1);
        assertEquals(receiver.results.get(2).rightChannelData.id(), 3);
        assertEquals(receiver.results.get(2).leftChannelData.id(), 3);
    }
}