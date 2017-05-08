package com.kco.thread.test1;

/**
 * com.kco.test1
 * Created by swlv on 2016/10/27.
 */
public class SubThread extends Thread{
    @Override
    public void run() {
        for (int i = 0; i < 100; i ++){
            System.out.println(Thread.currentThread().getName() + ":" + i);
        }
    }

    public static void main(String[] args) {
        System.out.println("begin main");
        SubThread sub = new SubThread();
        sub.start();
        System.out.println("end main");
        sub.interrupt();
    }
}
