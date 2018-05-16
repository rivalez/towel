package com.tabor.restaurant;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RestaurantManagement implements RestaurantManagementInterface {
    private Kitchen kitchen;
    private WaitersStorage waiters = new WaitersStorage();
    private WaitersManager waitersManager = new WaitersManager(waiters);
    private OrderGenerator orderGenerator = new OrderGenerator();

    @Override
    public void addWaiter(WaiterInterface waiter) {
        OrderInterface order = new OrderInterfaceImpl();
        waiter.registerOrder(order);
        waiters.add(new MyWaiter(waiter, order));
        int id = orderGenerator.generateId();
        waitersManager.newOrder(id, id);
    }

    @Override
    public void removeWaiter(WaiterInterface waiter) {
        waiters.remove(waiter.getID());
    }

    @Override
    public void setKitchen(KitchenInterface kitchen) {
        final ReceiverInterface receiver = new ReceiverInterfaceImpl(waitersManager);
        kitchen.registerReceiver(receiver);
        this.kitchen = new Kitchen(kitchen, receiver);
    }

    class WaitersManager {
        private WaitersStorage waiters;

        WaitersManager(WaitersStorage waiters) {
            this.waiters = waiters;
        }

        void go(int orderID, int tableId) {
            //todo how to determine which is free and get him
            waiters.getAll().get(0).go(orderID, tableId);
        }

        void newOrder(int orderID, int tableID) {
            //todo how to determine which is free and get him
            waiters.getAll().get(0).newOrder(orderID, tableID);
        }
    }

    class WaitersStorage {
        private CopyOnWriteArrayList<Waiter> waiters = new CopyOnWriteArrayList<>();

        void add(Waiter waiter) {
            waiters.add(waiter);
        }

        void remove(int id) {
            waiters.removeIf(w -> w.getId() == id);
        }

        CopyOnWriteArrayList<Waiter> getAll() {
            return waiters;
        }
    }

    class Kitchen {
        private KitchenInterface kitchen;
        private ReceiverInterface receiver;
        private ExecutorService workers;

        Kitchen(KitchenInterface kitchenInterface, ReceiverInterface receiver) {
            this.kitchen = kitchenInterface;
            this.receiver = receiver;
            this.workers = Executors.newFixedThreadPool(kitchen.getNumberOfParallelTasks());
        }

        void prepare(int orderId) {
            workers.execute(new KitchenOrder(() -> kitchen.prepare(orderId), receiver, orderId));
        }
    }

    private interface Waiter {
        void newOrder(int orderID, int tableID);
        void go(int orderID, int tableID);
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
            work.execute(() -> order.newOrder(orderID, tableID));
        }

        @Override
        public void go(int orderID, int tableID) {
            work.execute(() -> waiter.go(orderID, tableID));
        }

        @Override
        public int getId() {
            return waiter.getID();
        }
    }

    class ReceiverInterfaceImpl implements ReceiverInterface {
        private WaitersManager waitersManager;

        ReceiverInterfaceImpl(WaitersManager waitersManager) {
            this.waitersManager = waitersManager;
        }

        @Override
        public void mealReady(int orderID) {
            waitersManager.go(orderID , orderID);
        }
    }

    class OrderGenerator {
        private AtomicInteger counter = new AtomicInteger(1);
        int generateId() {
            return counter.getAndIncrement();
        }
    }

    class OrderInterfaceImpl implements OrderInterface {
        @Override
        public void newOrder(int orderID, int tableID) {
            kitchen.prepare(orderID);
        }

        @Override
        public void orderComplete(int orderID, int tableID) {
            System.out.println(String.format("order with nr: %s has been delivered to table with nr: %s", orderID, tableID));
        }
    }

    private class KitchenOrder extends FutureTask<Integer> {
        private int orderId;
        private ReceiverInterface receiver;

        private KitchenOrder(Runnable runnable, ReceiverInterface receiver, Integer orderId) {
            super(runnable, orderId);
            this.orderId = orderId;
            this.receiver = receiver;
        }

        @Override
        protected void done() {
            receiver.mealReady(orderId);
        }
    }
}