package com.tabor.restaurant;


import org.testng.annotations.Test;

import java.util.concurrent.LinkedBlockingDeque;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Test
public class RestaurantManagementTest {


    @Test(invocationCount = 100)
    public void simulation1() {

    }

    @Test(invocationCount = 100)
    public void storageTest() throws InterruptedException {
        //given
        RestaurantManagement rm = new RestaurantManagement();
        RestaurantManagement.Storage storage = rm.new StorageImpl();
        Waiterimpl waiter1 = new Waiterimpl(1);
        Waiterimpl waiter2 = new Waiterimpl(2);
        Waiterimpl waiter3 = new Waiterimpl(3);
        Waiterimpl waiter4 = new Waiterimpl(4);
        Waiterimpl waiter5 = new Waiterimpl(5);
        LinkedBlockingDeque<RestaurantManagement.Order> orders1 = new LinkedBlockingDeque<>();
        LinkedBlockingDeque<RestaurantManagement.Order> orders2 = new LinkedBlockingDeque<>();
        LinkedBlockingDeque<RestaurantManagement.Order> orders3 = new LinkedBlockingDeque<>();
        //when
        orders3.put(rm.new Order(1, waiter3));
        storage.put(waiter1, orders1);
        storage.put(waiter2, orders2);
        storage.put(waiter3, orders3);
        storage.put(waiter4, orders3);
        storage.put(waiter5, orders3);
        //then
        assertNotNull(storage.getOrders(waiter3));
        assertEquals(storage.getOrders(waiter3), orders3);
    }
}