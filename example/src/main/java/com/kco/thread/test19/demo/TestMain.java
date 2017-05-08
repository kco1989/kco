package com.kco.thread.test19.demo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by pc on 2017/4/18.
 */
public class TestMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinTask<Integer> task = pool.submit(new MakeMoneyTask(1000000));
        do {
            try {
                TimeUnit.MILLISECONDS.sleep(5);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }while (!task.isDone());
        pool.shutdown();
        System.out.println(task.get());

    }
}