package com.kco.thread.test13.demo1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * com.kco.test13.demo1
 * Created by swlv on 2016/10/31.
 */
public class Demo6 {
    public static void main(String[] args) throws ParseException {
        Timer timer = new Timer();
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                    System.out.println("sub1执行时间:" + dataFormat.format(this.scheduledExecutionTime()) + " --> 当前时间:" + dataFormat.format(new Date()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 2000);
    }
}