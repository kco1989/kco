package com.kco.thread.test7.demo1;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * com.kco.test3.demo1
 * Created by swlv on 2016/10/28.
 */
public class Room {
    Lock lock = new ReentrantLock();
    Condition boyIsCome = lock.newCondition();
    Condition girlIsCome = lock.newCondition();
    private String gril = null;

    public void makeGridInRoom(String gril){
        lock.lock();
        try {
            while (this.gril != null){
                System.out.println(gril + " 我的心在等待... 永远在等待.. " );
                girlIsCome.await();
            }

            Thread.sleep(10);
            this.gril = gril;
            boyIsCome.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public void playWithGril(String boy){
        lock.lock();
        try {
            while (this.gril == null){
                System.out.println(boy + " 我的心在等待... 永远在等待.. " );
                boyIsCome.await();
            }

            Thread.sleep(10);
            System.out.println(boy + " play with " + this.gril);
            Thread.sleep(500);
            this.gril = null;
            girlIsCome.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
