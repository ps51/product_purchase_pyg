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
 * @create: 2019-11-13 17:09
 **/
@Component("pageDeleteListener")
public class PageDeleteListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage)message;
        try {
            Long [] ids = (Long []) objectMessage.getObject();
            itemPageService.deleteItemHtml(ids);
            System.out.println("静态页面删除成功 - ");
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
