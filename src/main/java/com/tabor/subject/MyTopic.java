package com.tabor.subject;

import java.util.ArrayList;
import java.util.List;

public class MyTopic<T> implements Subject {
    private final List<Observer> observers = new ArrayList<>();
    private T msg;

    @Override
    public synchronized void register(Observer observer) {
        observers.add(observer);
    }

    @Override
    public synchronized void unregister(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public synchronized void notifyObservers() {
        observers.forEach(Observer::update);
    }

    @Override
    public T getUpdate(Observer observer) {
        return this.msg;
    }

    public void postMsg(T msg){
        this.msg = msg;
        notifyObservers();
    }
}
