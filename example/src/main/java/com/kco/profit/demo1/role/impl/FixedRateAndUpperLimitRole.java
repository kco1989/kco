package com.kco.profit.demo1.role.impl;

import com.kco.profit.demo1.role.ProfitRole;

/**
 * 每笔固定收益率,设置保底和封顶
 * Created by Administrator on 2017/4/14.
 */
public class FixedRateAndUpperLimitRole implements ProfitRole {


    private double lowerIncome;
    private double fixedRate;
    private double upperIncome;

    public FixedRateAndUpperLimitRole(double lowerIncome, double fixedRate, double upperIncome) {
        this.lowerIncome = lowerIncome;
        this.fixedRate = fixedRate * 0.01;
        this.upperIncome = upperIncome;
    }

    @Override
    public double getProfit(double money) {
        double profit = money * fixedRate;
        if (profit < lowerIncome){
            profit = lowerIncome;
        }
        if (profit > upperIncome){
            profit = upperIncome;
        }
        if (money - profit < 0){
            throw new RuntimeException(String.format("本金(%s)比利润(%s)元还低", money, profit));
        }
        return profit;
    }
}
