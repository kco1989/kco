package com.kco.maze;

import java.util.Arrays;
import java.util.Stack;

/**
 * Created by pc on 2016/8/27.
 */
public class MazeDemo {
    //保存路径的栈
    private Stack<Point> stack = new Stack<Point>();
    private boolean findAWay = false;

    /*
      功能:从一个迷宫走出的最短路徑

      输入:
          一个N*M的数组,int[][] maze迷宫图作为输入，如
          {0, 1, 0, 0, 0},
          {0, 1, 0, 1, 0},
          {0, 0, 0, 0, 0},
          {0, 1, 1, 1, 0},
          {0, 0, 0, 1, 0}};

      输出:从左上角到右下角的最短路线：(0, 0)(1, 0)(2, 0)(2, 1)(2, 2)(2, 3)(2, 4)(3, 4)(4, 4)

    */
    public Stack<Point> go(int[][] maze)
    {
        Point out = new Point(maze.length - 1, maze[0].length - 1); //出口
        Point in = new Point(0, 0); //入口
        maze = getNewMaze(maze);
        out = new Point(maze.length - 2,maze[0].length - 2);
        in = new Point(1, 1);
        findWay(maze, in, out);
        stack = reverseStack(in);
        return stack;
    }
    public Stack<Point> reverseStack(Point in){
        Stack<Point> newStack = new Stack<Point>();
        newStack.add(new Point(in.x - 1,in. y - 1));
        for (int i = stack.size() - 1; i >= 0; i--) {
            newStack.add(stack.get(i));
        }

        return newStack;
    }
    public void findWay(int[][] maze,Point in,Point out){
        if(in.x == out.x && in.y == out.y){
            findAWay = true;
            return;
        }
        int[][] newMaze = copyNewMaze(maze);
        newMaze[in.x][in.y] = 1;
        if(newMaze[in.x][in.y + 1] == 0 && ! findAWay){
            Point newIn = new Point(in.x,in.y + 1);
            findWay(newMaze, newIn, out);
            if(findAWay){
                stack.push(new Point(newIn.x - 1,newIn.y - 1));
                return ;
            }
        }

        if(newMaze[in.x + 1][in.y] == 0 && ! findAWay){
            Point newIn = new Point(in.x + 1,in.y);
            findWay(newMaze, newIn, out);
            if(findAWay){
                stack.push(new Point(newIn.x - 1,newIn.y - 1));
                return ;
            }
        }

        if(newMaze[in.x - 1][in.y] == 0 && ! findAWay){
            Point newIn = new Point(in.x - 1,in.y);
            findWay(newMaze, newIn, out);
            if(findAWay){
                stack.push(new Point(newIn.x - 1,newIn.y - 1));
                return ;
            }
        }

        if(newMaze[in.x][in.y - 1] == 0 && ! findAWay){
            Point newIn = new Point(in.x,in.y - 1);
            findWay(newMaze, newIn, out);
            if(findAWay){
                stack.push(new Point(newIn.x - 1,newIn.y - 1));
                return ;
            }
        }

    }
    public int[][] copyNewMaze(int[][] maze){
        int n = maze.length;
        int m = maze[0].length;
        int[][] newMaze = new int[n][m];
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j <maze[i].length; j++) {
                newMaze[i][j] = maze[i][j];
            }
        }
        return newMaze;
    }
    public int[][] getNewMaze(int[][] maze){
        if(maze == null || maze.length == 0){
            return null;
        }
        int n = maze.length;
        int m = maze[0].length;
        int[][] newMaze = new int[n+2][m+2];

        Arrays.fill(newMaze[0], 0, m+2, 1);
        for (int i = 0; i < maze.length; i++) {
            newMaze[i + 1][0] = 1;
            for (int j = 0; j < maze[i].length; j++) {
                newMaze[i + 1][j + 1] = maze[i][j];
            }
            newMaze[i + 1][maze[i].length + 1] = 1;
        }
        Arrays.fill(newMaze[n+1], 0, m+2, 1);
        return newMaze;
    }
}
