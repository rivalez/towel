package com.tabor.subject;

public class MyTopicObserver<T> implements Observer {
    private Subject subject;
    private T msg;

    @Override
    public void update() {
        System.out.println("msg is: " + subject.getUpdate(this));
    }

    @Override
    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}
