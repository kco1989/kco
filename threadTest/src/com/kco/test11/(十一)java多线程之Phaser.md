(十一)java多线程之Phaser
=====================================

>本人邮箱: <kco1989@qq.com>
>欢迎转载,转载请注明网址 <http://blog.csdn.net/tianshi_kco>
>github: <https://github.com/kco1989/kco>
>代码已经全部托管[github](https://github.com/kco1989/kco/blob/master/threadTest)有需要的同学自行下载

[TOC]
# 引言
讲完了`CyclicBarrier`和`CountDownLatch`,今天讲一个跟这两个类有点类似的`Phaser`.->移相器
>java7中引入了一种新的可重复使用的同步屏障,称为移相器`Phaser`.`Phaser`拥有与`CyclicBarrier`和`CountDownLatch`类似的功劳.但是这个类提供了更加灵活的应用.`CountDownLatch`和`CyclicBarrier`都是只适用于固定数量的参与者.移相器适用于可变数目的屏障,在这个意义上,可以在任何时间注册新的参与者.并且在抵达屏障是可以注销已经注册的参与者.因此,注册到同步移相器的参与者的数目可能会随着时间的推移而变化.如`CyclicBarrier`一样,移相器可以重复使用,这意味着当前参与者到达移相器后,可以再一次注册自己并等待另一次到达.因此,移相器会有多代.一旦为某个特定相位注册的所有参与者都到达移相器,就增加相数.相数从零开始,在达到`Integer.MAX_VALUE`后,再次绕回0.当移相器发生变化时,通过重写`onAdvance`方法,可以自行可选操作.这个方法也可用于终止移相器.移相器一旦被终止,所有的同步方法就会立即返回,并尝试注册新的失败的参与者.
>移相器的另一个重要特征是:移相器可能是分层的,这允许你以树形结构来安排移相器以减少竞争.很明显,更小的组将拥有更少的竞争同步的参与者.因此,将大量的参与者分成较小的组可以减少竞争.虽然创建移相器能增加中的吞吐量,但是这需要更多的开销.最后,移相器的另一个重要的特征在于监控功能,使用独立的对象可以监视移相器的当前状态.监视器可以查询注册到移相器的参与者的数量,以及已经到达和还没有到达某个特定相数的参与者的数量.[^java7]

 [^java7]: 以上两段文字是引用清华大学出版社的java 7编程高级进阶,我在写这篇博文的时候,对`Phaser`也是一知半解,不想耽误各位读者,所以直接照抄过来.不过例子则是在我自己理解的上写出来的.

# 例子1 用`Phaser`代替`CyclicBarrier`

将之前[(九)java多线程之CyclicBarrier](http://blog.csdn.net/tianshi_kco/article/details/52965561)旅游的例子改写一下,
>Phaser替代CyclicBarrier比较简单，CyclicBarrier的await()方法可以直接用Phaser的arriveAndAwaitAdvance()方法替代
>`CyclicBarrier`与`Phaser`:`CyclicBarrier`只适用于固定数量的参与者,而`Phaser`适用于可变数目的屏障.

* `TourismRunnable` 旅游类

```java 
public class TourismRunnable implements Runnable{
    Phaser phaser;
    Random random;
    public TourismRunnable(Phaser phaser) {
        this.phaser = phaser;
        this.random = new Random();
    }

    @Override
    public void run() {
        tourism();
    }

    /**
     * 旅游过程
     */
    private void tourism() {
        goToStartingPoint();
        goToHotel();
        goToTourismPoint1();
        goToTourismPoint2();
        goToTourismPoint3();
        goToEndPoint();
    }

    /**
     * 装备返程
     */
    private void goToEndPoint() {
        goToPoint("飞机场,准备登机回家");
    }

    /**
     * 到达旅游点3
     */
    private void goToTourismPoint3() {
        goToPoint("旅游点3");
    }

    /**
     * 到达旅游点2
     */
    private void goToTourismPoint2() {
        goToPoint("旅游点2");
    }

    /**
     * 到达旅游点1
     */
    private void goToTourismPoint1() {
        goToPoint("旅游点1");
    }

    /**
     * 入住酒店
     */
    private void goToHotel() {
        goToPoint("酒店");
    }

    /**
     * 出发点集合
     */
    private void goToStartingPoint() {
        goToPoint("出发点");
    }

    private int getRandomTime(){
        int time = this.random.nextInt(400) + 100;
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return time;
    }

    private void goToPoint(String point){
        try {
            String name = Thread.currentThread().getName();
            System.out.println(name + " 花了 " + getRandomTime() + " 时间才到了" + point);
            phaser.arriveAndAwaitAdvance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

* `TestMain` 测试类

```java 
public class TestMain {

    public static void main(String[] args) {
        String name = "明刚红丽黑白";
        Phaser phaser = new Phaser(name.length());
        List<Thread> tourismThread = new ArrayList<>();
        for (char ch : name.toCharArray()){
            tourismThread.add(new Thread(new TourismRunnable(phaser), "小" + ch));
        }
        for (Thread thread : tourismThread){
            thread.start();
        }
    }
}
```

运行结果

```
小红 花了 122 时间才到了出发点
小明 花了 259 时间才到了出发点
小白 花了 267 时间才到了出发点
小丽 花了 306 时间才到了出发点
小刚 花了 385 时间才到了出发点
小黑 花了 486 时间才到了出发点
小白 花了 299 时间才到了酒店
小刚 花了 345 时间才到了酒店
小黑 花了 449 时间才到了酒店
小丽 花了 452 时间才到了酒店
小明 花了 462 时间才到了酒店
小红 花了 480 时间才到了酒店
小丽 花了 107 时间才到了旅游点1
小红 花了 141 时间才到了旅游点1
小明 花了 212 时间才到了旅游点1
小黑 花了 286 时间才到了旅游点1
小白 花了 305 时间才到了旅游点1
小刚 花了 386 时间才到了旅游点1
小丽 花了 119 时间才到了旅游点2
小黑 花了 222 时间才到了旅游点2
小明 花了 259 时间才到了旅游点2
小刚 花了 299 时间才到了旅游点2
小红 花了 354 时间才到了旅游点2
小白 花了 422 时间才到了旅游点2
小丽 花了 112 时间才到了旅游点3
小白 花了 182 时间才到了旅游点3
小刚 花了 283 时间才到了旅游点3
小明 花了 295 时间才到了旅游点3
小红 花了 386 时间才到了旅游点3
小黑 花了 483 时间才到了旅游点3
小黑 花了 152 时间才到了飞机场,准备登机回家
小白 花了 178 时间才到了飞机场,准备登机回家
小明 花了 248 时间才到了飞机场,准备登机回家
小红 花了 362 时间才到了飞机场,准备登机回家
小丽 花了 428 时间才到了飞机场,准备登机回家
小刚 花了 432 时间才到了飞机场,准备登机回家
```

> * `Phaser(int parties)` 创建一个指定parties个线程参与同步任务.
> * ``

# 例子2 用`Phaser`代替`CountDownLatch`
将之前[(十)java多线程之CountDownLatch](http://blog.csdn.net/tianshi_kco/article/details/52972892)旅游回来坐飞机的例子改写一下,

>`CountDownLatch`主要使用的有2个方法
>*  `await()`方法，可以使线程进入等待状态，在`Phaser`中，与之对应的方法是`awaitAdvance(int n)`。
* `countDown()`，使计数器减一，当计数器为0时所有等待的线程开始执行，在Phaser中，与之对应的方法是`arrive()`

* `Airplane`飞机类

```java 
public class Airplane {
    private Phaser phaser;
    private Random random;
    public Airplane(int peopleNum){
        phaser = new Phaser(peopleNum);
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
            phaser.arrive();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void doWork(){

        String name = Thread.currentThread().getName();
        System.out.println(name + "准备做 清理 工作");
        phaser.awaitAdvance(phaser.getPhase());
        System.out.println("飞机的乘客都下机," + name + "可以开始做 清理 工作");

    }

}
```

* `TestMain` 测试类(没有改)

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

运行结果

```
小花空姐准备做 清理 工作
小惠空姐准备做 清理 工作
小美空姐准备做 清理 工作
小黑 在飞机在休息着....
小明 在飞机在休息着....
小红 在飞机在休息着....
小丽 在飞机在休息着....
小刚 在飞机在休息着....
小明 下飞机了
小红 下飞机了
小黑 下飞机了
小白 在飞机在休息着....
小丽 下飞机了
小刚 下飞机了
小白 下飞机了
飞机的乘客都下机,小美空姐可以开始做 清理 工作
飞机的乘客都下机,小花空姐可以开始做 清理 工作
飞机的乘客都下机,小惠空姐可以开始做 清理 工作
```
 
#例子3 高级用法
前面两个例子都比较简单,现在我们还用`Phaser`一个比较高级一点用法.还是用旅游的例子
假如有这么一个场景,在旅游过程中,有可能很凑巧遇到几个朋友,然后他们听说你们在旅游,所以想要加入一起继续接下来的旅游.也有可能,在旅游过程中,突然其中有某几个人临时有事,想退出这次旅游了.在自由行的旅游,这是很常见的一些事情.如果现在我们使用`CyclicBarrier`这个类来实现,我们发现是实现不了,这是用`Phaser`就可实现这个功能.

* 首先,我们改写旅游类 `TourismRunnable`,这次改动相对比较多一点

```java 
public class TourismRunnable implements Runnable{
    Phaser phaser;
    Random random;
    /**
     * 每个线程保存一个朋友计数器,比如小红第一次遇到一个朋友,则取名`小红的朋友0号`,
     * 然后旅游到其他景点的时候,如果小红又遇到一个朋友,这取名为`小红的朋友1号`
     */
    AtomicInteger frientCount = new AtomicInteger();
    public TourismRunnable(Phaser phaser) {
        this.phaser = phaser;
        this.random = new Random();
    }

    @Override
    public void run() {
        tourism();
    }

    /**
     * 旅游过程
     */
    private void tourism() {
        switch (phaser.getPhase()){
            case 0:if(!goToStartingPoint()) break;
            case 1:if(!goToHotel()) break;
            case 2:if(!goToTourismPoint1()) break;
            case 3:if(!goToTourismPoint2()) break;
            case 4:if(!goToTourismPoint3()) break;
            case 5:if(!goToEndPoint()) break;
        }
    }

    /**
     * 准备返程
     * @return 返回true,说明还要继续旅游,否则就临时退出了
     */
    private boolean goToEndPoint() {
        return goToPoint("飞机场,准备登机回家");
    }

    /**
     * 到达旅游点3
     * @return 返回true,说明还要继续旅游,否则就临时退出了
     */
    private boolean goToTourismPoint3() {
        return goToPoint("旅游点3");
    }

    /**
     * 到达旅游点2
     * @return 返回true,说明还要继续旅游,否则就临时退出了
     */
    private boolean goToTourismPoint2() {
        return goToPoint("旅游点2");
    }

    /**
     * 到达旅游点1
     * @return 返回true,说明还要继续旅游,否则就临时退出了
     */
    private boolean goToTourismPoint1() {
        return goToPoint("旅游点1");
    }

    /**
     * 入住酒店
     * @return 返回true,说明还要继续旅游,否则就临时退出了
     */
    private boolean goToHotel() {
        return goToPoint("酒店");
    }

    /**
     * 出发点集合
     * @return 返回true,说明还要继续旅游,否则就临时退出了
     */
    private boolean goToStartingPoint() {
        return goToPoint("出发点");
    }

    private int getRandomTime() throws InterruptedException {
        int time = random.nextInt(400) + 100;
        Thread.sleep(time);
        return time;
    }

    /**
     * @param point 集合点
     * @return 返回true,说明还要继续旅游,否则就临时退出了
     */
    private boolean goToPoint(String point){
        try {
            if(!randomEvent()){
                phaser.arriveAndDeregister();
                return false;
            }
            String name = Thread.currentThread().getName();
            System.out.println(name + " 花了 " + getRandomTime() + " 时间才到了" + point);
            phaser.arriveAndAwaitAdvance();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 随机事件
     * @return 返回true,说明还要继续旅游,否则就临时退出了
     */
    private boolean randomEvent() {
        int r = random.nextInt(100);
        String name = Thread.currentThread().getName();
        if (r < 10){
            int friendNum =  1;
            System.out.println(name + ":在这里竟然遇到了"+friendNum+"个朋友,他们说要一起去旅游...");
            phaser.bulkRegister(friendNum);
            for (int i = 0; i < friendNum; i ++){
                new Thread(new TourismRunnable(phaser), name + "的朋友" + frientCount.getAndAdd(1) + "号").start();
            }
        }else if(r > 90){
            System.out.println(name + ":突然有事要离开一下,不和他们继续旅游了....");
            return false;
        }
        return true;
    }
}
```

>代码解析
>> `tourism`这个方法的case写法看起有点怪异,如果是为了满足我们这个需求,这里的case的意思是-->`case 第几次集合: if(是否继续旅游) 若不继续则break,否则继续后面的旅游`
>> `phaser.getPhase()` 初始值为0,如果全部人到达集合点这个`Phase`+1,如果`phaser.getPhase()`达到Integer的最大值,这重新清空为0,在这里表示第几次集合了
>> `phaser.arriveAndDeregister();` 表示这个人旅游到这个景点之后,就离开这个旅游团了
>> `phaser.arriveAndAwaitAdvance();` 表示这个人在这个景点旅游完,在等待其他人
>> `phaser.bulkRegister(friendNum);` 表示这个人在这个景点遇到了`friendNum`个朋友,他们要加入一起旅游

* 最后我们的测试代码还是差不多的,比例子1多了一个到齐后的操作

```java 
public class TestMain {

    public static void main(String[] args) {
        String name = "明刚红丽黑白";
        Phaser phaser = new SubPhaser(name.length());
        List<Thread> tourismThread = new ArrayList<>();
        for (char ch : name.toCharArray()){
            tourismThread.add(new Thread(new TourismRunnable(phaser), "小" + ch));
        }
        for (Thread thread : tourismThread){
            thread.start();
        }
    }
    public static class SubPhaser extends Phaser{
        public SubPhaser(int parties) {
            super(parties);
        }

        @Override
        protected boolean onAdvance(int phase, int registeredParties) {

            System.out.println(Thread.currentThread().getName() + ":全部"+getArrivedParties()+"个人都到齐了,现在是第"+(phase + 1)
                    +"次集合准备去下一个地方..................\n");
            return super.onAdvance(phase, registeredParties);
        }
    }
}
```

运行输出以下结果:

```
小白 花了 109 时间才到了出发点
小红 花了 135 时间才到了出发点
小丽 花了 218 时间才到了出发点
小黑 花了 297 时间才到了出发点
小明 花了 303 时间才到了出发点
小刚 花了 440 时间才到了出发点
小刚:全部6个人都到齐了,现在是第1次集合准备去下一个地方..................

小明:突然有事要离开一下,不和他们继续旅游了....
小刚:突然有事要离开一下,不和他们继续旅游了....
小红 花了 127 时间才到了酒店
小丽 花了 162 时间才到了酒店
小黑 花了 365 时间才到了酒店
小白 花了 474 时间才到了酒店
小白:全部4个人都到齐了,现在是第2次集合准备去下一个地方..................

小黑:突然有事要离开一下,不和他们继续旅游了....
小丽:突然有事要离开一下,不和他们继续旅游了....
小红 花了 348 时间才到了旅游点1
小白 花了 481 时间才到了旅游点1
小白:全部2个人都到齐了,现在是第3次集合准备去下一个地方..................

小白 花了 128 时间才到了旅游点2
小红 花了 486 时间才到了旅游点2
小红:全部2个人都到齐了,现在是第4次集合准备去下一个地方..................

小红 花了 159 时间才到了旅游点3
小白 花了 391 时间才到了旅游点3
小白:全部2个人都到齐了,现在是第5次集合准备去下一个地方..................

小白:在这里竟然遇到了1个朋友,他们说要一起去旅游...
小白 花了 169 时间才到了飞机场,准备登机回家
小红 花了 260 时间才到了飞机场,准备登机回家
小白的朋友0号 花了 478 时间才到了飞机场,准备登机回家
小白的朋友0号:全部3个人都到齐了,现在是第6次集合准备去下一个地方..................
```

通过结果配合我上面的解释,还是比较好理解的.

#遗漏
这里还有`phaser`的中断和树形结构没有举例子,后续想到比较后的例子,我会继续做补充的

#后记
这篇是我目前为止写的最慢的一篇博文,因为之前没有使用过`phaser`,导致在写的出现很多问题.所以一边查资料,一边学习,总算还是把这个`phaser`给理解了.

---
# 打赏
>如果觉得我的文章写的还过得去的话,有钱就捧个钱场,没钱给我捧个人场(帮我点赞或推荐一下)
>![微信打赏](http://img.blog.csdn.net/20170508085654037?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast) 
>![支付宝打赏](http://img.blog.csdn.net/20170508085710334?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)