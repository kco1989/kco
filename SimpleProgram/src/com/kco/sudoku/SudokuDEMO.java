package com.kco.sudoku;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2016/8/27.
 */
public class SudokuDEMO {

    final static int MACH_RESULT = -1;  //多解
    final static int NONE_RESULT = 0;   //无解
    /*
    Description
             推理数独数字
    Input Param
             sdata  需要推理的数独数据
    Return Value
             推理成功的数组
             失败或其他异常返回null
    */
    public static  int[][] sudokuDataSolve(int[][] sdata)
    {
        int i =0,j = 0;
        while(!isGameOver(sdata)){

            if(sdata[i][j] == 0){
                int num = theValue(sdata,i,j);
                if(num == NONE_RESULT){
                    return null;
                }else if(num > 0){
                    sdata[i][j] = num;
                    i = 0;
                    j = 0;
                }
            }
            j ++;
            if(j == 9){
                j = 0;
                i ++;
                if(i == 9){
                    return null;
                }
            }

        }

        return sdata;
    }
    public static int theValue(int[][] sdate,int row,int line){
        List<Integer> maybeValue = new ArrayList<>();
        maybeValue.add(1);
        maybeValue.add(2);
        maybeValue.add(3);
        maybeValue.add(4);
        maybeValue.add(5);
        maybeValue.add(6);
        maybeValue.add(7);
        maybeValue.add(8);
        maybeValue.add(9);
        for (int i = 0; i < 9; i++) {
            if(sdate[row][i] != 0){
                maybeValue.remove(new Integer(sdate[row][i]));
            }
            if(sdate[i][line] != 0){
                maybeValue.remove(new Integer(sdate[i][line]));
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(sdate[row / 3 + i][line / 3 + j] != 0){
                    maybeValue.remove(new Integer(sdate[row / 3 + i][line / 3 + j]));
                }
            }
        }
        if (maybeValue.size() == 1) {
            return maybeValue.get(0);
        }else if (maybeValue.size() == 0) {
            return  NONE_RESULT;    //无解
        }else{
            return MACH_RESULT;
        }

    }
    public static boolean isGameOver(int[][] sdata){
        for (int i = 0; i < sdata.length; i++) {
            for (int j = 0; j < sdata[0].length; j++) {
                if(sdata[i][j] == 0){
                    return false;
                }
            }
        }
        return true;
    }

}
