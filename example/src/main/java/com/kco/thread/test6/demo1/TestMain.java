package com.kco.thread.test6.demo1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by pc on 2016/10/29.
 */
public class TestMain {

    public static void main(String[] args) {
        MockFile mockFile = new MockFile();
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        Thread thread1 = new Thread(new WriteRunnable(lock, mockFile, "ABCDEFGHIJKLMOOPQRSTUVWXYZ"));
        Thread thread2 = new Thread(new WriteRunnable(lock, mockFile, "abcdefghijklmoopqrstuvwxyz"));
        Thread thread3 = new Thread(new WriteRunnable(lock, mockFile, "0123456789"));
        List<Thread> readThreadList = new ArrayList<>();
        List<ReadRunnable> readRunnables = new ArrayList<>();
        for (int i = 0; i < 5; i ++){
            ReadRunnable readRunnable = new ReadRunnable(lock, mockFile);
            readThreadList.add(new Thread(readRunnable));
            readRunnables.add(readRunnable);
        }
        thread1.start();
        thread2.start();
        thread3.start();
        for (Thread readThread : readThreadList){
            readThread.start();
        }

        while (thread1.isAlive() || thread2.isAlive() || thread3.isAlive());

        for (ReadRunnable readRunnable : readRunnables){
            readRunnable.setStop();
        }
    }
}
