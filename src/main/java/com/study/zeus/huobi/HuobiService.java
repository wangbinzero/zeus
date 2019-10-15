package com.study.zeus.huobi;

import java.util.List;

public interface HuobiService {

    void onReceive(String message);

    List<String> channelCache();
}
