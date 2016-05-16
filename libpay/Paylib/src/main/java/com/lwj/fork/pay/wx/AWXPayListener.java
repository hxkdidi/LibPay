package com.lwj.fork.pay.wx;


import com.tencent.mm.sdk.modelbase.BaseResp;

public abstract class AWXPayListener {
	
	
	/**
	 * 成功支付
	 * @param resp
	 */
	public abstract void onSucccessPay(BaseResp resp);
    /**
     * 支付失败
     * @param resp
     */
	public abstract void onErrorPay(BaseResp resp);
	
    /**
	 * 用户取消
	 * @param resp
	 */
    public void onCanclePay(BaseResp resp){
    	
    }
    
    public void handlePayResp(BaseResp resp){
			if(0 == resp.errCode){  // 成功支付
				onSucccessPay(resp);
			}else if(-1 == resp.errCode){  // 支付错误
				onErrorPay(resp);
			}else if(-2 == resp.errCode){  // 用户取消
				onCanclePay(resp);
			}
    }
}
