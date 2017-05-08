package com.kco.searchfile;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * Created by pc on 2016/8/26.
 */
public class SearchFileUtils {

    private Analyzer analyzer;
    private String indexPath;
    private File searchBaseFile;
    private String suffix ;
    private ArrayBlockingQueue<File> queue;
    private IndexSearcher indexSearcher;

    public SearchFileUtils(Analyzer analyzer, String indexPath, File searchBaseFile,String suffix) throws IOException {
        this.analyzer = analyzer;
        this.indexPath = indexPath;
        this.searchBaseFile = searchBaseFile;
        this.queue = new ArrayBlockingQueue<>(100);
        this.suffix = suffix;

    }

    public void indexFile() throws Exception {
        IndexFileRunnable indexFileRunnable = new IndexFileRunnable(queue, indexPath, analyzer);
        new Thread(indexFileRunnable).start();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.invoke(new SearchFileAction(searchBaseFile, suffix, queue));
        long time1 = System.currentTimeMillis();
        forkJoinPool.awaitTermination(1, TimeUnit.MINUTES); //如果1分钟都没有扫描完,强制停止
        long time2 = System.currentTimeMillis();
        System.out.println(time2 - time1);
        forkJoinPool.shutdown();
        indexFileRunnable.setStop();
    }

    public void search(String search,String suffix) throws IOException {
        if (this.indexSearcher == null){
            this.indexSearcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(indexPath))));
        }
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        if (StringUtils.isNotBlank(search)){
            queryBuilder.add(new PhraseQuery.Builder().add(new Term(Constant.FIELD_NAME,search)).build(), BooleanClause.Occur.MUST);
        }
        if(StringUtils.isNotBlank(suffix)){
            queryBuilder.add(new TermQuery(new Term(Constant.FIELD_SUFFIX,suffix)), BooleanClause.Occur.MUST);
        }
        TopDocs topDocs = indexSearcher.search(queryBuilder.build(), 10);
        System.out.println("索引文件数量:" +indexSearcher.getIndexReader().maxDoc());
        System.out.println("搜索结果: " + topDocs.totalHits);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs){
            Document doc = indexSearcher.doc(scoreDoc.doc);
            System.out.println(doc.get(Constant.FIELD_PATH));
        }
    }

}
