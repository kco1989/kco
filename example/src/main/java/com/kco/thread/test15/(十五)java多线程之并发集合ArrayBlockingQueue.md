>本人邮箱: <kco1989@qq.com>
>欢迎转载,转载请注明网址 <http://blog.csdn.net/tianshi_kco>
>github: <https://github.com/kco1989/kco>
>代码已经全部托管[github](https://github.com/kco1989/kco/blob/master/threadTest)有需要的同学自行下载



#引言
做java的同学们或多或少的接触过**Java集合框架**.在**java集合框架**中,大多的集合类是线程不安全的.比如我们常用的`ArrayList`等等.我们写一个例子看,为什么说`ArrayList`是不安全的.

# 例子1 证明ArrayList是线程不安全的
我们开启100个线程.每个线程向`List`加`100`个数据,那么当所有线程执行完成之后应该是`10000`条,然后就对比一下结果,看看是否为`10000`条.

```java
public class Demo1 {
    public static void main(String[] args) throws InterruptedException {
        List<String> list = new ArrayList<>();
        Thread[] threads = new Thread[100];
        for (int i = 0; i < threads.length; i ++){
            threads[i] = new Thread(() -> {
                for (int j = 0;j < 100; j ++){
                    list.add(Thread.currentThread().getName() + ":" + j);
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads){
            thread.join();
        }

        System.out.println(list.size());
    }
}
```

> `thread.join();` 是让主线程等待所有的子线程执行完,才执行接下来的语句

运行结果为`9979`,而且每次运行结果还不一定是这个数.

当然,我们可以通过学过的知识,在执行`list.add`是给它加锁,比如将`list.add(Thread.currentThread().getName() + ":" + j);`改为`synchronized (list){list.add(Thread.currentThread().getName() + ":" + j);}`这样就能保证线程同步了.

#可直接用于并发的集合类
其实java中已经提供了可直接用于并发的集合类,它们可以在多线程中进行`CURD`[^CURD]操作,而且不需要程序员手动加`lock`或`synchronized`来保证同步.一般来说,它们分以下两种:

* **阻塞式集合(Blocking Collection)**: 这类集合一般在添加或删除数据,如果集合已满或为空时,则调用添加和删除方法的线程会被阻塞,直接该方法可以成功执行
* **非阻塞式集合(Non-Blocking Collection)**:这类集合一般在添加或删除数据,如果方法不能立即执行时,则会返回`Null`或抛出异常,但调用该方法的线程不会被阻塞.

这节课我将重点讲`ArrayBlockingQueue`,首先先看一下`ArrayBlockingQueue`的api,以及区分这些的差别

* `add(E)`,`offer(E)`,`pub(E)`都是这队列尾部加入元素E,如果队列不满,则加入成功,并立即返回.如果队列满了,那么
    + `add`会抛出`IllegalStateException`异常
    + `offer`立刻返回`false`
    + `put`会让调用的线程一直等待,直到方法执行成功

* `offer(E e, long timeout, TimeUnit unit)`,`offer`另一种方法,当集合已满,如果等待时间小于等于0,那么会离开返回false,否则等到指定的时间

* `poll()`,`take()`,获取队列的数据,如果队列为空,那么
    + `poll` 立刻返回null
    + `take` 线程等待,直到获取到数据,或被中断
* `poll(long timeout, TimeUnit unit)`,如队列为空,当指定时间小于等于,立刻返回null,否则等待指定的时间

* `peek()`: 看一下队列当前的数据,如果队列为空,则立即返回null

#例子
之前我们写过**山治和路飞**的故事,在[(十二)java多线程之Exchanger](http://blog.csdn.net/tianshi_kco/article/details/52987571)的例子中,其实**山治和路飞**是一个简单的**生产者-消费者**模式,只是山治和路飞都要等对方吃完或做完一个才能继续下一个.现在路飞想出另一个办法,在厨房和餐桌之间弄一个传送带,山治把食物做好之后,直接放传送带上,路飞就直接从传送带拿食物.传送带最多只能放10个食物.ok,开始编码..

* `Food` 没有改.
* `LuFeiRunnable`改为

```java
public class LuFeiRunnable implements Runnable{
    ArrayBlockingQueue<String> queue;
    Random random = new Random();
    public LuFeiRunnable(ArrayBlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {

        while (true){
            try {
                String take = queue.take();
                System.out.println("-->路飞拿到 " + take);
                Thread.sleep(random.nextInt(500));
                System.out.println("-->路飞吃完 " + take);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```
* `ShanZhiRunnable`改为

```java
public class ShanZhiRunnable implements Runnable{
    ArrayBlockingQueue<String> queue;
    Random random = new Random();
    public ShanZhiRunnable(ArrayBlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true){
            try {
                String food = Food.getRandomFood();
                System.out.println("==>山治开始做 " + food);
                Thread.sleep(random.nextInt(500));
                System.out.println("==>山治做好了 " + food);
                queue.put(food);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
```
* 测试类改为:

```java
public class TestMain {
    public static void main(String[] args) {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(10);
        new Thread(new LuFeiRunnable(queue)).start();
        new Thread(new ShanZhiRunnable(queue)).start();
    }
}
```

运行结果:

```
==>山治开始做 椒麻鸡
==>山治做好了 椒麻鸡
==>山治开始做 牛腩煲
-->路飞拿到 椒麻鸡
==>山治做好了 牛腩煲
==>山治开始做 豆苗炒虾片
==>山治做好了 豆苗炒虾片
==>山治开始做 火鞭牛肉
-->路飞吃完 椒麻鸡
-->路飞拿到 牛腩煲
==>山治做好了 火鞭牛肉
==>山治开始做 桃酥鸡糕
-->路飞吃完 牛腩煲
-->路飞拿到 豆苗炒虾片
==>山治做好了 桃酥鸡糕
==>山治开始做 鸡汤煮干丝
-->路飞吃完 豆苗炒虾片
-->路飞拿到 火鞭牛肉
==>山治做好了 鸡汤煮干丝
==>山治开始做 菊花猪肝汤
-->路飞吃完 火鞭牛肉
-->路飞拿到 桃酥鸡糕
==>山治做好了 菊花猪肝汤
==>山治开始做 清香炒悟鸡
-->路飞吃完 桃酥鸡糕
-->路飞拿到 鸡汤煮干丝
-->路飞吃完 鸡汤煮干丝
-->路飞拿到 菊花猪肝汤
==>山治做好了 清香炒悟鸡
==>山治开始做 槐花猪肠汤
-->路飞吃完 菊花猪肝汤
-->路飞拿到 清香炒悟鸡
-->路飞吃完 清香炒悟鸡
==>山治做好了 槐花猪肠汤
==>山治开始做 瑞士排骨
-->路飞拿到 槐花猪肠汤
-->路飞吃完 槐花猪肠汤
==>山治做好了 瑞士排骨
-->路飞拿到 瑞士排骨
==>山治开始做 芝麻鱼球
-->路飞吃完 瑞士排骨
```

山治和路飞都很happy,各自都在做自己喜欢做的事情,但是,如果路飞吃到一定阶段,吃不下了,会发生什么呢?比如把`LuFeiRunnable`中的`Thread.sleep(random.nextInt(500));`改为`Thread.sleep(random.nextInt(5000));`,然后运行结果,会发现山治做菜速度也变慢了,因为传送带的食物放不下,山治必须等路飞吃掉一些,这样传送带才能放下食物.

>这就想我们去银行排队,医院取号,或者去各种部门排队办业务的时候.如果人来的太多了,那么他们一般的做法就是没号,要么在网上预约,要么下次早点来.这就是生产者(排队的人)生产太快,消费者(银行,医院等)消费太慢了

#扩展
`ArrayBlockingQueue`的父类是`BlockingQueue`,通过查找`BlockingQueue`的子类,我们能找到以下这几个类.
* `SynchronousQueue`跟`ArrayBlockingQueue`类似,只不过它除了可以用`put`,`offer`,`take`,`add`,`poll`,`take`这几个方法外,其余的例如`isEmpty`,`size`,`clear`,`contains`,`remove`等等,都是无效的,大部分都是直接返回一个固定值.这是因为它是一个没有容量的队列.甚至连一个容量都没有.因此在每次做插入操作的时候,都必须等其他线程做删除操作.
* `LinkedBlockingQueue`跟`ArrayBlockingQueue`类似,只是`ArrayBlockingQueue`是通过数组的方式实现队列,而`LinkedBlockingQueue`是通过列表的方式实现队列.
* `LinkedBlockingDeque`跟`LinkedBlockingQueue`一样是用链表实现队形,只是`LinkedBlockingDeque`为双向链表,可以在头部或尾部进行添加和删除操作.
	+ `add*`,`offer*`,`put*`这些增加操作跟`LinkedBlockingQueue`和`LinkedBlockingQueue`的`add`,`offer`,`put`是类似的,如果这些方法不带`*`,则都是等价与`*Last`
	+ `poll*`,`take*`这些获取数据操作跟`LinkedBlockingQueue`和`LinkedBlockingQueue`的`poll`,`take`类似的,如果不带`*`,则等价于`*Frist`	

> 在学习中,我们要能举一反三.这样我们就会学的比较快.

[^CURD]: C:create 新增,U:update 更新,R:retrieve 读取,D:delete 删除
---
# 打赏
>如果觉得我的文章写的还过得去的话,有钱就捧个钱场,没钱给我捧个人场(帮我点赞或推荐一下)
>![微信打赏](http://img.blog.csdn.net/20170508085654037?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast) 
>![支付宝打赏](http://img.blog.csdn.net/20170508085710334?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)