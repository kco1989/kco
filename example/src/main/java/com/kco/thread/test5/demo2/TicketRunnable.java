package com.kco.thread.test5.demo2;

import java.util.Random;
import java.util.concurrent.locks.Lock;

/**
 * com.kco.test5
 * Created by swlv on 2016/10/28.
 */
public class TicketRunnable implements Runnable{
    private Ticket ticket;
    private Random random;
    private Lock lock;
    public TicketRunnable(Ticket ticket, Lock lock) {
        this.ticket = ticket;
        this.lock = lock;
        random = new Random();
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; ) {

            if(lock.tryLock()){
                try {
                    int count = random.nextInt(10) + 1;
                    boolean success = ticket.buyTicket(count);
                    System.out.println(String.format("%s打算买%d张票,买票%s了,还剩下%d张票,总共卖掉%d张票, 总票数%d",
                            Thread.currentThread().getName(), count, success ? "成功" : "失败",
                            ticket.getCount(), ticket.getBuyedCount(), ticket.getAllCount()));
                    if (!success) {
                        break;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    lock.unlock();
                    i ++;
                }
            }else{
                System.out.println(Thread.currentThread().getName() + " 买票系统被占用,尝试获取锁失败.");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
