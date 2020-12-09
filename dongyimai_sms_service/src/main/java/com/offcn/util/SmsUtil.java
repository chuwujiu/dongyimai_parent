package com.offcn.util;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmsUtil {
    @Value("${AccessKeyID}")
    private String AccessKeyID;
    @Value("${AccessKeySecret}")
    private String AccessKeySecret;

    public CommonResponse sendSms(String phoneNum,String template,String code) throws ClientException {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", AccessKeyID, AccessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("SignName", "优就业");

        request.putQueryParameter("TemplateCode",  template);
        request.putQueryParameter("PhoneNumbers", phoneNum);
        request.putQueryParameter("TemplateParam", "{code:" + code +"}");
        System.out.println("短信发送");
        return client.getCommonResponse(request);
    }
}
