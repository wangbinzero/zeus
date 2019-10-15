package com.study.zeus.server.service;

import com.study.zeus.server.ZeusWebSocketServer;
import com.study.zeus.service.KlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ZeusServiceImpl implements ZeusService {

    @Value("${zeus.server.port}")
    private int port;

    @Autowired
    private KlineService klineService;

    private ZeusWebSocketServer server;

    @Override
    public void run() {
        server = new ZeusWebSocketServer(klineService);
        server.run(port);
    }
}
