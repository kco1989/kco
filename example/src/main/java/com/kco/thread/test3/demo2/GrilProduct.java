package com.kco.thread.test3.demo2;

/**
 * com.kco.test3.demo1
 * Created by swlv on 2016/10/28.
 */
public class GrilProduct implements Runnable{
    private Room room;
    public GrilProduct(Room room) {
        this.room = room;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i ++){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            room.makeGridInRoom("小红" + i + "号");
        }
    }
}
