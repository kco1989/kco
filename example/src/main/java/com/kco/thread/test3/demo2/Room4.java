package com.kco.thread.test3.demo2;

/**
 * com.kco.test3.demo1
 * Created by swlv on 2016/10/28.
 */
public class Room4 extends Room {

    @Override
    public synchronized void makeGridInRoom(String gril){
        while (this.gril != null){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.notifyAll();
        this.gril = gril;
    }

    @Override
    public synchronized void playWithGril(String boy){
        while (this.gril == null){
            try {
                System.out.println(boy + " 我的心在等待,永远在等待...");
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(boy + " play with " + this.gril);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.notifyAll();
        this.gril = null;
    }
}
