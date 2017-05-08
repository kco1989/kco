

>本人邮箱: <kco1989@qq.com>
>欢迎转载,转载请注明网址 <http://blog.csdn.net/tianshi_kco>
>github: <https://github.com/kco1989/kco>
>代码已经全部托管[github](https://github.com/kco1989/kco/blob/master/threadTest)有需要的同学自行下载



#引言
java 提供的线程池还有一个,那就是任务调度线程池`ScheduledThreadPoolExecutor`,它其实是`ThreadPoolExecutor`的一个子类.

#理论
我们通过查看`ScheduledThreadPoolExecutor`的源代码,可以发现`ScheduledThreadPoolExecutor`的构造器都是调用父类的构造器,只是它使用的工作队列是`java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue`通过名字我们都可以猜到这个是一个延时工作队列.
因为`ScheduledThreadPoolExecutor`的最大线程是Integer.MAX_VALUE,而且根据源码可以看到`execute`和`submit`其实都是调用`schedule`这个方法,而且延时时间都是指定为0,所以调用`execute`和`submit`的任务都直接被执行.

#例子 搞几个延时炸弹
我们搞几个延时炸弹,让它们每个5s炸一次

```java
public class TestMain {
    public static void main(String[] args) throws InterruptedException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(5);
        for (int i = 0; i < 5; i ++){
            final int temp = i + 1;
            pool.schedule(() -> {
                System.out.println("第"+temp+"个炸弹爆炸时间:" + simpleDateFormat.format(new Date()));
            }, temp * 5, TimeUnit.SECONDS);
        }
        pool.shutdown();
        System.out.println("end main时间:" + simpleDateFormat.format(new Date()));
    }
}
```
运行结果:
>end main时间:2016-11-03 19:58:31
>第1个炸弹爆炸时间:2016-11-03 19:58:36
>第2个炸弹爆炸时间:2016-11-03 19:58:41
>第3个炸弹爆炸时间:2016-11-03 19:58:46
>第4个炸弹爆炸时间:2016-11-03 19:58:51
>第5个炸弹爆炸时间:2016-11-03 19:58:56

ok,这个类相对比较简单,我就不多讲了

#后记
在正在项目中,一般如果需要使用定时任务,不会直接使用这个类的.有一个`quartz`已经把定时任务封装的很好了.它是通过`cron`表示时,可以指定某一个任务每天执行,或者每周三下午5点执行.更多的资料可以去查百度.或者等以后有机会我再整理一写常用jar用法系列文章.就这样了.

---
# 打赏
>如果觉得我的文章写的还过得去的话,有钱就捧个钱场,没钱给我捧个人场(帮我点赞或推荐一下)
>![微信打赏](http://img.blog.csdn.net/20170508085654037?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast) 
>![支付宝打赏](http://img.blog.csdn.net/20170508085710334?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)