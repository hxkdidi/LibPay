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
