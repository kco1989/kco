package com.kco.thread.test4.demo1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * com.kco.test4.demo1
 * Created by swlv on 2016/10/28.
 */
public class TestMain3 {

    public static class CalcRunnable implements Runnable{
        ThreadLocal<Integer> threadLocal;
        public CalcRunnable(ThreadLocal<Integer> threadLocal) {
            this.threadLocal = threadLocal;
        }

        @Override
        public void run() {
            threadLocal.set(0);//设置默认值
            for (int i = 1; i <= 100; i++){
                threadLocal.set(threadLocal.get() + i);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName() + " 的计算结果为: " + threadLocal.get());
        }
    }

    public static void main(String[] args) {
        ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i ++){
            Thread thread = new Thread(new CalcRunnable(threadLocal));
            threads.add(thread);
        }
        for (Thread thread : threads){
            thread.start();
        }
    }
}
