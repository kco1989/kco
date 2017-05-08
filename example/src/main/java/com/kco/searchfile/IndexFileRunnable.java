package com.kco.searchfile;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 用于建文件索引
 * Created by pc on 2016/8/27.
 */
public class IndexFileRunnable implements Runnable {

    public static final Logger LOGGER = LoggerFactory.getLogger(IndexFileRunnable.class);
    private ArrayBlockingQueue<File> queue;
    private boolean stop;
    private IndexWriter indexWriter;
    // 停止信号
    public void setStop() {
        this.stop = true;
    }

    public IndexFileRunnable(ArrayBlockingQueue<File> queue, String indexPath, Analyzer analyzer) throws IOException {
        this.queue = queue;
        this.stop = false;
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        this.indexWriter = new IndexWriter(FSDirectory.open(Paths.get(indexPath)),config);
    }

    @Override
    public void run() {
        try {
            LOGGER.debug("执行建立索引线程");
            int count = 0;
            // 如果程序没有停止信号 这一直运行
            while (!stop || (stop && !queue.isEmpty())){
                try {
                    File file = queue.poll();
                    if (file != null){
                        String name = file.getName();
                        int suffixIndex = file.getName().lastIndexOf(".");
                        String suffix = suffixIndex == -1 ? "" : name.substring(suffixIndex + 1);
                        Document document = new Document();
                        document.add(new TextField(Constant.FIELD_NAME,name, Field.Store.YES));
                        document.add(new TextField(Constant.FIELD_PATH, file.getAbsolutePath(), Field.Store.YES));
                        document.add(new StringField(Constant.FIELD_SUFFIX, suffix, Field.Store.YES));
                        indexWriter.addDocument(document);
                        count ++;
                        if (count % 100 == 0){
                            indexWriter.commit();
                            LOGGER.debug("提交索引");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            LOGGER.debug("索引个数:" + count);
        } finally {
            LOGGER.debug("关闭索引");
            colseIndexWrite();
        }

    }

    private void colseIndexWrite() {
        try {
            indexWriter.commit();
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
