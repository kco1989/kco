package com.kco.thread.test4.demo1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * com.kco.test4.demo1
 * Created by swlv on 2016/10/28.
 */
public class TestMain2 {

    public static class CalcRunnable implements Runnable{
        Map<Thread,Integer> map;
        public CalcRunnable(Map<Thread,Integer> map) {
            this.map = map;
        }

        @Override
        public void run() {
            Thread self = Thread.currentThread();
            for (int i = 1; i <= 100; i++){
                map.put(self, map.get(self) + i);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        Map<Thread,Integer> map = new HashMap<>();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i ++){
            Thread thread = new Thread(new CalcRunnable(map));
            map.put(thread,0);
            threads.add(thread);
        }
        for (Thread thread : threads){
            thread.start();
        }

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(map);
    }
}
