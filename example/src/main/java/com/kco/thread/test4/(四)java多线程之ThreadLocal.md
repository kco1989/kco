(四)java多线程之同步基础ThreadLocal
=====================================

>本人邮箱: <kco1989@qq.com>
>欢迎转载,转载请注明网址 <http://blog.csdn.net/tianshi_kco>
>github: <https://github.com/kco1989/kco>
>代码已经全部托管[github](https://github.com/kco1989/kco/blob/master/threadTest)有需要的同学自行下载

[TOC]

# 引言
之前我们讲到都是多线程共享数据.那么有没有某一个共享的变量,在这变量里面,每个线程都能拥有自己的属性呢?比如说,去旅店开房休息.那么这个旅店就是一个共享的数据,但是每个人开的房间是不一样的.这个要怎么做呢?这里我先试着写一些

# 例子1
让我们编写一个程序,主线程开启十个子线程,然后每个子线程都做1~100的累加,都是共享同一个`List<Integer>`,每个线程占有固定的位置进行累加计算

```java
public class TestMain {

    public static class CalcRunnable implements Runnable{
        List<Integer> list ;
        int index;
        public CalcRunnable(List<Integer> list, int index) {
            this.list = list;
            this.index = index;
        }

        @Override
        public void run() {
            for (int i = 1; i <= 100; i++){
                list.set(index, list.get(index) + i);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i ++){
            list.add(0);
            threads.add(new Thread(new CalcRunnable(list,i)));
        }
        for (Thread thread : threads){
            thread.start();
        }

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(list);
    }
}
```
输出结果为

```
[5050, 5050, 5050, 5050, 5050, 5050, 5050, 5050, 5050, 5050]
```

>这里每个线程都共享了`list`,但是也没有使用关键字`synchronized`进行同步,为什么结果还是正确的呢?原因很简单,那就是每个线程都使用固定的索引进行计算,互不干扰.所以结果不会受其他线程影响的.

# 例子2
现在把上面的例子中的`List<Integer>`改为`Map<Thread,Integer>`来做累加

```java
public class TestMain1 {

    public static class CalcRunnable implements Runnable{
        Map<Thread,Integer> map;
        public CalcRunnable(Map<Thread,Integer> map) {
            this.map = map;
        }

        @Override
        public void run() {
            Thread self = Thread.currentThread();
            for (int i = 1; i <= 100; i++){
                map.put(self, map.get(self) + i);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        Map<Thread,Integer> map = new HashMap<>();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i ++){
            Thread thread = new Thread(new CalcRunnable(map));
            map.put(thread,0);
            threads.add(thread);
        }
        for (Thread thread : threads){
            thread.start();
        }

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(map);
    }
}

```

运行结果

```
{Thread[Thread-1,5,]=5050, Thread[Thread-3,5,]=5050, Thread[Thread-6,5,]=5050, Thread[Thread-0,5,]=5050, Thread[Thread-8,5,]=5050, Thread[Thread-5,5,]=5050, Thread[Thread-7,5,]=5050, Thread[Thread-2,5,]=5050, Thread[Thread-4,5,]=5050, Thread[Thread-9,5,]=5050}
```

>结果也是完全正确,道理跟上面的例子一样,每个线程虽然共用同一个数据`map`,但实际上每个线程都是用`map`中特定的那个元素

# 例子3
其实用`map`还有一种更简单的方式,那就是今天要讲的`ThreadLocal`,不废话,看例子

```java
public class TestMain3 {

    public static class CalcRunnable implements Runnable{
        ThreadLocal<Integer> threadLocal;
        public CalcRunnable(ThreadLocal<Integer> threadLocal) {
            this.threadLocal = threadLocal;
        }

        @Override
        public void run() {
            threadLocal.set(0);//设置默认值
            for (int i = 1; i <= 100; i++){
                threadLocal.set(threadLocal.get() + i);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName() + " 的计算结果为: " + threadLocal.get());
        }
    }

    public static void main(String[] args) {
        ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i ++){
            Thread thread = new Thread(new CalcRunnable(threadLocal));
            threads.add(thread);
        }
        for (Thread thread : threads){
            thread.start();
        }
    }
}
```

运行结果

```
Thread-0 的计算结果为: 5050
Thread-5 的计算结果为: 5050
Thread-1 的计算结果为: 5050
Thread-4 的计算结果为: 5050
Thread-7 的计算结果为: 5050
Thread-6 的计算结果为: 5050
Thread-3 的计算结果为: 5050
Thread-2 的计算结果为: 5050
Thread-8 的计算结果为: 5050
Thread-9 的计算结果为: 5050
```

>原理,其实就是跟例子2的`Map<Thread,Integer>`,在`ThreadLocal`中实现了一个`ThreadLocalMap`内部类,然后在调用`ThreadLocal.get`和`ThreadLocal.set`的时候,其实要获取当前线程去做相应的操作.


#总结
如果以后大家想让多线程共享一个变量,但又不想互相影响的时候,那么首选`ThreadLocal`.因为对比上面三个例子,发现使用`ThreadLocal`是最简单的,而且不容易出错的.比如在web开发中,可以在多线程中存放`session`,或者数据库连接池的时候,也可以使用`ThreadLocal`在存放数据库连接.


---
# 打赏
>如果觉得我的文章写的还过得去的话,有钱就捧个钱场,没钱给我捧个人场(帮我点赞或推荐一下)
>![微信打赏](http://img.blog.csdn.net/20170508085654037?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast) 
>![支付宝打赏](http://img.blog.csdn.net/20170508085710334?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)