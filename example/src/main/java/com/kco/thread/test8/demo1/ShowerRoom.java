package com.kco.thread.test8.demo1;

import java.util.concurrent.Semaphore;

/**
 * Created by pc on 2016/10/29.
 */
public class ShowerRoom {
    private static final int MAX_SIZE = 3;
    Semaphore semaphore = new Semaphore(MAX_SIZE);

    public void bathe(String name){
        try {
            semaphore.acquire();
            System.out.println(Thread.currentThread().getName() + " 洗唰唰啊..洗唰唰... ");
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            System.out.println(Thread.currentThread().getName() + " 终于洗完澡了...");
            semaphore.release();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
