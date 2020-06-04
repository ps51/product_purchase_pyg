package com.pinyougou.seckill.service;
import java.util.List;

import com.entity.pojo.PageResult;
import com.entity.pojo.Result;
import com.pinyougou.pojo.TbSeckillOrder;


/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillOrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSeckillOrder> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbSeckillOrder seckillOrder);
	
	
	/**
	 * 修改
	 */
	public void update(TbSeckillOrder seckillOrder);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbSeckillOrder findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize);


	/**
	 * 抢购商品 订单保存
	 * @param seckillId
	 * @param userId
	 */
	public Result submitOrder(Long seckillId, String userId);


	/**
	 * 保存订单到数据库
	 * @param userId
	 * @param orderId
	 */
	public Result saveOrderFromRedisToDb(String userId,Long orderId);


	public void saveSpikeAddress(TbSeckillOrder seckillOrder);


	/**
	 * 超时操作
	 * @param userId
	 * @param orderId
	 */
	public void deleteOrderFromRedis(String userId,Long orderId);
}
