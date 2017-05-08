package com.kco.thread.test13.demo1;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * com.kco.test13.demo1
 * Created by swlv on 2016/10/31.
 */
public class Demo1 {
    public static void main(String[] args) {
        Timer timer = new Timer();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("炸弹爆炸时间:" + dateFormat.format(new Date()));
            }
        }, 4000);
        System.out.println("炸弹安装时间:" + dateFormat.format(new Date()));
    }
}
