package com.kco.thread.test16.demo;

import java.util.Comparator;

/**
 * Created by pc on 2017/4/18.
 */
public class HumanComparator implements Comparator<Human> {
    @Override
    public int compare(Human o1, Human o2) {
        return o2.getManey() - o1.getManey();
    }
}