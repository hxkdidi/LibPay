package com.lwj.fork.pay.ali.utils;


import com.lwj.fork.pay.ali.constant.PayResult;

/**
 * Created by lwj on 2015/7/29.
 */
public interface IAliPayListener<T extends OrderInfoModel> {

       void paySuccess(PayResult payResult, T orderInfoModel);
       void payFail(PayResult payResult, T orderInfoModel);

       void payWaitConfirm(PayResult payResult, T orderInfoModel);


}
