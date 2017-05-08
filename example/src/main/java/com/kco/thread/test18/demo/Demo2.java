package com.kco.thread.test18.demo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by pc on 2017/4/18.
 */
public class Demo2 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main start");
        FutureTask task = new FutureTask(() -> {
            int sum = 0;
            for (int i = 1; i <= 100; i++){
                sum += i;
            }
            return sum;
        });
        new Thread(task).start();
        while (task.isDone());
        System.out.println("任务返回结果:" + task.get());
        System.out.println("main end");
    }
}
