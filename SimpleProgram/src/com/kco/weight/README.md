---
##### 本人邮箱,kco1989@qq.com
##### 欢迎转载,转载请注明网址 http://blog.csdn.net/tianshi_kco
##### github: https://github.com/kco1989/kco
---

# 概述
这是一道华为内部OJ平台中的题,感觉挺有意思.

# 题目描述

>砝码问题：
现有一组砝码，重量互不相等，分别为m1、m2……mn；他们可取的最大数量分别为x1、x2……xn
现在要用这些砝码去称物体的重量，问能称出多少中不同的重量

>输入：
int n：n表示有多少组重量不同的砝码，1<=n<=10
int[] weight：表示n组砝码的重量，1<=mi<=10
int[] nums：表示n组砝码的最大数量，1<=xi<=10

>输出：
只有一个数据，表示利用给定的砝码可以称出的不同的重量数；非法数据输出-1

>注：
称重重量包括0
要对输入数据进行校验

# 解题思路

把这个问题当作不规则的N进制[^footer1] 来看的话,问题就变得很简单了.比如程序中用`int[] count = new int[nums.length];`来创建一个`nums.length`位数,`count`数组的每个int表示对应`weight`砝码重量的个数,每一位的权对应当前砝码的个数,即`nums`,这样就得打一个我们自定义的那个不规则的N进制了,然后初始化为0,进行递增+1操作,等到这个N进制溢出,则就把所有的组合都已经计算了一遍.然后再递增+1操作的过程中,把得到的重量放到`set`里面,这样的实现了有可能重复出现的重量值.最终达到解决题目


> com.kco.weight.WeigthDemo#arrrayInc 这个是递增+1的操作


> [github地址](https://github.com/kco1989/kco/tree/master/SimpleProgram)


[^footer1]: 参考一下[进制](http://baike.baidu.com/view/15954.htm)
