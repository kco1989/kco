

>本人邮箱: <kco1989@qq.com>
>欢迎转载,转载请注明网址 <http://blog.csdn.net/tianshi_kco>
>github: <https://github.com/kco1989/kco>
>代码已经全部托管[github](https://github.com/kco1989/kco/blob/master/threadTest)有需要的同学自行下载



#引言
java 自动的两个线程池讲完,今天就讲跟他们有关的一个工具类吧--`Executors`

#理论
`Executors` 仅仅是一个线程池的工具类,它无法实例话,包含都是静态方法或静态类.

* `newFixedThreadPool(int nThreads)` 创建一个指定线程数量的线程池t nThreads, ThreadFactory threadFactory)` 创建一个可以自定义的线程工厂的指定线程数量的线程池
* `newSingleThreadExecutor()` 创建一个只有一个线程的线程池
* `newSingleThreadExecutor(ThreadFactory threadFactory)` 创建一个可以自定义线程工程的且只有一个线程的线程池
* `newCachedThreadPool` 创建一个可以缓存的线程池,它的源码其实是`return new ThreadPoolExecutor(0, Integer.MAX_VALUE,60L, TimeUnit.SECONDS,new SynchronousQueue<Runnable>());` 通过这里我们可以看出,他的核心线程数0,最大线程数为Integer.MAX_VALUE,但线程空闲时,线程可以缓存1分钟,之后如果还没有任务则被回收.工作队列是一个容量0大小的`SynchronousQueue`队列
* `newCachedThreadPool(ThreadFactory threadFactory)` 跟上面一样,只是多了可以自定义线程工厂
* `newSingleThreadScheduledExecutor()` 创建一个只有一个线程的任务调度线程池.
* `newSingleThreadScheduledExecutor(ThreadFactory threadFactory)` 创建一个只有一个线程且能自定义线程工程的任务调度线程池
* `newScheduledThreadPool(int corePoolSize)` 创建指定线程数量的任务调度线程池
* `newScheduledThreadPool(int corePoolSize, ThreadFactory threadFactory)` 创建一个可以指定数量写自定义线程工程的任务调度线程池

除此之外,`Executors`还提供了让`Runnable`转化为`Callable`的适配器

#例子
这个章节就不写例子,前面学了线程池的知识之后,直接看`Executors`源码,就感觉比较清晰

---
# 打赏
>如果觉得我的文章写的还过得去的话,有钱就捧个钱场,没钱给我捧个人场(帮我点赞或推荐一下)
>![微信打赏](http://img.blog.csdn.net/20170508085654037?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast) 
>![支付宝打赏](http://img.blog.csdn.net/20170508085710334?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)