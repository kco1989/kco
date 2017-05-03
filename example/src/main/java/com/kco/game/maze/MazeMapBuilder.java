package com.kco.game.maze;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * "Union-Find" 算法的一种实现
 * 使用map的方法
 * Created by pc on 2017/3/2.
 */
public class MazeMapBuilder extends AbstractMazeBuilder{
    private Map<Integer, Set<Integer>> unionAndFinds;

    public MazeMapBuilder(int row, int line){
        super(row, line);
        unionAndFinds = new HashMap<Integer, Set<Integer>>();
        for (int i = 0; i < row * line; i ++){
            Set<Integer> set = new HashSet<Integer>();
            set.add(i);
            unionAndFinds.put(i, set);
        }
    }

    @Override
    protected int find(MazePoint point){
        int index = point.x * line + point.y;
        Set<Integer> integers = unionAndFinds.get(index);
        if (integers != null){
            return index;
        }
        for (Map.Entry<Integer, Set<Integer>> entry : unionAndFinds.entrySet()){
            if (entry.getValue().contains(index)){
                return entry.getKey();
            }
        }
        //Never go here.
        return -1;
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
        Set<Integer> maxSet = unionAndFinds.get(max);
        Set<Integer> minSet = unionAndFinds.get(min);
        maxSet.addAll(minSet);
        unionAndFinds.remove(min);
    }

    @Override
    protected boolean hasPath(){
        return unionAndFinds.size() != 1;
    }
}
