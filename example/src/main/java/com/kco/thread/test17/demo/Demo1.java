package com.kco.thread.test17.demo;

import java.util.concurrent.*;

/**
 * Created by pc on 2017/4/18.
 */
public class Demo1 {
    public static void main(String[] args) {
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(10);
       // RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
       // RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardOldestPolicy();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        ThreadPoolExecutor pool = new ThreadPoolExecutor(3, 3, 0, TimeUnit.SECONDS, queue, handler);
        for (int i = 0; i < 20; i ++){
            final int temp = i;
            pool.execute(() -> {
                System.out.println("客户" + temp + "来了.......");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("客户" + temp + "走了.......");
            });
        }
        pool.shutdown();
    }
}
