package com.pinyougou.solrUtil;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @program: pinyougou-parent
 * @description: ${description}
 * @author: Mr.Cherry
 * @create: 2019-11-11 16:57
 **/
@Component
public class SolrUtil {

    @Autowired
    private SolrTemplate solrTemplatel;

    @Autowired
    private TbItemMapper itemMapper;

    public void importItemData(){

        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        //审核通过才导入
        criteria.andStatusEqualTo("1");
        List<TbItem> items = itemMapper.selectByExample(example);

        System.out.println("--- 开始 ---");
        for (TbItem item : items) {
            System.out.println(item.getId()+"   "+item.getTitle()+ "    " + item.getPrice());
            //从数据库中提取规格json字符串转为map
            Map map = JSON.parseObject(item.getSpec(), Map.class);
            item.setSpecMap(map);

        }
        solrTemplatel.saveBeans(items);
        solrTemplatel.commit();
        System.out.println("--- 结束 ---");
    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
        SolrUtil solrUtil = (SolrUtil)context.getBean("solrUtil");
        ///solrUtil.importItemData();

        solrUtil.dele();

    }


    public void dele(){
        Query query = new SimpleQuery("*:*");
        solrTemplatel.delete(query);
        solrTemplatel.commit();
    }
}
