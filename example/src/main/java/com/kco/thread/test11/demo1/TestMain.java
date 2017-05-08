package com.kco.thread.test11.demo1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Phaser;

/**
 * Created by pc on 2016/10/29.
 */
public class TestMain {

    public static void main(String[] args) {
        String name = "明刚红丽黑白";
        Phaser phaser = new Phaser(name.length());
        List<Thread> tourismThread = new ArrayList<>();
        for (char ch : name.toCharArray()){
            tourismThread.add(new Thread(new TourismRunnable(phaser), "小" + ch));
        }
        for (Thread thread : tourismThread){
            thread.start();
        }
    }


}
