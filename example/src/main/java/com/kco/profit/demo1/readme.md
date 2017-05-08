>本人邮箱: <kco1989@qq.com>
>欢迎转载,转载请注明网址 <http://blog.csdn.net/tianshi_kco>
>github: <https://github.com/kco1989/kco>
>代码已经全部托管[github](https://github.com/kco1989/maventest)有需要的同学自行下载

# 引言
在工作项目中遇到以下一个问题.
公司有一款产品,遇到给不同代理商代理售卖.但是不同的代理商分润的方式各自不同.有以下几个中分润方式.

1. 每笔固定收益1元，则填写代理商收益1.00
2. 每笔收益率为0.1%则填写代理商收益0.1%
3. 每笔收益率为0.1%加上固定收益1元，则填写代理商收益0.1%+1.00
4. 每笔收益率为0.1%，封顶3元，保底1元则填写代理商收益1.00~0.1%~3.00
5. 梯度分润 例如 0.1%<10000<0.2%<20000<0.3%<30000<0.5%
    - 少于10000 按照 0.1% 分润
    - 少于20000 按照 0.2% 分润
    - 少于30000 按照 0.3% 分润
    - 多于30000 按照 0.5% 分润

然而发现公司的项目代码中竟然是硬编码的.强硬的解析是否,把所有规则需要使用的参数信息都集中在一个类中,不利于维护.

多说无益,让各位感受一下项目中的代码吧.
```java
public class AgentShareRule {
	private Integer profitType; // 分润类型
    private BigDecimal perFixIncome;	//第一,四种情况时用到的参数
    private BigDecimal perFixInrate;	//第二.三,四种情况时用到的参数
    private BigDecimal safeLine;		//第三种情况用到的参数
    private BigDecimal capping;			//第三种情况用到的参数
    private BigDecimal ladder1Rate;	//第五种情况用到的参数
    private BigDecimal ladder1Max;//第五种情况用到的参数
    private BigDecimal ladder2Rate;//第五种情况用到的参数
    private BigDecimal ladder2Max;//第五种情况用到的参数
    private BigDecimal ladder3Rate;//第五种情况用到的参数
    private BigDecimal ladder3Max;//第五种情况用到的参数
    private BigDecimal ladder4Rate;//第五种情况用到的参数
    private BigDecimal ladder4Max;//第五种情况用到的参数
    // 省略部分代码和getter,setter方法
}
```
如果分润规则改变了,或者增加了新的分润规则,这代码就不知道要怎么维护了.
因此,基于这种情况,我按照给定的规则改写了一下分润规则.

# 改写后需要达到的目标
1. 修改原有规则,不会影响到其他规则
2. 可以能很方便的新增新的分润规则


# 改写示例

> 由于该改写程序只是一个测试程序,所以涉及的money都是用double,实际应用中,要考虑精度,则需要改为BigDecimal

## 分润接口

```java
public interface ProfitRole {
    double getProfit(double money);
}
```

分润接口很简单,就是传入需要分润的金额,然后计算出利润返回

## 分润类型

```java
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
```

1. 分润类型是一个枚举类,构造器参数是一个分润表达式`pattern`,
2. 其中`ProfitType`包含三个正则表达式:
    - `number`      匹配实数,但不捕获
    - `realNumber`  匹配实数,并捕获该实数
    - `rateNumber`  匹配百分比,只捕获实数,不捕获百分号
3. 五个枚举值分别对应五种分润规则
    - `FIXED_INCOME` : 每笔固定收益1元，则填写代理商收益1.00
    - `FIXED_RATE` : 每笔收益率为0.1%则填写代理商收益0.1%
    - `FIXED_RATE_AND_FIXED_INCOME` : 每笔收益率为0.1%加上固定收益1元，则填写代理商收益0.1%+1.00
    - `FIXED_RATE_AND_UPPER_LIMIT` : 每笔收益率为0.1%，封顶3元，保底1元则填写代理商收益1.00~0.1%~3.00
    - `GRADIENT_RATE` : 梯度分润 例如 0.1%<10000<0.2%<20000<0.3%<30000<0.5%

## 分润实现类
目前因为有五种分润规则,每一个对应一个实现类,因此有五个分润实现类,与`ProfitType`的五种分润规则一一对应

- `FIXED_INCOME` : `FixedIncomeRole`
- `FIXED_RATE` : `FixedRateRole`
- `FIXED_RATE_AND_FIXED_INCOME` : `FixedRateAndFixedIncomeRole`
- `FIXED_RATE_AND_UPPER_LIMIT` : `FixedRateAndUpperLimitRole`
- `GRADIENT_RATE` : `GradientRateRole`

> 分润实现类比较简单,这里就不贴代码了,有需要的上我[github](https://github.com/kco1989/maventest/tree/master/src/main/java/com/kco/profit下载

## 分润工厂类
最后在创建一个工厂,根据不同的`ProfitType`和分润表达式,创建不同的分润实现类

```java
public final class ProfitRoleFactory {

    public static ProfitRole parseProfitRole(ProfitType type, String expression){
        Matcher matcher = type.getPattern().matcher(expression);
        if (!matcher.matches()){
            throw new RuntimeException("分润表示时不符合" + type + "的规则.");
        }
        switch (type){
            case FIXED_INCOME:
                return new FixedIncomeRole(Double.parseDouble(matcher.group(1)));
            case FIXED_RATE:
                return new FixedRateRole(Double.parseDouble(matcher.group(1)));
            case FIXED_RATE_AND_UPPER_LIMIT:
                return new FixedRateAndUpperLimitRole(Double.parseDouble(matcher.group(1)),
                        Double.parseDouble(matcher.group(2)), Double.parseDouble(matcher.group(3)));
            case FIXED_RATE_AND_FIXED_INCOME:
                return new FixedRateAndFixedIncomeRole(Double.parseDouble(matcher.group(1)),
                        Double.parseDouble(matcher.group(2)));
            case GRADIENT_RATE:
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
            default: //never come here
                return null;
        }
    }

    public static double getProfit(double money, ProfitType type, String expression){
        ProfitRole profitRole = parseProfitRole(type, expression);
        if (profitRole != null){
            return profitRole.getProfit(money);
        }
        return 0;
    }
}
```

## 最后创建测试类测试

```java
public class TestProfitRole {

    private static final List<Double> testDate = Arrays.asList(100.0,200.0,300.0,400.0,700.0,
            1000.0,2000.0,3000.0,7000.0,
            10000.0, 20000.0, 30000.0, 70000.0);
    @Test
    public void testFixedIncome(){
        for (double data : testDate){
            double profit = ProfitRoleFactory.getProfit(data, ProfitType.FIXED_INCOME, "1.00");
            Assert.assertEquals(1.00, profit, 0.00001);
        }
    }

    @Test
    public void testFixedRate(){
        for (double data : testDate){
            double profit = ProfitRoleFactory.getProfit(data, ProfitType.FIXED_RATE, "0.1%");
            Assert.assertEquals(data * 0.1 * 0.01, profit, 0.00001);
        }
    }

    @Test
    public void testFixedRateAndFixedIncome(){
        for (double data : testDate){
            double profit = ProfitRoleFactory.getProfit(data, ProfitType.FIXED_RATE_AND_FIXED_INCOME, "0.63%+3.00");
            Assert.assertEquals(data * 0.63 * 0.01 + 3.0, profit, 0.00001);
        }
    }

    @Test
    public void testFixedRateAndUpperLimit(){
        for (double data : testDate){
            double profit = ProfitRoleFactory.getProfit(data, ProfitType.FIXED_RATE_AND_UPPER_LIMIT, "1.00~0.1%~3.00");
            double actual = data * 0.1 * 0.01;
            if (actual < 1.0){
                actual = 1.0;
            }
            if (actual > 3.0){
                actual = 3.0;
            }
            Assert.assertEquals(actual, profit, 0.00001);
        }
    }

    @Test
    public void testGradientRate(){
        for (double data : testDate){
            double profit = ProfitRoleFactory.getProfit(data, ProfitType.GRADIENT_RATE, "0.1%<1000<0.2%<5000<0.3%<15000<0.5%");
            if (data < 1000){
                Assert.assertEquals(data * 0.01 * 0.1, profit, 0.00001);
            }else if (data < 5000){
                Assert.assertEquals(data * 0.01 * 0.2, profit, 0.00001);
            }else if(data < 15000){
                Assert.assertEquals(data * 0.01 * 0.3, profit, 0.00001);
            }else{
                Assert.assertEquals(data * 0.01 * 0.5, profit, 0.00001);
            }
        }
    }
}
```

# 后记
现在回顾一下我们的目标,我们的目标是:
1. 修改原有规则,不会影响到其他规则
2. 可以能很方便的新增新的分润规则

目标1是达成了,但是目标2却有点差强人意.比如要新增一个分润规则的话,除了新增分润实现类外,还需要同时改`ProfitType`和`ProfitRoleFactory`类,没有达到完全解耦.但相对之前的代码还有有改进的.

这个问题先留个各位练习,后续有时间我再对该程序进行改进.

# 打赏
>如果觉得我的文章写的还过得去的话,有钱就捧个钱场,没钱给我捧个人场(帮我点赞或推荐一下)
>![微信打赏](http://img.blog.csdn.net/20170508085654037?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast) 
>![支付宝打赏](http://img.blog.csdn.net/20170508085710334?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)