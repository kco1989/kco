package com.kco.profit.demo1;

import java.util.regex.Pattern;

/**
 * 分润规则类型
 * Created by Administrator on 2017/4/14.
 */
public enum ProfitType {
    /**
     * 每笔固定收益1元，则填写代理商收益1.00;
     */
    FIXED_INCOME("^" + ProfitType.realNumber + "$"),
    /**
     * 每笔收益率为0.1%则填写代理商收益0.1%;
     */
    FIXED_RATE("^"+ ProfitType.rateNumber +"$"),
    /**
     * 每笔收益率为0.1%加上固定收益1元，则填写代理商收益0.1%+1.00;
     */
    FIXED_RATE_AND_FIXED_INCOME("^"+ ProfitType.rateNumber  + "\\+" + ProfitType.realNumber + "$"),
    /**
     * 每笔收益率为0.1%，封顶3元，保底1元则填写代理商收益1.00~0.1%~3.00;
     */
    FIXED_RATE_AND_UPPER_LIMIT("^"+ ProfitType.realNumber + "~" + ProfitType.rateNumber + "~" + ProfitType.realNumber  + "$"),
    /**
     * 梯度分润 例如 0.1%<10000<0.2%<20000<0.3%<30000<0.5%
     * 少于10000 按照 0.1% 分润
     * 少于20000 按照 0.2% 分润
     * 少于30000 按照 0.3% 分润
     * 多于30000 按照 0.5% 分润
     */
    GRADIENT_RATE("^"+ ProfitType.rateNumber+"(<"+ ProfitType.realNumber+"<"+ ProfitType.rateNumber+")+$");

    // 非捕捉匹配正实数
    private static final String number = "(?:(?:[1-9]+\\d*)|(?:\\d))(?:\\.\\d+)?";
    // 捕捉匹配正实数
    private static final String realNumber = "(" + number + ")";
    // 捕捉匹配百分比
    private static final String rateNumber = realNumber + "%";

    public static Pattern getNumberPattern(){
        return Pattern.compile(number);
    }
    private String pattern;
    ProfitType(String pattern) {
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return Pattern.compile(this.pattern);
    }

}
