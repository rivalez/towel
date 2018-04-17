package com.tabor.restaurant;

public class Waiterimpl implements WaiterInterface {
    private int id;

    public Waiterimpl(int id) {
        this.id = id;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void go(int orderID, int tableID) {

    }

    @Override
    public void registerOrder(OrderInterface orderInterface) {

    }
}
