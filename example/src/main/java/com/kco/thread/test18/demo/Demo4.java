package com.kco.thread.test18.demo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by pc on 2017/4/18.
 */
public class Demo4 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main start");
        FutureTask task = new FutureTask(() -> {
            System.out.println("start 正在执行子任务");
            Thread.sleep(500);
            System.out.println("end 执行子任务");
            return 0;
        });
        new Thread(task).start();
        Thread.sleep(250);
        task.cancel(true);
        System.out.println("main end");
    }
}
