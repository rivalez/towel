package com.tabor.restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RestaurantManagement implements RestaurantManagementInterface {
    private Kitchen kitchen;
    private Stuff<Waiter> waiters = new WaitersStorage();

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

    interface Stuff<T> {
        void add(T value);
        void remove(int id);
        List<T> getAll();
    }

    class WaitersStorage implements Stuff<Waiter> {
        private List<Waiter> waiters = new ArrayList<>();

        @Override
        public void add(Waiter waiter) {
            waiters.add(waiter);
        }

        @Override
        public void remove(int id) {
            waiters.removeIf(waiter -> waiter.getId() == id);
        }

        @Override
        public List<Waiter> getAll() {
            return waiters;
        }
    }

    interface OrderManagement {
        Order generateNewOrder();
        Order getOrderById(int id);
    }

    class OrderGenerator implements OrderManagement {
        @Override
        public Order generateNewOrder() {
            return null;
        }

        @Override
        public Order getOrderById(int id) {
            return null;
        }
    }

    interface Waiter {
        void newOrder(int orderID, int tableID);
        int getId();
    }

    class MyWaiter implements Waiter {
        private final WaiterInterface waiter;
        private final OrderInterface order;
        private final ExecutorService work = Executors.newFixedThreadPool(1);

        MyWaiter(WaiterInterface waiter, OrderInterface order) {
            this.waiter = waiter;
            this.order = order;
        }

        @Override
        public void newOrder(int orderID, int tableID) {
            work.execute(() -> order.newOrder();
        }

        @Override
        public int getId() {
            return waiter.getID();
        }
    }

    @Override
    public void addWaiter(WaiterInterface waiter) {
        OrderInterface order = new OrderInterfaceImpl();
        waiter.registerOrder(order);
        waiters.add(new MyWaiter(waiter, order));
    }

    @Override
    public void removeWaiter(WaiterInterface waiter) {
        waiters.remove(waiter.getID());
    }

    @Override
    public void setKitchen(KitchenInterface kitchen) {
        final ReceiverInterfaceImpl receiver = new ReceiverInterfaceImpl();
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
}
