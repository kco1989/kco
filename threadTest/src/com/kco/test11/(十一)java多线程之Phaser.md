(十一)java多线程之Phaser
=====================================

>本人邮箱: <kco1989@qq.com>
>欢迎转载,转载请注明网址 <http://blog.csdn.net/tianshi_kco>
>github: <https://github.com/kco1989/kco>
>代码已经全部托管[github](https://github.com/kco1989/kco/blob/master/threadTest)有需要的同学自行下载

[TOC]
# 引言
讲完了`CyclicBarrier`和`CountDownLatch`,今天讲一个跟这两个类有点类似的`Phaser`.

#原理 
`Phaser` 是一个可重复利用的同步界限`barrier`,它有点类似于`CyclicBarrier`和`CountDownLatch`,但是提供更多灵活的用途.
* 'Registration':注册.在同步块注册的数量是可以随时间变化的.任务可以在任何时候注册(调用`register`,`bulkRegister(int)`),或者可以使用`Phaser(int)`构造方法构造初始化数量,而且可以在任何可以被访问的地方调用`arriveAndDeregister`注销.与大多基础的同步结构一样,注册和注销只会影响内部计算,而不会影响到对应的任务,所以你无法查询该任务时候已经被注册.
* `Synchronization`: 同步.类似`CyclicBarrier`,`Phaser`也可以重复等待.方法`rrivalAndAwaitAdvance`的效果类似于`CyclicBarrier.await`.每一个`Phaser`都有自己内部的计数器.该计数器从0开始,当其他线程到达这个位置时,计数器为从0递增到`Integer.MAC_VALUE`,然后重新变回0.
* `Termination`,`Phaser`可能进入中断状态,可以定义`isTerminated()`检查是否是中断状态.在中断时,所有同步方法立即返回一个负数值而不提前等待.类似的,在中断时试图注册时没有效果的.
* `Tiering`,层叠的.`Phasers`是可以层叠的,就像树结果一样. 


讲理论太烦躁了,还是直接举例子吧.
# 例子1 用`Phaser`代替`CyclicBarrier`
将之前[(九)java多线程之CyclicBarrier](http://blog.csdn.net/tianshi_kco/article/details/52965561)旅游的例子改写一下,
>Phaser替代CyclicBarrier比较简单，CyclicBarrier的await()方法可以直接用Phaser的arriveAndAwaitAdvance()方法替代

* `TourismRunnable` 旅游类

```java 
public class TourismRunnable implements Runnable{
    Phaser phaser;
    Random random;
    public TourismRunnable(Phaser phaser) {
        this.phaser = phaser;
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
            phaser.arriveAndAwaitAdvance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

* `TestMain` 测试类

```java 
public class TestMain {

    public static void main(String[] args) {
        String name = "明刚红丽黑白";
        Phaser phaser = new Phaser(name.length());
        List<Thread> tourismThread = new ArrayList<>();
        for (char ch : name.toCharArray()){
            tourismThread.add(new Thread(new TourismRunnable(phaser), "小" + ch));
        }
        for (Thread thread : tourismThread){
            thread.start();
        }
    }
}
```

运行结果

```
小红 花了 122 时间才到了出发点
小明 花了 259 时间才到了出发点
小白 花了 267 时间才到了出发点
小丽 花了 306 时间才到了出发点
小刚 花了 385 时间才到了出发点
小黑 花了 486 时间才到了出发点
小白 花了 299 时间才到了酒店
小刚 花了 345 时间才到了酒店
小黑 花了 449 时间才到了酒店
小丽 花了 452 时间才到了酒店
小明 花了 462 时间才到了酒店
小红 花了 480 时间才到了酒店
小丽 花了 107 时间才到了旅游点1
小红 花了 141 时间才到了旅游点1
小明 花了 212 时间才到了旅游点1
小黑 花了 286 时间才到了旅游点1
小白 花了 305 时间才到了旅游点1
小刚 花了 386 时间才到了旅游点1
小丽 花了 119 时间才到了旅游点2
小黑 花了 222 时间才到了旅游点2
小明 花了 259 时间才到了旅游点2
小刚 花了 299 时间才到了旅游点2
小红 花了 354 时间才到了旅游点2
小白 花了 422 时间才到了旅游点2
小丽 花了 112 时间才到了旅游点3
小白 花了 182 时间才到了旅游点3
小刚 花了 283 时间才到了旅游点3
小明 花了 295 时间才到了旅游点3
小红 花了 386 时间才到了旅游点3
小黑 花了 483 时间才到了旅游点3
小黑 花了 152 时间才到了飞机场,准备登机回家
小白 花了 178 时间才到了飞机场,准备登机回家
小明 花了 248 时间才到了飞机场,准备登机回家
小红 花了 362 时间才到了飞机场,准备登机回家
小丽 花了 428 时间才到了飞机场,准备登机回家
小刚 花了 432 时间才到了飞机场,准备登机回家
```

# 例子2 用`Phaser`代替`CountDownLatch`
将之前[(十)java多线程之CountDownLatch](http://blog.csdn.net/tianshi_kco/article/details/52972892)旅游回来坐飞机的例子改写一下,

>`CountDownLatch`主要使用的有2个方法
>*  `await()`方法，可以使线程进入等待状态，在`Phaser`中，与之对应的方法是`awaitAdvance(int n)`。
* `countDown()`，使计数器减一，当计数器为0时所有等待的线程开始执行，在Phaser中，与之对应的方法是`arrive()`

* `Airplane`飞机类

```java 
public class Airplane {
    private Phaser phaser;
    private Random random;
    public Airplane(int peopleNum){
        phaser = new Phaser(peopleNum);
        random = new Random();
    }

    /**
     * 下机
     */
    public void getOffPlane(){
        try {
            String name = Thread.currentThread().getName();
            Thread.sleep(random.nextInt(500));
            System.out.println(name + " 在飞机在休息着....");
            Thread.sleep(random.nextInt(500));
            System.out.println(name + " 下飞机了");
            phaser.arrive();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void doWork(){

        String name = Thread.currentThread().getName();
        System.out.println(name + "准备做 清理 工作");
        phaser.awaitAdvance(phaser.getPhase());
        System.out.println("飞机的乘客都下机," + name + "可以开始做 清理 工作");

    }

}
```

* `TestMain` 测试类(没有改)

```java 
public class TestMain {

    public static void main(String[] args) {
        String visitor = "明刚红丽黑白";
        String kongjie = "美惠花";

        Airplane airplane = new Airplane(visitor.length());
        Set<Thread> threads = new HashSet<>();
        for (int i = 0; i < visitor.length(); i ++){
            threads.add(new Thread(() -> {
                airplane.getOffPlane();
            }, "小" + visitor.charAt(i)));
        }
        for (int i = 0; i < kongjie.length(); i ++){
            threads.add(new Thread(() ->{
                airplane.doWork();
            }, "小" + kongjie.charAt(i) + "空姐"));
        }

        for (Thread thread : threads){
            thread.start();
        }
    }
}
```

运行结果

```
小花空姐准备做 清理 工作
小惠空姐准备做 清理 工作
小美空姐准备做 清理 工作
小黑 在飞机在休息着....
小明 在飞机在休息着....
小红 在飞机在休息着....
小丽 在飞机在休息着....
小刚 在飞机在休息着....
小明 下飞机了
小红 下飞机了
小黑 下飞机了
小白 在飞机在休息着....
小丽 下飞机了
小刚 下飞机了
小白 下飞机了
飞机的乘客都下机,小美空姐可以开始做 清理 工作
飞机的乘客都下机,小花空姐可以开始做 清理 工作
飞机的乘客都下机,小惠空姐可以开始做 清理 工作
```


---
#打赏
>如果觉得我的文章写的好的话,有钱就捧个钱场,没钱就给我点个赞
>![微信打赏2元](http://img.blog.csdn.net/20161028223820526)![支付宝打赏2元](http://img.blog.csdn.net/20161028223845557)