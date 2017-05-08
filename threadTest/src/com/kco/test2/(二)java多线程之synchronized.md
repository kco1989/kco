(二)java多线程之synchronized
========================
>本人邮箱: <kco1989@qq.com>
>欢迎转载,转载请注明网址 <http://blog.csdn.net/tianshi_kco>
>github: <https://github.com/kco1989/kco>
>代码已经全部托管[github](https://github.com/kco1989/kco/blob/master/threadTest)有需要的同学自行下载

[TOC]

# 引言
现在,让我们来考虑一个问题,如果要让多个线程来访问同一份数据,会发生什么现象呢?比如12306的火车售票系统,比如银行的存取款系统等等.都可以会出现多线程访问同一个数据的情况.让我们先模拟写一个售票系统.

# 编码
* 首先创建一个`Ticket`类
	+ 增加两个成员变量`count`-->表示剩余的票,`buyedCount`-->已经卖出的票,并提供`getter`方法
	+ 增加一个`buyTicket`方法,用来模拟售票

```java
public class Ticket {
    private static final int DEFAULT_TICKET_COUNT = 1000;
    private int count = DEFAULT_TICKET_COUNT; //票的总数
    private int buyedCount = 0;

    public boolean buyTicket(int count) throws InterruptedException {
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

* 之后创建一个模拟售票的类`TicketRunnable`,该类的构造器接收一个`Ticket`类

```java
public static class TicketRunnable implements Runnable{
    private Ticket ticket;
    private Random random;
    public TicketRunnable(Ticket ticket) {
        this.ticket = ticket;
        random = new Random();
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i ++){
            try {
                int count =  random.nextInt(10) + 1;
                boolean success = ticket.buyTicket(count);
                System.out.println(String.format("%s打算买%d张票,买票%s了,还剩下%d张票,总共卖掉%d张票, 总票数%d",
                        Thread.currentThread().getName(), count, success ? "成功" : "失败",
                        ticket.getCount(),ticket.getBuyedCount(),ticket.getAllCount()));
                if (!success){
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
```

* 最后创建一个`main`模拟20个售票点同时售票

```java
public static void main(String[] args) throws InterruptedException {
    List<Thread> threads = new ArrayList<>();
    Ticket ticket = new Ticket();
    for (int i = 0; i < 20; i ++){
        threads.add(new Thread(new TicketRunnable(ticket)));
    }

    for (Thread thread : threads){
        thread.start();
    }
}
```

* 截取某一次的部分运行结果:

```
Thread-1打算买2张票,买票成功了,还剩下441张票,总共卖掉558张票, 总票数999
Thread-8打算买1张票,买票成功了,还剩下441张票,总共卖掉552张票, 总票数993
Thread-6打算买1张票,买票成功了,还剩下434张票,总共卖掉559张票, 总票数993
Thread-14打算买7张票,买票成功了,还剩下431张票,总共卖掉566张票, 总票数997
Thread-6打算买3张票,买票成功了,还剩下431张票,总共卖掉569张票, 总票数1000
```
# 问题
发现程序运行确实有问题

java提供了关键字`synchronized`可以保证数据同步,在`Ticket`的`buyTicket`和`getter`方法前加上`synchronized`,之后在运行一下程序,

```
Thread-13打算买4张票,买票成功了,还剩下457张票,总共卖掉543张票, 总票数1000
Thread-0打算买2张票,买票成功了,还剩下479张票,总共卖掉524张票, 总票数1000
Thread-6打算买4张票,买票成功了,还剩下444张票,总共卖掉556张票, 总票数1000
Thread-0打算买9张票,买票成功了,还剩下444张票,总共卖掉556张票, 总票数1000
Thread-6打算买2张票,买票成功了,还剩下442张票,总共卖掉558张票, 总票数1000
```

发现程序没有问题了
>在`getter`方法上加`synchronized`是因为获取的变量也是公共的数据

# 解决办法
`synchronized`的另外一种用法是在方法体内使用.在上述的例子中,在方法前加`synchronized`其实等效于`synchronized(this){方法体}`,因为在上述的例子中公共的数据就是`Ticket ticket = new Ticket();`这个变量,在`Ticket`类中就相当与变量`this`

还有不使用`synchronized(this){方法体}`中的this也可以替换为另外一个公共的变量,如在`Ticket`类中定义个成员变量`Object o = new Object();`,然后使用`synchronized(o){方法体}`也可以保证数据同步.
>打个比喻,比如现在有很多人都想进入某一个房间的卧室(至于想干嘛,大家自己脑补),`synchronized(对象)`中的对象就是一扇门,
而`synchronized`就是给这扇门加锁.那么不管这扇门是房间最外的大门,或者是卧室的门.只要所有人对这同一个门在同一个时间点仅仅有且只有一个能开门或者关门.那么就能保证进入卧室的人只有一个.
>这里举个反例,比如进入卧室有两种渠道,一种是进前门,一种是进后门.(为什么卧室有前后门,肯定是有特殊用户了,哈哈...),那么有些人对前门加锁,另外一些人对后门加锁.这样就不能保证进入卧室的人只有一个了.(悲剧说不定就这样发生了)

>在类的静态方法加`synchronized`,等效于`synchronized(类.class){方法体}`

另外,我们也在在不修改`Ticket`的基础上来保证售票数据的同步,只需要将`TicketRunnable.run`方法改为

```java
public void run() {
    for (int i = 0; i < 5; i ++){
        synchronized (ticket){
            try {
                int count =  random.nextInt(10) + 1;
                boolean success = ticket.buyTicket(count);
                System.out.println(String.format("%s打算买%d张票,买票%s了,还剩下%d张票,总共卖掉%d张票, 总票数%d",
                        Thread.currentThread().getName(), count, success ? "成功" : "失败",
                        ticket.getCount(),ticket.getBuyedCount(),ticket.getAllCount()));
                if (!success){
                    break;
                }
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

这样也能保证售票正常,那在这里能不能把`synchronized (ticket){...}`改为`synchronized (random){...}`呢?不能,因为random不是同一个对象,即各个线程只对自己的门加锁,不能保证是对同一个门加锁.

---
# 打赏
>如果觉得我的文章写的还过得去的话,有钱就捧个钱场,没钱给我捧个人场(帮我点赞或推荐一下)
>![微信打赏](http://img.blog.csdn.net/20170508085654037?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast) 
>![支付宝打赏](http://img.blog.csdn.net/20170508085710334?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)