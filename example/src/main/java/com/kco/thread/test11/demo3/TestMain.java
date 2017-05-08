package com.kco.thread.test11.demo3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Phaser;

/**
 * Created by pc on 2016/10/29.
 */
public class TestMain {

    public static void main(String[] args) {
        String name = "明刚红丽黑白";
        Phaser phaser = new SubPhaser(name.length());
        List<Thread> tourismThread = new ArrayList<>();
        for (char ch : name.toCharArray()){
            tourismThread.add(new Thread(new TourismRunnable(phaser), "小" + ch));
        }
        for (Thread thread : tourismThread){
            thread.start();
        }
    }
    public static class SubPhaser extends Phaser{
        public SubPhaser(int parties) {
            super(parties);
        }

        @Override
        protected boolean onAdvance(int phase, int registeredParties) {

            System.out.println(Thread.currentThread().getName() + ":全部"+getArrivedParties()+"个人都到齐了,现在是第"+(phase + 1)
                    +"次集合准备去下一个地方..................\n");
            return super.onAdvance(phase, registeredParties);
        }
    }
}
