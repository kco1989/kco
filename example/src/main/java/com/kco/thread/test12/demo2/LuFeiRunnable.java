package com.kco.thread.test12.demo2;

import java.util.Random;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * com.kco.test12.demo
 * Created by swlv on 2016/10/31.
 */
public class LuFeiRunnable implements Runnable{
    Exchanger<String> exchanger;
    Random random = new Random();
    public LuFeiRunnable(Exchanger<String> exchanger) {
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        String temp = "开吃啦...";
        while (true){
            try {
                String food = exchanger.exchange(temp, 300, TimeUnit.MILLISECONDS);
                System.out.println("--->路飞吃完" + food);
                temp = food + "太好吃!太感谢山治了...";
                Thread.sleep(random.nextInt(500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                System.out.println("--->路飞等的不耐烦了,不想等......开始喝起 东北风" );
            }
        }
    }
}
