package com.kco.thread.test1;

/**
 * com.kco.test1
 * Created by swlv on 2016/10/27.
 */
public class ThreadJoinTest {
    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            for (int i = 0;i < 10; i ++){
                try {
                    Thread.sleep(10);
                    System.out.println(Thread.currentThread().getName() + ":" + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "thread1");
        Thread thread2 = new Thread(() -> {
            try {
                thread1.join(30);
                for (int i = 0;i < 10; i ++){
                    Thread.sleep(10);
                    System.out.println(Thread.currentThread().getName() + ":" + i);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "thread2");
        thread1.start();
        thread2.start();

    }
}
