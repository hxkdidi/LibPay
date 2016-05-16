package com.lwj.fork.pay.ali.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.alipay.sdk.app.PayTask;
import com.lwj.fork.pay.ali.config.PartnerConfig;
import com.lwj.fork.pay.ali.constant.PayResult;
import com.lwj.fork.pay.ali.constant.SignUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by lwj on 2015/7/29.
 */
public class AliPayUtils<T extends OrderInfoModel> {
    public static String TAG = "AliPayUtils";
    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;


    private AliPayUtils engine;
    private volatile static AliPayUtils aliPayUtils;

    /**
     * Returns singleton class instance
     */

    public static AliPayUtils getInstance() {
        if (aliPayUtils == null) {
            synchronized (AliPayUtils.class) {
                if (aliPayUtils == null) {
                    aliPayUtils = new AliPayUtils();
                }
            }
        }
        return aliPayUtils;
    }

    /**
     * 支付过程的回调
     */
    private IAliPayListener alipayListener;

    private T orderInfoModel;
    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case SDK_PAY_FLAG:

                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        //TODO 支付成功
                        alipayListener.paySuccess(payResult, orderInfoModel);
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            //TODO 支付结果确认中
                            //  UtilWidget.showToast(mContext, "支付结果确认中");
                            alipayListener.payWaitConfirm(payResult, orderInfoModel);

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            //TODO 支付失败
                            // UtilWidget.showToast(mContext, "支付失败");
                            alipayListener.payFail(payResult, orderInfoModel);
                        }
                    }
                    break;
                case SDK_CHECK_FLAG:
                    // Toast.makeText(activity, "true", Toast.LENGTH_LONG).show();
                    break;

                default:
                    break;
            }
        }

        ;
    };


    /**
     * 客户端签名
     *
     * @param orderInfo
     * @param alipayListener
     */
    public void payClientSign(Activity activity, T orderInfo, IAliPayListener alipayListener) {

        String payInfo = getPayInfo(orderInfo, true);
        this.alipayListener = alipayListener;
        this.orderInfoModel = orderInfo;
        aliPay(activity, payInfo);
    }

    /**
     * 服务端签名
     *
     * @param orderInfo
     * @param alipayListener
     */
    public void pay(Activity activity, T orderInfo, IAliPayListener alipayListener) {
        String payInfo = getPayInfo(orderInfo, false);
        this.alipayListener = alipayListener;
        this.orderInfoModel = orderInfo;
        aliPay(activity, payInfo);
    }


    public void aliPay(final Activity activity, final String payInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(activity);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    public String getPayInfo(T orderInfoModel, boolean isClientSign) {

        // 订单
        String orderInfo = orderInfoModel.getPay_info();
//        String sign = URLDecoder.decode(orderInfoModel.getSign());
        String sign = orderInfoModel.getSign();
        String signType = orderInfoModel.getSign_type();


        if (isClientSign) {
            // 对订单做RSA 签名
            sign = sign(orderInfo);
            try {
                // 仅需对sign 做URL编码
                sign = URLEncoder.encode(sign, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        signType = getSignType(signType);


// 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                + signType;
        return payInfo;
    }


    /**
     * get the sdk version. 获取SDK版本号
     */
    public void getSDKVersion(Activity activity) {
        PayTask payTask = new PayTask(activity);
        String version = payTask.getVersion();
        Toast.makeText(activity, version, Toast.LENGTH_SHORT).show();
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    public String sign(String content) {
        return SignUtils.sign(content, PartnerConfig.RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    public String getSignType(String signType) {
        if (TextUtils.isEmpty(signType)) {
            return "sign_type=\"RSA\"";
        } else {
            return "sign_type=\"" + signType + "\"";

        }
    }

    /**
     * check whether the device has authentication alipay account.
     * 查询终端设备是否存在支付宝认证账户
     */
    private Activity activity;

    public void check(final Activity activity) {
        this.activity = activity;
        Runnable checkRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask(activity);
                // 调用查询接口，获取查询结果
                boolean isExist = payTask.checkAccountIfExist();

                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };

        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();

    }
}
