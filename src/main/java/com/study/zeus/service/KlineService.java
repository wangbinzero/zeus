package com.study.zeus.service;

import com.mongodb.WriteResult;
import com.study.zeus.entity.KlineDO;

import java.util.Date;
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
     * 查询K线 从 from时间点开始向后推
     * @param from
     * @param symbol
     * @param type
     * @return
     */
    List<KlineDO> queryFrom(long from,String symbol,String type);

    /**
     * K线查询
     * @param kType
     * @return
     */
    List<KlineDO> queryKline(String kType, String symbol);

    /**
     * K线查询
     * @param kType
     * @param symbol
     * @param from
     * @param to
     * @return
     */
    List<KlineDO> richQuery(String kType, String symbol, Date from,Date to);

}
