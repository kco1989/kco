package com.kco.thread.test6.demo1;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Created by pc on 2016/10/29.
 */
public class WriteRunnable implements Runnable {
    ReadWriteLock lock;
    MockFile file;
    String content;

    public WriteRunnable(ReadWriteLock lock, MockFile file, String content) {
        this.lock = lock;
        this.file = file;
        this.content = content;
    }

    @Override
    public void run() {
        Thread self = Thread.currentThread();
        for (char ch : content.toCharArray()){
            Lock lock = this.lock.writeLock();
            try {
                lock.lock();
                System.out.println(self.getName() + " write " + ch);
                file.writeFile(ch);
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
