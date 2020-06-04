package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.entity.pojo.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @program: pinyougou-parent
 * @description: ${description}
 * @author: Mr.Cherry
 * @create: 2019-11-05 21:09
 **/
@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;


    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(Integer pageName, Integer pageSize) {
        //分页 (当前页,每页显示数量)
        PageHelper.startPage(pageName,pageSize);
        Page<TbBrand> brands = (Page<TbBrand>) brandMapper.selectByExample(null);
        return new PageResult(brands.getTotal(),brands.getResult());
    }

    @Override
    public void addBrand(TbBrand brand) {
        brandMapper.insert(brand);
    }

    @Override
    public TbBrand findOneBrandById(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void updateBrand(TbBrand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }
    @Override
    public void deleteMultipleBrandByIdArray(Long[] ids) {
        brandMapper.deleteMultipleBrandByIdArray(ids);
    }

    @Override
    public PageResult findPage(TbBrand brand, Integer pageName, Integer pageSize) {
        //分页 (当前页,每页显示数量)
        PageHelper.startPage(pageName,pageSize);
        TbBrandExample example = new TbBrandExample();
        Criteria criteria = example.createCriteria();
        if(brand!=null){
            if(brand.getName()!=null && brand.getName().length() > 0){
                criteria.andNameLike("%" + brand.getName() + "%");
            }
            if(brand.getFirstChar()!=null && brand.getFirstChar().length() > 0){
                criteria.andFirstCharLike("%" + brand.getFirstChar() + "%");
            }
        }
        Page<TbBrand> brands = (Page<TbBrand>) brandMapper.selectByExample(example);
        return new PageResult(brands.getTotal(),brands.getResult());
    }

    @Override
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}
