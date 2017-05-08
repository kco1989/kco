(十)java多线程之CountDownLatch
=====================================

>本人邮箱: <kco1989@qq.com>
>欢迎转载,转载请注明网址 <http://blog.csdn.net/tianshi_kco>
>github: <https://github.com/kco1989/kco>
>代码已经全部托管[github](https://github.com/kco1989/kco/blob/master/threadTest)有需要的同学自行下载

[TOC]

# 引言
有一个同步助手,可以让一个或一些线程等待直到另外一些线程执行完一些操作.这就是`CountDownLatch`

# 理论
`CountDownLatch`在初始化的时候需要一个`count`参数.调用`await()`的线程会一直等待,直到其他线程调用`countDown`使`count`清空为0.通常所有等待中的线程会被释放并且后面继续调用`await`的线程会立即返回.这个是一次性操作,`count`不能被重置的.如果想要被重置的话,就可以参考[(九)java多线程之CyclicBarrier](http://blog.csdn.net/tianshi_kco/article/details/52965561)

* `CountDownLatch(int count)` 构造一个指定`count`的`CountDownLatch`
* `await()` 如果当前的`count`为0的话,则会立即返回,否则当前线程一直等待,直到以下情况至少发生一个
	+ 其他线程调用`countDown`使`count`清空为0
	+ 当前线程被其他线程中断

* `await(long timeout, TimeUnit unit)` 如果当前的`count`为0的话,则会立即返回,否则当前线程一直等待,直到以下情况至少发生一个
	+ 其他线程调用`countDown`使`count`清空为0
	+ 当前线程被其他线程中断
	+ 指定的时间超时
* `countDown()` 如果当前的`count`大于0,则`count`减1,否则,所有等待的线程重新获得执行机会

# 例子
我们还是用上一篇文章旅游做例子吧.`小明`,`小刚`,`小红','小丽`,`小黑`,`小白`他们六个最后旅游回来时坐飞机,飞机上就有空姐啦.那么等他们六个人下飞机之后,空姐就要考试清理垃圾啦,检查设备等等,这些事情都必须飞机上的全部乘客下机之后才能做.行,按照这个场景,我们编写一下程序吧.

* 首先,想定义一个飞机类 `Airplane`, 里面包含乘客下机和空姐的清理工作

```java
public class Airplane {
    private CountDownLatch countDownLatch;
    private Random random;
    public Airplane(int peopleNum){
        countDownLatch = new CountDownLatch(peopleNum);
        random = new Random();
    }

    /**
     * 下机
     */
    public void getOffPlane(){
        try {
            String name = Thread.currentThread().getName();
			Thread.sleep(random.nextInt(500));
            System.out.println(name + " 在飞机在休息着....");
            Thread.sleep(random.nextInt(500));
            System.out.println(name + " 下飞机了");
            countDownLatch.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void doWork(){
        try {
            String name = Thread.currentThread().getName();
            System.out.println(name + "准备做 清理 工作");
            countDownLatch.await();
            System.out.println("飞机的乘客都下机," + name + "可以开始做 清理 工作");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
```

* 最后,就可以直接写测试类

```java
public class TestMain {

    public static void main(String[] args) {
        String visitor = "明刚红丽黑白";
        String kongjie = "美惠花";

        Airplane airplane = new Airplane(visitor.length());
        Set<Thread> threads = new HashSet<>();
        for (int i = 0; i < visitor.length(); i ++){
            threads.add(new Thread(() -> {
                airplane.getOffPlane();
            }, "小" + visitor.charAt(i)));
        }
        for (int i = 0; i < kongjie.length(); i ++){
            threads.add(new Thread(() ->{
                airplane.doWork();
            }, "小" + kongjie.charAt(i) + "空姐"));
        }

        for (Thread thread : threads){
            thread.start();
        }
    }
}
```

运行一下结果:

```
小花空姐准备做 清理 工作
小惠空姐准备做 清理 工作
小美空姐准备做 清理 工作
小丽 在飞机在休息着....
小明 在飞机在休息着....
小白 在飞机在休息着....
小刚 在飞机在休息着....
小刚 下飞机了
小明 下飞机了
小黑 在飞机在休息着....
小丽 下飞机了
小红 在飞机在休息着....
小红 下飞机了
小白 下飞机了
小黑 下飞机了
飞机的乘客都下机,小花空姐可以开始做 清理 工作
飞机的乘客都下机,小美空姐可以开始做 清理 工作
飞机的乘客都下机,小惠空姐可以开始做 清理 工作
```

通过结果可以发现,空姐都是等所有的乘客下飞机之后,才做清理工作的.结果正确,无毛病.

---
# 打赏
>如果觉得我的文章写的还过得去的话,有钱就捧个钱场,没钱给我捧个人场(帮我点赞或推荐一下)
>![微信打赏](http://img.blog.csdn.net/20170508085654037?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast) 
>![支付宝打赏](http://img.blog.csdn.net/20170508085710334?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)