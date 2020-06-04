package com.pinyougou.seckill.controller;
import java.util.List;

import com.entity.pojo.PageResult;
import com.entity.pojo.Result;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillOrder;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {

	@Reference(timeout = 1000000)
	private SeckillOrderService seckillOrderService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbSeckillOrder> findAll(){			
		return seckillOrderService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){
		return seckillOrderService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param seckillOrder
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbSeckillOrder seckillOrder){
		try {
			seckillOrderService.add(seckillOrder);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param seckillOrder
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbSeckillOrder seckillOrder){
		try {
			seckillOrderService.update(seckillOrder);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbSeckillOrder seckillOrder, int page, int rows  ){
		return seckillOrderService.findPage(seckillOrder, page, rows);		
	}


	@RequestMapping("/submitOrder")
	public Result submitOrder(Long seckillId){
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		if("anonymousUser".equals(userId)){//如果未登录
			return new Result(false, "用户未登录");
		}
		try {
			Result result = seckillOrderService.submitOrder(seckillId, userId);
			return result;
		}catch (RuntimeException e) {
			e.printStackTrace();
			return new Result(false, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "提交失败");
		}

	}


	@RequestMapping("/saveOrderFromRedisToDb")
	public Result saveOrderFromRedisToDb(Long orderId) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		try {
			Result result = seckillOrderService.saveOrderFromRedisToDb(username, orderId);
			return result;
		}catch (RuntimeException e) {
			e.printStackTrace();
			return new Result(false, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "提交失败");
		}
	}

	@RequestMapping("/saveSpikeAddress")
	public Result saveSpikeAddress(@RequestBody TbSeckillOrder seckillOrder){
		try {
			seckillOrderService.saveSpikeAddress(seckillOrder);
			return new Result(true,"下单成功");
		}catch (Exception e){
			e.printStackTrace();
			return new Result(false,"操作失败");
		}
	}
	@RequestMapping("/timedelete")
	public Result timedelete(Long orderId){
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		try {
			seckillOrderService.deleteOrderFromRedis(name,orderId);
			System.out.println("删除成功");
			return new Result(true,"订单超时");
		}catch (Exception e){
			e.printStackTrace();
			return new Result(false,"订单失败");
		}
	}


}
