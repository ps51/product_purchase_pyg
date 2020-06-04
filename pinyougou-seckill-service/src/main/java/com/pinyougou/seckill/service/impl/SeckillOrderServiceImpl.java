package com.pinyougou.seckill.service.impl;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.entity.pojo.PageResult;
import com.entity.pojo.Result;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.pojo.TbSeckillOrderExample;
import com.pinyougou.pojo.TbSeckillOrderExample.Criteria;
import org.springframework.data.redis.core.RedisTemplate;
import util.IdWorker;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillOrder> findAll() {
		return seckillOrderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillOrder> page=   (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillOrder seckillOrder) {
		seckillOrderMapper.insert(seckillOrder);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillOrder seckillOrder){
		seckillOrderMapper.updateByPrimaryKey(seckillOrder);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillOrder findOne(Long id){
		return seckillOrderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillOrderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillOrderExample example=new TbSeckillOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillOrder!=null){			
						if(seckillOrder.getUserId()!=null && seckillOrder.getUserId().length()>0){
				criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
			}
			if(seckillOrder.getSellerId()!=null && seckillOrder.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
			}
			if(seckillOrder.getStatus()!=null && seckillOrder.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
			}
			if(seckillOrder.getReceiverAddress()!=null && seckillOrder.getReceiverAddress().length()>0){
				criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
			}
			if(seckillOrder.getReceiverMobile()!=null && seckillOrder.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
			}
			if(seckillOrder.getReceiver()!=null && seckillOrder.getReceiver().length()>0){
				criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
			}
			if(seckillOrder.getTransactionId()!=null && seckillOrder.getTransactionId().length()>0){
				criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
			}
	
		}
		
		Page<TbSeckillOrder> page= (Page<TbSeckillOrder>)seckillOrderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;



	private final String redisKey = "seckillGoods";
	@Override
	public Result submitOrder(Long seckillId, String userId) {
		//1.查询缓存中的商品
		TbSeckillGoods seckillGoods = JSON.parseObject((String)redisTemplate.boundHashOps(redisKey).get(seckillId.toString()),TbSeckillGoods.class);
		if(seckillGoods == null){
			throw new RuntimeException("商品不存在 - ");
		}
		if(seckillGoods.getStockCount() <= 0){
			throw new RuntimeException("商品已经被抢光了 - ");
		}

		//2.减少库存
		seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
		//存入缓存
		redisTemplate.boundHashOps(redisKey).put(seckillId.toString(),JSON.toJSONString(seckillGoods));
		if(seckillGoods.getStockCount() == 0){
			//更新数据库
			seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
			redisTemplate.boundHashOps(redisKey).delete(seckillId.toString());
			System.out.println("商品同步数据库 - ");
		}
		//3.存储秒杀订单 (不向数据库存，只向缓存中存储)
		TbSeckillOrder seckillOrder = new TbSeckillOrder();
		//保存（redis）订单
		IdWorker idWorker = new IdWorker();
		long orderId = idWorker.nextId();
		seckillOrder.setId(orderId);
		seckillOrder.setCreateTime(new Date());
		seckillOrder.setMoney(seckillGoods.getCostPrice());//秒杀价格
		seckillOrder.setSeckillId(seckillId);//商家id
		seckillOrder.setSellerId(seckillGoods.getSellerId());
		seckillOrder.setUserId(userId);//设置用户ID
		seckillOrder.setStatus("0");//状态

		redisTemplate.boundHashOps("seckillOrder").put(userId,JSON.toJSONString(seckillOrder));
		System.out.println("订单保存到redis - ");

		return new Result(true ,seckillOrder.getId().toString());

	}

	@Override
	public Result saveOrderFromRedisToDb(String userId, Long orderId) {

		//1.从缓存中提取订单数据
		TbSeckillOrder seckillOrder = JSON.parseObject((String) redisTemplate.boundHashOps("seckillOrder").get(userId), TbSeckillOrder.class);
		if(seckillOrder == null){
			throw  new RuntimeException("不存在订单 - ");
		}
		if(seckillOrder.getId().longValue() != orderId.longValue()){
			throw  new RuntimeException("订单号不符 - ");
		}
		//2.修改订单实体的属性
		seckillOrder.setPayTime(new Date());
		seckillOrder.setStatus("1");
		IdWorker idWorker = new IdWorker(1, 2);
		seckillOrder.setTransactionId(idWorker.nextId()+"");
		//3.将订单存入数据库
		seckillOrderMapper.insert(seckillOrder);
		//4.清除缓存订单
		redisTemplate.boundHashOps("seckillOrder").delete(userId);
		return new Result(true,seckillOrder.getId()+"");
	}

	@Override
	public void saveSpikeAddress(TbSeckillOrder seckillOrder) {
		TbSeckillOrderExample example = new TbSeckillOrderExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdEqualTo(seckillOrder.getId());
		seckillOrder.setId(null);
		seckillOrderMapper.updateByExampleSelective(seckillOrder,example);
	}



	@Override
	public void deleteOrderFromRedis(String userId, Long orderId) {
		//1.从缓存中提取订单数据
		TbSeckillOrder seckillOrder = JSON.parseObject((String) redisTemplate.boundHashOps("seckillOrder").get(userId), TbSeckillOrder.class);
		if(seckillOrder != null){
			//2.删除缓存中的订单
			redisTemplate.boundHashOps("seckillOrder").delete(userId);
			//3.回退库存
			TbSeckillGoods seckillGoods = JSON.parseObject((String)redisTemplate.boundHashOps(redisKey).get(seckillOrder.getSeckillId().toString()),TbSeckillGoods.class);
			if(seckillGoods != null){
				seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
				redisTemplate.boundHashOps(redisKey).put(seckillOrder.getSeckillId().toString(),JSON.toJSONString(seckillGoods));
			}else{
				TbSeckillGoods seckillGoods2 = seckillGoodsMapper.selectByPrimaryKey(seckillOrder.getSeckillId());
				seckillGoods2.setStockCount(1);
				redisTemplate.boundHashOps(redisKey).put(seckillOrder.getSeckillId().toString(),JSON.toJSONString(seckillGoods2));
			}
		}


	}

}
