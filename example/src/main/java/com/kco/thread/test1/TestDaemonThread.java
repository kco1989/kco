package com.kco.thread.test1;

/**
 * com.kco.test1
 * Created by swlv on 2016/10/27.
 */
public class TestDaemonThread {
    public static void main(String[] args) {
        System.out.println("start main");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i ++){
                    try {
                        System.out.println(Thread.currentThread().getName() + ":" + i);
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        System.out.println("end main");
    }
}
