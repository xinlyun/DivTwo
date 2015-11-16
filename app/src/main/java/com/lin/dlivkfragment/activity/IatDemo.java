package com.lin.dlivkfragment.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.sunflower.FlowerCollector;
import com.lin.dlivkfragment.R;
import com.lin.dlivkfragment.setting.IatSettings;
import com.lin.dlivkfragment.util.ApkInstaller;
import com.lin.dlivkfragment.util.JsonParser;
import com.lin.dlivkfragment.util.Utils;
import com.lin.myfloatactionbtn.VoiceView;
import com.lin.myfloatactionbtn.swipeback.SwipeBackActivity;
import com.lin.myfloatactionbtn.swipeback.SwipeBackLayout;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

public class IatDemo extends SwipeBackActivity implements OnClickListener,
		PoiSearch.OnPoiSearchListener, RouteSearch.OnRouteSearchListener,TextWatcher
{
	private static String TAG = IatDemo.class.getSimpleName();
	// 语音听写对象
	private SpeechRecognizer mIat;
	// 用HashMap存储听写结果
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

	//	private EditText mResultText;
	private Toast mToast;
	private SharedPreferences mSharedPreferences;
	// 引擎类型
	private String mEngineType = SpeechConstant.TYPE_CLOUD;
	// 语记安装助手类
	ApkInstaller mInstaller;

	//----------导航--------

	private String strStart ;
	private String cityCode;
	//    private LatLonPoint startPoint;
//    private PoiSearch.Query startSearchQuery;
//    private PoiSearch.Query endSearchQuery;
	private RouteSearch routeSearch;
	private EditText mAutoText;
	private ListView mlistview;
	private LatLonPoint mLocation;
	private PoiSearch.Query query;
	private PoiSearch poiSearch;
	private ArrayAdapter<String> arrayAdapter;
	private MyAdapter myAdapter;
	private ArrayList<ListItem> listItems;
	private AMapNavi mAmapNavi;
	private List<NaviLatLng> startPoint;
	private List<NaviLatLng> endPoint,wayPoint;
	private AMapNaviListener mAmapNaviListener;
	//    private String posi;
	private Stack<String> names;
	private List<PoiItem> poiItems;
	private boolean calueSuccess=false;
	private AMapNavi mapNavi;
	private CardView mlistCard;

	//----
	private VoiceView vx;
	private RotateAnimation rotateAnimation;
	private boolean flagListen=false;
	private Intent intent1;

	private Bundle bundle;
//	@SuppressLint("ShowToast")
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
////		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.iatdemo);
//		setDragEdge(SwipeBackLayout.DragEdge.LEFT);
//		initLayout();
//		initView();
//		// 初始化识别无UI识别对象
//		// 使用SpeechRecognizer对象，可根据回调消息自定义界面；
//		mIat = SpeechRecognizer.createRecognizer(IatDemo.this, mInitListener);
//
//		// 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
//
//		mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME,
//				Activity.MODE_PRIVATE);
//		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
//
//		mInstaller = new ApkInstaller(IatDemo.this);
//
//
//	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			//透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//透明导航栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}


		setContentView(R.layout.iatdemo);

//		SystemBarTintManager tintManager = new SystemBarTintManager(this);
//		// enable status bar tint
//		tintManager.setStatusBarTintEnabled(true);
//		// enable navigation bar tint
//		tintManager.setNavigationBarTintEnabled(true);
//		tintManager.setTintColor(Color.parseColor("#80000FFF"));

		setDragEdge(SwipeBackLayout.DragEdge.LEFT);
		initLayout();
		initView();
		// 初始化识别无UI识别对象
		// 使用SpeechRecognizer对象，可根据回调消息自定义界面；
		mIat = SpeechRecognizer.createRecognizer(IatDemo.this, mInitListener);

		// 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer

		mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME,
				Activity.MODE_PRIVATE);
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

		mInstaller = new ApkInstaller(IatDemo.this);

	}

	private void initView(){
		names = new Stack<>();
		mapNavi = AMapNavi.getInstance(getApplicationContext());
//        mapNavi.setAMapNaviListener(getAMapNaviListener());
		listItems = new ArrayList<ListItem>();
		myAdapter = new MyAdapter(this,R.layout.searchpage_listview_item);

		startPoint = new ArrayList<NaviLatLng>();
		endPoint = new ArrayList<NaviLatLng>();
		wayPoint = new ArrayList<NaviLatLng>();

		mAmapNavi = AMapNavi.getInstance(this);
		mAmapNavi.setAMapNaviListener(getAMapNaviListener());

		mAutoText = (EditText) findViewById(R.id.iat_text);
		mAutoText.addTextChangedListener(this);
		mAutoText.setOnClickListener(this);
		mlistview = (ListView) findViewById(R.id.iat_listview);
		routeSearch = new RouteSearch(this);
		routeSearch.setRouteSearchListener(this);
		mlistCard = (CardView) findViewById(R.id.iat_list_card);
		mlistCard.setVisibility(View.GONE);
//        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
//        mlistview.setAdapter(arrayAdapter);
		mlistview.setAdapter(myAdapter);

		Intent intent = getIntent();
		bundle = intent.getBundleExtra("myown");

		int style = bundle.getInt("style",0);
		switch (style) {
			case 0:
				initPosi(bundle);
				/**
				 * 当被展示的地点被点击时，获取地点坐标，根据当前坐标重新开始计算路径，因为AMapNavi是单例对象，在其完成路径计算后即可关闭当前活动
				 */
				mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						Log.d("SearchPage", "onItemCLick");
						PoiItem poiItem = poiItems.get(position);

						endPoint.clear();
						endPoint.add(new NaviLatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude()));

//                AMapNavi.getInstance(SearchPage.this).calculateDriveRoute(startPoint, endPoint,
//                        wayPoint, AMapNavi.DrivingDefault);
						mapNavi.calculateDriveRoute(startPoint, endPoint, wayPoint, AMapNavi.DrivingDefault);

//              SearchPage.this.finish();
						IatDemo.this.onBackPressed();
					}
				});
				break;
			case 1:
				initPosi(bundle);
				mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						PoiItem poiItem = poiItems.get(position);
						bundle.putParcelable("myown", poiItem.getLatLonPoint());
						bundle.putString("myname", poiItem.getTitle());
						intent1 = new Intent();
						intent1.putExtra("myown",bundle);
						setResult(RESULT_OK,intent1);
						finish();
					}
				});
				break;
			case 2:
				initPosi(bundle);
				mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						PoiItem poiItem = poiItems.get(position);
						bundle.putParcelable("posi", poiItem.getLatLonPoint());
						bundle.putString("posiname", poiItem.getTitle());
						intent1 = new Intent();
						intent1.putExtra("myown", bundle);
						setResult(RESULT_OK,intent1);
						finish();
					}
				});
				break;
			case 3:
				initPosi(bundle);
				mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						PoiItem poiItem = poiItems.get(position);
						bundle.putParcelable("homeposi", poiItem.getLatLonPoint());
						bundle.putString("home", poiItem.getTitle());
						intent1 = new Intent();
						intent1.putExtra("myown", bundle);
						setResult(RESULT_OK, intent1);
						finish();
					}
				});
				break;
			case 4:
				initPosi(bundle);
				mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						PoiItem poiItem = poiItems.get(position);
						bundle.putParcelable("companyposi", poiItem.getLatLonPoint());
						bundle.putString("company", poiItem.getTitle());
						intent1 = new Intent();
						intent1.putExtra("myown", bundle);
						setResult(RESULT_OK, intent1);
						finish();
					}
				});
				break;

		}
	}


	private void initPosi(Bundle bundle){
		try {
			mLocation = bundle.getParcelable("myown");
			cityCode = bundle.getString("city");
			startPoint.add(new NaviLatLng(mLocation.getLatitude(), mLocation.getLongitude()));
			Log.d("SearchPage", mLocation.getLatitude() + " " + mLocation.getLongitude() + " " + cityCode);
		} catch (Exception e) {
			Log.d("SearchPage", e.toString());
		}
		if (mLocation == null) {
			SharedPreferences mshare = getSharedPreferences("myown", Context.MODE_PRIVATE);
			String x = mshare.getString("x", "0");
			String y = mshare.getString("y", "0");
			cityCode = mshare.getString("city", "");
			Log.d("SearchPage", x + " " + y + " " + cityCode);
			mLocation = new LatLonPoint(Double.valueOf(x), Double.valueOf(y));
			startPoint.add(new NaviLatLng(mLocation.getLatitude(), mLocation.getLongitude()));
		}
	}

	/**
	 * 初始化Layout。
	 */
	private void initLayout() {
//		findViewById(R.id.iat_recognize).setOnClickListener(IatDemo.this);
//		findViewById(R.id.iat_recognize_stream).setOnClickListener(IatDemo.this);
//		findViewById(R.id.iat_stop).setOnClickListener(IatDemo.this);
//		findViewById(R.id.iat_cancel).setOnClickListener(IatDemo.this);
		vx = (VoiceView) findViewById(R.id.iat_image);
		rotateAnimation = new RotateAnimation(2f,720f, Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0.5f);

		rotateAnimation.setDuration(800);
		rotateAnimation.setRepeatCount(-1);
		vx.setOnClickListener(this);

		// 选择云端or本地
		mEngineType = SpeechConstant.TYPE_LOCAL;
		/**
		 * 选择本地听写 判断是否安装语记,未安装则跳转到提示安装页面
		 */
//		if (!SpeechUtility.getUtility().checkServiceInstalled()) {
//			mInstaller.install();
//		} else {
//			String result = FucUtil.checkLocalResource();
//			if (!TextUtils.isEmpty(result)) {
//				showTip(result);
//			}
//		}
	}

	int ret = 0; // 函数调用返回值

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			// 进入参数设置页面

			// 开始听写
			// 如何判断一次听写结束：OnResult isLast=true 或者 onError
