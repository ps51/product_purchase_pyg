package com.pinyougou.user.service.impl;
import java.util.List;

import com.entity.pojo.PageResult;
import com.pinyougou.mapper.TbAreasMapper;
import com.pinyougou.mapper.TbCitiesMapper;
import com.pinyougou.mapper.TbProvincesMapper;
import com.pinyougou.pojo.*;
import com.pinyougou.user.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbAddressMapper;
import com.pinyougou.pojo.TbAddressExample.Criteria;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service(timeout = 5000)
public class AddressServiceImpl implements AddressService {

	@Autowired
	private TbAddressMapper addressMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbAddress> findAll() {
		return addressMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbAddress> page=   (Page<TbAddress>) addressMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbAddress address) {
		if(address.getIsDefault() != null){
			updateIsDefault(address);
		}
		addressMapper.insert(address);		
	}



	private void updateIsDefault(TbAddress address){
		if(address.getIsDefault().equals("1")){
			TbAddressExample example = new TbAddressExample();
			Criteria criteria = example.createCriteria();
			criteria.andUserIdEqualTo(address.getUserId());
			List<TbAddress> addressList = addressMapper.selectByExample(example);
			for (TbAddress tbAddress : addressList) {
				TbAddressExample example1 = new TbAddressExample();
				Criteria criteria1 = example1.createCriteria();
				criteria1.andIdEqualTo(tbAddress.getId());
				tbAddress.setIsDefault("0");
				tbAddress.setId(null);
				addressMapper.updateByExampleSelective(tbAddress,example1);
			}
		}
	}
	
	/**
	 * 修改
	 */
	@Override
	public void update(TbAddress address){
		if(address.getIsDefault() != null){
			updateIsDefault(address);
		}
		addressMapper.updateByPrimaryKey(address);

	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbAddress findOne(Long id){
		return addressMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long id) {
		addressMapper.deleteByPrimaryKey(id);
	}
	
	
		@Override
	public PageResult findPage(TbAddress address, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbAddressExample example=new TbAddressExample();
		Criteria criteria = example.createCriteria();
		
		if(address!=null){			
						if(address.getUserId()!=null && address.getUserId().length()>0){
				criteria.andUserIdLike("%"+address.getUserId()+"%");
			}
			if(address.getProvinceId()!=null && address.getProvinceId().length()>0){
				criteria.andProvinceIdLike("%"+address.getProvinceId()+"%");
			}
			if(address.getCityId()!=null && address.getCityId().length()>0){
				criteria.andCityIdLike("%"+address.getCityId()+"%");
			}
			if(address.getTownId()!=null && address.getTownId().length()>0){
				criteria.andTownIdLike("%"+address.getTownId()+"%");
			}
			if(address.getMobile()!=null && address.getMobile().length()>0){
				criteria.andMobileLike("%"+address.getMobile()+"%");
			}
			if(address.getAddress()!=null && address.getAddress().length()>0){
				criteria.andAddressLike("%"+address.getAddress()+"%");
			}
			if(address.getContact()!=null && address.getContact().length()>0){
				criteria.andContactLike("%"+address.getContact()+"%");
			}
			if(address.getIsDefault()!=null && address.getIsDefault().length()>0){
				criteria.andIsDefaultLike("%"+address.getIsDefault()+"%");
			}
			if(address.getNotes()!=null && address.getNotes().length()>0){
				criteria.andNotesLike("%"+address.getNotes()+"%");
			}
			if(address.getAlias()!=null && address.getAlias().length()>0){
				criteria.andAliasLike("%"+address.getAlias()+"%");
			}
	
		}
		
		Page<TbAddress> page= (Page<TbAddress>)addressMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private TbProvincesMapper provincesMapper;

	@Autowired
	private TbCitiesMapper citiesMapper;

	@Autowired
	private TbAreasMapper areasMapper;

	@Override
	public List<TbAddress> findListByUserId(String userId) {
		TbAddressExample example = new TbAddressExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(userId);
		List<TbAddress> addressList = addressMapper.selectByExample(example);
		for (TbAddress address : addressList) {
			TbProvincesExample example1 = new TbProvincesExample();
			TbProvincesExample.Criteria criteria1 = example1.createCriteria();
			criteria1.andProvinceidEqualTo(address.getProvinceId());
			List<TbProvinces> tbProvinces = provincesMapper.selectByExample(example1);
			address.setProvinceId(tbProvinces.get(0).getProvince());

			TbCitiesExample example2 = new TbCitiesExample();
			TbCitiesExample.Criteria criteria2 = example2.createCriteria();
			criteria2.andCityidEqualTo(address.getCityId());
			List<TbCities> tbCities = citiesMapper.selectByExample(example2);
			address.setCityId(tbCities.get(0).getCity());

			TbAreasExample example3 = new TbAreasExample();
			TbAreasExample.Criteria criteria3 = example3.createCriteria();
			criteria3.andAreaidEqualTo(address.getTownId());
			List<TbAreas> tbAreas = areasMapper.selectByExample(example3);
			address.setTownId(tbAreas.get(0).getArea());
		}
		return addressList;
	}

}
