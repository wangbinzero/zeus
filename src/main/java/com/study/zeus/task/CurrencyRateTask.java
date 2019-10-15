package com.study.zeus.task;

import com.alibaba.fastjson.JSONObject;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

import java.io.IOException;

@EnableScheduling
@Configuration
public class CurrencyRateTask {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyRateTask.class);
    private final String RATE = "coin:rate";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Scheduled(cron = "0/20 * * * * ?")
    private void task() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request
                .Builder()
                .url("https://www.okex.me/api/futures/v3/rate")
                .get()
                .build();

        final Call call = client.newCall(request);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    String result = response.body().string();
                    JSONObject json = JSONObject.parseObject(result);
                    if (!StringUtils.isEmpty(json)) {
                        String name = json.getString("instrument_id");
                        String rate = json.getString("rate");
                        if (!StringUtils.isEmpty(rate)) {
                            redisTemplate.opsForValue().set(RATE + name.toLowerCase(), rate);
                            logger.info("写入汇率: [{}] ---- [{}]",name.toLowerCase(),rate);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
