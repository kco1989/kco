package com.kco.thread.test1;

/**
 * com.kco.test1
 * Created by swlv on 2016/10/27.
 */
public class StopThread2 {
    public static class StopRunnable implements Runnable{
        private boolean isStop = false;

        public void setStop(){
            this.isStop = true;
        }
        @Override
        public void run() {
            int count = 0;
            while (!isStop){
                try {
                    Thread.sleep(100);
                    System.out.println(Thread.currentThread().getName() + ":" + count++);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("begin main");
        StopRunnable stop = new StopRunnable();
        Thread thread = new Thread(stop);
        thread.start();
        Thread.sleep(200);
        stop.setStop();
        System.out.println("end main");
    }
}
