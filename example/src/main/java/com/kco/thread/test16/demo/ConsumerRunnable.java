package com.kco.thread.test16.demo;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by pc on 2017/4/18.
 */
public class ConsumerRunnable implements Runnable{

    private PriorityBlockingQueue<Human> queue;
    public ConsumerRunnable(PriorityBlockingQueue<Human> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true){
            Human take = queue.poll();
            if (take == null){
                break;
            }
            System.out.println(take + " 办理业务.");
        }
    }
}