//			case R.id.iat_recognize:
//				mAutoText.setText(null);
//				readyListen();
//				break;
//			// 音频流识别
//			case R.id.iat_recognize_stream:
//				mAutoText.setText(null);// 清空显示内容
//				mIatResults.clear();
//				// 设置参数
//				setParam();
//				// 设置音频来源为外部文件
//				mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
//				// 也可以像以下这样直接设置音频文件路径识别（要求设置文件在sdcard上的全路径）：
//				// mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-2");
//				// mIat.setParameter(SpeechConstant.ASR_SOURCE_PATH, "sdcard/XXX/XXX.pcm");
//				ret = mIat.startListening(mRecognizerListener);
//				if (ret != ErrorCode.SUCCESS) {
//					showTip("识别失败,错误码：" + ret);
//				} else {
//					byte[] audioData = FucUtil.readAudioFile(IatDemo.this, "iattest.wav");
//
//					if (null != audioData) {
//						showTip(getString(R.string.text_begin_recognizer));
//						// 一次（也可以分多次）写入音频文件数据，数据格式必须是采样率为8KHz或16KHz（本地识别只支持16K采样率，云端都支持），位长16bit，单声道的wav或者pcm
//						// 写入8KHz采样的音频时，必须先调用setParameter(SpeechConstant.SAMPLE_RATE, "8000")设置正确的采样率
//						// 注：当音频过长，静音部分时长超过VAD_EOS将导致静音后面部分不能识别
//						mIat.writeAudio(audioData, 0, audioData.length);
//						mIat.stopListening();
//					} else {
//						mIat.cancel();
//						showTip("读取音频流失败");
//					}
//				}
//				break;
//			// 停止听写
//			case R.id.iat_stop:
//				mIat.stopListening();
//				showTip("停止听写");
//				break;
//			// 取消听写
//			case R.id.iat_cancel:
//				mIat.cancel();
//				showTip("取消听写");
//				break;
			case R.id.iat_image:
				if(!flagListen){
					readyListen();
					mAutoText.setText("正在听取您的声音~~");
				}

				else if(flagListen){
					mIat.stopListening();
					vx.Lock();
				}
				break;
			case R.id.iat_text:
				mAutoText.setText("");
				break;
			default:
				break;
		}
	}
	private void readyListen(){
//		mAutoText.setText(null);// 清空显示内容
		mIatResults.clear();
		// 设置参数
		setParam();
		// 不显示听写对话框
		ret = mIat.startListening(mRecognizerListener);
		if (ret != ErrorCode.SUCCESS) {
			showTip("听写失败,错误码：" + ret);
//			if(ret==21003)
//				readyListen();
		} else {
			showTip(getString(R.string.text_begin));
		}
//			}
	}

	/**
	 * 初始化监听器。
	 */
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d(TAG, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("初始化失败，错误码：" + code);
			}
		}
	};


	/**
	 * 听写监听器。
	 */
	private RecognizerListener mRecognizerListener = new RecognizerListener() {
		@Override
		public void onBeginOfSpeech() {
			// 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
//			waiting.startAnimation(rotateAnimation);
			flagListen = true;
			vx.unLock();
			showTip("开始说话");
		}

		@Override
		public void onError(SpeechError error) {
			// Tips：
			// 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
			// 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
			flagListen = false;
			showTip(error.getPlainDescription(true));
		}

		@Override
		public void onEndOfSpeech() {
			// 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
//			waiting.clearAnimation();
			flagListen = false;
			vx.Lock();
			showTip("结束说话");
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			Log.d(TAG, results.getResultString());
			printResult(results);
			flagListen = false;
			if (isLast) {
				// TODO 最后的结果
			}
		}

		@Override
		public void onVolumeChanged(int volume, byte[] data) {
			showTip("当前正在说话，音量大小：" + volume);
			vx.setVoice((float)(volume)/20f);
			Log.d(TAG, "返回音频数据："+data.length);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			// 若使用本地能力，会话id为null
			//	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			//		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			//		Log.d(TAG, "session id =" + sid);
			//	}
		}
	};

	private void printResult(RecognizerResult results) {
		String text = JsonParser.parseIatResult(results.getResultString());

		String sn = null;
		// 读取json结果中的sn字段
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mIatResults.put(sn, text);

		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mIatResults.keySet()) {
			resultBuffer.append(mIatResults.get(key));
		}

		mAutoText.setText(resultBuffer.toString());
		mAutoText.setSelection(mAutoText.length());
	}





	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}

	/**
	 * 参数设置
	 *
	 * @param param
	 * @return
	 */
	public void setParam() {
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);

		// 设置听写引擎
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

		String lag = mSharedPreferences.getString("iat_language_preference",
				"mandarin");
		if (lag.equals("en_us")) {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mIat.setParameter(SpeechConstant.ACCENT, lag);
		}

		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

		// 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
		mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "0"));

		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 退出时释放连接
		mIat.cancel();
		mIat.destroy();
	}

	@Override
	protected void onResume() {
		// 开放统计 移动数据统计分析
		FlowerCollector.onResume(IatDemo.this);
		FlowerCollector.onPageStart(TAG);
		super.onResume();
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				readyListen();
			}
		}, 500);
