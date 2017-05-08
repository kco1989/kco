package com.kco.thread.test8.demo1;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by pc on 2016/10/29.
 */
public class TestMain {

    public static void main(String[] args) {
        Set<Thread> boyAndGril = new HashSet<>();
        ShowerRoom showerRoom = new ShowerRoom();
        for (int i = 0; i < 10; i ++){
            boyAndGril.add(new Thread(new BoyAndGril(showerRoom), "小明" + i + "号"));
        }
        for (int i = 0; i < 10; i ++){
            boyAndGril.add(new Thread(new BoyAndGril(showerRoom), "小红" + i + "号"));
        }
        for (Thread thread : boyAndGril){
            thread.start();
        }
    }
}
