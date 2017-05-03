package com.kco.profit.demo2.role.factory.impl;

import com.kco.profit.demo2.ProfitType;
import com.kco.profit.demo2.role.ProfitRole;
import com.kco.profit.demo2.role.factory.ProfitRoleFactory;
import com.kco.profit.demo2.role.impl.FixedIncomeRole;

import java.util.regex.Matcher;

/**
 * 每笔固定收益
 * Created by Administrator on 2017/4/14.
 */
public class FixedIncomeRoleFactory extends ProfitRoleFactory{

    @Override
    protected ProfitRole newProfitRole(ProfitType profitType, Matcher matcher, String expression) {
        return new FixedIncomeRole(Double.parseDouble(matcher.group(1)));
    }
}
