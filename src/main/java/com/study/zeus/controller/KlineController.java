package com.study.zeus.controller;

import com.study.zeus.entity.KlineDO;
import com.study.zeus.service.KlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/kline")
public class KlineController {

    @Autowired
    private KlineService service;

    /**
     * 简单查询
     *
     * @param kType  K线类型
     * @param symbol 交易对
     * @return
     */
    @GetMapping("/simple_query")
    public List<KlineDO> simpleQuery(String kType, String symbol) {
        List<KlineDO> list = service.queryKline(kType, symbol);
        return list;
    }


    /**
     * 丰富查询
     *
     * @param kType  K线类型
     * @param symbol 交易对
     * @param from   起始时间
     * @param to     结束时间
     * @return
     */
    @GetMapping("/rich_query")
    public List<KlineDO> richQuery(String kType, String symbol, Date from, Date to) {

        return null;
    }
}
