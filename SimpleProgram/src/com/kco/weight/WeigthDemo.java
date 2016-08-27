package com.kco.weight;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by pc on 2016/8/27.
 */
public class WeigthDemo {
    /*
     * 砝码问题：
     * 现有一组砝码，重量互不相等，分别为m1、m2……mn；他们可取的最大数量分别为x1、x2……xn
     * 现在要用这些砝码去称物体的重量，问能称出多少中不同的重量
     *
     * 输入：
     * int n：n表示有多少组重量不同的砝码，1<=n<=10
     * int[] weight：表示n组砝码的重量，1<=mi<=10
     * int[] nums：表示n组砝码的最大数量，1<=xi<=10
     *
     * 输出：
     * 只有一个数据，表示利用给定的砝码可以称出的不同的重量数；非法数据输出-1
     *
     * 注：
     * 称重重量包括0
     * 要对输入数据进行校验
     */
    public static int fama(int n, int[] weight, int[] nums) {
        if (!isRightInput(n,weight,nums)) {
            return -1;
        }
        //可以称的重量
        Set<Integer> weightSet = new HashSet<Integer>();
        int[] count = new int[nums.length];
        while(arrrayInc(count,nums,count.length - 1)){
            int weights = 0;
            for (int i = 0; i < count.length; i++) {
                weights += weight[i] * count[i];
            }
            weightSet.add(weights);
        }
        return weightSet.size() + 1;//加重量为0的
    }

    private static boolean arrrayInc(int[] count,int[] nums,int flag){

        if(flag < 0){
            return false;
        }
        count[flag] ++;
        if(count[flag] > nums[flag]){
            count[flag] = 0;
            flag --;
            return arrrayInc(count,nums,flag);
        }
        return true;
    }


    //校验输入参数
    private static boolean isRightInput(int n, int[] weight, int[] nums){
        //判断 n 的取值范围
        if(n > 10 || n <= 0){
            return false;
        }
        //判断空指针
        if(weight == null || nums == null){
            return false;
        }
        //判断数字大小不等于n
        if(weight.length != n || nums.length != n){
            return false;
        }
        Set<Integer> set = new HashSet<>();
        //判断数据元素取值范围
        for (int i = 0; i < nums.length; i++) {
            if(weight[i] > 10 || weight[i] <= 0 || nums[i] > 10 || nums[i] <= 0){
                return false;
            }
            set.add(weight[i]);
        }
        //判断砝码有是否有重复的
        if (set.size() != weight.length) {
            return false;
        }
        return true;
    }
}
