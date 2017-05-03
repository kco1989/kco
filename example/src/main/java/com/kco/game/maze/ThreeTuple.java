package com.kco.game.maze;

/**
 * 三元组
 * Created by pc on 2017/3/2.
 */
public class ThreeTuple<T1, T2, T3> extends Tuple<T1, T2>{
    public final T3 three;

    public ThreeTuple(T1 one, T2 two, T3 three) {
        super(one, two);
        this.three = three;
    }

    @Override
    public String toString() {
        return "{"+one+","+ two + "," + three+"}";
    }
}
