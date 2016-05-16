# LibPay
## 支付宝支付
### 准备工作  
  
#####   权限配置

     <uses-permission android:name="android.permission.INTERNET" />
     <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS"/>
     <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    
#####  activity配置
       <!-- alipay sdk begin -->
         <activity
            android:name="com.alipay.sdk.app.H5PayActivity"
            android:configChanges="orientation|keyboardHidden|navigation"
            android:exported="false"
            android:screenOrientation="behind" >
        </activity>
        <activity
            android:name="com.alipay.sdk.auth.AuthActivity"
            android:configChanges="orientation|keyboardHidden|navigation"
            android:exported="false"
            android:screenOrientation="behind" >
        </activity>

        <!-- alipay sdk end -->
#####使用
##### 客户端签名支付 （不建议）
    调用 AliPayUtils.getInstance().payClientSign
     
    此时需要配置  进行签名的
     
     使用demo
        OrderInfoModel orderInfoModel = new OrderInfoModel()
        //  自己拼接  订单信息
        orderInfoModel.orderInfo = getOrderInfo(orderInfoModel);
        AliPayUtils.getInstance().payClientSign(this, orderInfoModel, new IAlipayListener() {

            @Override
            public void payWaitConfirm(PayResult payResult) {
                Toast.makeText(AlipaydemoActivity.this, "确认中" + payResult.getResultStatus(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void paySuccess(PayResult payResult) {
                Toast.makeText(AlipaydemoActivity.this, "成功" + payResult.getResultStatus(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void payFail(PayResult payResult) {
                Toast.makeText(AlipaydemoActivity.this, "失败" + payResult.getResultStatus(), Toast.LENGTH_LONG).show();

            }
        });          



##### 服务端签名支付 
    调用 AliPayUtils.getInstance().pay
       demo
        OrderInfoModel orderInfoModel = new OrderInfoModel();
        /**
         * 必须赋值   （ 此处有大坑）
         *    注意  是否 encode 编码了
         *             支付宝接受 待签名字串 是 明文  不可  encode 编码
         *                       签名字串  必须是 encode 过一次的
        orderInfoModel.sign = "";
        orderInfoModel.orderInfo = "";
        orderInfoModel.sign_type = "";
         sign_type 可以不赋值
       **/

        AliPayUtils.getInstance().pay(this, orderInfoModel, new IAlipayListener() {

            @Override
            public void payWaitConfirm(PayResult payResult) {
                // TODO Auto-generated method stub
                Toast.makeText(AlipaydemoActivity.this, "确认中" + payResult.getResultStatus(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void paySuccess(PayResult payResult) {
                // TODO Auto-generated method stub
                Toast.makeText(AlipaydemoActivity.this, "成功" + payResult.getResultStatus(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void payFail(PayResult payResult) {
                // TODO Auto-generated method stub
                Toast.makeText(AlipaydemoActivity.this, "失败" + payResult.getResultStatus(), Toast.LENGTH_LONG).show();

            }
        });
        
## 微信支付
### 准备工作
  配置   WXPayEntryActivity
  在packagename.wxapi 包下新建  activity－－>WXPayEntryActivity    
  (例如包名为 com.lwj.fork  则在  com.lwj.fork.wxapi 包下新建  ) 


	public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	
		private IWXAPI api;
	
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.wx_pay_result);
	
	        api = WXPayUtils.getInstance().registerWXAPI(this, ShareAPI.WX_APP_ID);
			api.handleIntent(getIntent(), this);
		}
	
		@Override
		protected void onNewIntent(Intent intent) {
			super.onNewIntent(intent);
			setIntent(intent);
			api.handleIntent(intent, this);
		}
	
		@Override
		public void onReq(BaseReq req) {
		}
	
		/**
		 * 覆写 该方法即可
		 */
		@Override
		public void onResp(BaseResp resp) {
	
			if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
				AWXpayListener payListener = WXPayUtils.aWXpayListener;
				if (null != payListener) {
					payListener.handlePayResp(resp);
				}
			}
			finish();
	
			
		}
	  }
	   
使用前需要注册 appid	  
与WXPayEntryActivity一样的路径下 新建 BroadcastReceiver 注册 wxapi

	public class AppRegister extends BroadcastReceiver {
	
		@Override
		public void onReceive(Context context, Intent intent) {
	
			WXPayUtils.getInstance().registerWXAPI(context,ShareAPI.WX_APP_ID);
		}
	}
	
### 支付
		 WXPayUtils.getInstance().pay(context, payReq, new AWXpayListener() {
		                    @Override
		                    public void onSucccessPay(BaseResp resp) {
		                        LogUtil.d("paySuccess %s", "WX  paySuccess");
		                        orderPaySuccess();
		                    }
		
		                    @Override
		                    public void onErrorPay(BaseResp resp) {
		                        LogUtil.d("WXErrorPay_errCode %s", resp.errCode);
		                    }
		
		                    @Override
		                    public void onCanclePay(BaseResp resp) {
		
		                        LogUtil.d("WXCanclePay %s", " WXCanclePay");
		                    }
		                });
		                
###  payReq
  PayReq  是  微信自带的 
  需要设置的变量有
  
        PayReq payReq = new PayReq();
        //  appid
        payReq.appId = ShareAPI.WX_APP_ID;
        // 商户id
        payReq.partnerId = ShareAPI.WX_MCH_ID;
        // 预支付ID
        payReq.prepayId = pay_info.prepayid;
        //扩展字段
        payReq.packageValue = pay_info.packageValue;
        // 随机字符串
        payReq.nonceStr = pay_info.nonce_str;
        // 时间戳
        payReq.timeStamp = pay_info.timestamp;
            // 签名
        payReq.sign = sign;

                 
	
  
