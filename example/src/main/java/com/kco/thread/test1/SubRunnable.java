package com.kco.thread.test1;

/**
 * com.kco.test1
 * Created by swlv on 2016/10/27.
 */
public class SubRunnable implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i < 10; i ++){
            System.out.println(Thread.currentThread().getName() + ":" + i);
        }
    }
    public static void main(String[] args) {
        System.out.println("begin main");
        Thread thread = new Thread(new SubRunnable());
        thread.start();
        System.out.println("end main");
    }
}
