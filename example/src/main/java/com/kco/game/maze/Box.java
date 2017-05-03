package com.kco.game.maze;

/**
 * 迷宫的格子,每个格子有四面墙,默认都是不通的
 * Created by pc on 2017/3/2.
 */
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
