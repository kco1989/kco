package com.kco;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by pc on 2016/8/27.
 */
public class IndexFileRunnable implements Runnable {

    private ArrayBlockingQueue<File> queue ;
    private boolean stop;
    private IndexWriter indexWriter;
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
            int count = 0;
            while (!stop){
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
                        if (count >= 100){
                            indexWriter.commit();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally { 
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
