package com.kco.thread.test13.demo1;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * com.kco.test13.demo1
 * Created by swlv on 2016/10/31.
 */
public class TestMain {
    public static void main(String[] args) {
        Random random = new Random();
        Timer timer = new Timer();

        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int index = random.nextInt(5);
                try {

                    Thread.sleep(4 * 1000);
                    Calendar instance = Calendar.getInstance();
                    System.out.println("sub1当前时间:" + dataFormat.format(this.scheduledExecutionTime()) + " --> " + instance.get(Calendar.SECOND));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 2000);
//        Timer timer1 = new Timer();
//        timer1.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                int index = random.nextInt(5);
//                try {
//
//                    Thread.sleep(4 * 1000);
//                    Calendar instance = Calendar.getInstance();
//                    System.out.println("sub2当前时间:" + dataFormat.format(this.scheduledExecutionTime()) + " --> " + instance.get(Calendar.SECOND));
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 0, 2000);
        System.out.println(Calendar.getInstance().get(Calendar.SECOND) + " main");

    }
}
