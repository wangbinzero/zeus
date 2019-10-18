package com.study.zeus.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * mongoDB 定时任务
 */
@EnableScheduling
@Configuration
public class MongoTask {

    private static final Logger logger = LoggerFactory.getLogger(MongoTask.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 每天凌晨检索，扫描
     */
//    @Scheduled(cron = "0/20 * * * * ?")
//    private void clean() {
//        logger.info("mongoDB 清理任务开始:[{}]", DateUtil.format(LocalDateTime.now()));
//    }
}
