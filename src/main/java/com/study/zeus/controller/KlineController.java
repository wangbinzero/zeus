package com.study.zeus.controller;

import com.study.zeus.entity.KlineDO;
import com.study.zeus.service.KlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class KlineController {

    @Autowired
    private KlineService service;

    @GetMapping("/kline")
    public List<KlineDO> kline(String kType,String symbol){
        List<KlineDO> list = service.queryKline(kType,symbol);
        return list;
    }
}
