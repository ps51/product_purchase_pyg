package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * @program: pinyougou-parent
 * @description: ${description}
 * @author: Mr.Cherry
 * @create: 2019-11-13 15:52
 **/
@Component("pageListener")
public class PageListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage)message;
        try {
            Long [] ids = (Long [])objectMessage.getObject();
            for (Long id : ids) {
                itemPageService.genItemHtml(id);
            }
            System.out.println("静态页面生成成功 - ");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
