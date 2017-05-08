package com.kco.thread.test12.demo;

import java.util.Random;
import java.util.concurrent.Exchanger;

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
                String food = exchanger.exchange(temp);
                System.out.println("--->路飞拿到山治做的菜: " + food);
                Thread.sleep(random.nextInt(500));
                System.out.println("--->路飞吃完" + food);
                temp = food + "太好吃!太感谢山治了...";
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
