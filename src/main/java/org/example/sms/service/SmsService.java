package org.example.sms.service;

import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.google.gson.Gson;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import org.example.sms.util.SmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author: world
 * @date: 2022/5/31 15:33
 * @description:
 */
@Service
public class SmsService {

    @Autowired
    private SmsUtils smsUtils;

    public String sendSms() {
        try {
            final HashMap<String, String> map = new HashMap<>();
            map.put("code","2333");
            return smsUtils.send( "15520014886", map);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
