package com.kco.game.maze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 使用 "Union-Find" 算法创建迷宫
 * Created by pc on 2017/3/2.
 */
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
     * 生成迷宫路径
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
        for (Position position : Position.values()){
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
