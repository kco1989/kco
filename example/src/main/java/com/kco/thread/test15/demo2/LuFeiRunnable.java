package com.kco.thread.test15.demo2;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * com.kco.test12.demo
 * Created by swlv on 2016/10/31.
 */
public class LuFeiRunnable implements Runnable{
    ArrayBlockingQueue<String> queue;
    Random random = new Random();
    public LuFeiRunnable(ArrayBlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {

        while (true){
            try {
                String take = queue.take();
                System.out.println("-->路飞拿到 " + take);
                Thread.sleep(random.nextInt(500));
                System.out.println("-->路飞吃完 " + take);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
