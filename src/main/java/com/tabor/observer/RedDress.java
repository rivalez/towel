package com.tabor.observer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RedDress implements Observable {
    private List<Observer> observers = new ArrayList<>();
    private boolean inStock = false;

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
        if (inStock) {
            notifyObservers();
        }
    }

    @Override
    public void register(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void unregister(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        Iterator<Observer> iterator = observers.iterator();
        while (iterator.hasNext()) {
            iterator.next().update(inStock);
        }
    }

    @Override
    public Boolean getUpdate(Observer observer) {
        return this.inStock;
    }

}
