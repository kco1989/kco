package com.kco.thread.test11.demo1;

import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Phaser;

/**
 * Created by pc on 2016/10/29.
 */
public class TourismRunnable implements Runnable{
    Phaser phaser;
    Random random;
    public TourismRunnable(Phaser phaser) {
        this.phaser = phaser;
        this.random = new Random();
    }

    @Override
    public void run() {
        tourism();
    }

    /**
     * 旅游过程
     */
    private void tourism() {
        goToStartingPoint();
        goToHotel();
        goToTourismPoint1();
        goToTourismPoint2();
        goToTourismPoint3();
        goToEndPoint();
    }

    /**
     * 装备返程
     */
    private void goToEndPoint() {
        goToPoint("飞机场,准备登机回家");
    }

    /**
     * 到达旅游点3
     */
    private void goToTourismPoint3() {
        goToPoint("旅游点3");
    }

    /**
     * 到达旅游点2
     */
    private void goToTourismPoint2() {
        goToPoint("旅游点2");
    }

    /**
     * 到达旅游点1
     */
    private void goToTourismPoint1() {
        goToPoint("旅游点1");
    }

    /**
     * 入住酒店
     */
    private void goToHotel() {
        goToPoint("酒店");
    }

    /**
     * 出发点集合
     */
    private void goToStartingPoint() {
        goToPoint("出发点");
    }

    private int getRandomTime(){
        int time = 100;
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return time;
    }

    private void goToPoint(String point){
        try {
            String name = Thread.currentThread().getName();

            System.out.println(name + " 花了 " + getRandomTime() + " 时间才到了" + point);
            phaser.arriveAndAwaitAdvance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
