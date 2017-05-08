(十四)java多线程之死锁以及解决方案
=====================================

>本人邮箱: <kco1989@qq.com>
>欢迎转载,转载请注明网址 <http://blog.csdn.net/tianshi_kco>
>github: <https://github.com/kco1989/kco>
>代码已经全部托管[github](https://github.com/kco1989/kco/blob/master/threadTest)有需要的同学自行下载

[TOC]
#引言
多线程如果设计的不合理的话,很可能就会出现死锁.当两个或者多个线程同事想要去获取共享资源的锁时,但每个线程都要等其他线程把他们各自的锁给释放,才能继续运行,这就是死锁.出现死锁必须具备以下几点
* 要有两个或两个以上的线程
* 至少有两个共享资源的锁
* 至少存在两个线程各自拥有一个锁
* 现在这两个线程在等待获取彼此的锁,这就出现死锁了

比如`Thread1`

```java
synchronized(A){
    //Thread1 执行到这里
    synchronized(B){
    ...
    }
}
```

`Thread2`

```java
synchronized(B){
    //Thread2 执行到这里
    synchronized(A){
    ...
    }
}
```
以上这种情况就是死锁,如果是两个线程出现死锁,问题可能还比较好找.更复杂是有多个线程,比如`线程n`各自拥有`锁n`,然后`线程1`到`线程n-1`,正在请求获取`锁n+1`,而`线程n`正在请求获取`锁1`,这样也或出现死锁,而且还更难被发现.


#例子
我们要看一个例子

```java
public class Demo1 {
    public static void main(String[] args) {
        Object bigGate = new Object();
        Object smallGate = new Object();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = Thread.currentThread().getName();
                synchronized (bigGate){
                    System.out.println(name + ":我把大门给锁了...然后我休息一下...");
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(name + ":我现在要进入小门.....");
                    synchronized (smallGate){
                        System.out.println(name + ":我永远都进不来啊.....");
                    }

                }
            }
        },"小明").start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = Thread.currentThread().getName();
                synchronized (smallGate){
                    System.out.println(name + ":我把小门给锁了...然后我休息一下...");
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(name + ":我现在要进入大门.....");
                    synchronized (bigGate){
                        System.out.println(name + ":我永远都进不来啊.....");
                    }

                }
            }
        },"小红").start();
    }
}
```
运行结果

```
小明我把大门给锁了...然后我休息一下...
小红我把小门给锁了...然后我休息一下...
小明:我现在要进入小门.....
小红:我现在要进入大门.....
//然后程序到这里就一直不动了.....
```

#解决办法
* 锁的顺序,让两个线程获取锁的顺序是一直,则不会出现死锁

```java
public class Demo2 {
    public static void main(String[] args) {
        Object bigGate = new Object();
        Object smallGate = new Object();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = Thread.currentThread().getName();
                synchronized (bigGate){
                    System.out.println(name + ":我把大门给锁了...然后我休息一下...");
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(name + ":我现在要进入小门.....");
                    synchronized (smallGate){
                        System.out.println(name + ":我终于进来了.....");
                    }

                }
            }
        },"小明").start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = Thread.currentThread().getName();
                synchronized (bigGate){
                    System.out.println(name + ":我把大门给锁了...然后我休息一下...");
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(name + ":我现在要进入小门.....");
                    synchronized (smallGate){
                        System.out.println(name + ":我终于进来了.....");
                    }

                }
            }
        },"小红").start();
    }
}
```

运行结果:

```
小明:我把大门给锁了...然后我休息一下...
小明:我现在要进入小门.....
小明:我终于进来了.....
小红:我把大门给锁了...然后我休息一下...
小红:我现在要进入小门.....
小红:我终于进来了.....
```

* 在获取锁的时候加超时时间,这里我们用之前学的`Lock`来做例子

```java
public class Demo3 {
    public static void main(String[] args) {
        Lock bigGate = new ReentrantLock();
        Lock smallGate = new ReentrantLock();
        Random random = new Random();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = Thread.currentThread().getName();
                bigGate.lock();
                try {
                    System.out.println(name + ":我把大门给锁了...然后我休息一下...");
                    Thread.sleep(100);
                    System.out.println(name + ":我现在要进入小门.....");
                    if(smallGate.tryLock(random.nextInt(500), TimeUnit.MILLISECONDS)){
                        try {
                            System.out.println(name + ":我终于进来了.....");
                        }finally {
                            smallGate.unlock();
                        }
                    }else{
                        System.out.println(name + ":我进不去小门,算了,不进了...");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    bigGate.unlock();
                }
            }
        },"小明").start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = Thread.currentThread().getName();
                smallGate.lock();
                try {
                    System.out.println(name + ":我把小门给锁了...然后我休息一下...");
                    Thread.sleep(100);
                    System.out.println(name + ":我现在要进入大门.....");
                    if(bigGate.tryLock(random.nextInt(500), TimeUnit.MILLISECONDS)){
                        try {
                            System.out.println(name + ":我终于进来了.....");
                        }finally {
                            bigGate.unlock();
                        }
                    }else{
                        System.out.println(name + ":我进不去大门,算了,不进了...");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    smallGate.unlock();
                }
            }
        },"小红").start();
    }
}
```

运行结果:

```
小明:我把大门给锁了...然后我休息一下...
小红:我把小门给锁了...然后我休息一下...
小明:我现在要进入小门.....
小红:我现在要进入大门.....
小红:我进不去大门,算了,不进了...
小明:我终于进来了.....
```

这样也可以保证不会出现死锁.

---
# 打赏
>如果觉得我的文章写的还过得去的话,有钱就捧个钱场,没钱给我捧个人场(帮我点赞或推荐一下)
>![微信打赏](http://img.blog.csdn.net/20170508085654037?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast) 
>![支付宝打赏](http://img.blog.csdn.net/20170508085710334?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)