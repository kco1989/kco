package com.kco.game.maze;

/**
 * 方向
 * Created by pc on 2017/3/2.
 */
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
