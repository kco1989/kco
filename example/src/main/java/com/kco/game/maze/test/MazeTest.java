package com.kco.game.maze.test;

import com.kco.game.maze.MazeArrayBuilder;

/**
 * Created by pc on 2017/3/2.
 */
public class MazeTest {
    public static void main(String[] args) {
        MazeArrayBuilder builder = new MazeArrayBuilder(5, 5);
        builder.makeMaze();
        System.out.println(builder);
    }
}
