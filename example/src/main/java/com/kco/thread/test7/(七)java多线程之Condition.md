(七)java多线程之Condition
=====================================

>本人邮箱: <kco1989@qq.com>
>欢迎转载,转载请注明网址 <http://blog.csdn.net/tianshi_kco>
>github: <https://github.com/kco1989/kco>
>代码已经全部托管[github](https://github.com/kco1989/kco/blob/master/threadTest)有需要的同学自行下载

[TOC]

#引言
在写[(五)java多线程之Lock类](http://blog.csdn.net/tianshi_kco/article/details/52960709)时,我们暂时忽略掉`Lock`的一个方法,那就是`Lock.newCondition()`,这个方法返回一个`Condition`,现在我们这章就重点讲这个东东是什么,以及怎么使用.

#理论
`Condition`的中文翻译是`状态`.没错,这个就是让多线程在不同状态切换其他线程执行.跟`Object.wait`和`Object.notify`有点类似.

+ `Condition.await`: 让当前线程一直等到直到获取信号,或者发生中断.通过和`lock`锁互相配合,会使当前线程一直睡眠直到一下四种情况的其中一种发生:
    * 另外一个线程调用当前`Condition`的`signal`方法,这当前线程会被挑选出来,并被唤醒
    * 其他线程调用了当前`Condition`的`signalAll`方法
	* 其他线程调用了当前线程的中断,这当前中断会被挂起
	* 当前线程被假唤醒
* `Condition.awaitUninterruptibly` 跟`Condition.await`类似,只是不能被中断
* `Condition.await(long time, TimeUnit unit)` 当前线程会被唤醒,要么是获得信号,要么是中断,要么是指定时间到
* `Condition.awaitUntil(Date deadline)`当前线程会被唤醒,要么是获得信号,要么是中断,要么是到了指定结束时间
> 一般用法如下
>```java
>boolean aMethod(Date deadline) {
>  boolean stillWaiting = true;
>  lock.lock();
>  try {
>    while (!conditionBeingWaitedFor()) {
>      if (!stillWaiting)
>        return false;
>      stillWaiting = theCondition.awaitUntil(deadline);
>    }
>    // ...
>  } finally {
>    lock.unlock();
>  }
>}
>```
* `Condition.signal` 唤醒等待的线程.如果当前的`condition`有多个线程在等待的话,这会唤醒其中一,且这个线程在返回`await`前必须重新获得锁
* 'Condition.signalAll' 唤醒所有等待的线程.如果当前的`condition`有多个线程在等待,则所有的线程都会被唤醒,且这些被唤醒的线程必须在返回`await`之前重新获得锁

#例子1
枯燥无聊的理论,看完之后就忘记,还是要写一个例子加深印象吧.这里我们还是用之前`小明`和`小红`谈人生,谈理想的例子继续说明吧

* 首先要中间人`GrilProduct`,花花公子`PlayBoy`和测试类`TestMain`,都是跟之前[(三)java多线程之wait notify notifyAll](../test3/(三)java多线程之wait notify notifyAll,md)一样,这里就不占用篇幅了

* 我们继续改写卧室`Room`类

```java 
public class Room {
    Lock lock = new ReentrantLock();
    Condition boyIsCome = lock.newCondition();
    Condition girlIsCome = lock.newCondition();
    private String gril = null;

    public void makeGridInRoom(String gril){
        lock.lock();
        try {
            while (this.gril != null){
                System.out.println(gril + " 我的心在等待... 永远在等待.. " );
                girlIsCome.await();
            }

            Thread.sleep(10);
            this.gril = gril;
            boyIsCome.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public void playWithGril(String boy){
        lock.lock();
        try {
            while (this.gril == null){
                System.out.println(boy + " 我的心在等待... 永远在等待.. " );
                boyIsCome.await();
            }

            Thread.sleep(10);
            System.out.println(boy + " play with " + this.gril);
            Thread.sleep(500);
            this.gril = null;
            girlIsCome.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
```

运行一下,结果如下
```
小红1号 我的心在等待... 永远在等待.. 
小明0号 play with 小红0号
小明9号 我的心在等待... 永远在等待.. 
小明2号 我的心在等待... 永远在等待.. 
小明5号 我的心在等待... 永远在等待.. 
小明7号 我的心在等待... 永远在等待.. 
小明4号 我的心在等待... 永远在等待.. 
小明6号 我的心在等待... 永远在等待.. 
小明1号 我的心在等待... 永远在等待.. 
小明3号 我的心在等待... 永远在等待.. 
小明8号 我的心在等待... 永远在等待.. 
小红2号 我的心在等待... 永远在等待.. 
小明9号 play with 小红1号
小明2号 我的心在等待... 永远在等待.. 
小明5号 我的心在等待... 永远在等待.. 
小明7号 我的心在等待... 永远在等待.. 
小明4号 我的心在等待... 永远在等待.. 
小明6号 我的心在等待... 永远在等待.. 
小明1号 我的心在等待... 永远在等待.. 
小明3号 我的心在等待... 永远在等待.. 
小明8号 我的心在等待... 永远在等待.. 
小红3号 我的心在等待... 永远在等待.. 
小明2号 play with 小红2号
小明5号 我的心在等待... 永远在等待.. 
小明7号 我的心在等待... 永远在等待.. 
小明4号 我的心在等待... 永远在等待.. 
小明6号 我的心在等待... 永远在等待.. 
小明1号 我的心在等待... 永远在等待.. 
小明3号 我的心在等待... 永远在等待.. 
小明8号 我的心在等待... 永远在等待.. 
小红4号 我的心在等待... 永远在等待.. 
小明5号 play with 小红3号
小明7号 我的心在等待... 永远在等待.. 
小明4号 我的心在等待... 永远在等待.. 
小明6号 我的心在等待... 永远在等待.. 
小明1号 我的心在等待... 永远在等待.. 
小明3号 我的心在等待... 永远在等待.. 
小明8号 我的心在等待... 永远在等待.. 
小红5号 我的心在等待... 永远在等待.. 
小明7号 play with 小红4号
小明4号 我的心在等待... 永远在等待.. 
小明6号 我的心在等待... 永远在等待.. 
小明1号 我的心在等待... 永远在等待.. 
小明3号 我的心在等待... 永远在等待.. 
小明8号 我的心在等待... 永远在等待.. 
小红6号 我的心在等待... 永远在等待.. 
小明4号 play with 小红5号
小明6号 我的心在等待... 永远在等待.. 
小明1号 我的心在等待... 永远在等待.. 
小明3号 我的心在等待... 永远在等待.. 
小明8号 我的心在等待... 永远在等待.. 
小红7号 我的心在等待... 永远在等待.. 
小明6号 play with 小红6号
小明1号 我的心在等待... 永远在等待.. 
小明3号 我的心在等待... 永远在等待.. 
小明8号 我的心在等待... 永远在等待.. 
小红8号 我的心在等待... 永远在等待.. 
小明1号 play with 小红7号
小明3号 我的心在等待... 永远在等待.. 
小明8号 我的心在等待... 永远在等待.. 
小红9号 我的心在等待... 永远在等待.. 
小明3号 play with 小红8号
小明8号 我的心在等待... 永远在等待.. 
小明8号 play with 小红9号
```

跟之前的结果差不多.

#例子2
这时候就有人质疑了,既然结果跟用`Object.wait`和`Object.notify`一样,那为什么要用这个呢?细心的读者可以发现了,每次运行,同一个`小明`可能和不同的`小红`谈人生和理想.这时候`小明`是很开心.但`小红`却不乐意,觉得`小明`太花心,太不专一,什么甜言蜜语都是骗人的.好,那我们现在就让`小明`专一.帮`小红`排除他们的烦恼.

这时候全部的类都要改造了,没办法`小红`太强势了

* 首先我们改造一下中间人`GrilProduct`,让每一个`小红`都自带编号进入卧室等候
```java 
public class GrilProduct implements Runnable{

    private Room room;
    public GrilProduct(Room room) {
        this.room = room;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i ++){
            room.makeGridInRoom("小红" + i + "号", i);
        }
    }
}
```
* 之后我们改造花花公子,他们进入房间时,要拿着`小红`对应的编号进入

```java 
public class PlayBoy implements Runnable{
    private Room room;
    private String boy;
    private int index;
    public PlayBoy(Room room, String boy, int index) {
        this.room = room;
        this.boy = boy;
        this.index = index;
    }

    @Override
    public void run() {
        room.playWithGril(boy, index);
    }
}
```

* 之后就是要改写卧室类`Room`了 ,让`小红`和`小明`的号码对应上,才让他们谈人生和理想,不然就让他们继续等待..

```java 
public class Room {
    Lock lock;
    List<Condition> boyIsCome;
    Condition girlIsCome;
    private String gril = null;
    private int index = -1;
    public Room(){
        lock = new ReentrantLock();
        girlIsCome = lock.newCondition();
        boyIsCome = new ArrayList<>();
        for (int i = 0; i < 10; i ++){
            boyIsCome.add(lock.newCondition());
        }
    }

    public void makeGridInRoom(String gril, int index){
        lock.lock();
        try {
            while (this.gril != null){
                System.out.println(gril + " 我的心在等待... 永远在等待.. " );
                girlIsCome.await();
            }

            Thread.sleep(10);
            this.gril = gril;
            this.index = index;
            boyIsCome.get(index).signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public void playWithGril(String boy, int index){
        lock.lock();
        try {
            while (this.gril == null || this.index != index){
                System.out.println(boy + " 我的心在等待... 永远在等待.. " );
                boyIsCome.get(index).await();
            }
            Thread.sleep(10);
            System.out.println(boy + " play with " + this.gril);
            Thread.sleep(500);
            this.gril = null;
            this.index = -1;
            girlIsCome.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
```

* 最后改写我们的测试类`TestMain`

```java 
public class TestMain {
    public static void main(String[] args) {
        Room room = new Room();
        Thread grilProduct = new Thread(new GrilProduct(room));
        Set<Thread> boyThread = new HashSet<>();
        for (int i = 0; i < 10; i ++){
            boyThread.add(new Thread(new PlayBoy(room, "小明" + i + "号", i)));
        }

        grilProduct.start();
        for (Thread boy : boyThread){
            boy.start();
        }
    }
}
```

运行一下,结果如下:

```
小红1号 我的心在等待... 永远在等待.. 
小明9号 我的心在等待... 永远在等待.. 
小明0号 play with 小红0号
小明2号 我的心在等待... 永远在等待.. 
小明5号 我的心在等待... 永远在等待.. 
小明7号 我的心在等待... 永远在等待.. 
小明4号 我的心在等待... 永远在等待.. 
小明6号 我的心在等待... 永远在等待.. 
小明1号 我的心在等待... 永远在等待.. 
小明3号 我的心在等待... 永远在等待.. 
小明8号 我的心在等待... 永远在等待.. 
小红2号 我的心在等待... 永远在等待.. 
小明1号 play with 小红1号
小红3号 我的心在等待... 永远在等待.. 
小明2号 play with 小红2号
小红4号 我的心在等待... 永远在等待.. 
小明3号 play with 小红3号
小红5号 我的心在等待... 永远在等待.. 
小明4号 play with 小红4号
小红6号 我的心在等待... 永远在等待.. 
小明5号 play with 小红5号
小红7号 我的心在等待... 永远在等待.. 
小明6号 play with 小红6号
小红8号 我的心在等待... 永远在等待.. 
小明7号 play with 小红7号
小红9号 我的心在等待... 永远在等待.. 
小明8号 play with 小红8号
小明9号 play with 小红9号
```

恩~~`小明`和`小红`终于配对了,妈妈再也不用我被`小红`们追着打了,不过,`小明`们,我就对不住你们了.~~~~~~~~


---
# 打赏
>如果觉得我的文章写的还过得去的话,有钱就捧个钱场,没钱给我捧个人场(帮我点赞或推荐一下)
>![微信打赏](http://img.blog.csdn.net/20170508085654037?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast) 
>![支付宝打赏](http://img.blog.csdn.net/20170508085710334?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)