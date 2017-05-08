package com.kco.thread.test1;

/**
 * com.kco.test1
 * Created by swlv on 2016/10/27.
 */
public class ThreadAttr {
    public static void main(String[] args) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                Thread self = Thread.currentThread();
                System.out.println("id:" + self.getId());
                System.out.println("name:" + self.getName());
                System.out.println("state:" + self.getState());
                System.out.println("priority" + self.getPriority());
                System.out.println("daemon" + self.isDaemon());
            }
        });
        thread.start();
    }
}
