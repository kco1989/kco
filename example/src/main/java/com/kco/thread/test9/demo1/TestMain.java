package com.kco.thread.test9.demo1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by pc on 2016/10/29.
 */
public class TestMain {

    public static void main(String[] args) {
        String name = "明刚红丽黑白";
        CyclicBarrier cyclicBarrier = new CyclicBarrier(name.length(), new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() +
                        " 清点人数,1,2,3...,ok,人到齐了,准备出发..... go go go....");
            }
        });
        List<Thread> tourismThread = new ArrayList<>();
        for (char ch : name.toCharArray()){
            tourismThread.add(new Thread(new TourismRunnable(cyclicBarrier), "小" + ch));
        }
        for (Thread thread : tourismThread){
            thread.start();
        }
    }
}
