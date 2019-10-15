package com.study.zeus.entity;

import java.io.Serializable;

public class DetailDO implements Serializable {

    private static final long serialVersionUID = 5539611315871271310L;

    //成交量
    private float amount;

    //收盘价
    private float close;

    //CNY
    private float cny;

    //成交笔数
    private float count;

    //涨幅
    private float diff;

    //最高价
    private float high;

    //最低价
    private float low;

    //开盘价
    private float open;

    //交易对btcusdt
    private String pair;

    //涨幅百分比
    private float percent;

    //交易对 BTC/USDT
    private String symbol;

    //成交时间戳
    private long ts;

    //类型  detail
    private String type;

    //成交额
    private float vol;

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public float getCny() {
        return cny;
    }

    public void setCny(float cny) {
        this.cny = cny;
    }

    public float getCount() {
        return count;
    }

    public void setCount(float count) {
        this.count = count;
    }

    public float getDiff() {
        return diff;
    }

    public void setDiff(float diff) {
        this.diff = diff;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getLow() {
        return low;
    }

    public void setLow(float low) {
        this.low = low;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getVol() {
        return vol;
    }

    public void setVol(float vol) {
        this.vol = vol;
    }

    @Override
    public String toString() {
        return "DetailDO{" +
                "amount=" + amount +
                ", close=" + close +
                ", cny=" + cny +
                ", count=" + count +
                ", diff=" + diff +
                ", high=" + high +
                ", low=" + low +
                ", open=" + open +
                ", pair='" + pair + '\'' +
                ", percent=" + percent +
                ", symbol='" + symbol + '\'' +
                ", ts=" + ts +
                ", type='" + type + '\'' +
                ", vol=" + vol +
                '}';
    }
}
