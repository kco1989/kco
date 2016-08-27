package com.kco;

import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.junit.BeforeClass;

import java.io.File;
import java.io.IOException;

/**
 * Created by pc on 2016/8/27.
 */
public class Test {

    private static SearchFileUtils searchFileUtils;

    @BeforeClass
    public static void init(){
        try {
            File basefile = new File("H:\\IT 猿");
            searchFileUtils = new SearchFileUtils(new CJKAnalyzer(),"E:/index",basefile,"*");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testIndex(){
        try {
            searchFileUtils.indexFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testSearch(){
        try {
            searchFileUtils.search("高清","pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
