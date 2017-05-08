package com.kco.thread.test3.demo1;

/**
 * com.kco.test3.demo1
 * Created by swlv on 2016/10/28.
 */
public class Room3 extends Room {

    @Override
    public synchronized void makeGridInRoom(String gril){
        while (this.gril != null){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.gril = gril;
    }

    @Override
    public synchronized void playWithGril(String boy){
        while (this.gril == null){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(boy + " play with " + this.gril);
        this.gril = null;
    }
}
