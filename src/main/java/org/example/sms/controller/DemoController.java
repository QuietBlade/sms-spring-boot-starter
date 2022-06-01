package org.example.sms.controller;

import org.example.sms.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.ExecutionException;

/**
 * @author: world
 * @date: 2022/5/31 17:11
 * @description:
 */
@RestController
@RequestMapping("/api/v1")
public class DemoController {

    @Autowired
    private SmsService smsService;

    @GetMapping("sendSms")
    public String sendSms(){
        return smsService.sendSms();
    }

    @GetMapping("test")
    public String test(){
        return "hello,world";
    }
}
