package com.kco.thread.test16.demo;

/**
 * Created by pc on 2017/4/18.
 */
public class Human {
    private int maney;
    private String name;
    public Human(int maney, String name){
        this.maney = maney;
        this.name = name;
    }

    public int getManey() {
        return maney;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName() + "[存款:"+getManey()+"]";
    }
}
