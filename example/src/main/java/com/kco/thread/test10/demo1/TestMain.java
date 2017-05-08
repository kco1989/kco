package com.kco.thread.test10.demo1;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by pc on 2016/10/30.
 */
public class TestMain {

    public static void main(String[] args) {
        String visitor = "明刚红丽黑白";
        String kongjie = "美惠花";

        Airplane airplane = new Airplane(visitor.length());
        Set<Thread> threads = new HashSet<>();
        for (int i = 0; i < visitor.length(); i ++){
            threads.add(new Thread(() -> {
                airplane.getOffPlane();
            }, "小" + visitor.charAt(i)));
        }
        for (int i = 0; i < kongjie.length(); i ++){
            threads.add(new Thread(() ->{
                airplane.doWork();
            }, "小" + kongjie.charAt(i) + "空姐"));
        }

        for (Thread thread : threads){
            thread.start();
        }
    }
}
