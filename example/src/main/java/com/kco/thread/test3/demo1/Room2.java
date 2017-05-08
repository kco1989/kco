package com.kco.thread.test3.demo1;

/**
 * com.kco.test3.demo1
 * Created by swlv on 2016/10/28.
 */
public class Room2 extends Room {

    @Override
    public synchronized void makeGridInRoom(String gril){
        if (this.gril != null){
            return;
        }
        this.gril = gril;
    }

    @Override
    public synchronized void playWithGril(String boy){
        if (this.gril == null){
            return;
        }
        System.out.println(boy + " play with " + this.gril);
        this.gril = null;
    }
}
