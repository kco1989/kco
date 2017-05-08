package com.kco.primarykeybuild.bean;

/**
 * 主键生成策略的bean
 * com.kco.bean
 * Created by swlv on 2016/10/25.
 */
public class SequenceNumberBean {

    private String prefix;
    private String name;
    private String today;
    private int minNum;
    private int currentNum;
    private int numLength;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public int getMinNum() {
        return minNum;
    }

    public void setMinNum(int minNum) {
        this.minNum = minNum;
    }

    public int getCurrentNum() {
        return currentNum;
    }

    public void setCurrentNum(int currentNum) {
        this.currentNum = currentNum;
    }

    public int getNumLength() {
        return numLength;
    }

    public void setNumLength(int numLength) {
        this.numLength = numLength;
    }

    public SequenceNumberBean() {
    }

    public SequenceNumberBean(String prefix, String name, int minNum, int currentNum, int numLength) {
        this.prefix = prefix;
        this.name = name;
        this.minNum = minNum;
        this.currentNum = currentNum;
        this.numLength = numLength;
    }

    @Override
    public String toString() {
        return "SequenceNumberBean{" +
                "prefix='" + prefix + '\'' +
                ", name='" + name + '\'' +
                ", today='" + today + '\'' +
                ", minNum=" + minNum +
                ", currentNum=" + currentNum +
                ", numLength=" + numLength +
                '}';
    }
}
