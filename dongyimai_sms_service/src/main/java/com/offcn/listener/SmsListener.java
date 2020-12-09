package com.offcn.listener;

import com.aliyuncs.CommonResponse;
import com.aliyuncs.exceptions.ClientException;
import com.offcn.util.SmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

@Component
public class SmsListener implements MessageListener {
    @Autowired
    private SmsUtil smsUtil;
    @Override
    public void onMessage(Message message) {
        if (message instanceof MapMessage){
            MapMessage map=(MapMessage)message;
            try {
                String phoneNum = map.getString("phoneNum");
                String code = map.getString("code");
                String template = map.getString("template");
                CommonResponse commonResponse = smsUtil.sendSms(phoneNum, template, code);
                System.out.println(commonResponse.getData());
            } catch (JMSException e) {
                e.printStackTrace();
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }
    }
}
