(九)java多线程之CyclicBarrier
=====================================

>本人邮箱: <kco1989@qq.com>
>欢迎转载,转载请注明网址 <http://blog.csdn.net/tianshi_kco>
>github: <https://github.com/kco1989/kco>
>代码已经全部托管[github](https://github.com/kco1989/kco/blob/master/threadTest)有需要的同学自行下载

[TOC]

#引言
一个同步辅助类，它允许一组线程互相等待，直到到达某个公共屏障点 (common barrier point)。在涉及一组固定大小的线程的程序中，这些线程必须不时地互相等待，此时 CyclicBarrier 很有用。因为该 barrier 在释放等待线程后可以重用，所以称它为循环 的 barrier。

#理论
CyclicBarrier 支持一个可选的 Runnable 命令，在一组线程中的最后一个线程到达之后（但在释放所有线程之前），该命令只在每个屏障点运行一次。若在继续所有参与线程之前更新共享状态，此屏障操作 很有用。
示例用法：下面是一个在并行分解设计中使用 barrier 的例子：

class Solver {
    final int N;
    final float[][] data;
    final CyclicBarrier barrier;

    class Worker implements Runnable {
        int myRow;

        Worker(int row) {
            myRow = row;
        }

        public void run() {
            while (!done()) {
                processRow(myRow);
                try {
                    barrier.await();
                } catch (InterruptedException ex) {
                    return;
                } catch (BrokenBarrierException ex) {
                    return;
                }
            }
        }
    }

    public Solver(float[][] matrix) {
        data = matrix;
        N = matrix.length;
        barrier = new CyclicBarrier(N,
                new Runnable() {
                    public void run() {
                        mergeRows(...);
                    }
                });
        for (int i = 0; i < N; ++i)
            new Thread(new Worker(i)).start();

        waitUntilDone();
    }
}

>在这个例子中，每个 worker 线程处理矩阵的一行，在处理完所有的行之前，该线程将一直在屏障处等待。处理完所有的行之后，将执行所提供的 Runnable 屏障操作，并合并这些行。如果合并者确定已经找到了一个解决方案，那么 done() 将返回 true，所有的 worker 线程都将终止。

如果屏障操作在执行时不依赖于正挂起的线程，则线程组中的任何线程在获得释放时都能执行该操作。为方便此操作，每次调用 await() 都将返回能到达屏障处的线程的索引。然后，您可以选择哪个线程应该执行屏障操作，例如：

  if (barrier.await() == 0) {
     // log the completion of this iteration
   }
对于失败的同步尝试，CyclicBarrier 使用了一种要么全部要么全不 (all-or-none) 的破坏模式：如果因为中断、失败或者超时等原因，导致线程过早地离开了屏障点，那么在该屏障点等待的其他所有线程也将通过 BrokenBarrierException（如果它们几乎同时被中断，则用 InterruptedException）以反常的方式离开。

内存一致性效果：线程中调用 await() 之前的操作 happen-before 那些是屏障操作的一部份的操作，后者依次 happen-before 紧跟在从另一个线程中对应 await() 成功返回的操作。

* `CyclicBarrier(int parties)` 创建一个新的 CyclicBarrier，它将在给定数量的参与者（线程）处于等待状态时启动，但它不会在启动 barrier 时执行预定义的操作。
* `CyclicBarrier(int parties, Runnable barrierAction)` 创建一个新的 CyclicBarrier，它将在给定数量的参与者（线程）处于等待状态时启动，并在启动 barrier 时执行给定的屏障操作，该操作由最后一个进入 barrier 的线程执行。
* `await()` 在所有参与者都已经在此 barrier 上调用 await 方法之前，将一直等待。
* `await(long timeout, TimeUnit unit)` 在所有参与者都已经在此屏障上调用 await 方法之前将一直等待,或者超出了指定的等待时间。
* `getNumberWaiting()`    返回当前在屏障处等待的参与者数目。
* `getParties()` 返回要求启动此 barrier 的参与者数目。
* `isBroken()` 查询此屏障是否处于损坏状态。
* `reset()`  将屏障重置为其初始状态。

# 例子
还是一样,理论是比较枯燥的,咱们还是举例来说,比较生动一点.一些教科书举一些1~100000分批累加例子.咱们不举这种比较无聊的例子.旅游,相信很多人都喜欢吧!旅游的套路一般都出发点集合,入住酒店,到旅游点1,再到旅游点2,再到旅游点3,在集合返回.每次都某一个地点时.导游都会清点人数.不要把人给弄丢.ok,开始编码...

* 首先写一个旅游类 `TourismRunnable`,`run`方法很简单就调用`tourism()`旅游的行程,然后在`tourism`,再调用旅游点路程,看代码..

```java 
public class TourismRunnable implements Runnable{
    CyclicBarrier cyclicBarrier;
    Random random;
    public TourismRunnable(CyclicBarrier cyclicBarrier) {
        this.cyclicBarrier = cyclicBarrier;
        this.random = new Random();
    }

    @Override
    public void run() {
        tourism();
    }

    /**
     * 旅游过程
     */
    private void tourism() {
        goToStartingPoint();
        goToHotel();
        goToTourismPoint1();
        goToTourismPoint2();
        goToTourismPoint3();
        goToEndPoint();
    }

    /**
     * 装备返程
     */
    private void goToEndPoint() {
        goToPoint("飞机场,准备登机回家");
    }

    /**
     * 到达旅游点3
     */
    private void goToTourismPoint3() {
        goToPoint("旅游点3");
    }

    /**
     * 到达旅游点2
     */
    private void goToTourismPoint2() {
        goToPoint("旅游点2");
    }

    /**
     * 到达旅游点1
     */
    private void goToTourismPoint1() {
        goToPoint("旅游点1");
    }

    /**
     * 入住酒店
     */
    private void goToHotel() {
        goToPoint("酒店");
    }

    /**
     * 出发点集合
     */
    private void goToStartingPoint() {
        goToPoint("出发点");
    }

    private int getRandomTime(){
        int time = this.random.nextInt(400) + 100;
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return time;
    }

    private void goToPoint(String point){
        try {
            String name = Thread.currentThread().getName();
            System.out.println(name + " 花了 " + getRandomTime() + " 时间才到了" + point);
            cyclicBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
 
>在每个旅游点,每个人旅游所花的时间是随机的,有些人玩的比较久一点,有些则走马观花,拍拍照就完事了

* 再写一个测试类,让一群小朋友去旅游吧

```java 
public class TestMain {

    public static void main(String[] args) {
        String name = "明刚红丽黑白";
        CyclicBarrier cyclicBarrier = new CyclicBarrier(name.length(), new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() +
                        " 清点人数,1,2,3...,ok,人到齐了,准备出发..... go go go....");
            }
        });
        List<Thread> tourismThread = new ArrayList<>();
        for (char ch : name.toCharArray()){
            tourismThread.add(new Thread(new TourismRunnable(cyclicBarrier), "小" + ch));
        }
        for (Thread thread : tourismThread){
            thread.start();
        }
    }
}
```

运行结果:

```
小刚 花了 131 时间才到了出发点
小黑 花了 237 时间才到了出发点
小丽 花了 250 时间才到了出发点
小红 花了 335 时间才到了出发点
小明 花了 379 时间才到了出发点
小白 花了 398 时间才到了出发点
小白 清点人数,1,2,3...,ok,人到齐了,准备出发..... go go go....
小红 花了 128 时间才到了酒店
小刚 花了 156 时间才到了酒店
小黑 花了 240 时间才到了酒店
小白 花了 280 时间才到了酒店
小明 花了 492 时间才到了酒店
小丽 花了 499 时间才到了酒店
小丽 清点人数,1,2,3...,ok,人到齐了,准备出发..... go go go....
小丽 花了 188 时间才到了旅游点1
小刚 花了 315 时间才到了旅游点1
小明 花了 374 时间才到了旅游点1
小白 花了 395 时间才到了旅游点1
小黑 花了 428 时间才到了旅游点1
小红 花了 496 时间才到了旅游点1
小红 清点人数,1,2,3...,ok,人到齐了,准备出发..... go go go....
小明 花了 206 时间才到了旅游点2
小刚 花了 223 时间才到了旅游点2
小红 花了 302 时间才到了旅游点2
小白 花了 308 时间才到了旅游点2
小黑 花了 317 时间才到了旅游点2
小丽 花了 400 时间才到了旅游点2
小丽 清点人数,1,2,3...,ok,人到齐了,准备出发..... go go go....
小白 花了 100 时间才到了旅游点3
小丽 花了 132 时间才到了旅游点3
小红 花了 157 时间才到了旅游点3
小黑 花了 165 时间才到了旅游点3
小刚 花了 375 时间才到了旅游点3
小明 花了 416 时间才到了旅游点3
小明 清点人数,1,2,3...,ok,人到齐了,准备出发..... go go go....
小刚 花了 100 时间才到了飞机场,准备登机回家
小黑 花了 137 时间才到了飞机场,准备登机回家
小红 花了 232 时间才到了飞机场,准备登机回家
小明 花了 260 时间才到了飞机场,准备登机回家
小丽 花了 264 时间才到了飞机场,准备登机回家
小白 花了 394 时间才到了飞机场,准备登机回家
小白 清点人数,1,2,3...,ok,人到齐了,准备出发..... go go go....
```

ok, 运行结果一目了然,中途没有落下任何一个人!`CyclicBarrier`这个类,还是比较容易使用的


---
# 打赏
>如果觉得我的文章写的还过得去的话,有钱就捧个钱场,没钱给我捧个人场(帮我点赞或推荐一下)
>![微信打赏](http://img.blog.csdn.net/20170508085654037?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast) 
>![支付宝打赏](http://img.blog.csdn.net/20170508085710334?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)