package com.tabor.observer;

public interface Observable {
    void register(Observer observer);
    void unregister(Observer observer);
    void notifyObservers();
    Boolean getUpdate(Observer observer);
}
