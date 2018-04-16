package com.tabor.prir2;

import java.util.ArrayList;
import java.util.List;

public class ConversionReceiverImpl implements ConversionManagementInterface.ConversionReceiverInterface {
    List<ConversionManagementInterface.ConversionResult> results = new ArrayList<>();

    @Override
    public void result(ConversionManagementInterface.ConversionResult result) {
        results.add(result);
    }
}
