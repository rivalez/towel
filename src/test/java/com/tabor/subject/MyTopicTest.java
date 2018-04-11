package com.tabor.subject;

import org.junit.jupiter.api.Test;

class MyTopicTest {
    @Test
    public void observerTest() {
        //given
        MyTopic<String> topic = new MyTopic<>();
        //when
        Observer<String> ob1 = new MyTopicObserver<>();
        Observer<String> ob2 = new MyTopicObserver<>();
        Observer<String> ob3 = new MyTopicObserver<>();

        topic.register(ob1);
        topic.register(ob2);
        topic.register(ob3);

        ob1.setSubject(topic);
        ob2.setSubject(topic);
        ob3.setSubject(topic);

        ob1.update();

        topic.postMsg("Hello");
        //then
    }

}