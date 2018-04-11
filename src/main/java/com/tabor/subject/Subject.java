package com.tabor.subject;

interface Subject<T> {
    void register(Observer observer);
    void unregister(Observer observer);

    void notifyObservers();

    T getUpdate(Observer observer);
}
