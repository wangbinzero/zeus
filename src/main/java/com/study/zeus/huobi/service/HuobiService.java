package com.study.zeus.huobi.service;

import java.util.List;

public interface HuobiService {

    void onReceive(String message);

    List<String> channelCache();
}
