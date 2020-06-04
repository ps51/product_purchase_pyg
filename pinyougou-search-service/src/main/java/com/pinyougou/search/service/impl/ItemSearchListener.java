package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.soap.Text;
import java.util.List;

/**
 * @program: pinyougou-parent
 * @description: ${description}
 * @author: Mr.Cherry
 * @create: 2019-11-13 15:08
 **/
@Component("itemSearchListener")
public class ItemSearchListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        //json字符串
        TextMessage textMessage = (TextMessage) message;
        String text = null;
        try {
            text = textMessage.getText();
            List<TbItem> tbItemList = JSON.parseArray(text, TbItem.class);
            if(tbItemList != null && tbItemList.size() > 0){
                itemSearchService.importList(tbItemList);
                System.out.println("导入Solr索引库成功 - ");
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }



    }
}
