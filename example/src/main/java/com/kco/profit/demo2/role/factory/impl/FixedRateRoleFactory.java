package com.kco.profit.demo2.role.factory.impl;

import com.kco.profit.demo2.ProfitType;
import com.kco.profit.demo2.role.ProfitRole;
import com.kco.profit.demo2.role.factory.ProfitRoleFactory;
import com.kco.profit.demo2.role.impl.FixedRateRole;

import java.util.regex.Matcher;

/**
 * 每笔固定收益
 * Created by Administrator on 2017/4/14.
 */
public class FixedRateRoleFactory extends ProfitRoleFactory {

    @Override
    protected ProfitRole newProfitRole(ProfitType profitType, Matcher matcher, String expression) {
        return new FixedRateRole(Double.parseDouble(matcher.group(1)));
    }
}
