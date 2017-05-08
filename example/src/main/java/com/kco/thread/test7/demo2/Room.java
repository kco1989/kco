package com.kco.thread.test7.demo2;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * com.kco.test3.demo1
 * Created by swlv on 2016/10/28.
 */
public class Room {
    Lock lock;
    List<Condition> boyIsCome;
    Condition girlIsCome;
    private String gril = null;
    private int index = -1;
    public Room(){
        lock = new ReentrantLock();
        girlIsCome = lock.newCondition();
        boyIsCome = new ArrayList<>();
        for (int i = 0; i < 10; i ++){
            boyIsCome.add(lock.newCondition());
        }
    }

    public void makeGridInRoom(String gril, int index){
        lock.lock();
        try {
            while (this.gril != null){
                System.out.println(gril + " 我的心在等待... 永远在等待.. " );
                girlIsCome.await();
            }

            Thread.sleep(10);
            this.gril = gril;
            this.index = index;
            boyIsCome.get(index).signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public void playWithGril(String boy, int index){
        lock.lock();
        try {
            while (this.gril == null || this.index != index){
                System.out.println(boy + " 我的心在等待... 永远在等待.. " );
                boyIsCome.get(index).await();
            }
            Thread.sleep(10);
            System.out.println(boy + " play with " + this.gril);
            Thread.sleep(500);
            this.gril = null;
            this.index = -1;
            girlIsCome.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
