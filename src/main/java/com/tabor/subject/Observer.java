package com.tabor.subject;

interface Observer<T> {
    void setSubject(Subject<T> subject);
    void update();
}
