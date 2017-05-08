package com.kco.thread.test12.demo2;

import java.util.Random;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * com.kco.test12.demo
 * Created by swlv on 2016/10/31.
 */
public class ShanZhiRunnable implements Runnable{
    Exchanger<String> exchanger;
    Random random = new Random();
    public ShanZhiRunnable(Exchanger<String> exchanger) {
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        while (true){
            String food = Food.getRandomFood();
            try {
                System.out.println("==>山治开始做 " + food);
                Thread.sleep(random.nextInt(500));
                System.out.println("==>山治把 " + food + " 给做好了,给路飞送过去");
                String exchange = exchanger.exchange(food, 300, TimeUnit.MILLISECONDS);
                System.out.println("==>山治收到路飞的评语:" + exchange);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                System.out.println("==>山治等的不耐烦了,不想等......,把 " + food + " 其他船员吃了");
            }
        }

    }
}
