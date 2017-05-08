package com.kco.thread.test3.demo2;

/**
 * com.kco.test3.demo1
 * Created by swlv on 2016/10/28.
 */
public class PlayBoy implements Runnable{
    private Room room;
    private String boy;
    public PlayBoy(Room room, String boy) {
        this.room = room;
        this.boy = boy;
    }

    @Override
    public void run() {
        room.playWithGril(boy);
    }
}
