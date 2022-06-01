package org.example.sms.config;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import com.google.gson.Gson;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: world
 * @date: 2022/5/31 15:33
 * @description: 短信配置
 */
@Configuration
public class SmsConfiguration {

    @Bean(name = "aliyunAccess")
    @ConfigurationProperties(prefix = "sms.aliyun",ignoreUnknownFields = true)
    public AccessKeyProperties aliyunAccess(){
        return new AccessKeyProperties();
    }

    @Bean(name = "tencentAccess")
    @ConfigurationProperties(prefix = "sms.tencent",ignoreUnknownFields = true)
    public AccessKeyProperties tencentAccess(){
        return new AccessKeyProperties();
    }

    /**
     * 设置阿里云的连接池
     * @return
     * @throws Exception
     */
    @Bean
    public Client createAliyunClient() throws Exception {
        AccessKeyProperties accessKey = aliyunAccess();
        Config config = new Config()
                // 您的 AccessKey ID
                .setAccessKeyId(accessKey.getAccessKeyId())
                // 您的 AccessKey Secret
                .setAccessKeySecret(accessKey.getAccessKeySecret())
                .setEndpoint(accessKey.getAreaAddress());
        return new Client(config);
    }

    /**
     * 获取腾讯云短信客户端
     * @return
     */
    @Bean
    public SmsClient createTencentClient(){
        AccessKeyProperties accessKey = tencentAccess();
        Credential cred = new Credential(accessKey.getAccessKeyId(), accessKey.getAccessKeySecret());

        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setReqMethod("POST");
        httpProfile.setConnTimeout(60);
        httpProfile.setEndpoint(accessKey.getAreaAddress());
        // 设置代理（无需要直接忽略）
        // httpProfile.setProxyHost("真实代理ip");
        // httpProfile.setProxyPort(真实代理端口);

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setSignMethod("HmacSHA256");
        clientProfile.setHttpProfile(httpProfile);

        return new SmsClient(cred, accessKey.getAreaId(),clientProfile);
    }


}
