package com.study.zeus.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "kline")
public class KlineDO implements Serializable {

    private static final long serialVersionUID = 474943427702494244L;

    @Id
    //主键 unix时间 + symbol
    private String id;

    //类型  kline
    private String type;

    //成交量
    private float amount;

    //成交比数
    private int count;

    //开盘价
    private float open;

    //收盘价
    private float close;

    //最低价
    private float low;

    //最高价
    private float high;

    //成交额 成交价*成交量
    private float vol;

    //交易对  btcusdt
    private String pair;

    //交易对  BTC/USDT
    private String symbol;

    //K线类型  1m  3m ...
    private String kType;

    //K线时间
    private int kTime;

    //K线写入时间
    private Long createTime;

    //K线更新时间
    private Long updateTime;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public float getLow() {
        return low;
    }

    public void setLow(float low) {
        this.low = low;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getVol() {
        return vol;
    }

    public void setVol(float vol) {
        this.vol = vol;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public String getkType() {
        return kType;
    }

    public void setkType(String kType) {
        this.kType = kType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getkTime() {
        return kTime;
    }

    public void setkTime(int kTime) {
        this.kTime = kTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
}
