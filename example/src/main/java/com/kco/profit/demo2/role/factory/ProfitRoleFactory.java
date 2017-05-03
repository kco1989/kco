package com.kco.profit.demo2.role.factory;

import com.kco.profit.demo2.ProfitType;
import com.kco.profit.demo2.role.ProfitRole;

import java.util.regex.Matcher;

/**
 * 分润实现类抽象工厂类
 * Created by Administrator on 2017/4/19.
 */
public abstract class ProfitRoleFactory {

    public static ProfitRole createProfitRole(String profitTypeName, String expression){
        ProfitType profitType = ProfitType.getProfitType(profitTypeName);
        Matcher matcher = profitType.getPattern().matcher(expression);
        if (!matcher.matches()){
            throw new RuntimeException("分润表示时不符合" + profitType.getName() + "的规则.");
        }
        return profitType.getFactory().newProfitRole(profitType, matcher, expression);
    }

    protected abstract ProfitRole newProfitRole(ProfitType profitType, Matcher matcher, String expression);


}
