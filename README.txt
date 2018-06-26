1. 注册 http://dev.getui.com

2. 登陆

3. 登记应用

4. 进入“应用配置”，获取到相应的AppID、AppKey、AppSecret信息

5. Android Studio创建一个Project

6. 把GetuiSDK2.12.4.0.jar和json_simple-1.1.jar复制到app模块目录下的libs文件夹中

7. 打开app/build.gradle，在dependencies中添加相应的jar包的引用：
	compile files('libs/GetuiSDK2.12.4.0.jar')
    compile files('libs/json_simple-1.1.jar')
	
8. 把armeabi、armeabi-v7a、x86_64拷贝到app/src/main/jniLibs目录下

9. 在项目根目录下的gradle.properties文件中配置useDeprecatedNdk参数，android.useDeprecatedNdk=true

10. 在项目根目录下的local.properties文件中配置ndk的路径，ndk.dir=E\:\\android-ndk-r10e

11. 导入布局文件，复制getui_notification.xml布局文件复制到app/src/main/res/layout目录下

12. 导入通知栏图标，为了修改默认的通知图标以及通知栏顶部提示小图标，请在资源目录的res/drawable-ldpi/、res/drawable-mdpi/、res/drawable-hdpi/、res/drawable-xhdpi/、res/drawable-xxhdpi/等各分辨率目录下，放置相应尺寸的文件名为push.png和push_small.png图片

13. 配置个推应用参数，在app/build.gradle文件中的android.defaultConfig下添加manifestPlaceholders，配置个推相关的应用参数：
	manifestPlaceholders = [
		GETUI_APP_ID : "APP_ID",
		GETUI_APP_KEY : "APP_KEY",
		GETUI_APP_SECRET : "APP_SECRET"
	]

14. 在Manifest中配置个推SDK组件，在AndroidManifest.xml中需要正确配置个推SDK所需的Service、Activity、以及BroadcastReceiver等组件。请在<application>标签内增加以下组件配置（由于使用了manifestPlaceholders来做参数替换，因此以下配置无需手工修改，直接复制粘贴即可）：
	<!-- 个推SDK配置开始 -->
	<!-- 配置的第三方参数属性 -->
	<meta-data
		android:name="PUSH_APPID"
		android:value="${GETUI_APP_ID}" />
	<meta-data
		android:name="PUSH_APPKEY"
		android:value="${GETUI_APP_KEY}" />
	<meta-data
		android:name="PUSH_APPSECRET"
		android:value="${GETUI_APP_SECRET}" />

	<!-- 配置SDK核心服务 -->
	<service
		android:name="com.igexin.sdk.PushService"
		android:exported="true"
		android:label="NotificationCenter"
		android:process=":pushservice">
		<intent-filter>
			<action android:name="com.igexin.sdk.action.service.message"/>
		</intent-filter>
	</service>

	<receiver android:name="com.igexin.sdk.PushReceiver" >
		<intent-filter>
			<action android:name="android.intent.action.BOOT_COMPLETED" />
			<action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
			<action android:name="android.intent.action.USER_PRESENT" />
			<action android:name="com.igexin.sdk.action.refreshls" />
			<!-- 以下三项为可选的action声明，可大大提高service存活率和消息到达速度 -->
			<action android:name="android.intent.action.MEDIA_MOUNTED" />
			<action android:name="android.intent.action.ACTION_POWER_CONNECTED" />
			<action android:name="android.intent.action.ACTION_POWER_DISCONNECTED" />
		</intent-filter>
	</receiver>

	<activity
		android:name="com.igexin.sdk.PushActivity"
		android:excludeFromRecents="true"
		android:exported="false"
		android:process=":pushservice"
		android:taskAffinity="com.igexin.sdk.PushActivityTask"
		android:theme="@android:style/Theme.Translucent.NoTitleBar" >
	</activity>

	<activity
	 android:name="com.igexin.sdk.GActivity"
	 android:excludeFromRecents="true"
	 android:exported="true"
	 android:process=":pushservice"
	 android:taskAffinity="com.igexin.sdk.PushActivityTask"
	 android:theme="@android:style/Theme.Translucent.NoTitleBar"/>

	<!-- 个推SDK配置结束 -->

15. 添加权限声明
	<!-- 个推SDK权限配置开始 -->
	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.READ_PHONE_STATE" />
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
	<uses-permission android:name="android.permission.WAKE_LOCK" />
	<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	<uses-permission android:name="android.permission.VIBRATE" />
	<uses-permission android:name="android.permission.GET_TASKS" />
	<!-- 支持iBeancon 需要蓝牙权限 -->
	<uses-permission android:name="android.permission.BLUETOOTH" />
	<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
	<!-- 支持个推3.0 电子围栏功能 -->
	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
	<!-- 浮动通知权限 -->
	<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
	<!-- 自定义权限 -->
	<uses-permission android:name="getui.permission.GetuiService.${applicationId}" />

	<permission
		android:name="getui.permission.GetuiService.${applicationId}"
		android:protectionLevel="normal" >
	</permission>

	<!-- 个推SDK权限配置结束 -->

