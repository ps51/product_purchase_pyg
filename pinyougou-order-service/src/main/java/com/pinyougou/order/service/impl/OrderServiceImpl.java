package com.pinyougou.order.service.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.entity.pojo.PageResult;
import com.pinyougou.mapper.*;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbOrderExample.Criteria;
import org.springframework.data.redis.core.RedisTemplate;
import util.IdWorker;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbOrder> page=   (Page<TbOrder>) orderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}


	@Autowired
	private RedisTemplate redisTemplate;

	private final String redisKey = "CARTLIST";

	@Autowired
	private TbOrderItemMapper orderItemMapper;

	@Autowired
	private IdWorker idWorker;


	@Autowired
	private TbPayLogMapper payLogMapper;
	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {
		//1.从redis中提取购物车列表
		List<Cart> cartList = JSON.parseArray((String)redisTemplate.boundHashOps(redisKey).get(order.getUserId()),Cart.class);

		List<String> orderIdList=new ArrayList();//订单ID集合
		double total_money=0;//总金额

		//2.循环购物车列表添加订单
		for (Cart cart : cartList) {
			TbOrder order1 = new TbOrder();
			//获取id
			long orderId = idWorker.nextId();
			order1.setOrderId(orderId);
			//支付类型
			order1.setPaymentType(order.getPaymentType());
			//状态 1 未付款
			order1.setStatus("1");
			order1.setCreateTime(new Date());
			order1.setUpdateTime(new Date());
			//用户id
			order1.setUserId(order.getUserId());
			//收货人地址
			order1.setReceiverAreaName(order.getReceiverAreaName());
			//收货人电话
			order1.setReceiverMobile(order.getReceiverMobile());
			//收货人
			order1.setReceiver(order.getReceiver());
			//订单来源
			order1.setSourceType(order.getSourceType());
			//商家id
			order1.setSellerId(cart.getSellerId());
			//合计数
			double money = 0;
			//循环购物车中每条明细记录
			for (TbOrderItem item : cart.getOrderItemList()) {
				//主键
				item.setId(idWorker.nextId());
				//订单编号
				item.setOrderId(orderId);
				//商家id
				item.setSellerId(cart.getSellerId());
				orderItemMapper.insert(item);
				money += item.getTotalFee().doubleValue();
			}
			//合计
			order1.setPayment(new BigDecimal(money));

			orderMapper.insert(order1);

			orderIdList.add(orderId+"");
			total_money+=money;
		}


		//添加支付日志
		if("1".equals(order.getPaymentType())){
			TbPayLog payLog=new TbPayLog();

			payLog.setOutTradeNo(idWorker.nextId()+"");//支付订单号
			payLog.setCreateTime(new Date());
			payLog.setUserId(order.getUserId());//用户ID
			payLog.setOrderList(orderIdList.toString().replace("[", "").replace("]", ""));//订单ID串
			payLog.setTotalFee( (long)( total_money*100)   );//金额（分）
			payLog.setTradeState("0");//交易状态
			payLog.setPayType("1");//微信
			payLogMapper.insert(payLog);

			//redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);//放入缓存
		}


		//3.清除redis中的购物车
		redisTemplate.boundHashOps(redisKey).delete(order.getUserId());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order){
		orderMapper.updateByPrimaryKey(order);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findOne(Long id){
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			orderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbOrderExample example=new TbOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(order!=null){			
						if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
				criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}
			if(order.getPostFee()!=null && order.getPostFee().length()>0){
				criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}
			if(order.getStatus()!=null && order.getStatus().length()>0){
				criteria.andStatusLike("%"+order.getStatus()+"%");
			}
			if(order.getShippingName()!=null && order.getShippingName().length()>0){
				criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}
			if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
				criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}
			if(order.getUserId()!=null && order.getUserId().length()>0){
				criteria.andUserIdLike("%"+order.getUserId()+"%");
			}
			if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
				criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}
			if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
				criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}
			if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
				criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}
			if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
				criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}
			if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}
			if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
				criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}
			if(order.getReceiver()!=null && order.getReceiver().length()>0){
				criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}
			if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
				criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}
			if(order.getSourceType()!=null && order.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}
			if(order.getSellerId()!=null && order.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}
	
		}
		
		Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}


	@Autowired
	private TbProvincesMapper provincesMapper;

	@Autowired
	private TbCitiesMapper citiesMapper;

	@Autowired
	private TbAreasMapper areasMapper;

	/**地址
	@Override
	public List address(String id,String index) {
		List<TbProvinces> result = new ArrayList<>();
		if(index.equals("1")){
			result = provincesMapper.selectByExample(null);
		}


		return result;
	}*/

		//地址
	 @Override
	 public List address(String id,String index) {
		 List<TbProvinces> result = new ArrayList<>();
		 if( id.equals("null") || index.equals("null") ){
			return provincesMapper.selectByExample(null);
		 }
		 if(index.equals("2")){
			 TbCitiesExample example = new TbCitiesExample();
			 TbCitiesExample.Criteria criteria = example.createCriteria();
			 criteria.andProvinceidEqualTo(id);
			 return citiesMapper.selectByExample(example);
		 }else{
			 TbAreasExample example = new TbAreasExample();
			 TbAreasExample.Criteria criteria = example.createCriteria();
			 criteria.andCityidEqualTo(id);
			 return areasMapper.selectByExample(example);
		 }
	 }
}
