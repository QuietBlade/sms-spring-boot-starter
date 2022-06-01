package org.example.sms.util;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import com.google.gson.Gson;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import org.example.sms.config.AccessKeyProperties;
import org.example.sms.enums.SmsTypeEnums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author: world
 * @date: 2022/5/31 16:59
 * @description: 发送的短信工具类
 */
@Component
public class SmsUtils {

    @Value("${sms.type}")
    private String type;

    @Resource(name = "aliyunAccess")
    private AccessKeyProperties aliyunAccessKey;

    @Resource(name = "tencentAccess")
    private AccessKeyProperties tencentAccessKey;

    @Autowired
    private Client aliyunClient;

    @Autowired
    private SmsClient tencentClient;

    /**
     * 根据配置类的type自动判断发送的短信
     * @param to 收件人（11位的电话号码）
     * @param params 模板变量赋值, 当参数只有一个的时候允许是普通字符串,其余必须是 json
     * @return 返回处理信息 允许异步
     */
    public String send(String to, HashMap<String, String> params) throws Exception {
        if (type.equals(SmsTypeEnums.Aliyun.toString())){
            return sendAliyunSms(to,new Gson().toJson(params));
        } else if (type.equals(SmsTypeEnums.Tencent.toString())) {
            String[] param = new String[params.size()];
            int index = 0;
            for (String s : params.keySet()) {
                param[index] = params.get(s);
            }
            return sendTencentSms(to,param);
        }
        throw new Exception("发送短信类型错误, 请检查配置文件");
    }

    /**
     * 发送aliyun短信
     * @param to 收件人（11位的电话号码）
     * @param params 模板参数的名称需要与 TemplateId 对应模板的变量名保持一致，
     *               若只有一个模板参数可以直接给定字符串, 若无模板参数，则设置为空
     * @exception Exception 抛出调用错误异常
     * @return 返回json结果字符串
     */
    public String sendAliyunSms(String to, String params) throws Exception {
        return this.sendAliyunSms(aliyunAccessKey.getTemplateCode(),to,params);
    }

    /**
     * 发送aliyun短信
     * @param to 收件人（11位的电话号码）
     * @param params 模板参数的名称需要与 TemplateId 对应模板的变量名保持一致，
     *               若只有一个模板参数可以直接给定字符串, 若无模板参数，则设置为空
     * @exception Exception 抛出调用错误异常
     * @return 返回json结果字符串
     */
    public String sendAliyunSms(String templateCode, String to, Map<String,String> params) throws Exception {
        return this.sendAliyunSms(templateCode, to, new Gson().toJson(params));
    }

    /**
     * 发送aliyun短信
     * @param templateCode 模板id
     * @param to 收件人（11位的电话号码）
     * @param params 模板参数的名称需要与 TemplateId 对应模板的变量名保持一致，
     *               若只有一个模板参数可以直接给定字符串, 若无模板参数，则设置为空
     * @exception Exception 抛出调用错误异常
     * @return 返回json结果字符串
     */
    public String sendAliyunSms(String templateCode, String to, String params) throws Exception {
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                /* 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名 */
                .setSignName(aliyunAccessKey.getSignName())
                /* 模板 ID: 必须填写已审核通过的模板 ID */
                .setTemplateCode(templateCode)
                /* 模板参数: 模板参数的名称需要与 TemplateId 对应模板的变量名保持一致，
                            若只有一个模板参数可以直接给定字符串, 若无模板参数，则设置为空 */
                .setTemplateParam(params)
                /* 下发手机号码, 此处使用 11位手机号 */
                .setPhoneNumbers(to);
        final SendSmsResponse sendSmsResponse = aliyunClient.sendSmsWithOptions(sendSmsRequest, new RuntimeOptions());
        return new Gson().toJson(sendSmsResponse.getBody());
    }



    /**
     * 发送腾讯云短信
     * @param to 收件人（11位的电话号码）
     * @param params 模板对应参数，需要注意必须是数组, 允许为空
     * @exception TencentCloudSDKException 抛出腾讯云sdk的异常
     * @return 返回json结果字符串
     */
    public String sendTencentSms(String to, String[] params) throws TencentCloudSDKException {
        return this.sendTencentSms(tencentAccessKey.getTemplateCode(),to,params);
    }

    /**
     * 发送腾讯云短信
     * @param templateCode 模板id
     * @param to 收件人（11位的电话号码）
     * @param params 模板对应参数，需要注意必须是数组, 允许为空
     * @exception TencentCloudSDKException 抛出腾讯云sdk的异常
     * @return 返回json结果字符串
     */
    public String sendTencentSms(String templateCode,String to, String[] params) throws TencentCloudSDKException {
        com.tencentcloudapi.sms.v20210111.models.SendSmsRequest req = new com.tencentcloudapi.sms.v20210111.models.SendSmsRequest();
        /* 短信应用ID: 短信SdkAppId在 [短信控制台] 添加应用后生成的实际SdkAppId */
        req.setSmsSdkAppId(tencentAccessKey.getAppId());

        /* 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名 */
        req.setSignName(tencentAccessKey.getSignName());

        /* 模板 ID: 必须填写已审核通过的模板 ID */
        req.setTemplateId(templateCode);

        /* 模板参数: 模板参数的个数需要与 TemplateId 对应模板的变量个数保持一致，若无模板参数，则设置为空 */
        req.setTemplateParamSet(params);

        /* 下发手机号码，采用 E.164 标准，+[国家或地区码][手机号]
         * 示例如：+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号 */
        req.setPhoneNumberSet(new String[]{"+86" + to});

        /* 用户的 session 内容（无需要可忽略）: 可以携带用户侧 ID 等上下文信息，server 会原样返回 */
        String sessionContext = "";
        req.setSessionContext(sessionContext);

        /* 短信码号扩展号（无需要可忽略）: 默认未开通，如需开通请联系 [腾讯云短信小助手] */
        String extendCode = "";
        req.setExtendCode(extendCode);

        /* 国际/港澳台短信 SenderId（无需要可忽略）: 国内短信填空，默认未开通，如需开通请联系 [腾讯云短信小助手] */
        String senderid = "";
        req.setSenderId(senderid);

        /* 发送请求并返回json字符串 */
        com.tencentcloudapi.sms.v20210111.models.SendSmsResponse res = tencentClient.SendSms(req);

        return com.tencentcloudapi.sms.v20210111.models.SendSmsResponse.toJsonString(res);
    }




}