16. 配置自定义推送服务
	public class DemoPushService extends Service {

		@Override
		public void onCreate() {
			super.onCreate();
			GTServiceManager.getInstance().onCreate(this);
		}

		@Override
		public int onStartCommand(Intent intent, int flags, int startId) {
			super.onStartCommand(intent, flags, startId);
			return GTServiceManager.getInstance().onStartCommand(this, intent, flags, startId);
		}

		@Override
		public IBinder onBind(Intent intent) {
			return GTServiceManager.getInstance().onBind(intent);
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			GTServiceManager.getInstance().onDestroy();
		}

		@Override
		public void onLowMemory() {
			super.onLowMemory();
			GTServiceManager.getInstance().onLowMemory();
		}
	}

	在AndroidManifest.xml中添加上述自定义Service“
	<service
	  android:name="com.getui.demo.DemoPushService"
	  android:exported="true"
	  android:label="PushService"
	  android:process=":pushservice">
	</service>

17. 配置可选权限
	<!-- iBeancon功能所需权限 -->;
	<uses-permission android:name="android.permission.BLUETOOTH"/>
	<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
	<!-- 个推3.0电子围栏功能所需权限 -->
	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

18. Proguard混淆配置，
	如果您的工程启用了Proguard混淆，即如果在app/build.gradle的android.buildTypes.release下配置了minifyEnabled true，
	为了避免个推SDK被错误混淆导致功能异常，需要在app/proguard-rules.pro混淆配置文件中添加如下配置：
	-dontwarn com.igexin.**
	-keep class com.igexin.** { *; }
	-keep class org.json.** { *; }

19. 资源精简配置，
	如果您的工程启用了资源精简，即如果在app/build.gradle的android.buildTypes.release下配置了shrinkResources true，
	为了避免个推SDK所需资源被错误精简导致功能异常，需要在项目资源目录res/raw中添加keep.xml文件，内容如下：
	<?xml version="1.0" encoding="utf-8"?>
	<resources
		xmlns:tools="http://schemas.android.com/tools"
		tools:keep="@layout/getui_notification,
		@drawable/push,
		@drawable/push_small"/>
		<!-- 若您需要使用其他自定义推送图标，也需要在此处添加 -->

	果您的工程使用了AndResGuard进行资源精简，为了避免个推SDK所需资源被错误精简导致功能异常，需要为个推添加白名单配置。
	gradle集成AndResGuard的方式，需要您在andResGuard的whiteList节点下添加如下代码:
	andResGuard {
		...
		whiteList = [
			   ...
			   // for getui
			   "R.drawable.push",        
			   "R.drawable.push_small",
			   "R.layout.getui_notification",
			   "R.id.getui_*"
			   // 若您需要使用其他自定义推送图标，也需要在此处添加
		]
		...
	}

	命令行使用AndResGuard的方式，需要您在config.xml文件中的<issue id=whitelist>节点下添加如下代码
	<issue id="whitelist" isactive="true">
		<path value="<your_package_name>.R.drawable.push"/>
		<path value="<your_package_name>.R.drawable.push_small"/>
		<path value="<your_package_name>.R.layout.getui_notification"/>
		<path value="<your_package_name>.R.id.getui_*"/>
		<!-- 若您需要使用其他自定义推送图标，也需要在此处添加 -->
	</issue>

20. 初始化SDK
	在 Activity 的onCreate()或者onResume()方法中调用个推SDK初始化方法。如果使用了自定义推送服务，初始化方法还需要传入新的自定义推送服务名：
	// com.getui.demo.DemoPushService 为第三方自定义推送服务
	PushManager.getInstance().initialize(this.getApplicationContext(), com.getui.demo.DemoPushService.class);

21. 接收推送服务事件
	从2.9.5.0版本开始，为了解决小概率发生的Android广播丢失问题，我们推荐应用开发者使用新的IntentService方式来接收推送服务事件（包括CID获取通知、透传消息通知等）
	在项目源码中添加一个继承自com.igexin.sdk.GTIntentService的类，用于接收CID、透传消息以及其他推送服务事件。请参考下列代码实现各个事件回调方法：
	/**
	 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
	 * onReceiveMessageData 处理透传消息<br>
	 * onReceiveClientId 接收 cid <br>
	 * onReceiveOnlineState cid 离线上线通知 <br>
	 * onReceiveCommandResult 各种事件处理回执 <br>
	 */
	public class DemoIntentService extends GTIntentService {

		public DemoIntentService() {

		}

		@Override
		public void onReceiveServicePid(Context context, int pid) {
		}

		@Override
		public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
		}

		@Override
		public void onReceiveClientId(Context context, String clientid) {
			Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
		}

		@Override
		public void onReceiveOnlineState(Context context, boolean online) {
		}

		@Override
		public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
		}

		@Override
		public void onNotificationMessageArrived(Context context, GTNotificationMessage msg) {
		}

		@Override
		public void onNotificationMessageClicked(Context context, GTNotificationMessage msg) {   
		}
	}

	在AndroidManifest.xml中配置上述 IntentService 类：
	<service android:name="com.getui.demo.DemoIntentService"/>

	在个推SDK初始化后，注册上述 IntentService 类：
	// com.getui.demo.DemoIntentService 为第三方自定义的推送服务事件接收类
	PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), com.getui.demo.DemoIntentService.class);

20. 测试，运行，获取clientid

21. 根据获取的clientid到http://dev.getui.com进行推送通知

22. 如果需要从客户端请求个推服务器推送透传信息或者通知信息，需要MasterSecret，生成Sign值，用于鉴权