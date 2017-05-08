package com.kco.thread.test12.demo2;

import java.util.concurrent.Exchanger;

/**
 * com.kco.test12.demo
 * Created by swlv on 2016/10/31.
 */
public class TestMain {
    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger<>();
        new Thread(new LuFeiRunnable(exchanger)).start();
        new Thread(new ShanZhiRunnable(exchanger)).start();
    }
}
