(十三)java多线程之Timer
=====================================

>本人邮箱: <kco1989@qq.com>
>欢迎转载,转载请注明网址 <http://blog.csdn.net/tianshi_kco>
>github: <https://github.com/kco1989/kco>
>代码已经全部托管[github](https://github.com/kco1989/kco/blob/master/threadTest)有需要的同学自行下载

[TOC]
#引言
同步工具都讲的差不多了,今天我们换一下口味.讲一下定时任务`Timer`吧.

#理论
* `schedule(TimerTask task, long delay)` 延时`delay`ms后执行定时任务`task`
* `schedule(TimerTask task, Date time)` 到达这个`time`时间点执行定时任务`task`
* `schedule(TimerTask task, long delay, long period)` 延时`delay`ms后执行定时任务`task`,之后以`period`ms为周期重复执行`task`
* `schedule(TimerTask task, Date firstTime, long period)` 到达这个`time`时间点执行定时任务`task`,之后以`period`ms为周期重复执行`task`
* `scheduleAtFixedRate(TimerTask task, long delay, long period)` 延时`delay`ms后执行定时任务`task`,之后以`period`ms为周期重复执行`task`
* `scheduleAtFixedRate(TimerTask task, Date firstTime, long period)` 到达这个`time`时间点执行定时任务`task`,之后以`period`ms为周期重复执行`task`

> 细心的人会发现带参数`period`的`schedule`和`scheduleAtFixedRate`的解释是一样,但是他们有什么区别
> 如果周期是30s,任务执行时间是8s,那么两者的执行效果是一样的
> 但是如果任务执行时间大于周期时间呢?
> `scheduleAtFixedRate`会按照周期时间来,即不管任务执行多久,他都是周期一到就重新执行`task`,
> 而`schedule`的下一次开始执行时间是取决与上一次结束时间,如果任务执行时间大于周期时间呢,那么它会按照执行时间为周期执行任务`task`

# 例子1 延时炸弹-倒计时炸弹

```java
public class Demo1 {
    public static void main(String[] args) {
        Timer timer = new Timer();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("炸弹爆炸时间:" + dateFormat.format(new Date()));
            }
        }, 4000);
        System.out.println("炸弹安装时间:" + dateFormat.format(new Date()));
    }
}
```
运行结果:

```
炸弹安装时间:2016-10-31 20:00:25
炸弹爆炸时间:2016-10-31 20:00:29
```
# 例子2 延时炸弹-时间点炸弹(到某个时间点就爆炸的炸弹)

```java
public class Demo2 {
    public static void main(String[] args) throws ParseException {
        Timer timer = new Timer();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("炸弹爆炸时间:" + dateFormat.format(new Date()));
            }
        }, dateFormat.parse("2016-10-31 20:04:00"));
        System.out.println("炸弹安装时间:" + dateFormat.format(new Date()));
    }
}
```

运行结果

```
炸弹安装时间:2016-10-31 20:03:11
炸弹爆炸时间:2016-10-31 20:04:00
```

#例子3 延时连环炸弹 

```java
public class Demo3 {
    public static void main(String[] args) {
        Timer timer = new Timer();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("炸弹爆炸时间:" + dateFormat.format(new Date()));
            }
        }, 2000,3000);
        System.out.println("炸弹安装时间:" + dateFormat.format(new Date()));
    }
}
```

运行结果
```
炸弹安装时间:2016-10-31 20:05:46
炸弹爆炸时间:2016-10-31 20:05:48
炸弹爆炸时间:2016-10-31 20:05:51
炸弹爆炸时间:2016-10-31 20:05:54
炸弹爆炸时间:2016-10-31 20:05:57
炸弹爆炸时间:2016-10-31 20:06:00
炸弹爆炸时间:2016-10-31 20:06:03
炸弹爆炸时间:2016-10-31 20:06:06
......
```

#例子4 时间点连环炸弹 

```java
public class Demo4 {
    public static void main(String[] args) throws ParseException {
        Timer timer = new Timer();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("炸弹爆炸时间:" + dateFormat.format(new Date()));
            }
        }, dateFormat.parse("2016-10-31 20:08:30"), 2000);
        System.out.println("炸弹安装时间:" + dateFormat.format(new Date()));
    }
}
```

运行结果

```
炸弹安装时间:2016-10-31 20:08:19
炸弹爆炸时间:2016-10-31 20:08:30
炸弹爆炸时间:2016-10-31 20:08:32
炸弹爆炸时间:2016-10-31 20:08:34
炸弹爆炸时间:2016-10-31 20:08:36
炸弹爆炸时间:2016-10-31 20:08:38
炸弹爆炸时间:2016-10-31 20:08:40
炸弹爆炸时间:2016-10-31 20:08:42
炸弹爆炸时间:2016-10-31 20:08:44
炸弹爆炸时间:2016-10-31 20:08:46
炸弹爆炸时间:2016-10-31 20:08:48
......
```
#例子5 带参数`period`的`schedule`和`scheduleAtFixedRate`的区别

```java
public class Demo5 {
    public static void main(String[] args) throws ParseException {
        Timer timer = new Timer();
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                    System.out.println("sub1执行时间:" + dataFormat.format(this.scheduledExecutionTime()) + " --> 当前时间:" + dataFormat.format(new Date()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 2000);
    }
}
```
运行结果:

```
sub1执行时间:2016-10-31 08:12:05 --> 当前时间:2016-10-31 08:12:09
sub1执行时间:2016-10-31 08:12:09 --> 当前时间:2016-10-31 08:12:13
sub1执行时间:2016-10-31 08:12:13 --> 当前时间:2016-10-31 08:12:17
sub1执行时间:2016-10-31 08:12:17 --> 当前时间:2016-10-31 08:12:21
sub1执行时间:2016-10-31 08:12:21 --> 当前时间:2016-10-31 08:12:25
sub1执行时间:2016-10-31 08:12:25 --> 当前时间:2016-10-31 08:12:29
sub1执行时间:2016-10-31 08:12:29 --> 当前时间:2016-10-31 08:12:33
sub1执行时间:2016-10-31 08:12:33 --> 当前时间:2016-10-31 08:12:37
sub1执行时间:2016-10-31 08:12:37 --> 当前时间:2016-10-31 08:12:41
.....
```

现在将`schedule`改为`scheduleAtFixedRate`

```java
public class Demo6 {
    public static void main(String[] args) throws ParseException {
        Timer timer = new Timer();
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                    System.out.println("sub1执行时间:" + dataFormat.format(this.scheduledExecutionTime()) + " --> 当前时间:" + dataFormat.format(new Date()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 2000);
    }
}
```

运行结果:

```
sub1执行时间:2016-10-31 08:13:51 --> 当前时间:2016-10-31 08:13:55
sub1执行时间:2016-10-31 08:13:53 --> 当前时间:2016-10-31 08:13:59
sub1执行时间:2016-10-31 08:13:55 --> 当前时间:2016-10-31 08:14:03
sub1执行时间:2016-10-31 08:13:57 --> 当前时间:2016-10-31 08:14:07
sub1执行时间:2016-10-31 08:13:59 --> 当前时间:2016-10-31 08:14:11
sub1执行时间:2016-10-31 08:14:01 --> 当前时间:2016-10-31 08:14:15
sub1执行时间:2016-10-31 08:14:03 --> 当前时间:2016-10-31 08:14:19
.....
```

> 两个结果一对比,区别就很明显了

---
# 打赏
>如果觉得我的文章写的还过得去的话,有钱就捧个钱场,没钱给我捧个人场(帮我点赞或推荐一下)
>![微信打赏](http://img.blog.csdn.net/20170508085654037?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast) 
>![支付宝打赏](http://img.blog.csdn.net/20170508085710334?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)