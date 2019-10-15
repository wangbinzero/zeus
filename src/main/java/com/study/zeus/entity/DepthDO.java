package com.study.zeus.entity;

import java.io.Serializable;

public class DepthDO implements Serializable {

    private static final long serialVersionUID = 7507080145174541575L;

    //交易对 btcusdt
    private String pair;

    //交易对 BTC/USDT
    private String symbol;

    //卖盘
    private float[][] bids;

    //买盘
    private float[][] asks;

    private long version;
    private long ts;

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public float[][] getBids() {
        return bids;
    }

    public void setBids(float[][] bids) {
        this.bids = bids;
    }

    public float[][] getAsks() {
        return asks;
    }

    public void setAsks(float[][] asks) {
        this.asks = asks;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }
}
