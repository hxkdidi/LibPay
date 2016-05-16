package com.lwj.fork.pay.wx;

import android.content.Context;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


public class WXPayUtils {

	private volatile static WXPayUtils payUtils;

	/** Returns singleton class instance */

	public static WXPayUtils getInstance() {
		if (payUtils == null) {
			synchronized (WXPayUtils.class) {
				if (payUtils == null) {
					payUtils = new WXPayUtils();
				}
			}
		}
		return payUtils;
	}

    public IWXAPI registerWXAPI(Context context, String APP_ID){
		IWXAPI api = WXAPIFactory.createWXAPI(context, null);
		api.registerApp(APP_ID);
		return  api;
	}


	public void pay(Context context, PayReq payReq,
					AWXPayListener aWXpayListener) {
		if(context == null || payReq == null || aWXpayListener == null){
			return;
		}
		// 注册 微信支付 API
		IWXAPI msgApi = registerWXAPI(context,payReq.appId);

		// 发起支付
		startPay(msgApi, payReq);
		WXPayUtils.aWXpayListener = aWXpayListener;
	}

	public void startPay(IWXAPI msgApi, PayReq payReq) {
		msgApi.registerApp(payReq.appId);
		msgApi.sendReq(payReq);
	}

	/**
	 * 设置支付结果回调
	 */
	public static AWXPayListener aWXpayListener;
	
}
