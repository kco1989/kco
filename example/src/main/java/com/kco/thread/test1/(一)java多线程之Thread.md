(一)java多线程之Thread
======================

>本人邮箱: <kco1989@qq.com>
>欢迎转载,转载请注明网址 <http://blog.csdn.net/tianshi_kco>
>github: <https://github.com/kco1989/kco>
>代码已经全部托管[github](https://github.com/kco1989/kco/blob/master/threadTest)有需要的同学自行下载


[TOC]

# Thread类
学习java线程的开发者,首先遇到的第一个类就是Thread,通过使用Thread类,我们就可以启动,停止,中断一个线程. 在同一个时间片里, 可能会有多个线程在执行, 每个线程都拥有它自己的方法调用堆栈, 参数和变量.每个app至少会有一个线程--主线程(main thread).

# 创建一个线程

## java创建线程有两种方式
1. 创建一个继承`Thread`的子类,并实现`run`方法
2. 使用`Thread`的构造方法`public Thread(Runnable target)`创建,这个需要传入一个实现Runnable接口的子类

## 实现

下面我们分别以这两种方式实现一下. 

+ 编写SubThread继承Thread,并覆盖run方法 __SubThread.java__

```java
public class SubThread extends Thread{
    @Override
    public void run() {
        for (int i = 0; i < 10; i ++){
            System.out.println(Thread.currentThread().getName() + ":" + i);
        }
    }

    public static void main(String[] args) {
        System.out.println("begin main");
        SubThread sub = new SubThread();
        sub.start();
        System.out.println("end main");
    }
}
```

+ 编写SubRunnable实现Runnable,然后使用构造器Thread(Runnable) 创建一个线程

```java
public class SubRunnable implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i < 10; i ++){
            System.out.println(Thread.currentThread().getName() + ":" + i);
        }
    }
    public static void main(String[] args) {
        System.out.println("begin main");
        Thread thread = new Thread(new SubRunnable());
        thread.start();
        System.out.println("end main");
    }
}
``` 

## 区别
* 使用第一种方法创建的话,你可以在run方法中,可以用`this`直接调用线程的方法,比如获取线程的id-->`this.getId()`
* 而使用第二方法创建线程,在`run`中,`this`对象压根就没有`getId()`这个方法,这个时候你只能用`Thread.cuurentThread()`这个静态方法获取该线程.
* 在这里一般推荐使用第二种方法创建,因为这样比较符合面对象的思路,`Thread`就只负责线程的启动,停止,中断等操作,而`Runnable`就只负责线程要运行某一个具体任务.

>不管使用那种方式创建线程,都可以调用`Thread.cuurentThread()`获取当前的线程
>还有,`Thread`其实也是`Runnable`的一个子类
>除了上面两种创建方法,其中还有另外一种方法创建线程,那就是实现`ThreadFactory`接口,这种比较适合批量生成某一种规格的线程

# 让线程"睡"一会
调用线程的`Thread.sleep()`方法会让线程睡眠一段时间,这个时候线程会挂起,然后将CPU的时间片转移给其他线程,让其他线程获得执行的机会.

>`Thread.sleep()`接收一个毫秒值做完参数,并抛出一个`InterruptedException`异常.

# 停止线程
不管是使用哪一种方法创建线程,`run`方法的任务执行完了,线程就自动停止.
如果想在中途就停止线程,有下面几种方式
* 调用线程的`interrupt()`方法,这时线程的中断位会被标识,并抛出`InterruptedException`,例如:

```java
public class StopThread1 {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("begin main");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < 10; i ++){
                    try {
                        Thread.sleep(100);
                        System.out.println(Thread.currentThread().getName() + ":" + i);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });
        thread.start();
        System.out.println("main sleep 500ms");
        Thread.sleep(500);
        thread.interrupt();
        System.out.println("end main");
    }
}
```

> 在调用`thread.interrupt();`这个语句时,会对该线程的中断状态标识为true,然后在抛出`InterruptedException`异常时,会清空该中断位.
>修改程序,在抛出`InterruptedException`中添加`System.out.println("InterruptedException:" + Thread.currentThread().isInterrupted());`,然后再`thread.interrupt();`后面添加`System.out.println("thread.isInterrupted:" + thread.isInterrupted());`.然后运行程序.
>> 这时候运行结果有可能打印出`thread.isInterrupted:true;InterruptedException:false`或者打印出`thread.isInterrupted:false;InterruptedException:false`,运行多次结果都有可能不一致,这个是因为主线程和子线程都通知在执行,还没有来的及执行主线程的打印语句,子线程异常中的打印语句就已经执行了.

* 可以在线程中加一个`boolean`成员变量,提供`setter`方法,然后在`run`方法中判断该变量是否为`true`,若为`true`则停止线程,否则继续

```java
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
```

# 线程的属性
* id: 通过`Thread.getId()`可以获取线程的id,线程的id是一个自增长的long, 不能修改
* name: 通过`Thread.getName()`, 用一个字符串来标识线程的名字,可以通过`Thread.setName()`或部分构造器修改线程的名字
* priority: 线程的优先级,线程创建默认优先级为5, 最小为优先级为1, 最大为10.优先级大的线程有机会先执行.但具体那个线程先执行还是要看CPU的心情了.
* state: 线程的状态, 线程的状态有以下几种
    + `Thread.State.NEW`: _新建状态_:这个是线程已经被创建但还没有调用'start()'方法时的状态
    + `Thread.State.RUNNABLE`: _运行状态_ 当前线程已经在JVM中执行
    + `Thread.State.BLOCKED`: _阻塞状态_ 表示当前线程在等待进入一个同步块或同步方法,也可以等到一个同步快被提交. 常见的有IO阻塞等.
    + `Thread.State.WAITING`: _等待状态_ 但线程调用`Object.wait()`,`Thread.join()`,`LockSupport.park()`就会进入等待状态.当前线程在等待其他线程执行某一个特定操作.比如:当前线程执行`Object.wait()`,那么就需要其他线程执行`Object.notify()`或`Object.notifyAll()`,如果线程执行了`Thread.join()`,则需要等到指定的线程执行结束.
    + `Thread.State.TIMED_WAITING`: _有时间的等待_ 线程在等待某一个等待的时间.比如,线程执行了`Thread.sleep`,`Object.wait(long)`,`Thread.join(long)`等
    + `Thread.State.TERMINATED`: _终结_ 线程已经执行完毕.
* daemon: 这个用来标识线程为守护线程或非守护线程的,默认创建的线程都是非守护线程.应用程序所有的非守护线程执行完毕之后,则程序就停止运行.比如主线程都是非守护线程,所以主线程会等到主线程的所有语句执行完成,程序才会停止运行.JVM的资源回收则是一个守护线程.

```java
public class TestDaemonThread {
    public static void main(String[] args) {
        System.out.println("start main");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i ++){
                    try {
                        System.out.println(Thread.currentThread().getName() + ":" + i);
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        System.out.println("end main");
    }
}
```

>该例子中,程序必须等到主线程和子线程同时执行完成才会停止,因为默认创建的线程都是非守护线程,如果在`thread.start();`前加入`thread.setDaemon(true);`, 那么程序不会等子线程执行完才结束程序的.

# Thread.join()
等到某线程执行完毕才开始执行,如果调用`Thread.join(long)`则表示等到某线程执行完毕或指定的超时时间结束后才开始执行

```java
public class ThreadJoinTest {
    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            for (int i = 0;i < 10; i ++){
                try {
                    Thread.sleep(10);
                    System.out.println(Thread.currentThread().getName() + ":" + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "thread1");
        Thread thread2 = new Thread(() -> {
            try {
                thread1.join();
                for (int i = 0;i < 10; i ++){
                    Thread.sleep(10);
                    System.out.println(Thread.currentThread().getName() + ":" + i);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "thread2");
        thread1.start();
        thread2.start();

    }
}
```

>上面的例子,`thread2`线程会等`thread1`执行完之后才开始执行


# Thread.yield
这个方法标识当前线程会按时线程调度者让其他线程先执行.但CPU是否让其他线程优先执行,还是要看CPU的心情了.

# 线程的异常 
如果线程发现一些运行时异常而没有在`run`方法俘获,会怎么办?
>程序会打印出一推错误堆栈,如果我们先把线程的错误按照某种可读的方式打印到问题,但又不想在每个`run`方法中增加`try{...}catch(Exception e){...}`怎么办?
我们查看`Thread`类的源码发现,在`Thread`中有一个内部接口`UncaughtExceptionHandler`,这个正是我们所需要的.实现这个接口,并调用`Thread.setUncaughtExceptionHandler`,那么但线程出现时,则会回调`uncaughtException`方法

```java
public class ThreadExceptionTest {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("begin main");
        Thread thread = new Thread(() -> {
            int i = 1 / 0;
        },"myThread");
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println(String.format("%s发生异常%s", t.getName(), e.getMessage()));
            }
        });
        thread.start();
        System.out.println("end main");
    }
}
```

---
# 打赏
>如果觉得我的文章写的还过得去的话,有钱就捧个钱场,没钱给我捧个人场(帮我点赞或推荐一下)
>![微信打赏](http://img.blog.csdn.net/20170508085654037?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast) 
>![支付宝打赏](http://img.blog.csdn.net/20170508085710334?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)