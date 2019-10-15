package com.study.zeus.proto;

import java.io.Serializable;

public class Request implements Serializable {

    private static final long serialVersionUID = -7575016000400510716L;

    private String event;
    private String[]channel;
    private String type;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String[] getChannel() {
        return channel;
    }

    public void setChannel(String[] channel) {
        this.channel = channel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
