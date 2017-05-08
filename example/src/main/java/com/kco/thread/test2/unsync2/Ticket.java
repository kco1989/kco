package com.kco.thread.test2.unsync2;

/**
 * com.kco.test2.unsync
 * Created by swlv on 2016/10/27.
 */
public class Ticket {
    private static final int DEFAULT_TICKET_COUNT = 1000;
    private int count = DEFAULT_TICKET_COUNT; //票的总数
    private int buyedCount = 0;

    public boolean buyTicket(int count) throws InterruptedException {
        synchronized (new Object()){
            if (this.count - count < 0){
                Thread.sleep(10);
                return false;
            }else{
                this.count = this.count - count;
                Thread.sleep(1);
                this.buyedCount = this.buyedCount + count;
                return true;
            }
        }
    }

    public int getCount() {
        synchronized (new Object()) {
            return count;
        }
    }

    public int getBuyedCount() {
        synchronized (new Object()) {
            return buyedCount;
        }
    }

    public int getAllCount(){
        synchronized (new Object()) {
            return count + buyedCount;
        }
    }
}
