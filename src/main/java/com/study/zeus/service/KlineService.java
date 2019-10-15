package com.study.zeus.service;

import com.mongodb.WriteResult;
import com.study.zeus.entity.KlineDO;

import java.util.List;

public interface KlineService {

    /**
     * 更新K线
     *
     * @return
     */
    WriteResult updateKline(KlineDO klineDO);

    /**
     * 查询K线列表
     *
     * @param from   开始时间
     * @param to     结束时间
     * @param symbol 交易对
     * @param type   类型
     * @return
     */
    List<KlineDO> list(long from, long to, String symbol, String type);

    /**
     * 测试K线查询
     * @param kType
     * @return
     */
    List<KlineDO> queryKline(String kType, String symbol);

}
