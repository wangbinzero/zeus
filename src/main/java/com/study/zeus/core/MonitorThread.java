package com.study.zeus.core;

/**
 * 线程监视器
 */
public class MonitorThread implements Runnable {

    private long currentTime = System.currentTimeMillis();
    private long timeoutInterval = 5000L;
    private AbstractWebsocketClient client;

    public MonitorThread(AbstractWebsocketClient client) {
        this.client = client;
    }

    public void updateTimeoutInterval() {
        this.timeoutInterval = System.currentTimeMillis();
    }

    @Override
    public void run() {
        if (System.currentTimeMillis() - currentTime > timeoutInterval) {
            if (!client.isAlive()) {
                client.reConnect();
            }
        }
    }
}
