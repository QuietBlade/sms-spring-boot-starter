package org.example.sms.enums;

/**
 * @author: world
 * @date: 2022/6/1 10:18
 * @description:
 */
public enum SmsTypeEnums {
    Tencent("tencent"),
    Aliyun("aliyun");
    private String key;

    SmsTypeEnums(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
