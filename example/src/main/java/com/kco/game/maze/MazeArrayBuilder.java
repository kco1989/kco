package com.kco.game.maze;

/**
 *  "Union-Find" 算法的一种实现
 *  使用数组的方法
 * Created by pc on 2017/3/2.
 */
public class MazeArrayBuilder extends AbstractMazeBuilder {

    private static final int DIS_UNION = -1;
    private int[] unionAndFinds;

    public MazeArrayBuilder(int row, int line) {
        super(row, line);
        unionAndFinds = new int[row * line];
        for (int i = 0; i < unionAndFinds.length; i ++){
            unionAndFinds[i] = DIS_UNION;
        }
    }

    @Override
    protected int find(MazePoint point){
        int index = point.x * line + point.y;
        int result = index;
        while (unionAndFinds[result] != DIS_UNION){
            result = unionAndFinds[result];
        }
        return result;
    }

    @Override
    protected void union(MazePoint point1, MazePoint point2){
        if (find(point1) == find(point2)){
            return;
        }
        int result1 = find(point1);
        int result2 = find(point2);
        int max = Math.max(result1, result2);
        int min = Math.min(result1, result2);
        unionAndFinds[min] = max;
    }

    @Override
    protected boolean hasPath(){
        boolean firstAndLast = find(new MazePoint(0, 0)) != row * line - 1;
        if (firstAndLast){
            return true;
        }
        for (int i = 0; i < unionAndFinds.length - 1; i ++){
            if (unionAndFinds[i] == DIS_UNION){
                return true;
            }
        }
        return firstAndLast;
    }
}
