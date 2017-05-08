(五)java多线程之Lock类
========================
>本人邮箱: <kco1989@qq.com>
>欢迎转载,转载请注明网址 <http://blog.csdn.net/tianshi_kco>
>github: <https://github.com/kco1989/kco>
>代码已经全部托管[github](https://github.com/kco1989/kco/blob/master/threadTest)有需要的同学自行下载

# 理论
`java.util.concurrent.locks.Lock`: `Lock`主要提供更多锁的特性让线程能获取同步方法或同步块的执行.它们提供更多的灵活的结果,能拥有不多的属性,并且可以配合`Condition`类提供多样的组合.
一个`Lock`是控制多线程去访问一个共享的资源.一般来说,一个`lock`会提供更高级的方法去访问共享资源:比如在某一个时间点,只能有一个线程获得`lock`,那么这个这个`lock`就能访问所有的共享资源.然而,有一种`lock`允许多个线程同时访问共享资源,这种`lock`就是读写锁`ReadWriteLock`.

一般使用`synchronized`的同步方法或同步块对每个对象提供的绝对的锁去访问它们, 但也是所有的锁的请求或释放都发生阻塞.当多线程访问共享资料,只要有一个锁获取了请求,那么其他锁则必须被释放掉.

在`synchronized`的同步方法或同步块中,若使用监视锁会是编程更加简单.它已经帮助我们把在访问锁的时候屏蔽掉一下程序公共的错误.而且使我们能更加灵活用锁进行多线程操作.

一般`lock`的用法如下:

```java
Lock l = ...;
l.lock();
try{
	
}finally(
	l.unlock();
)
```

这是为了确保我们在使用的时候,如果执行方法出现异常,也能正常的释放锁.

# 编码
讲了那么多还是通过编码来学习感觉更加实际点.我们还是用之前那个售票系统

票类 `Ticket`

```java
public class Ticket {
    private static final int DEFAULT_TICKET_COUNT = 1000;
    private int count = DEFAULT_TICKET_COUNT; //票的总数
    private int buyedCount = 0;
    private Object o = new Object();
    public  boolean buyTicket(int count) throws InterruptedException {

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

    public int getCount() {
        return count;
    }

    public int getBuyedCount() {
        return buyedCount;
    }

    public int getAllCount(){
        return count + buyedCount;
    }
}
```

然后再写一个售票类 `TicketRunnable` 

```java
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
        for (int i = 0; i < 5; i ++) {
            lock.lock();
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
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```

然后写个程序测试一下

```java
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
```

运行一下程序,截取部分输出

```
Thread-7打算买8张票,买票成功了,还剩下654张票,总共卖掉346张票, 总票数1000
Thread-8打算买1张票,买票成功了,还剩下653张票,总共卖掉347张票, 总票数1000
Thread-9打算买5张票,买票成功了,还剩下648张票,总共卖掉352张票, 总票数1000
Thread-10打算买10张票,买票成功了,还剩下638张票,总共卖掉362张票, 总票数1000
Thread-11打算买3张票,买票成功了,还剩下635张票,总共卖掉365张票, 总票数1000
Thread-12打算买2张票,买票成功了,还剩下633张票,总共卖掉367张票, 总票数1000
Thread-13打算买10张票,买票成功了,还剩下623张票,总共卖掉377张票, 总票数1000
Thread-14打算买5张票,买票成功了,还剩下618张票,总共卖掉382张票, 总票数1000
Thread-15打算买10张票,买票成功了,还剩下608张票,总共卖掉392张票, 总票数1000
Thread-16打算买2张票,买票成功了,还剩下606张票,总共卖掉394张票, 总票数1000
Thread-17打算买2张票,买票成功了,还剩下604张票,总共卖掉396张票, 总票数1000
Thread-18打算买1张票,买票成功了,还剩下603张票,总共卖掉397张票, 总票数1000
Thread-19打算买6张票,买票成功了,还剩下597张票,总共卖掉403张票, 总票数1000
Thread-0打算买8张票,买票成功了,还剩下589张票,总共卖掉411张票, 总票数1000
Thread-1打算买2张票,买票成功了,还剩下587张票,总共卖掉413张票, 总票数1000
Thread-2打算买8张票,买票成功了,还剩下579张票,总共卖掉421张票, 总票数1000
Thread-3打算买5张票,买票成功了,还剩下574张票,总共卖掉426张票, 总票数1000
Thread-4打算买6张票,买票成功了,还剩下568张票,总共卖掉432张票, 总票数1000
Thread-5打算买1张票,买票成功了,还剩下567张票,总共卖掉433张票, 总票数1000
Thread-6打算买3张票,买票成功了,还剩下564张票,总共卖掉436张票, 总票数1000
Thread-7打算买1张票,买票成功了,还剩下563张票,总共卖掉437张票, 总票数1000
Thread-8打算买5张票,买票成功了,还剩下558张票,总共卖掉442张票, 总票数1000
Thread-9打算买8张票,买票成功了,还剩下550张票,总共卖掉450张票, 总票数1000
Thread-10打算买4张票,买票成功了,还剩下546张票,总共卖掉454张票, 总票数1000
Thread-11打算买5张票,买票成功了,还剩下541张票,总共卖掉459张票, 总票数1000
Thread-12打算买6张票,买票成功了,还剩下535张票,总共卖掉465张票, 总票数1000
Thread-13打算买1张票,买票成功了,还剩下534张票,总共卖掉466张票, 总票数1000
Thread-14打算买8张票,买票成功了,还剩下526张票,总共卖掉474张票, 总票数1000
Thread-15打算买2张票,买票成功了,还剩下524张票,总共卖掉476张票, 总票数1000
Thread-16打算买10张票,买票成功了,还剩下514张票,总共卖掉486张票, 总票数1000
```

发现运行结果都是正确的

现在尝试使用`lock.tryLock()`方法.不管能不能获得锁,每个线程都必须卖出5次票,修改`TicketRunnable`

```java
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
```

截取部分运行结果

```
Thread-11打算买7张票,买票成功了,还剩下613张票,总共卖掉387张票, 总票数1000
Thread-11打算买2张票,买票成功了,还剩下611张票,总共卖掉389张票, 总票数1000
Thread-11打算买7张票,买票成功了,还剩下604张票,总共卖掉396张票, 总票数1000
Thread-11打算买3张票,买票成功了,还剩下601张票,总共卖掉399张票, 总票数1000
Thread-11打算买4张票,买票成功了,还剩下597张票,总共卖掉403张票, 总票数1000
Thread-12 买票系统被占用,尝试获取锁失败.
Thread-10 买票系统被占用,尝试获取锁失败.
Thread-16 买票系统被占用,尝试获取锁失败.
Thread-19打算买2张票,买票成功了,还剩下595张票,总共卖掉405张票, 总票数1000
Thread-19打算买8张票,买票成功了,还剩下587张票,总共卖掉413张票, 总票数1000
Thread-19打算买1张票,买票成功了,还剩下586张票,总共卖掉414张票, 总票数1000
Thread-19打算买5张票,买票成功了,还剩下581张票,总共卖掉419张票, 总票数1000
Thread-19打算买1张票,买票成功了,还剩下580张票,总共卖掉420张票, 总票数1000
```

发现程序也是正常的.

# 原理

* `Lock.lock()` 当前线程尝试获取一个锁,如果这个锁获取不到,则当前线程会一直休眠直到获取这个锁.
* `Lock.lockInterruptibly()` 让当前线程获取一个锁,如果锁可以用,则直接返回.否则当前线程会一直休眠直到一下两种情况中的其中一个发生:
	+ 当前线程获取到这个锁
	+ 其他线程打断当前线程, 打断当前线程获取锁的操作是允许的.
* `Lock.tryLock()` 尝试获得一个锁,如果锁是可用的,则直接返回ture,并获取到这个锁.否则,直接返回false
* `Lock.tryLock(long time, TimeUnit unit)` 在某一段时间内尝试获取一个锁,如果锁可用,则直接返回true,否则等待超时返回fasle
* `Lock.unlock()` 释放锁
* `Lock.newCondition()` 这个后面的章节再谈论

---
# 打赏
>如果觉得我的文章写的还过得去的话,有钱就捧个钱场,没钱给我捧个人场(帮我点赞或推荐一下)
>![微信打赏](http://img.blog.csdn.net/20170508085654037?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast) 
>![支付宝打赏](http://img.blog.csdn.net/20170508085710334?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)