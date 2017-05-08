package com.kco.thread.test3.demo1;

/**
 * com.kco.test3.demo1
 * Created by swlv on 2016/10/28.
 */
public class Room1 extends Room {

    private String gril;

    @Override
    public synchronized void makeGridInRoom(String gril){
        this.gril = gril;
    }

    @Override
    public synchronized void playWithGril(String boy){
        System.out.println(boy + " play with " + this.gril);
        this.gril = null;
    }
}
