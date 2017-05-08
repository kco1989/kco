package com.kco.thread.test6.demo1;

/**
 * Created by pc on 2016/10/29.
 */
public class MockFile {

    // 使用StringBuilder而不是使用StringBuffer
    // 是因为StringBuilder是线程不安全的,而StringBuffer是线程安全的
    StringBuilder fileContent;

    public MockFile() {
        this.fileContent = new StringBuilder();
    }

    public String readFile(){
        try {
            Thread.sleep(10);
            String content = fileContent.toString();
            Thread.sleep(10);
            return content;
        } catch (InterruptedException e) {
            return null;
        }
    }

    public void writeFile(char content){
        try {
            Thread.sleep(10);
            fileContent.append(content);
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
