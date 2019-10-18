package com.study.zeus.service;

import com.mongodb.WriteResult;
import com.study.zeus.entity.KlineDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class KlineServiceImpl implements KlineService {

    private static final Logger logger = LoggerFactory.getLogger(KlineServiceImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public WriteResult updateKline(KlineDO klineDO) {
        Query query = new Query(Criteria.where("id").is(klineDO.getId()));
        KlineDO result = mongoTemplate.findOne(query, KlineDO.class);
        if (null != result) {
            updateNew(query, klineDO);
        } else {
            klineDO.setCreateTime(new Date().getTime());
            mongoTemplate.insert(klineDO);
        }
        return null;
    }

    @Override
    public List<KlineDO> list(long from, long to, String symbol, String kType) {
        logger.info("分页加载K线");
        Query query = new Query();
        query.addCriteria(Criteria.where("kTime").gte(from).lte(to).and("symbol").is(symbol).and("kType").is(kType))
                .with(Sort.by("kTime").descending())
                .limit(200);
        List<KlineDO> list = mongoTemplate.find(query, KlineDO.class);
        return list;
    }

    @Override
    public List<KlineDO> queryFrom(long from, String symbol, String type) {
        Query query = new Query();
        query.addCriteria(Criteria.where("kTime").lt(from).and("symbol").is(symbol).and("kType").is(type)).with(Sort.by("kTime").descending()).limit(200);
        List<KlineDO> list = mongoTemplate.find(query, KlineDO.class);
        return list;
    }

    @Override
    public List<KlineDO> queryKline(String kType, String symbol) {
        Query query = new Query();
        query.addCriteria(Criteria.where("kType").is(kType).and("symbol").is(symbol))
                .with(Sort.by("kTime").descending())
                .limit(200);
        List<KlineDO> result = mongoTemplate.find(query, KlineDO.class);
        return result;
    }

    @Override
    public List<KlineDO> richQuery(String kType, String symbol, Date from, Date to) {
        return null;
    }


    /**
     * @param klineDO
     */
    private void updateNew(Query query, KlineDO klineDO) {
        Update update = new Update();
        update.set("amount", klineDO.getAmount());
        update.set("count", klineDO.getCount());
        update.set("open", klineDO.getOpen());
        update.set("close", klineDO.getClose());
        update.set("low", klineDO.getLow());
        update.set("high", klineDO.getHigh());
        update.set("vol", klineDO.getVol());
        update.set("id", klineDO.getId());
        update.set("updateTime", new Date().getTime());
        mongoTemplate.updateFirst(query, update, KlineDO.class);
    }
}
