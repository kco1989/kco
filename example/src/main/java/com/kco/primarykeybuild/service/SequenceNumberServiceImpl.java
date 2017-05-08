package com.kco.primarykeybuild.service;

import com.kco.primarykeybuild.Enum.SequenceNumberEnum;
import com.kco.primarykeybuild.bean.SequenceNumberBean;
import com.kco.primarykeybuild.dao.SequenceNumberDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * com.kco.service
 * Created by swlv on 2016/10/25.
 */
@Service
public class SequenceNumberServiceImpl implements SequenceNumberService{
    @Resource
    private SequenceNumberDao sequenceNumberDao;
    @Override
    public synchronized String newSequenceNumber(SequenceNumberEnum sequenceNumberEnum) {
        if (sequenceNumberEnum == null){
            return null;
        }

        SequenceNumberBean sequenceNumberBean = sequenceNumberDao.newSequenceNumber(sequenceNumberEnum.getSequenceNumberBean().getPrefix());
        if (sequenceNumberBean == null){
            sequenceNumberBean = sequenceNumberEnum.getSequenceNumberBean();
            sequenceNumberBean.setToday(sequenceNumberDao.getToday());
        }
        sequenceNumberDao.updateSequenceNumber(sequenceNumberBean);
        return String.format("%s%6s%08d",
                sequenceNumberBean.getPrefix(), sequenceNumberBean.getToday(), sequenceNumberBean.getCurrentNum());


    }

}
