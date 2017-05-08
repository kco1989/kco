package com.kco.thread.test15.demo1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2017/4/18.
 */
public class Demo1 {
    public static void main(String[] args) throws InterruptedException {
        List<String> list = new ArrayList<>();
        Thread[] threads = new Thread[100];
        for (int i = 0; i < threads.length; i ++){
            threads[i] = new Thread(() -> {
                for (int j = 0;j < 100; j ++){
                    list.add(Thread.currentThread().getName() + ":" + j);
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads){
            thread.join();
        }

        System.out.println(list.size());
    }
}
