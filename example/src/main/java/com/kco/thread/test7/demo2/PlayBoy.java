package com.kco.thread.test7.demo2;


/**
 * com.kco.test3.demo1
 * Created by swlv on 2016/10/28.
 */
public class PlayBoy implements Runnable{
    private Room room;
    private String boy;
    private int index;
    public PlayBoy(Room room, String boy, int index) {
        this.room = room;
        this.boy = boy;
        this.index = index;
    }

    @Override
    public void run() {
        room.playWithGril(boy, index);
    }
}
