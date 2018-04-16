package com.tabor.restaurant;

public class RestaurantManagement implements RestaurantManagementInterface {
    private KitchenInterface kitchen;
    private ReceiverInterface receiver;
    private OrderInterface order = new OrderInterfaceImpl();

    @Override
    public void addWaiter(WaiterInterface waiter) {
        waiter.registerOrder(order);
    }

    @Override
    public void removeWaiter(WaiterInterface waiter) {

    }

    @Override
    public void setKitchen(KitchenInterface kitchen) {
        this.receiver = new ReceiverInterfaceImpl();
        this.kitchen = kitchen;
        this.kitchen.registerReceiver(receiver);
    }

    class ReceiverInterfaceImpl implements ReceiverInterface {
        @Override
        public void mealReady(int orderID) {

        }
    }

    class OrderInterfaceImpl implements OrderInterface {
        @Override
        public void newOrder(int orderID, int tableID) {

        }

        @Override
        public void orderComplete(int orderID, int tableID) {

        }
    }
}
