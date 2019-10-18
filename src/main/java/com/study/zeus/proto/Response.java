package com.study.zeus.proto;

import java.io.Serializable;

public class Response<T> implements Serializable {

    private static final long serialVersionUID = -7690140196887231575L;
    //时间戳
    private long ts;

    //数据
    private T data;

    //频道
    private String channel;

    //事件
    private String event;

    //消息
    private String message;

    //编码
    private int code;

    public Response(T data, String channel, String event) {
        this.data = data;
        this.ts = System.currentTimeMillis();
        this.message = "success";
        this.code = 0;
        this.channel = channel;
        this.event = event;
    }

    public Response(String channel, String event, String message, int code) {
        this.channel = channel;
        this.event = event;
        this.message = message;
        this.code = code;
        this.ts = System.currentTimeMillis();
    }

    public Response(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static <T> Response sucess(T data, String channel, String event) {
        return new Response(data, channel, event);
    }


    public static Response fail(String channel, String event, String message, int code) {
        return new Response(channel, event, message, code);
    }

    public static Response paramError(String message, int code) {
        return new Response(message, code);
    }

    public static Response illegaError(){
        return new Response("非法操作",10002);
    }


    @Override
    public String toString() {
        return "Response{" +
                "ts=" + ts +
                ", data=" + data +
                ", channel='" + channel + '\'' +
                ", event='" + event + '\'' +
                ", message='" + message + '\'' +
                ", code=" + code +
                '}';
    }
}
