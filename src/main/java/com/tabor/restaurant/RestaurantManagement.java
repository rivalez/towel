package com.tabor.restaurant;

import java.util.Comparator;
import java.util.concurrent.*;

public class RestaurantManagement implements RestaurantManagementInterface {
    private ReceiverInterface receiver;
    private OrderInterface order = new OrderInterfaceImpl();
    private Storage storage = new StorageImpl();

    private Kitchen kitchen;

    class Kitchen {
        private KitchenInterface kitchen;
        private ReceiverInterface receiver;
        private ExecutorService workers;

        Kitchen(KitchenInterface kitchenInterface, ReceiverInterface receiver) {
            this.kitchen = kitchenInterface;
            this.receiver = receiver;
            this.workers = Executors.newFixedThreadPool(kitchen.getNumberOfParallelTasks());
        }

        public void prepare(int orderId) {
            workers.execute(new Order(() -> kitchen.prepare(orderId), receiver, orderId));
        }
    }



    @Override
    public void addWaiter(WaiterInterface waiter) {
        waiter.registerOrder(order);
        storage.put(waiter, new LinkedBlockingDeque<>());
    }

    @Override
    public void removeWaiter(WaiterInterface waiter) {
    }

    @Override
    public void setKitchen(KitchenInterface kitchen) {
        this.receiver = new ReceiverInterfaceImpl();
        kitchen.registerReceiver(receiver);
        this.kitchen = new Kitchen(kitchen, receiver);
    }

    class ReceiverInterfaceImpl implements ReceiverInterface {
        @Override
        public void mealReady(int orderID) {
            //todo notify waiter to take away meal

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
    class Order extends FutureTask<Integer> {
        private int orderId;
        private ReceiverInterface receiver;

        Order(Runnable runnable, ReceiverInterface receiver, Integer orderId) {
            super(runnable, orderId);
            this.orderId = orderId;
            this.receiver = receiver;
        }

        @Override
        public void run() {
            super.run();
        }

        @Override
        protected void done() {
            receiver.mealReady(orderId);
        }
    }

    interface Storage {
        void put(WaiterInterface waiter, LinkedBlockingDeque<Order> orders);
        LinkedBlockingDeque<Order> getOrders(WaiterInterface waiter);
        void removeOrder(WaiterInterface waiter);
        void addOrder(WaiterInterface waiter, Order order);
    }

    class StorageImpl implements Storage {
        private ConcurrentSkipListMap<WaiterInterface, LinkedBlockingDeque<Order>> waiters = new ConcurrentSkipListMap<>(Comparator.comparing(WaiterInterface::getID));

        public void put(WaiterInterface waiter, LinkedBlockingDeque<Order> orders) {
            waiters.put(waiter, orders);
        }

        public LinkedBlockingDeque<Order> getOrders(WaiterInterface waiter) {
            return waiters.get(waiter);
        }

        @Override
        public void removeOrder(WaiterInterface waiter) {

        }

        @Override
        public void addOrder(WaiterInterface waiter, Order order) {

        }
    }
}
