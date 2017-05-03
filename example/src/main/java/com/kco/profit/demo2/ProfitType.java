package com.kco.profit.demo2;

import com.kco.profit.demo2.role.factory.ProfitRoleFactory;
import com.kco.profit.demo2.role.factory.impl.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/4/19.
 */
public class ProfitType {
    // 非捕捉匹配正实数
    public static final String number = "(?:(?:[1-9]+\\d*)|(?:\\d))(?:\\.\\d+)?";
    // 捕捉匹配正实数
    public static final String realNumber = "(" + number + ")";
    // 捕捉匹配百分比
    public static final String rateNumber = realNumber + "%";

    // 分润规则map
    private static final Map<String, ProfitType> profitTypeMap = new HashMap<>();
    static {
        ProfitType.registerProfitRole("FIXED_RATE",
                "^"+ ProfitType.rateNumber +"$",
                new FixedRateRoleFactory(),
                "每笔收益率为0.1%则填写代理商收益0.1%;");

        ProfitType.registerProfitRole("FIXED_INCOME",
                "^" + ProfitType.realNumber + "$",
                new FixedIncomeRoleFactory(),
                "每笔固定收益1元，则填写代理商收益1.00");

        ProfitType.registerProfitRole("FIXED_RATE_AND_FIXED_INCOME",
                "^"+ ProfitType.rateNumber  + "\\+" + ProfitType.realNumber + "$",
                new FixedRateAndFixedIncomeRoleFactory(),
                "每笔收益率为0.1%加上固定收益1元，则填写代理商收益0.1%+1.00");

        ProfitType.registerProfitRole("FIXED_RATE_AND_UPPER_LIMIT",
                "^"+ ProfitType.realNumber + "~" + ProfitType.rateNumber + "~" + ProfitType.realNumber  + "$",
                new FixedRateAndUpperLimitRoleFactory(),
                "每笔收益率为0.1%，封顶3元，保底1元则填写代理商收益1.00~0.1%~3.00;");

        ProfitType.registerProfitRole("GRADIENT_RATE",
                "^"+ ProfitType.rateNumber+"(<"+ ProfitType.realNumber+"<"+ ProfitType.rateNumber+")+$",
                new GradientRateRoleFactory(),
                "梯度分润 例如 0.1%<10000<0.2%<20000<0.3%<30000<0.5%");
    }
    
    private String name;
    private String expression;
    private String description;
    private ProfitRoleFactory factory;

    public ProfitType(String name, String expression, ProfitRoleFactory factory, String description) {
        this.name = name;
        this.expression = expression;
        this.factory = factory;
        this.description = description;
    }

    public static Pattern getNumberPattern() {
        return Pattern.compile(number);
    }

    public String getName() {
        return name;
    }

    public Pattern getPattern(){
        return Pattern.compile(this.expression);
    }

    /**
     * 注册分润规则类型
     */
    public static void registerProfitRole(String name, String profitRoleExpression, ProfitRoleFactory factory, String description){
        if (profitTypeMap.containsKey(name)){
            throw new RuntimeException("该"+name+"分润规则已经存在");
        }
        profitTypeMap.put(name, new ProfitType(name, profitRoleExpression, factory, description));
    }

    public ProfitRoleFactory getFactory() {
        return factory;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据分润规则名字获取分润规则类型
     */
    public static ProfitType getProfitType(String name){
        return profitTypeMap.get(name);
    }

    public static String getProfitTypeInfo(){
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, ProfitType> entry : profitTypeMap.entrySet()){
            sb.append(entry.getKey() + " --> " + entry.getValue().getDescription() + "\n");
        }
        return sb.toString();
    }
}
