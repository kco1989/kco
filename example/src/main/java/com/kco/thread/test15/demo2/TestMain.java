package com.kco.thread.test15.demo2;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Exchanger;

/**
 * com.kco.test12.demo
 * Created by swlv on 2016/10/31.
 */
public class TestMain {
    public static void main(String[] args) {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(10);
        new Thread(new LuFeiRunnable(queue)).start();
        new Thread(new ShanZhiRunnable(queue)).start();
    }
}
