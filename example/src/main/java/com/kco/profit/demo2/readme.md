>本人邮箱: <kco1989@qq.com>
>欢迎转载,转载请注明网址 <http://blog.csdn.net/tianshi_kco>
>github: <https://github.com/kco1989/kco>
>代码已经全部托管[github](https://github.com/kco1989/maventest)有需要的同学自行下载


# 引言
在上一篇文章中[使用简单工厂编写不同的分润规则]()遗留着一个问题,那就是如果要新增分润规则,则需要修改原来的类.也就是代码没有完全解耦.
因此在这一篇中,我将分润规则的设计改为抽象工厂模式来编写.以解决上次遗留的问题.

# 改写示例

## 分润规则接口类
该类与上一篇是一样的.

```java
public interface ProfitRole {
    double getProfit(double money);
}
```

## 分润规则抽象工厂类

```java
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
```
> 该类主要提供一个统一的接口`ProfitRoleFactory.createProfitRole`创建分润规则实现类
> 每一个分润规则实现类对应一个分润规则工厂类,由分润规则工厂类来创建出分润规则对象
> 再由分润规则对象实现具体的分润细节

## 分润规则类型
上一篇使用到的枚举类型.但是java不支持动态增加枚举成员.所以如果这一篇还是使用枚举类型的话,则在增加新的分润规则时难免需要修改该枚举类型.
因此,这一篇不是枚举类型.

```java
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
    public static void registerProfitRole(String name, String profitRoleExpression,ProfitRoleFactory factory, String description){
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
```

> `ProfitType.profitTypeMap` 存放分润类型名称和分润类型的key-value值
> `ProfitType.registerProfitRole` 提供一个注册分润类型的借口
> `ProfitType static域` 默认增加上一篇提到的5种分润规则


## 分润规则工厂类和分润规则实现类

目前提供五种分润规则:
1. 每笔固定收益1元，则填写代理商收益1.00
2. 每笔收益率为0.1%则填写代理商收益0.1%
3. 每笔收益率为0.1%加上固定收益1元，则填写代理商收益0.1%+1.00
4. 每笔收益率为0.1%，封顶3元，保底1元则填写代理商收益1.00~0.1%~3.00
5. 梯度分润 例如 0.1%<10000<0.2%<20000<0.3%<30000<0.5% 
    - 少于10000 按照 0.1% 分润
    - 少于20000 按照 0.2% 分润
    - 少于30000 按照 0.3% 分润    
    - 多于30000 按照 0.5% 分润
    
分别对应于`分润规则工厂类` -> `分润规则实现类`:

1. `FixedIncomeRoleFactory` -> `FixedIncomeRole` 
2. `FixedRateRoleFactory` -> `FixedRateRole`
3. `FixedRateAndFixedIncomeRoleFactory` -> `FixedRateAndFixedIncomeRole`
4. `FixedRateAndUpperLimitRoleFactory` -> `FixedRateAndUpperLimitRole`
5. `GradientRateRoleFactory` -> `GradientRateRole`

## 实现新的分润规则
如果需要实现新的分润规则,则分别编写一个分润规则工厂类和分润规则实现类,使其实分别继承(/实现)`ProfitRoleFactory`和`ProfitRole`,然后再调用`ProfitType.registerProfitRole`注册一下新的分润规则就万事大吉了.不需要修改原有的代码.也就是实现了完全解耦.

## 测试类

```java
public class TestProfitRole2 {

    private static final List<Double> testDate = Arrays.asList(100.0,200.0,300.0,400.0,700.0,
            1000.0,2000.0,3000.0,7000.0,
            10000.0, 20000.0, 30000.0, 70000.0);

    @Test
    public void test(){
        String profitTypeInfo = ProfitType.getProfitTypeInfo();
        System.out.println(profitTypeInfo);

    }
    @Test
    public void testFixedIncome(){
        for (double data : testDate){
            ProfitRole fixedIncome = ProfitRoleFactory.createProfitRole("FIXED_INCOME", "1.00");
            double profit = fixedIncome.getProfit(data);
            Assert.assertEquals(1.00, profit, 0.00001);
        }
    }

    @Test
    public void testFixedRate(){
        for (double data : testDate){
            ProfitRole fixedRate = ProfitRoleFactory.createProfitRole("FIXED_RATE", "0.1%");
            double profit = fixedRate.getProfit(data);
            Assert.assertEquals(data * 0.1 * 0.01, profit, 0.00001);
        }
    }

    @Test
    public void testFixedRateAndFixedIncome(){
        for (double data : testDate){
            ProfitRole profitRole = ProfitRoleFactory.createProfitRole("FIXED_RATE_AND_FIXED_INCOME", "0.63%+3.00");
            double profit = profitRole.getProfit(data);
            Assert.assertEquals(data * 0.63 * 0.01 + 3.0, profit, 0.00001);
        }
    }

    @Test
    public void testFixedRateAndUpperLimit(){
        for (double data : testDate){
            ProfitRole profitRole = ProfitRoleFactory.createProfitRole("FIXED_RATE_AND_UPPER_LIMIT", "1.00~0.1%~3.00");
            double profit = profitRole.getProfit(data);
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
            ProfitRole profitRole = ProfitRoleFactory.createProfitRole("GRADIENT_RATE", "0.1%<1000<0.2%<5000<0.3%<15000<0.5%");
            double profit = profitRole.getProfit(data);
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

# 打赏
>如果觉得我的文章写的还过得去的话,有钱就捧个钱场,没钱给我捧个人场(帮我点赞或推荐一下)
>![微信打赏](http://img.blog.csdn.net/20170508085654037?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast) 
>![支付宝打赏](http://img.blog.csdn.net/20170508085710334?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)