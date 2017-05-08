package com.kco.thread.test1;

/**
 * com.kco.test1
 * Created by swlv on 2016/10/27.
 */
public class ThreadExceptionTest {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("begin main");
        Thread thread = new Thread(() -> {
            int i = 1 / 0;
        },"myThread");
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println(String.format("%s发生异常%s", t.getName(), e.getMessage()));
            }
        });
        thread.start();
        System.out.println("end main");
    }
}
