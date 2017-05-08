package com.kco.thread.test16.demo;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by pc on 2017/4/18.
 */
public class TestMain {

    public static void main(String[] args) throws InterruptedException {

        PriorityBlockingQueue<Human> queue = new PriorityBlockingQueue<>(200, new HumanComparator());
        Thread thread = new Thread(new ProducerRunnable(queue));
        thread.start();
        thread.join();
        new Thread(new ConsumerRunnable(queue)).start();
    }
}