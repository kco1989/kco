package com.kco.profit.demo1.role.impl;

import com.kco.profit.demo1.role.ProfitRole;

/**
 * 每笔固定收益和固定收益率
 * Created by Administrator on 2017/4/14.
 */
public class FixedRateAndFixedIncomeRole implements ProfitRole {

    private double fixedRate;
    private double fixedIncome;

    public FixedRateAndFixedIncomeRole(double fixedRate, double fixedIncome) {
        this.fixedRate = fixedRate * 0.01;
        this.fixedIncome = fixedIncome;
    }

    @Override
    public double getProfit(double money) {
        double profit = money * fixedRate + fixedIncome;
        if (money - profit < 0){
            throw new RuntimeException(String.format("本金(%s)比利润(%s)元还低", money, profit));
        }
        return profit;
    }

}
