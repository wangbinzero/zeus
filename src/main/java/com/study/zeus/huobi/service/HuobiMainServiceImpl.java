package com.study.zeus.huobi.service;

import com.google.common.collect.Lists;
import com.study.zeus.common.Constant;
import com.study.zeus.huobi.HuobiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class HuobiMainServiceImpl implements HuobiMainService {

    @Value("${hb.ws.url}")
    private String url;

    @Autowired
    private HuobiService service;

    private HuobiClient client;
    private static List<String> channelCache = Lists.newLinkedList();

    @Override
    public void run() {
        List<String>channelList = service.channelCache();
        if(CollectionUtils.isEmpty(channelList)){
            return;
        }

        channelCache = channelList;
        firstSub(channelList);
    }

    @Override
    public void refreshSubChannel() {
        List<String> channelList = service.channelCache();
        if (CollectionUtils.isEmpty(channelList)) {
            return;
        }
        reSub(channelList, Constant.KLINE_SUB);
    }


    /**
     * 重新订阅  有问题
     * @param channelList
     * @param topicFormat
     */
    private void reSub(List<String> channelList, String topicFormat) {
        for (String sub : channelList) {
            if (!channelCache.contains(sub)) {
                client.addSub(formatChannel(topicFormat, sub));
            }
        }
        channelCache = channelList;
    }


    private void firstSub(List<String> channelList) {
        client = new HuobiClient(url, service);
        client.start();
        for (String channel : channelList) {
            client.addSub(formatChannel(Constant.MARKET_DETAIL_SUB, channel));
            client.addSub(formatChannel(Constant.MARKET_DEPTH_SUB, channel));
            client.addSub(formatChannel(Constant.MARKET_TRADE_SUB, channel));

            for (int i = 0; i < Constant.PERIOD.length; i++) {
                client.addSub(String.format(Constant.KLINE_SUB, channel, Constant.PERIOD[i]));
            }
        }
    }


    /**
     * 格式化订阅通道
     *
     * @param topic
     * @param channel
     * @return
     */
    private String formatChannel(String topic, String channel) {
        return String.format(topic, channel);
    }
}
