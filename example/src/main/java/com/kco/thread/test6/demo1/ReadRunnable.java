package com.kco.thread.test6.demo1;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Created by pc on 2016/10/29.
 */
public class ReadRunnable implements Runnable {
    ReadWriteLock lock;
    MockFile file;
    boolean isStop;
    public ReadRunnable(ReadWriteLock lock, MockFile file) {
        this.lock = lock;
        this.file = file;
        isStop = false;
    }

    @Override
    public void run() {
        Thread self = Thread.currentThread();
        while(!isStop){
            Lock lock = this.lock.readLock();
            try {
                lock.lock();
                String content = file.readFile();
                System.out.println(self.getName() + " read: " + content);
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setStop() {
        isStop = true;
    }
}
