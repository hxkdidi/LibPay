package com.lwj.fork.pay.ali.utils;

/**
 * Created by lwj on 2015/7/29.
 */
public class OrderInfoModel {

    public String pay_info;// 待签名的字符串
    public String sign;// 签名后的字串
    public String sign_type = "";// 签名类型


    public String getPay_info() {
        return pay_info;
    }

    public void setPay_info(String pay_info) {
        this.pay_info = pay_info;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }
}
