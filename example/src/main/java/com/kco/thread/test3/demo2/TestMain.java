package com.kco.thread.test3.demo2;

import java.util.HashSet;
import java.util.Set;

/**
 * com.kco.test3.demo1
 * Created by swlv on 2016/10/28.
 */
public class TestMain {
    public static void main(String[] args) {

//        Room room4 = new Room4();
//        playGameInRoom(room4);

        Room room5 = new Room5();
        playGameInRoom(room5);
    }

    public static void playGameInRoom(Room room){
        Thread grilProduct = new Thread(new GrilProduct(room));
        Set<Thread> boyThread = new HashSet<>();
        for (int i = 0; i < 10; i ++){
            boyThread.add(new Thread(new PlayBoy(room, "小明" + i + "号")));
        }

        grilProduct.start();
        for (Thread boy : boyThread){
            boy.start();
        }
    }

}
