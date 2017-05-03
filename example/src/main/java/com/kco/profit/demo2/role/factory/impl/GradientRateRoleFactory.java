package com.kco.profit.demo2.role.factory.impl;

import com.kco.profit.demo2.ProfitType;
import com.kco.profit.demo2.role.ProfitRole;
import com.kco.profit.demo2.role.factory.ProfitRoleFactory;
import com.kco.profit.demo2.role.impl.GradientRateRole;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/4/19.
 */
public class GradientRateRoleFactory extends ProfitRoleFactory {

    @Override
    protected ProfitRole newProfitRole(ProfitType profitType, Matcher matcher, String expression) {
        List<Double> rates = new ArrayList<>();
        List<Double> limits = new ArrayList<>();
        Pattern numberPattern = ProfitType.getNumberPattern();
        Matcher numberMatcher = numberPattern.matcher(expression);
        for (int i = 0;numberMatcher.find();i ++){
            if (i % 2 == 0){
                rates.add(Double.parseDouble(numberMatcher.group()));
            }else{
                limits.add(Double.parseDouble(numberMatcher.group()));
            }
        }
        return new GradientRateRole(rates, limits);
    }
}
