package com.tabor.observer;

public class User implements Observer {
    private Observable observable;

    public User(Observable observable) {
        this.observable = observable;
    }

    @Override
    public void update(boolean inStock) {
        if (inStock) {
            Boolean update = observable.getUpdate(this);
            if(update) {
                buyDress();
                unsubscribe();
            }
        }
    }

    private void buyDress() {
        System.out.println("Got my new red dress");
    }

    public void unsubscribe() {
        if (observable != null) {
            observable.unregister(this);
        }
    }
}
