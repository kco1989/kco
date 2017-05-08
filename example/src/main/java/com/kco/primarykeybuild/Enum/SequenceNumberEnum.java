package com.kco.primarykeybuild.Enum;


import com.kco.primarykeybuild.bean.SequenceNumberBean;

/**
 * com.kco.Enum
 * Created by swlv on 2016/10/25.
 */
public enum SequenceNumberEnum {
    GD(new SequenceNumberBean("GD","工单主键生成策略", 1, 1, 8)),
    GDD(new SequenceNumberBean("GDD","工单主键生成策略", 1, 1, 8))
    ;

    private SequenceNumberBean sequenceNumberBean;
    SequenceNumberEnum(SequenceNumberBean sequenceNumberBean){
        this.sequenceNumberBean = sequenceNumberBean;
    }

    public SequenceNumberBean getSequenceNumberBean() {
        return sequenceNumberBean;
    }
}
