package com.tabor.observer;

import org.junit.jupiter.api.Test;

class ObserverTest {

    @Test
    public void observerTest() {
        RedDress redDress = new RedDress();
        Observer ob1 = new User(redDress);
        Observer ob2 = new User(redDress);
        Observer ob3 = new User(redDress);

        redDress.register(ob1);
        redDress.register(ob2);
        redDress.register(ob3);

        redDress.setInStock(true);
    }
}