package com.kco.thread.test5.demo2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * com.kco.test2.unsync
 * Created by swlv on 2016/10/27.
 */
public class SyncThreadTest {

    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        Ticket ticket = new Ticket();
        Lock lock = new ReentrantLock();
        for (int i = 0; i < 20; i ++){
            threads.add(new Thread(new TicketRunnable(ticket, lock)));
        }

        for (Thread thread : threads){
            thread.start();
        }
    }
}
