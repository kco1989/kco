package com.kco.thread.test18.demo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by pc on 2017/4/18.
 */
public class Demo3 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main start");
        FutureTask task = new FutureTask(() -> {
            System.out.println("正在执行子任务");
            int i = 1 / 0;
            return 0;
        });
        new Thread(task).start();
        while (task.isDone());
        System.out.println("任务返回结果:" + task.get());
        System.out.println("main end");
    }
}
