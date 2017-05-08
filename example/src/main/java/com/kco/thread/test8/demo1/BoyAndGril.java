package com.kco.thread.test8.demo1;

/**
 * Created by pc on 2016/10/29.
 */
public class BoyAndGril implements Runnable{
    ShowerRoom showerRoom;
    public BoyAndGril(ShowerRoom showerRoom) {
        this.showerRoom = showerRoom;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        showerRoom.bathe(name);
    }
}
