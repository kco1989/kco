package com.kco.thread.test4.demo1;

import java.util.ArrayList;
import java.util.List;

/**
 * com.kco.test4.demo1
 * Created by swlv on 2016/10/28.
 */
public class TestMain1 {

    public static class CalcRunnable implements Runnable{
        List<Integer> list ;
        int index;
        public CalcRunnable(List<Integer> list, int index) {
            this.list = list;
            this.index = index;
        }

        @Override
        public void run() {
            for (int i = 1; i <= 100; i++){
                list.set(index, list.get(index) + i);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i ++){
            list.add(0);
            threads.add(new Thread(new CalcRunnable(list,i)));
        }
        for (Thread thread : threads){
            thread.start();
        }

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(list);
    }
}
