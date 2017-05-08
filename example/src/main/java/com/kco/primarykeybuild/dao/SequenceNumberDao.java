package com.kco.primarykeybuild.dao;

import com.kco.primarykeybuild.bean.SequenceNumberBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * com.kco.dao
 * Created by swlv on 2016/10/25.
 */
@Repository
public interface SequenceNumberDao {
    /**
     * 根据前缀生成一个序列号信息
     * @param prefix 前缀
     * @return 新的序列号信息
     */
    SequenceNumberBean newSequenceNumber(@Param("prefix") String prefix);

    /**
     * 将生成的序列号信息更新到数据库中
     * @param sequenceNumberBean 需要更新信息
     */
    void updateSequenceNumber(@Param("bean") SequenceNumberBean sequenceNumberBean);

    /**
     * 获取数据库当天日期
     * @return
     */
    String getToday();
}