//		readyListen();
	}

	@Override
	protected void onPause() {
		// 开放统计 移动数据统计分析
		FlowerCollector.onPageEnd(TAG);
		FlowerCollector.onPause(IatDemo.this);
		super.onPause();
	}





//---------------导航相关函数-----------------
	/**
	 * 初始化路线描述信息和加载线路
	 */
	private void initNavi() {
		Log.d("SearchPage","initNavi");

		AMapNaviPath naviPath = mAmapNavi.getNaviPath();
		if (naviPath == null) {
			Log.d("SearchPage","initNavi  naviPath null");
			return;
		}
		// 获取路径规划线路，显示到地图上
//        PoiOverlay poiOverlay = new PoiOverlay(mAmap,null);

		double length = ((int) (naviPath.getAllLength() / (double) 1000 * 10))
				/ (double) 10;
		// 不足分钟 按分钟计
		int time = (naviPath.getAllTime() + 59) / 60;
		int cost = naviPath.getTollCost();
//        mRouteDistanceView.setText(String.valueOf(length));
//        mRouteTimeView.setText(String.valueOf(time));
//        mRouteCostView.setText(String.valueOf(cost));
		Log.d("SearchPage",String.valueOf(length));
	}
	/**
	 * 导航回调函数
	 *
	 * @return
	 */
	private AMapNaviListener getAMapNaviListener() {
		if (mAmapNaviListener == null) {

			mAmapNaviListener = new AMapNaviListener() {

				@Override
				public void onTrafficStatusUpdate() {
					// TODO Auto-generated method stub
					Log.d(TAG,"onTrafficStatusUpdate");
				}

				@Override
				public void onStartNavi(int arg0) {
					// TODO Auto-generated method stub
					Log.d(TAG,"onStartNavi");
				}

				@Override
				public void onReCalculateRouteForYaw() {
					// TODO Auto-generated method stub
					Log.d(TAG,"onReCalculateRouteForYaw");
				}

				@Override
				public void onReCalculateRouteForTrafficJam() {
					// TODO Auto-generated method stub
					Log.d(TAG,"onReCalculateRouteForTrafficJam");
				}

				@Override
				public void onLocationChange(AMapNaviLocation location) {
					Log.d(TAG,"onLocationChange");

				}

				@Override
				public void onInitNaviSuccess() {
					// TODO Auto-generated method stub
					Log.d(TAG,"onInitNaviSuccess");
				}

				@Override
				public void onInitNaviFailure() {
					// TODO Auto-generated method stub
					Log.d(TAG,"onInitNaviFailure");
				}

				@Override
				public void onGetNavigationText(int arg0, String arg1) {
					// TODO Auto-generated method stub
					Log.d(TAG,"onGetNavigationText");
				}

				@Override
				public void onEndEmulatorNavi() {
					// TODO Auto-generated method stub
					Log.d(TAG,"onEndEmulatorNavi");
				}

				@Override
				public void onCalculateRouteSuccess() {
					IatDemo.this.finish();
					Log.d(TAG, "onCalculateRouteSuccess");
				}

				@Override
				public void onCalculateRouteFailure(int arg0) {
					Log.d(TAG, "onCalculateRouteFailure");
				}

				@Override
				public void onArrivedWayPoint(int arg0) {
					// TODO Auto-generated method stub
					Log.d(TAG, "onArrivedWayPoint");
				}

				@Override
				public void onArriveDestination() {
					// TODO Auto-generated method stub
					Log.d(TAG, "onArriveDestination");
				}

				@Override
				public void onGpsOpenStatus(boolean arg0) {
					// TODO Auto-generated method stub
					Log.d(TAG, "onGpsOpenStatus");
				}

				@Override
				public void onNaviInfoUpdated(AMapNaviInfo arg0) {
					// TODO Auto-generated method stub
					Log.d(TAG, "onNaviInfoUpdated");
				}

				@Override
				public void onNaviInfoUpdate(NaviInfo arg0) {

					// TODO Auto-generated method stub
					Log.d(TAG, "onNaviInfoUpdate");
				}
			};
		}
		return mAmapNaviListener;
	}

	@Override
	public void onPoiSearched(PoiResult poiResult, int i) {
		Log.d("IatDemo","onPoiSearched");
		if (poiResult != null && poiResult.getQuery() != null
				&& poiResult.getPois() != null && poiResult.getPois().size() > 0) {// 搜索poi的结果
//            if (poiResult.getQuery().equals(startSearchQuery)) {
			poiItems = poiResult.getPois();// 取得poiitem数据

//                arrayAdapter.clear();
			myAdapter.clear();
			names.clear();
//                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
			if (poiItems.size()==0){
				mlistCard.setVisibility(View.GONE);
			}else {
				mlistCard.setVisibility(View.VISIBLE);
			}
			for(PoiItem poiItem:poiItems){

//                    RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mLocation,poiItem.getLatLonPoint());
//                    RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo,RouteSearch.DrivingDefault,null,null,"");
//
////                        routeSearch.calculateDriveRouteAsyn(query);
//
//                    names.push(poiItem.toString());

				ListItem listItem = new ListItem(poiItem.toString(),poiItem.getTypeDes(),poiItem.getSnippet());
				myAdapter.addItem(listItem);
				myAdapter.notifyDataSetChanged();

			}
		}
	}

	@Override
	public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {

	}

	@Override
	public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
		Log.d("SearchPage", "Distance  " + driveRouteResult.getPaths().get(0).getDistance());
