package com.kco.game.maze;

/**
 * 二元祖
 * Created by pc on 2017/3/2.
 */
public class Tuple<T1, T2> {
    public final T1 one;
    public final T2 two;

    public Tuple(T1 one, T2 two) {
        this.one = one;
        this.two = two;
    }
}
