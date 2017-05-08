>本人邮箱: <kco1989@qq.com>
>欢迎转载,转载请注明网址 <http://blog.csdn.net/tianshi_kco>
>github: <https://github.com/kco1989/kco>
>代码已经全部托管[github](https://github.com/kco1989/maventest/tree/master/src/main/java/com/kco/game/maze)有需要的同学自行下载


# 引言
迷宫对于大家都不会陌生.那么迷宫是怎么生成,已经迷宫要如何找到正确的路径呢?用java代码又怎么实现?带着这些问题.我们继续往下看.

# 并查集(find-union)

## 朋友圈
有一种算法就做并查集(find-union).什么意思呢?比如现在有零散的`甲乙丙丁戊`五个人.他们之间刚开始互相不认识.用代码解释就是`find(person1, person2) == false`,之后在某一次聚合中,`甲`认识了`乙`,`乙`认识了`丙`,`丙`认识了`戊`和`丁`等等,那么就可以用代码解释如下.

```java
    union("甲", "乙");
    union("乙", "丙");
    union("丙", "戊");
    union("丙", "丁");
```

那么这个时候`甲`就可以通过`乙`认识到了`丙`在通过`丙`认识到`庚`和`丁`.
这是`甲乙丙丁戊`通过朋友或者朋友的朋友最终都互相认识.换另一种说法就是如果`甲`要认识`丁`,那么`甲`必须先通过`乙`认识`丙`,再通过`丙`去认识`丁`就行了.

## 迷宫
对于迷宫生成,其实更上面*朋友圈*有点类似.生成步骤如下
1. 首先,先创建一个n*m的二维密室.每个单元格四方都是墙.
2. 随机选择密室中的一个单元格.之后在随机选择一面要打通的墙壁.
3. 判断要打通的墙壁是否为边界.是则返回步骤3,不是则继续
4. 判断步骤的单元个和要打通的墙壁的对面是否联通(用find算法)
5. 如果两个单元格不联通,则把步骤2选中的墙壁打通(用union算法).否则返回步骤2.
6. 判断迷宫起点和终点是否已经联通,是则迷宫生成结束,否则返回步骤2.


> 对于`并查集(find-union)`的实现,网络上有不少实现,这里不展开说明了.

# 实现代码
## 墙壁
```java
public enum Wall {
    /**
     * 墙壁
     */
    BLOCK,
    /**
     * 通道
     */
    ACCESS
}
```

> 墙壁,是一个枚举变量,用于判断当前的墙壁是否可以通行.

## 单元格

```java
public class Box {
    private Wall[] walls;

    public Box(){
        walls = new Wall[4];
        for (int i = 0; i < walls.length; i ++){
            walls[i] = Wall.BLOCK;
        }
    }

    public void set(Position position, Wall wall){
        walls[position.getIndex()] = wall;
    }

    public Wall get(Position position){
        return walls[position.getIndex()];
    }
}
```

> 一个单元格(小房间)是有四面墙组成的,刚开始四面墙都是墙壁.

## 方向

```java
public enum Position {
    TOP(0), RIGHT(1), DOWN(2), LEFT(3);

    public int index;

    Position(int index) {
        this.index = index;
    }

    public static Position indexOf(int index){
        int pos = index % Position.values().length;
        switch (pos){
            case 0:
                return TOP;
            case 1:
                return RIGHT;
            case 2:
                return DOWN;
            case 3:
            default:
                return LEFT;
        }
    }

    public Position anotherSide(){
        switch (this){
            case TOP:
                return DOWN;
            case RIGHT:
                return LEFT;
            case DOWN:
                return TOP;
            case LEFT:
            default:
                return RIGHT;
        }
    }

    public int getIndex() {
        return index;
    }
}
```

> 方向,枚举类,用于判断单元格的那一面墙壁需要打通.

## 迷宫生成类
这里我使用`find-union`的两种实现方式实现,
1. 一种是用数组的方式`MazeArrayBuilder`
2. 一种使用map的方式实现`MazeMapBuilder`
所以我把迷宫生成的一些共同方法和属性抽取出现,编写了一个抽象类`AbstractMazeBuilder`.然后再在`MazeArrayBuilder`或`MazeMapBuilder`做具体的实现.
现在我们来看看`AbstractMazeBuilder`这个类

```java
public abstract class AbstractMazeBuilder {
    /**
     * 迷宫行列最大值
     */
    public static final int MAX_ROW_LINE = 200;
    /**
     * 行
     */
    protected int row;
    /**
     * 列
     */
    protected int line;
    /**
     * 迷宫格子集合,每个格子有四面墙
     */
    protected Box[][] boxes;
    /**
     * 求解迷宫中间变量
     */
    protected int[][] solverPath;
    /**
     * 迷宫时候已经算出最佳路径
     */
    protected boolean isSolver;
    /**
     * 使用贪婪算法,算出最佳路径集合
     */
    protected List<MazePoint> bestPath;
    protected Random random;

    public AbstractMazeBuilder(int row, int line){
        if (row < 3 || row > MAX_ROW_LINE || line < 3 || line > MAX_ROW_LINE){
            throw new IllegalArgumentException("row/line 必须大于3,小于" + MAX_ROW_LINE);
        }
        this.row = row;
        this.line = line;

        isSolver = false;
        boxes = new Box[row][line];
        solverPath = new int[row][line];
        bestPath = new ArrayList<MazePoint>();
        random = new Random();

        for (int i = 0; i < row; i ++){
            for (int j = 0; j < line; j ++){
                boxes[i][j] = new Box();
                solverPath[i][j] = Integer.MAX_VALUE;
            }
        }
    }

    /**
     * 查询与point点联通的最大格子的值
     * @param point point
     * @return 查询与point点联通的最大格子的值
     */
    protected abstract int find(MazePoint point);

    /**
     * 联通point1和point2点
     * @param point1 point1
     * @param point2 point2
     */
    protected abstract void union(MazePoint point1, MazePoint point2);

    /**
     * 判断时候已经生成迷宫路径
     * @return 判断时候已经生成迷宫路径
     */
    protected abstract boolean hasPath();

    /**
     * 生成迷宫
     */
    public void makeMaze(){

        while (hasPath()){
            // 生成 当前点, 当前点联通的方向, 当前点联通的方向对应的点
            ThreeTuple<MazePoint, Position, MazePoint> tuple = findNextPoint();
            if (tuple == null){
                continue;
            }
            union(tuple.one, tuple.three);
            breakWall(tuple.one, tuple.two);
            breakWall(tuple.three, tuple.two.anotherSide());
        }
        breakWall(new MazePoint(0,0), Position.LEFT);
        breakWall(new MazePoint(row - 1, line - 1), Position.RIGHT);
    }

    /**
     * 生成 当前点, 当前点联通的方向, 当前点联通的方向对应的点
     * @return
     * ThreeTuple.one 当前点
     * ThreeTuple.two 当前点联通的方向
     * ThreeTuple.three 当前点联通的方向对应的点
     */
    private ThreeTuple<MazePoint, Position, MazePoint> findNextPoint() {
        MazePoint currentPoint = new MazePoint(random.nextInt(row), random.nextInt(line));
        Position position = Position.indexOf(random.nextInt(Position.values().length));
        MazePoint nextPoint = findNext(currentPoint, position);
        if (nextPoint == null || find(currentPoint) == find(nextPoint)){
            return null;
        }
        return new ThreeTuple<MazePoint, Position, MazePoint>(currentPoint, position, nextPoint);
    }

    /**
     * 打通墙
     * @param point   当前点
     * @param position 当前点的方向
     */
    private void breakWall(MazePoint point, Position position) {
        boxes[point.x][point.y].set(position, Wall.ACCESS);
    }

    /**
     * 通过当前点以及对应当前点的方向找到下一个点
     * @param currentPoint 当前点
     * @param position 方向
     * @return 下个点,若该点在迷宫内,这返回,否则返回null
     */
    private MazePoint findNext(MazePoint currentPoint, Position position) {
        MazePoint nextPoint;
        switch (position){
            case TOP:
                nextPoint = new MazePoint(currentPoint.x - 1, currentPoint.y);
                break;
            case RIGHT:
                nextPoint = new MazePoint(currentPoint.x, currentPoint.y + 1);
                break;
            case DOWN:
                nextPoint = new MazePoint(currentPoint.x + 1, currentPoint.y);
                break;
            case LEFT:
            default:
                nextPoint = new MazePoint(currentPoint.x, currentPoint.y - 1);
                break;
        }
        if (nextPoint.x < 0 || nextPoint.x >= row || nextPoint.y < 0 || nextPoint.y >= line){
            return null;
        }
        return nextPoint;
    }

    public Box getBoxes(int x, int y) {
        return boxes[x][y];
    }

    public int getRow() {
        return row;
    }

    public int getLine() {
        return line;
    }

    /**
     * 求解迷宫路径
     * @return 迷宫路径
     */
    public List<MazePoint> solvePath(){
        // 1 迷宫时候已经求解完成,是的话,则直接返回,不必再次计算
        if (isSolver){
            return bestPath;
        }
        // 2 否则计算迷宫最佳路径
        bestPath = new ArrayList<MazePoint>();
        solverPath(new MazePoint(0, 0), 0);
        addPath(new MazePoint(row - 1, line - 1));
        Collections.reverse(bestPath);
        isSolver = true;
        return bestPath;
    }

    /**
     * 从终点逆推,添加最佳路径
     * @param point 当前点
     */
    private void addPath(MazePoint point) {
        bestPath.add(point);
        // 遍历当前点的每个方向,如果该方向能联通,这步数跟当前点的步数相差1步,这添加改点,递归计算下去
        for (Position position :Position.values()){
            MazePoint next = findNext(point, position);
            if (next == null || getBoxes(point.x, point.y).get(position) == Wall.BLOCK){
                continue;
            }
            if (solverPath[point.x][point.y] - 1 == solverPath[next.x][next.y]){
                addPath(next);
                return;
            }
        }
    }

    /**
     * 递归求解迷宫最佳路径
     * @param point 当前点
     * @param count 从开始走到当前点所需要的步数
     */
    private void solverPath(MazePoint point, int count) {
        // 判断当前点的步数时候小于现在走到这个点的步数,
        // 如果当前点的步数比较小,则直接返回
        if (solverPath[point.x][point.y] <= count){
            return;
        }
        // 否则表示当前点,有更短的路径
        solverPath[point.x][point.y] = count;
        // 再遍历当前点的每个方向
        for (Position position : Position.values()){
            MazePoint next = findNext(point, position);
            // 如果下一个点不在迷宫内,或当前点对应的方向是一面墙壁,则跳过继续编写下一个方向
            if (next == null || getBoxes(point.x, point.y).get(position) == Wall.BLOCK){
                continue;
            }
            // 否则,步数加1, 递归计算
            solverPath(next, count + 1);
        }
    }

    public static class MazePoint{
        public final int x;
        public final int y;

        public MazePoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "MazePoint{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}
```

> 代码上有注释,理解起来还是比较容易.`MazeArrayBuilder`和`MazeMapBuilder`的实现就参考github了.
>> `AbstractMazeBuilder` 中还包括了迷宫的求解.

# 迷宫的求解
迷宫的求解,一般我会使用以下两种方法
1. 右手规则,从起点出发,遇到墙壁,则向右手边转,按照这个规则.一般是可以找到出口的.不过如果迷宫有闭环,则无法求解,而且解出来的路径也不是最短路径.
2. 迷宫最短路径算法.
	1. 从起点出发,计数count=0
	2. 遍历该点的任意方向,如果是墙壁,则忽略,不然count++,进入下一个联通的格子
	3. 判断当前格子的的count(默认值一般是比较大的数)是否比传入的参数大,是说明该格子是一条捷径,将当前各自的count=入参,继续第2步;否则,说明该点已经被探索过且不是一条捷径,忽略
	4. 如果反复,暴力遍历所有单元格,即可以求出最短路径
	5. 遍历完之后,从出口开始找,此时出口的数字,表示从入口走到出口需要的最小步数.依次减1,找到下一个格子,直到找打入口.则最短路径就生成了.

# 打赏
>如果觉得我的文章写的还过得去的话,有钱就捧个钱场,没钱给我捧个人场(帮我点赞或推荐一下)
>![微信打赏](http://img.blog.csdn.net/20170508085654037?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast) 
>![支付宝打赏](http://img.blog.csdn.net/20170508085710334?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbnNoaV9rY28=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)	

