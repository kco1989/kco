package com.kco.primarykeybuild.service;


import com.kco.primarykeybuild.Enum.SequenceNumberEnum;

/**
 * com.kco.service
 * Created by swlv on 2016/10/25.
 */
public interface SequenceNumberService {
    /**
     * 生成一个主键
     * @param sequenceNumberEnum 主键生成类型
     * @return 返回一个生成的主键
     */
    String newSequenceNumber(SequenceNumberEnum sequenceNumberEnum);

}
