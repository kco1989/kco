

>本人邮箱: <kco1989@qq.com>
>欢迎转载,转载请注明网址 <http://blog.csdn.net/tianshi_kco>
>github: <https://github.com/kco1989/kco>
>代码已经全部托管[github](https://github.com/kco1989/kco/blob/master/threadTest)有需要的同学自行下载



#引言
前面我们讲了那么多有关线程的知识.不知道读者有没有想过这么一个问题,如果有这么一个比较耗时的任务,必须使用线程来执行,但是在这个任务执行完之后,我需要得到这个线程的返回值.以目前我们学到的知识,具体实现,我这里不说,大家自行发挥.除此之外,如果线程发生了非运行时异常,我们在主线程就会收到一堆错误信息.还有我们也无法判断任务是否执行完成,有些人会说用`Thread1.isAlive()`就可以判断任务是否执行完成.这是错的,因为`isAlive`只是判断线程是否存活,而无法判断任务是否完成,两者是不一样的概念.如果有没有不明白的,请考虑使用线程池`ThreadPoolExecutor`的情况.
以上说了那么多,其实就是为了引入今天要讲的`Callable`,`Future`,`FutureTask`.ok,让我们看一下这些类是干什么的.

#理论
*`Callable` 可以说是 `Runnable`的升级版本,既有抛出错误也有返回类型.
*`Future` 是执行异步任务后的返回值,这个接口包括一下几个方法
    * `cancel(boolean mayInterruptIfRunning)` 取消任务,如果`mayInterruptIfRunning`为`true`,即使该任务在运行也可以被中断.否则在运行中的任务不能被取消.
    * `isCancelled`判断任务是否被取消
    * `isDone` 判断任务是否完成
    * `get` 获取任务的返回值,如果任务有异常,则在调用这个方法的时候被抛出
    * `get(long timeout, TimeUnit unit)` 在指定时间内等待获取任务的返回值,如果任务有异常,则在调用这个方法的时候被抛出
`FutureTask` 是`Runnable`和`Future`的子类

#例子1 获取异步任务的返回值 `Runnable` 版本

```java
public class Demo1 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main start");
        FutureTask task = new FutureTask(() -> System.out.println("执行子任务"), "hello");
        new Thread(task).start();
        while (task.isDone());
        System.out.println("任务返回结果:" + task.get());
        System.out.println("main end");
    }
}
```
运行结果

>main start
>执行子任务
>任务返回结果:hello
>main end

`public FutureTask(Runnable runnable, V result) `这个比较适合固定任务返回固定值的情况,如果返回的值需要进过计算,所以有多个情况,则不适合使用这个构造.

# 例子1 获取异步任务的返回值 `Callable` 版本

```java
public class Demo2 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main start");
        FutureTask task = new FutureTask(() -> {
            int sum = 0;
            for (int i = 1; i <= 100; i++){
                sum += i;
            }
            return sum;
        });
        new Thread(task).start();
        while (task.isDone());
        System.out.println("任务返回结果:" + task.get());
        System.out.println("main end");
    }
}
```

返回结果

>main start
>任务返回结果:5050
>main end

# 例子3 测试运行时异常情况

```java
public class Demo3 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main start");
        FutureTask task = new FutureTask(() -> {
            System.out.println("正在执行子任务");
            int i = 1 / 0;
            return 0;
        });
        new Thread(task).start();
        while (task.isDone());
        System.out.println("任务返回结果:" + task.get());
        System.out.println("main end");
    }
}
```

运行结果:

>main start
>正在执行子任务
>Exception in thread "main" java.util.concurrent.ExecutionException: java.lang.ArithmeticException: / by zero
>   at java.util.concurrent.FutureTask.report(FutureTask.java:122)
>   at java.util.concurrent.FutureTask.get(FutureTask.java:192)
>   at com.kco.test18.demo.Demo3.main(Demo3.java:19)
>   at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
>   at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
>   at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
>   at java.lang.reflect.Method.invoke(Method.java:497)
>   at com.intellij.rt.execution.application.AppMain.main(AppMain.java:140)
>Caused by: java.lang.ArithmeticException: / by zero
>   at com.kco.test18.demo.Demo3.lambda$main$0(Demo3.java:14)
>   at com.kco.test18.demo.Demo3$$Lambda$1/27095111.call(Unknown Source)
>   at java.util.concurrent.FutureTask.run(FutureTask.java:266)
>   at java.lang.Thread.run(Thread.java:745)

需要注意的是,这是抛出的异常是在调用`task.get()`才抛出的,如果把`task.get()`注释掉,是不会抛出异常的.所以我们就可以对异常做一下自定义处理.比如写到日志中.

# 例子4 取消正在执行的任务

```java
public class Demo4 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main start");
        FutureTask task = new FutureTask(() -> {
            System.out.println("start 正在执行子任务");
            Thread.sleep(500);
            System.out.println("end 执行子任务");
            return 0;
        });
        new Thread(task).start();
        Thread.sleep(250);
        task.cancel(true);
        System.out.println("main end");
    }
}
```

运行结果

>main start
>start 正在执行子任务
>main end

发现子任务确实运行运行了一般,然后被取消了.

#补充
上一篇讲到了`ThreadPoolExecutor`,我们在执行任务的时候只使用了`execute`,这个是没有返回值的.而且如果任务抛出异常,则会直接在主线程打印出错误堆栈的.其实`ThreadPoolExecutor`还有另外一个提交任务的方法,就是`submit(Runnable task, T result)`和`submit(Callable<T> task)`,使用的就是我们这章节所讲的内容.大家可以自行写例子测试这两个方法.

---
# 打赏
>如果觉得我的文章写的还过得去的话,有钱就捧个钱场,没钱给我捧个人场(帮我点赞或推荐一下)
>![微信打赏](http://img.blog.csdn.net/20170508085654037?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast) 
>![支付宝打赏](http://img.blog.csdn.net/20170508085710334?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)