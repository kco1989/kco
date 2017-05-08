(六)java多线程之ReadWriteLock
=====================================

>本人邮箱: <kco1989@qq.com>
>欢迎转载,转载请注明网址 <http://blog.csdn.net/tianshi_kco>
>github: <https://github.com/kco1989/kco>
>代码已经全部托管[github](https://github.com/kco1989/kco/blob/master/threadTest)有需要的同学自行下载

[TOC]

#引言
上一篇讲到了`ReentrantLock`,在同一个时间点,只能用一个线程能够访问共享资源.今天我们来讲一下读写锁`ReadWriteLock`,读写锁提供了在同一个时间点,可以有多个线程共享去读取共享资源,当只有有一个线程去写共享资源.

`ReadWriteLock`主要是`Lock`锁使用的,提供一个读和一个写的操作.`readLock`可以在有多个线程同时操作直到没有写的操作.`writeLock`却在同一个时间点只能有一个线程在操作.
所有`ReadWriteLock`的实现类必须在`writeLock`写操作时,保证内存同步,同时还必须和`readLock`相关联,比如当一个线程获取到读锁时,这必须保证当前线程能在写锁释放后看到所有的更新操作.

#例子
* 首先定一个模拟读写文件的类`MockFIle`

```java
public class MockFile {

    // 使用StringBuilder而不是使用StringBuffer
    // 是因为StringBuilder是线程不安全的,而StringBuffer是线程安全的
    StringBuilder fileContent;

    public MockFile() {
        this.fileContent = new StringBuilder();
    }

    public String readFile(){
        try {
            Thread.sleep(10);
            String content = fileContent.toString();
            Thread.sleep(10);
            return content;
        } catch (InterruptedException e) {
            return null;
        }
    }

    public void writeFile(char content){
        try {
            Thread.sleep(10);
            fileContent.append(content);
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

```

> 注意在该类使用`StringBuilder`是因为这个类是线程不安全的
> `StringBuffer`是线程安全的

* 之后编写读文件操作 `ReadRunnable`

```java
public class ReadRunnable implements Runnable {
    ReadWriteLock lock;
    MockFile file;
    boolean isStop;
    public ReadRunnable(ReadWriteLock lock, MockFile file) {
        this.lock = lock;
        this.file = file;
        isStop = false;
    }

    @Override
    public void run() {
        Thread self = Thread.currentThread();
        while(!isStop){
            Lock lock = this.lock.readLock();
            try {
                lock.lock();
                String content = file.readFile();
                System.out.println(self.getName() + " read: " + content);
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setStop() {
        isStop = true;
    }
}

```

* 然后再编写 写文件操作 `WriteRunnable`

```java 
public class WriteRunnable implements Runnable {
    ReadWriteLock lock;
    MockFile file;
    String content;

    public WriteRunnable(ReadWriteLock lock, MockFile file, String content) {
        this.lock = lock;
        this.file = file;
        this.content = content;
    }

    @Override
    public void run() {
        Thread self = Thread.currentThread();
        for (char ch : content.toCharArray()){
            Lock lock = this.lock.writeLock();
            try {
                lock.lock();
                System.out.println(self.getName() + " write " + ch);
                file.writeFile(ch);
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```

> 这里需要注意以下几点
> 1. 读文件操作比写文件操作频繁,这样才能看到边写边读的效果,这就需要在释放锁的时候,让写线程`睡`久一点,让读线程`睡`少一点
> 2. 还有我这里采用判断标记位的方式,让读线程停止操作

* 最后,我们需要写一个测试类 `TestMain`

```java 
public class TestMain {

    public static void main(String[] args) {
        MockFile mockFile = new MockFile();
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        Thread thread1 = new Thread(new WriteRunnable(lock, mockFile, "ABCDEFGHIJKLMOOPQRSTUVWXYZ"));
        Thread thread2 = new Thread(new WriteRunnable(lock, mockFile, "abcdefghijklmoopqrstuvwxyz"));
        Thread thread3 = new Thread(new WriteRunnable(lock, mockFile, "0123456789"));
        List<Thread> readThreadList = new ArrayList<>();
        List<ReadRunnable> readRunnables = new ArrayList<>();
        for (int i = 0; i < 5; i ++){
            ReadRunnable readRunnable = new ReadRunnable(lock, mockFile);
            readThreadList.add(new Thread(readRunnable));
            readRunnables.add(readRunnable);
        }
        thread1.start();
        thread2.start();
        thread3.start();
        for (Thread readThread : readThreadList){
            readThread.start();
        }

        while (thread1.isAlive() || thread2.isAlive() || thread3.isAlive());

        for (ReadRunnable readRunnable : readRunnables){
            readRunnable.setStop();
        }
    }
}

```

> 测试类,需要注意以下几点
> 1. 创建了三个写操作,
>>     + 第一个依次写入26个大写字母
>>     + 第二个依次写入26个小写字母
>>     + 弟三个依次写入0-9十个数字
> 2. 创建了五个读操作,让它们不断的读取文件
> 3. 当全部写操作结束时,这停止这五个读操作

运行程序,结果如下

```
Thread-0 write A
Thread-1 write a
Thread-2 write 0
Thread-4 read: Aa0
Thread-5 read: Aa0
Thread-7 read: Aa0
Thread-3 read: Aa0
Thread-6 read: Aa0
Thread-6 read: Aa0
Thread-3 read: Aa0
Thread-4 read: Aa0
Thread-7 read: Aa0
Thread-5 read: Aa0
Thread-0 write B
Thread-6 read: Aa0B
Thread-1 write b
Thread-5 read: Aa0Bb
Thread-3 read: Aa0Bb
Thread-4 read: Aa0Bb
Thread-7 read: Aa0Bb
Thread-2 write 1
Thread-6 read: Aa0Bb1
Thread-5 read: Aa0Bb1
Thread-3 read: Aa0Bb1
Thread-4 read: Aa0Bb1
Thread-7 read: Aa0Bb1
Thread-0 write C
Thread-7 read: Aa0Bb1C
Thread-4 read: Aa0Bb1C
Thread-5 read: Aa0Bb1C
Thread-6 read: Aa0Bb1C
Thread-3 read: Aa0Bb1C
Thread-1 write c
Thread-3 read: Aa0Bb1Cc
Thread-4 read: Aa0Bb1Cc
Thread-7 read: Aa0Bb1Cc
Thread-5 read: Aa0Bb1Cc
Thread-6 read: Aa0Bb1Cc
Thread-2 write 2
Thread-3 read: Aa0Bb1Cc2
Thread-4 read: Aa0Bb1Cc2
Thread-5 read: Aa0Bb1Cc2
Thread-7 read: Aa0Bb1Cc2
Thread-6 read: Aa0Bb1Cc2
Thread-0 write D
Thread-7 read: Aa0Bb1Cc2D
Thread-6 read: Aa0Bb1Cc2D
Thread-3 read: Aa0Bb1Cc2D
Thread-4 read: Aa0Bb1Cc2D
Thread-5 read: Aa0Bb1Cc2D
Thread-1 write d
Thread-2 write 3
Thread-7 read: Aa0Bb1Cc2Dd3
Thread-6 read: Aa0Bb1Cc2Dd3
Thread-3 read: Aa0Bb1Cc2Dd3
Thread-4 read: Aa0Bb1Cc2Dd3
Thread-5 read: Aa0Bb1Cc2Dd3
Thread-0 write E
Thread-3 read: Aa0Bb1Cc2Dd3E
Thread-6 read: Aa0Bb1Cc2Dd3E
Thread-7 read: Aa0Bb1Cc2Dd3E
Thread-4 read: Aa0Bb1Cc2Dd3E
Thread-5 read: Aa0Bb1Cc2Dd3E
Thread-1 write e
Thread-2 write 4
Thread-5 read: Aa0Bb1Cc2Dd3Ee4
Thread-4 read: Aa0Bb1Cc2Dd3Ee4
Thread-7 read: Aa0Bb1Cc2Dd3Ee4
Thread-6 read: Aa0Bb1Cc2Dd3Ee4
Thread-3 read: Aa0Bb1Cc2Dd3Ee4
Thread-0 write F
Thread-1 write f
Thread-2 write 5
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5
Thread-0 write G
Thread-1 write g
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg
Thread-2 write 6
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6
Thread-0 write H
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6H
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6H
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6H
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6H
Thread-1 write h
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh
Thread-2 write 7
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7
Thread-0 write I
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7I
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7I
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7I
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7I
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7I
Thread-1 write i
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii
Thread-2 write 8
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8
Thread-0 write J
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8J
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8J
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8J
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8J
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8J
Thread-1 write j
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj
Thread-2 write 9
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9
Thread-0 write K
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9K
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9K
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9K
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9K
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9K
Thread-1 write k
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9Kk
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9Kk
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9Kk
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9Kk
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9Kk
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9Kk
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9Kk
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9Kk
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9Kk
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9Kk
Thread-0 write L
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkL
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkL
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkL
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkL
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkL
Thread-1 write l
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLl
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLl
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLl
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLl
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLl
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLl
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLl
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLl
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLl
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLl
Thread-0 write M
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlM
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlM
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlM
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlM
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlM
Thread-1 write m
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMm
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMm
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMm
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMm
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMm
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMm
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMm
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMm
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMm
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMm
Thread-0 write O
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmO
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmO
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmO
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmO
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmO
Thread-1 write o
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOo
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOo
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOo
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOo
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOo
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOo
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOo
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOo
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOo
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOo
Thread-0 write O
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoO
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoO
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoO
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoO
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoO
Thread-1 write o
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOo
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOo
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOo
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOo
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOo
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOo
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOo
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOo
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOo
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOo
Thread-0 write P
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoP
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoP
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoP
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoP
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoP
Thread-1 write p
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPp
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPp
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPp
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPp
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPp
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPp
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPp
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPp
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPp
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPp
Thread-0 write Q
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQ
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQ
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQ
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQ
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQ
Thread-1 write q
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQq
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQq
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQq
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQq
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQq
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQq
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQq
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQq
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQq
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQq
Thread-0 write R
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqR
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqR
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqR
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqR
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqR
Thread-1 write r
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRr
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRr
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRr
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRr
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRr
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRr
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRr
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRr
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRr
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRr
Thread-0 write S
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrS
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrS
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrS
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrS
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrS
Thread-1 write s
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSs
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSs
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSs
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSs
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSs
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSs
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSs
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSs
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSs
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSs
Thread-0 write T
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsT
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsT
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsT
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsT
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsT
Thread-1 write t
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTt
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTt
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTt
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTt
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTt
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTt
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTt
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTt
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTt
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTt
Thread-0 write U
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtU
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtU
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtU
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtU
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtU
Thread-1 write u
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUu
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUu
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUu
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUu
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUu
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUu
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUu
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUu
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUu
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUu
Thread-0 write V
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuV
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuV
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuV
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuV
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuV
Thread-1 write v
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVv
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVv
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVv
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVv
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVv
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVv
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVv
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVv
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVv
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVv
Thread-0 write W
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvW
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvW
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvW
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvW
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvW
Thread-1 write w
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWw
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWw
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWw
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWw
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWw
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWw
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWw
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWw
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWw
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWw
Thread-0 write X
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwX
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwX
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwX
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwX
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwX
Thread-1 write x
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXx
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXx
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXx
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXx
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXx
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXx
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXx
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXx
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXx
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXx
Thread-0 write Y
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxY
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxY
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxY
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxY
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxY
Thread-1 write y
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYy
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYy
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYy
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYy
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYy
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYy
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYy
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYy
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYy
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYy
Thread-0 write Z
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYyZ
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYyZ
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYyZ
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYyZ
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYyZ
Thread-1 write z
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYyZz
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYyZz
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYyZz
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYyZz
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYyZz
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYyZz
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYyZz
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYyZz
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYyZz
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYyZz
Thread-3 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYyZz
Thread-7 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYyZz
Thread-5 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYyZz
Thread-4 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYyZz
Thread-6 read: Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmOoOoPpQqRrSsTtUuVvWwXxYyZz
```

通过运行结果,我们可以发现,读写操作是互不干扰的.每次写操作释放完锁,读操作都可以把之前写的数据全部读取出来.

---
# 打赏
>如果觉得我的文章写的还过得去的话,有钱就捧个钱场,没钱给我捧个人场(帮我点赞或推荐一下)
>![微信打赏](http://img.blog.csdn.net/20170508085654037?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast) 
>![支付宝打赏](http://img.blog.csdn.net/20170508085710334?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)