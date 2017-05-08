
>本人邮箱: <kco1989@qq.com>
>欢迎转载,转载请注明网址 <http://blog.csdn.net/tianshi_kco>
>github: <https://github.com/kco1989/kco>
>代码已经全部托管[github](https://github.com/kco1989/kco/blob/master/threadTest)有需要的同学自行下载


#引言
在银行排队办理业务,通常会有一个VIP通道,让一些有VIP贵宾卡的优先办理业务,而不需要排队.这就是我们今天要讲的优先队列.

# 例子
假设在这么一个场景下,银行开始办理业务之前,已经来了20个客户,而且银行认为谁钱多,谁就优先办理业务.

* 首先创建一个`Human`类,它包括**姓名**和**存款**两个属性

```java
public class Human {

    private int maney;
    private String name;
    public Human(int maney, String name){
        this.maney = maney;
        this.name = name;
    }

    public int getManey() {
        return maney;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName() + "[存款:"+getManey()+"]";
    }
}
```

* 之后再增加一个比较`Human`存款多少的类`HumanComparator`

```java
public class HumanComparator implements Comparator<Human>  {
    @Override
    public int compare(Human o1, Human o2) {
        return o2.getManey() - o1.getManey();
    }
}
```

* 再增加来排队的类 `ProducerRunnable`

```java
public class ProducerRunnable implements Runnable{
    private static final String name = "明刚红李刘吕赵黄王孙朱曾游丽吴昊周郑秦丘";
    private Random random = new Random();
    private PriorityBlockingQueue<Human> queue;
    public ProducerRunnable(PriorityBlockingQueue<Human> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        for(int i = 0; i < 20; i ++){
            Human human = new Human(random.nextInt(10000), "小" + name.charAt(i));
            queue.put(human);
            System.out.println(human + " 开始排队...");
        }
    }

}

```
* 然后再增加办理业务的类 `ConsumerRunnable`

```java
public class ConsumerRunnable implements Runnable{

    private PriorityBlockingQueue<Human> queue;
    public ConsumerRunnable(PriorityBlockingQueue<Human> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true){
            Human take = queue.poll();
            if (take == null){
                break;
            }
            System.out.println(take + " 办理业务.");
        }
    }
}
```
* ok,增加测试类

```java
public class TestMain {

    public static void main(String[] args) throws InterruptedException {

        PriorityBlockingQueue<Human> queue = new PriorityBlockingQueue<>(200, new HumanComparator());
        Thread thread = new Thread(new ProducerRunnable(queue));
        thread.start();
        thread.join();
        new Thread(new ConsumerRunnable(queue)).start();
    }
}
```

> 'thread.join();':先等20个来齐了,再开始办理业务额

运行结果:

```
小明[存款:9296] 开始排队...
小刚[存款:765] 开始排队...
小红[存款:333] 开始排队...
小李[存款:3912] 开始排队...
小刘[存款:5732] 开始排队...
小吕[存款:3714] 开始排队...
小赵[存款:8439] 开始排队...
小黄[存款:7330] 开始排队...
小王[存款:9535] 开始排队...
小孙[存款:1421] 开始排队...
小朱[存款:8308] 开始排队...
小曾[存款:5211] 开始排队...
小游[存款:8264] 开始排队...
小丽[存款:8383] 开始排队...
小吴[存款:7664] 开始排队...
小昊[存款:2715] 开始排队...
小周[存款:9760] 开始排队...
小郑[存款:8158] 开始排队...
小秦[存款:869] 开始排队...
小丘[存款:1806] 开始排队...
小周[存款:9760] 办理业务.
小王[存款:9535] 办理业务.
小明[存款:9296] 办理业务.
小赵[存款:8439] 办理业务.
小丽[存款:8383] 办理业务.
小朱[存款:8308] 办理业务.
小游[存款:8264] 办理业务.
小郑[存款:8158] 办理业务.
小吴[存款:7664] 办理业务.
小黄[存款:7330] 办理业务.
小刘[存款:5732] 办理业务.
小曾[存款:5211] 办理业务.
小李[存款:3912] 办理业务.
小吕[存款:3714] 办理业务.
小昊[存款:2715] 办理业务.
小丘[存款:1806] 办理业务.
小孙[存款:1421] 办理业务.
小秦[存款:869] 办理业务.
小刚[存款:765] 办理业务.
小红[存款:333] 办理业务.
```

通过结果你会发现,有钱就是老大啊!!!**小周**跌二个来,结果却倒数第二个办理业务,只因为他是穷B.而**小周***倒数第四个到,结果却第一个办理业务,有钱就是任性啊...


---
# 打赏
>如果觉得我的文章写的还过得去的话,有钱就捧个钱场,没钱给我捧个人场(帮我点赞或推荐一下)
>![微信打赏](http://img.blog.csdn.net/20170508085654037?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast) 
>![支付宝打赏](http://img.blog.csdn.net/20170508085710334?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)