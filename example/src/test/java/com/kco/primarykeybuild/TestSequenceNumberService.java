package com.kco.primarykeybuild;


import com.kco.primarykeybuild.Enum.SequenceNumberEnum;
import com.kco.primarykeybuild.service.SequenceNumberService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * com.cmbchina.base.service
 * Created by swlv on 2016/10/25.
 */

public class TestSequenceNumberService {

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("META-INF/spring/spring-base-jdbc.xml");
        SequenceNumberService sequenceNumberService = applicationContext.getBean(SequenceNumberService.class);
        Queue<String> queue = new ArrayBlockingQueue<String>(200);
        List<Thread> list = new ArrayList<>();
        for (int i = 0;i < 200; i ++){
            list.add(new Thread(()->{
                String key = sequenceNumberService.newSequenceNumber(SequenceNumberEnum.GDD);

                queue.add(key);
            }));
        }
        for (Thread thread : list){
            thread.start();
        }

        while (queue.size() != 200);
        System.out.println(queue);
    }

}
