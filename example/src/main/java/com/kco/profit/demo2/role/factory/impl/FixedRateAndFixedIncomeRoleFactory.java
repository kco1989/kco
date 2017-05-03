package com.kco.profit.demo2.role.factory.impl;

import com.kco.profit.demo2.ProfitType;
import com.kco.profit.demo2.role.ProfitRole;
import com.kco.profit.demo2.role.factory.ProfitRoleFactory;
import com.kco.profit.demo2.role.impl.FixedRateAndFixedIncomeRole;

import java.util.regex.Matcher;

/**
 * 每笔固定收益和固定收益率
 * Created by Administrator on 2017/4/14.
 */
public class FixedRateAndFixedIncomeRoleFactory extends ProfitRoleFactory {

    @Override
    protected ProfitRole newProfitRole(ProfitType profitType, Matcher matcher, String expression) {
        return new FixedRateAndFixedIncomeRole(Double.parseDouble(matcher.group(1)),
                    Double.parseDouble(matcher.group(2)));
    }
}
