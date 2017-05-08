(十二)java多线程之Exchanger
=====================================

>本人邮箱: <kco1989@qq.com>
>欢迎转载,转载请注明网址 <http://blog.csdn.net/tianshi_kco>
>github: <https://github.com/kco1989/kco>
>代码已经全部托管[github](https://github.com/kco1989/kco/blob/master/threadTest)有需要的同学自行下载

[TOC]
#引言
今天我们讲最后一个同步工具类`Exchanger`,这个比较简单,就是让两个线程交换数据.

#理论
`Exchanger`只有两个方法而已,而且两个还是一样的,只是参数不通而已
* `exchange(V x)` 跟另外一个线程交换数据x,如果另外一个线程的数据准备好,那么当前线程会立刻返回,并获得另外一个线程的数据;否则当前线程会进入等待状态
* `V exchange(V x, long timeout, TimeUnit unit)`: 跟`exchange`,如果会有一个指定的超时时间,如果在等待时间超时了,而且还没有收到对方的数据的话,则会抛出`TimeoutException`异常

#例子 有耐心的山治和路飞
看过海贼王的人都知道山治和路飞,山治是一个厨师,手艺那是杠杠的.路飞则是一个大胃王,超能吃.现在编写一个程序,让山治不断给路飞做食物,而路飞不断吃,吃完之后要对山治说感谢.ok,开始编码

* 先编写一个`Food`食物类,这个比较简单,就是定义一些食物

```java
public class Food {
    public final static String[] food = {
            "打边炉","奶味芦笋汤","糟片鸭","烤花揽桂鱼","苦中作乐","七星丸","鸭黄豆腐","贝丝扒菜胆","脆炒南瓜丝","龙凤双腿",
            //省略部分代码....
    };

    private static Random random = new Random();
    public static String getRandomFood(){
        return food[random.nextInt(food.length)];
    }
}
```

* 之后编写山治做菜的类 `ShanZhiRunnable`

```java
public class ShanZhiRunnable implements Runnable{
    Exchanger<String> exchanger;
    Random random = new Random();
    public ShanZhiRunnable(Exchanger<String> exchanger) {
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        while (true){
            try {
                String food = Food.getRandomFood();
                System.out.println("==>山治开始做 " + food);
                Thread.sleep(random.nextInt(500));
                System.out.println("==>山治把 " + food + " 给做好了,给路飞送过去");
                String exchange = exchanger.exchange(food);
                System.out.println("==>山治收到路飞的评语:" + exchange);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
```

* 然后编写路飞吃饭的类 `LuFeiRunnable`

```java
public class LuFeiRunnable implements Runnable{
    Exchanger<String> exchanger;
    Random random = new Random();
    public LuFeiRunnable(Exchanger<String> exchanger) {
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        String temp = "开吃啦...";
        while (true){
            try {
                String food = exchanger.exchange(temp);
                System.out.println("--->路飞拿到山治做的菜: " + food);
                Thread.sleep(random.nextInt(500));
                System.out.println("--->路飞吃完" + food);
                temp = food + "太好吃!太感谢山治了...";
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

* 最后编写测试类 `TestMain`

```java
public class TestMain {
    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger<>();
        new Thread(new LuFeiRunnable(exchanger)).start();
        new Thread(new ShanZhiRunnable(exchanger)).start();
    }
}
```

运行结果如下,并截取部分输出

```
==>山治开始做 一品鲍鱼
==>山治把 一品鲍鱼 给做好了,给路飞送过去
==>山治收到路飞的评语:开吃啦...
==>山治开始做 芝麻肉丝
--->路飞拿到山治做的菜: 一品鲍鱼
==>山治把 芝麻肉丝 给做好了,给路飞送过去
--->路飞吃完一品鲍鱼
--->路飞拿到山治做的菜: 芝麻肉丝
==>山治收到路飞的评语:一品鲍鱼太好吃!太感谢山治了...
==>山治开始做 鸡蛋炒西红柿
--->路飞吃完芝麻肉丝
==>山治把 鸡蛋炒西红柿 给做好了,给路飞送过去
==>山治收到路飞的评语:芝麻肉丝太好吃!太感谢山治了...
--->路飞拿到山治做的菜: 鸡蛋炒西红柿
==>山治开始做 油豆腐镶肉
==>山治把 油豆腐镶肉 给做好了,给路飞送过去
--->路飞吃完鸡蛋炒西红柿
--->路飞拿到山治做的菜: 油豆腐镶肉
==>山治收到路飞的评语:鸡蛋炒西红柿太好吃!太感谢山治了...
==>山治开始做 梅菜蒸鱼尾
==>山治把 梅菜蒸鱼尾 给做好了,给路飞送过去
--->路飞吃完油豆腐镶肉
==>山治收到路飞的评语:油豆腐镶肉太好吃!太感谢山治了...
==>山治开始做 炸子鸡
--->路飞拿到山治做的菜: 梅菜蒸鱼尾
==>山治把 炸子鸡 给做好了,给路飞送过去
--->路飞吃完梅菜蒸鱼尾
--->路飞拿到山治做的菜: 炸子鸡
==>山治收到路飞的评语:梅菜蒸鱼尾太好吃!太感谢山治了...
==>山治开始做 翠竹粉蒸鱼
==>山治把 翠竹粉蒸鱼 给做好了,给路飞送过去
--->路飞吃完炸子鸡
--->路飞拿到山治做的菜: 翠竹粉蒸鱼
==>山治收到路飞的评语:炸子鸡太好吃!太感谢山治了...
==>山治开始做 风情羊柳
==>山治把 风情羊柳 给做好了,给路飞送过去
```

# 例子2 没耐心的山治和路飞
现在假设他们俩都没有耐心,不想一直等一下

* 将`LuFeiRunnable`修改为:

```java
public class LuFeiRunnable implements Runnable{
    Exchanger<String> exchanger;
    Random random = new Random();
    public LuFeiRunnable(Exchanger<String> exchanger) {
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        String temp = "开吃啦...";
        while (true){
            try {
                String food = exchanger.exchange(temp, 300, TimeUnit.MILLISECONDS);
                System.out.println("--->路飞吃完" + food);
                temp = food + "太好吃!太感谢山治了...";
                Thread.sleep(random.nextInt(500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                System.out.println("--->路飞等的不耐烦了,不想等......开始喝起 东北风" );
            }
        }
    }
}
```

* 将`ShanZhiRunnable`修改为

```java
public class ShanZhiRunnable implements Runnable{
    Exchanger<String> exchanger;
    Random random = new Random();
    public ShanZhiRunnable(Exchanger<String> exchanger) {
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        while (true){
            String food = Food.getRandomFood();
            try {
                System.out.println("==>山治开始做 " + food);
                Thread.sleep(random.nextInt(500));
                System.out.println("==>山治把 " + food + " 给做好了,给路飞送过去");
                String exchange = exchanger.exchange(food, 300, TimeUnit.MILLISECONDS);
                System.out.println("==>山治收到路飞的评语:" + exchange);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                System.out.println("==>山治等的不耐烦了,不想等......,把 " + food + " 其他船员吃了");
            }
        }

    }
}
```

运行一下程序:

```java
==>山治开始做 辣子肉丁
--->路飞等的不耐烦了,不想等......开始喝起 东北风
==>山治把 辣子肉丁 给做好了,给路飞送过去
==>山治收到路飞的评语:开吃啦...
==>山治开始做 砂锅三味
--->路飞吃完辣子肉丁
==>山治把 砂锅三味 给做好了,给路飞送过去
==>山治收到路飞的评语:辣子肉丁太好吃!太感谢山治了...
--->路飞吃完砂锅三味
==>山治开始做 甜椒肉丝
==>山治把 甜椒肉丝 给做好了,给路飞送过去
--->路飞吃完甜椒肉丝
==>山治收到路飞的评语:砂锅三味太好吃!太感谢山治了...
==>山治开始做 一品鲍鱼
==>山治把 一品鲍鱼 给做好了,给路飞送过去
==>山治等的不耐烦了,不想等......,把 一品鲍鱼 其他船员吃了
==>山治开始做 香酥凤腿
--->路飞等的不耐烦了,不想等......开始喝起 东北风
==>山治把 香酥凤腿 给做好了,给路飞送过去
==>山治收到路飞的评语:甜椒肉丝太好吃!太感谢山治了...
--->路飞吃完香酥凤腿
==>山治开始做 雪花片汤
==>山治把 雪花片汤 给做好了,给路飞送过去
--->路飞吃完雪花片汤
==>山治收到路飞的评语:香酥凤腿太好吃!太感谢山治了...
==>山治开始做 凤尾金鱼
==>山治把 凤尾金鱼 给做好了,给路飞送过去
==>山治收到路飞的评语:雪花片汤太好吃!太感谢山治了...
--->路飞吃完凤尾金鱼
==>山治开始做 三菇浸鱼云
--->路飞等的不耐烦了,不想等......开始喝起 东北风
==>山治把 三菇浸鱼云 给做好了,给路飞送过去
==>山治收到路飞的评语:凤尾金鱼太好吃!太感谢山治了...
--->路飞吃完三菇浸鱼云
==>山治开始做 辣子鸡丁
==>山治把 辣子鸡丁 给做好了,给路飞送过去
==>山治收到路飞的评语:三菇浸鱼云太好吃!太感谢山治了...
--->路飞吃完辣子鸡丁
==>山治开始做 梅干菜烧肉
```

通过以上两个例子,掌握`Exchanger`应该没有什么难度.好的,这篇就到这里.

---
# 打赏
>如果觉得我的文章写的还过得去的话,有钱就捧个钱场,没钱给我捧个人场(帮我点赞或推荐一下)
>![微信打赏](http://img.blog.csdn.net/20170508085654037?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast) 
>![支付宝打赏](http://img.blog.csdn.net/20170508085710334?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)