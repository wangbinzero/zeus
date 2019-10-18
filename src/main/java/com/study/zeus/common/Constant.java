package com.study.zeus.common;

public interface Constant {

    /**
     * K线订阅
     */
    String KLINE_SUB = "market.%s.kline.%s";

    /**
     * 交易深度
     * <p>
     * step0  默认150档数据
     * step1  20档数据
     */
    String MARKET_DEPTH_SUB = "market.%s.depth.step1";

    /**
     * 交易行情
     */
    String MARKET_TRADE_SUB = "market.%s.trade.detail";

    /**
     * 详情
     */
    String MARKET_DETAIL_SUB = "market.%s.detail";

    /**
     * K线交易周期
     */
    String[] PERIOD = {"1min", "5min", "15min", "30min", "60min", "1day", "1mon", "1week", "1year"};


    String TICKER_KEY = "ticker:%s";

    interface Cmd {
        String DETAIL = "detail";
        String DEPTH = "depth";
        String KLINE = "kline";
        String ALL = "all";
    }
}
