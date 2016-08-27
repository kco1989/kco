package com.kco;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RecursiveAction;

/**
 * Created by pc on 2016/8/26.
 */
public class SearchFileAction extends RecursiveAction{

    private File baseFile;      //查找的基础路径
    private String suffix;      //查询的文件后缀,*号表示所有的文件类型
    private ArrayBlockingQueue<File> queue; //将找到的文件放到队列中

    public SearchFileAction(File baseFile, String suffix, ArrayBlockingQueue<File> queue) {
        this.baseFile = baseFile;
        this.suffix = suffix;
        this.queue = queue;
    }

    @Override
    protected void compute() {
        if (baseFile == null || !baseFile.exists() || !baseFile.isDirectory()){
            return;
        }
        try {
            search(baseFile);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void search(File baseFile) throws InterruptedException {

        File[] files = baseFile.listFiles();
        for (File file : files) {
            if (file.isHidden()){
                continue;
            }else if (file.isFile()){
                if ("*".equals(suffix)){
                    System.out.println(file);
                    queue.put(file);
                }else if(file.getName().toLowerCase().endsWith(suffix.toLowerCase())){
                    System.out.println(file);
                    queue.put(file);
                }
            }else if(file.isDirectory()){
                if (getQueuedTaskCount() < 50){
                    SearchFileAction searchFile = new SearchFileAction(file, suffix, queue);
                    searchFile.fork();
                }else{
                    search(file);
                }
            }
        }
    }
}
