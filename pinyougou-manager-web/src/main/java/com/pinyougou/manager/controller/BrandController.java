package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.entity.pojo.PageResult;
import com.entity.pojo.Result;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @program: pinyougou-parent
 * @description: 品牌表操作
 * @author: Mr.Cherry
 * @create: 2019-11-05 21:13
 **/
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    @RequestMapping("/findPage")
    public PageResult findPage(Integer page,Integer size){
        return brandService.findPage(page,size);
    }

    @RequestMapping("/add")
    public Result addBrand(@RequestBody TbBrand brand){
        try {
            brandService.addBrand(brand);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"新增失败");
        }
        return new Result(true,"新增成功");
    }

    @RequestMapping("/findOne")
    public TbBrand findOneBrandById(Long id){
        return brandService.findOneBrandById(id);
    }

    @RequestMapping("/update")
    public Result updateBrand(@RequestBody TbBrand brand){
        try {
            brandService.updateBrand(brand);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
        return new Result(true,"修改成功");
    }

    @RequestMapping("/delete")
    public Result deleteBrands(Long [] ids){
        try {
            brandService.deleteMultipleBrandByIdArray(ids);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
        return new Result(true,"删除成功");
    }

    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand brand,
                             Integer page,Integer size){
        return brandService.findPage(brand, page, size);
    }


    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }


}
