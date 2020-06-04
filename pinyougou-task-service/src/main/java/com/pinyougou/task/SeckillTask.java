package com.pinyougou.task;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @program: pinyougou-parent
 * @description: ${description}
 * @author: Mr.Cherry
 * @create: 2019-11-19 18:08
 **/
@Component
public class SeckillTask {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Scheduled(cron = "0 * * * * ?")
    public void refreshSeckillGoods(){
        System.out.println("执行了秒杀商品增量更新任务调度  - "+new Date());

        Set seckillGoods = redisTemplate.boundHashOps("seckillGoods").keys();

        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        //审核通过的商品
        criteria.andStatusEqualTo("1");
        //库存数大于0的商品
        criteria.andStockCountGreaterThan(0);
        //开始日期 小于等于当前日期
        criteria.andStartTimeLessThanOrEqualTo(new Date());
        //结束日期 大于等于当前日期
        criteria.andEndTimeGreaterThanOrEqualTo(new Date());
        //排除缓存中已经存在的商品id集合
        if(seckillGoods.size() > 0){
            criteria.andIdNotIn(new ArrayList<Long>(seckillGoods));
        }
        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);
        for (TbSeckillGoods goods : seckillGoodsList) {
            redisTemplate.boundHashOps("seckillGoods").put(goods.getId().toString(), JSON.toJSONString(goods));
            System.out.println("增量更新秒杀商品id " + goods.getId());
        }
    }

    @Scheduled(cron = "* * * * * ?")
    public void removeSeckillGoods(){
        //查询出缓存中的数据 扫描每条记录 判断时间 如果当前时间超过了截止的时间 移除此记录
        List seckillGoods = redisTemplate.boundHashOps("seckillGoods").values();
        List<TbSeckillGoods> seckillGoods1 = JSON.parseArray(seckillGoods.toString(), TbSeckillGoods.class);

        for (TbSeckillGoods seckillGood : seckillGoods1) {
            if(seckillGood.getEndTime().getTime() < System.currentTimeMillis()){
                seckillGoodsMapper.updateByPrimaryKey(seckillGood);
                //清除缓存
                redisTemplate.boundHashOps("seckillGoods").delete(seckillGood.getId());
                System.out.println("秒杀商品 - " + seckillGood.getTitle() + "id ： " + seckillGood.getId() + " 已过期");
            }
        }


    }
}