//        arrayAdapter.add(names.pop() + "  Distance  " + driveRouteResult.getPaths().get(0).getDistance());
//        arrayAdapter.notifyDataSetChanged();

		ListItem listItem = new ListItem(names.pop(), "" + driveRouteResult.getPaths().get(0).getDistance(),
				"" + driveRouteResult.getPaths().get(0).getDuration());
		myAdapter.addItem(listItem);
		myAdapter.notifyDataSetChanged();
	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String newText = s.toString();
		Log.d("SearchPage","text changed");
		if(!newText.equals("正在听取您的声音~~")&& !newText.equals("")) {
			query = new PoiSearch.Query(newText, "", cityCode);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
			query.setPageSize(20);// 设置每页最多返回多少条poiitem
			query.setPageNum(0);// 设置查第一页

			poiSearch = new PoiSearch(this, query);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.searchPOIAsyn();
		}
	}

	@Override
	public void afterTextChanged(Editable s) {

	}


	//-------------------------以下为ListView的适配器使用类------------------------
	class ListItem {
		String name ;
		String distance;
		String time;
		int id;
		public ListItem(String name,String distance,String time){
			this.name = name ;
			this.distance = distance;
			this.time = time;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getTime() {
			return time;
		}

		public String getDistance() {
			return distance;
		}
	}

	/**
	 * ListView适配器
	 */
	class MyAdapter extends ArrayAdapter<ListItem>{

		List<ListItem> listItems;
		public MyAdapter(Context context, int resource,List<ListItem> listItems) {
			super(context, resource);
			this.listItems = listItems;
		}

		public MyAdapter(Context context,int resource){
			super(context,resource);
			listItems = new ArrayList<ListItem>();
		}

		public void setListItems(ArrayList<ListItem> listItems){
			this.listItems = listItems;
		}
		@Override
		public void clear() {
			super.clear();
			this.listItems.clear();
		}

		public void addItem(ListItem listItem){
			add(listItem);
			this.listItems.add(listItem);
		}






		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ListItem listItem = listItems.get(position);
			LayoutInflater inflater = LayoutInflater.from(getContext());
			View view = inflater.inflate(R.layout.searchpage_listview_item, null);
			TextView name,distance,time;
			ImageView imageView;
			imageView = (ImageView) view.findViewById(R.id.id_search_listview_item_image);
			name = (TextView) view.findViewById(R.id.id_search_listview_item_name);
			distance = (TextView) view.findViewById(R.id.id_search_listview_item_distance);
			time = (TextView) view.findViewById(R.id.id_search_listview_item_time);
			name.setText(listItem.getName());
			distance.setText(listItem.getDistance());
			time.setText(listItem.getTime());
			switch (Utils.poisitemType(listItem.getDistance())){
				case 0:
					imageView.setImageResource(R.drawable.default_generalsearch_nearby_icon_service);
					break;
				case 1:
					imageView.setImageResource(R.drawable.default_generalsearch_nearby_icon_shopping);
					break;
				case 2:
					imageView.setImageResource(R.drawable.default_generalsearch_nearby_icon_eat);
					break;
				case 3:
					imageView.setImageResource(R.drawable.default_generalsearch_nearby_icon_play);
					break;
				case 4:
					imageView.setImageResource(R.drawable.default_generalsearch_nearby_icon_life);
					break;
				case 5:
					imageView.setImageResource(R.drawable.default_generalsearch_nearby_icon_travel);
					break;
				case 6:
					imageView.setImageResource(R.drawable.default_generalsearch_nearby_icon_live);
					break;
				case 7:
					imageView.setImageResource(R.drawable.default_generalsearch_nearby_icon_walk);
					break;
			}

			return view;
		}
	}




}
