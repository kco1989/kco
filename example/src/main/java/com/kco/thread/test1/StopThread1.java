package com.kco.thread.test1;

/**
 * com.kco.test1
 * Created by swlv on 2016/10/27.
 */
public class StopThread1 {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("begin main");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i ++){
                    try {
                        Thread.sleep(100);
                        System.out.println(Thread.currentThread().getName() + ":" + i);
                    } catch (InterruptedException e) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        System.out.println("InterruptedException:" + Thread.currentThread().isInterrupted());
                        break;
                    }
                }
            }
        });
        thread.start();
        System.out.println("main sleep 500ms");
        Thread.sleep(500);
        thread.interrupt();
        System.out.println("thread.isInterrupted:" + thread.isInterrupted());
        System.out.println("end main");
    }
}
