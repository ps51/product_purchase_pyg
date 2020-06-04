package com.pinyougou.sellergoods.service;

import com.entity.pojo.PageResult;
import com.pinyougou.pojo.TbBrand;

import java.util.List;
import java.util.Map;

/**
 * 品牌接口
 */
public interface BrandService {

    public List<TbBrand> findAll();

    /**
     * 品牌分页
     * @param pageName 当前页
     * @param pageSize 每页记录数
     * @return 结果
     */
    public PageResult findPage(Integer pageName,Integer pageSize);

    /**
     * 新增
     * @param brand 新增内容对象
     */
    public void addBrand(TbBrand brand);

    /**
     * 根据id查询一个品牌信息
     * @param id id
     * @return 品牌信息
     */
    public TbBrand findOneBrandById(Long id);

    /**
     * 修改品牌信息
     * @param brand 最新的品牌信息
     */
    public void updateBrand(TbBrand brand);

    /**
     * 根据多个id删除多条数据
     * @param ids id数组
     */
    public void deleteMultipleBrandByIdArray(Long[] ids);


    /**
     * 根据条件筛选
     * @param brand 筛选条件/名称/首字母
     * @param pageName 当前页
     * @param pageSize 每页记录数
     * @return 查询结果
     */
    public PageResult findPage(TbBrand brand ,Integer pageName,Integer pageSize);

    /**
     * 返回下拉列表数据
     * @return
     */
    public List<Map> selectOptionList();

}
