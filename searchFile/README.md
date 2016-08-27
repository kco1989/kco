---
##### 本人邮箱: kco1989@qq.com
##### 欢迎转载,转载请注明网址 http://blog.csdn.net/tianshi_kco
##### github: https://github.com/kco1989/kco
---

---
# 概述
改程序是通过对系统的文件用lucene来建立索引,然后再通过制定的关键词和后缀进行搜索.

---

# 程序说明

## 程序概述
该程序使用`fork/join`模式进行对目标文件的搜索,并且以`生产者->消费者`的模式对收集的目标文件进行索引的建立,该程序相对比较简单一点,可以供初学者对`fork/join`和`生产者->消费者`有简单的认识和应用.

## 使用说明

- 首先运行com.kco.Test#testIndex 先对指定的文件夹建立索引
- 再运行com.kco.Test#testSearch 输入指定的关键字进行查询

## 程序结构
- src
  - main
    - java
      - com.kco
        - Constant.java           常量类
        - IndexFileRunnable.java  对文件建索引的Runnabel
        - SearchFileAction.java   查询需要建索引的文件Action
        - SearchFileUtils.java    查询文件的工具类
  - test
    - java
      - com.kco
        - Test.java               测试程序

- Constant.java 仅仅存放了程序用到的一些常量类
- SearchFileUtils.java 这个用来建立索引和查询的工具栏
  - 构造方法 `public SearchFileUtils(Analyzer analyzer, String indexPath, File searchBaseFile,String suffix) throws IOException`
    - Analyzer analyzer 用于指定分词器
    - String indexPath  索引的存放路径
    - File searchBaseFile 需要建立索引的目标文件夹
    - String suffix 需要建立索引的文件后缀名,如果该值为"\*",则对该目录的所有文件都进行索引
  - 建立索引方法 `public void indexFile() throws Exception{}`
    这里使用的java7的`ForkJoinPool`,实现的步骤为:
    1. 判断目标文件夹这个目录时候存在,不存在的直接返回.若存在,这继续.
    2. 遍历目标文件下的所有文件,依次判断
      2.1. 判断目标时候为隐藏文件,若是,这跳过
      2.2. 判断目标时候为文件,若否,则继续.如是,判断时候满足以下条件,满足这目标文件放到队列中
        2.2.1. 判断`suffix`时候为"\*"
        2.2.2. 如果`suffix`不是"\*",着判断目标文件时候和`suffix`一致
      2.3. 判断目标时候为文件夹,若是,则判断当前的线程池的个数术后超过50个,若是,这继续第1步,否则建立新的线程继续处理
    3. 启动线程`IndexFileRunnable`,一致去查询队列时候有值,若有,则获取目标文件,并对目标文件建立索引
    4. 等到`ForkJoinPool`线程池时候执行完成,则调用`IndexFileRunnable#setStop`停止建立索引的线程
  - 查询方法`public void search(String search,String suffix) throws IOException {}`,这个就相对简单一点了,
    - search 需要查询的文件名
    - suffix 查询文件名的后缀

# 程序github路径
[searchFile download](https://github.com/kco1989/kco/tree/master/searchFile)
> 最后声明: 因本人程序水平有限,程序中难免有bug,欢迎大家可以对程序bug或者又不懂的,可以与我进行讨论,kco1989@qq.com
