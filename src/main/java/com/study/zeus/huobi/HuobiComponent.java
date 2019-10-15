package com.study.zeus.huobi;

import com.study.zeus.huobi.service.HuobiMainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class HuobiComponent {

    private static final Logger logger = LoggerFactory.getLogger(HuobiComponent.class);

    @Autowired
    private HuobiMainService service;

    @PostConstruct
    public void run() {
        service.run();
    }

    @Scheduled(cron = "0 0 */1 * * *")
    public void doSub() {
        try {
            service.refreshSubChannel();
            logger.info("重新订阅");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("刷新火币网交易缓存，尝试重新订阅 [{}]", e.getMessage());
        }
    }

}
