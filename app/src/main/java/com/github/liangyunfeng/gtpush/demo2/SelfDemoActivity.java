package com.github.liangyunfeng.gtpush.demo2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.liangyunfeng.gtpush.R;

import com.igexin.sdk.PushManager;

public class SelfDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo2);

        // com.getui.demo.DemoPushService 为第三方自定义推送服务
        PushManager.getInstance().initialize(this.getApplicationContext(), DemoPushService.class);

        /**
         * 1.如果调用了registerPushIntentService方法注册自定义IntentService，则SDK仅通过IntentService回调推送服务事件；
         * 2.如果未调用registerPushIntentService方法进行注册，则原有的广播接收器仍然可以继续使用。
         *
         * com.getui.demo.DemoIntentService 为第三方自定义的推送服务事件接收类
         */
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);
    }
}
