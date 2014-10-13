package net.oschina.app;

import java.util.Properties;
import java.util.UUID;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import net.oschina.app.api.ApiHttpClient;
import net.oschina.app.base.BaseApplication;
import net.oschina.app.bean.UserInformation;
import net.oschina.app.util.CyptoUtils;
import net.oschina.app.util.FileUtils;
import net.oschina.app.util.StringUtils;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 * 
 * @author 火蚁 (http://my.oschina.net/LittleDY)
 * @version 1.0
 * @created 2014-04-22
 */
public class AppContext extends BaseApplication {
	
	private static final String KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN";
	private static final String KEY_SOFTKEYBOARD_HEIGHT = "KEY_SOFTKEYBOARD_HEIGHT";
	private static final String KEY_LOAD_IMAGE = "KEY_LOAD_IMAGE";
	private static final String KEY_NOTIFICATION_SOUND = "KEY_NOTIFICATION_SOUND";
	private static final String LAST_QUESTION_CATEGORY_IDX = "LAST_QUESTION_CATEGORY_IDX";
	private static final String KEY_DAILY_ENGLISH = "KEY_DAILY_ENGLISH";
	private static final String KEY_GET_LAST_DAILY_ENG = "KEY_GET_LAST_DAILY_ENG";
	private static final String KEY_NOTIFICATION_DISABLE_WHEN_EXIT = "KEY_NOTIFICATION_DISABLE_WHEN_EXIT";
	private static final String KEY_TWEET_DRAFT = "key_tweet_draft";
	private static final String KEY_QUESTION_TITLE_DRAFT = "key_question_title_draft";
	private static final String KEY_QUESTION_CONTENT_DRAFT = "key_question_content_draft";
	private static final String KEY_QUESTION_TYPE_DRAFT = "key_question_type_draft";
	private static final String KEY_QUESTION_LMK_DRAFT = "key_question_lmk_draft";
	
	// 手机网络类型
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;

	public static final int PAGE_SIZE = 20;// 默认分页大小
	private static final int CACHE_TIME = 60 * 60000;// 缓存失效时间
	
	private static AppContext instance;
	
	private int loginUid;
	
	private boolean login;
	
	@Override
	public void onCreate() {
		super.onCreate();
		// 注册App异常崩溃处理器
		Thread.setDefaultUncaughtExceptionHandler(AppException
				.getAppExceptionHandler(this));
		instance = this;
		init();
		initLogin();
		// 初始化图片加载
		initImageLoader(this);
	}
	
	private void init() {
		// 初始化网络请求
		AsyncHttpClient client = new AsyncHttpClient();
		PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
		client.setCookieStore(myCookieStore);
		ApiHttpClient.setHttpClient(client);
		ApiHttpClient.setCookie(ApiHttpClient.getCookie(this));
	}
	
	private void initLogin() {
		UserInformation user = getLoginUser();
		if (null != user && user.getUid() > 0) {
			login = true;
			loginUid = user.getUid();
		} else {
			this.cleanLoginInfo();
		}
	}
	
	/**
	 * 配置图片加载器
	 * @param context
	 */
	public static void initImageLoader(Context context) {
		DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
				.preProcessor(new BitmapProcessor() {
					@Override
					public Bitmap process(Bitmap source) {
						return source;
					}
				}).cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Config.ARGB_8888).build();
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)
				// 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.defaultDisplayImageOptions(displayOptions).build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	
	/**
	 * 获得当前app运行的AppContext
	 * @return
	 */
	public static AppContext getInstance() {
		return instance;
	}
	
	public boolean containsProperty(String key) {
		Properties props = getProperties();
		return props.containsKey(key);
	}

	public void setProperties(Properties ps) {
		AppConfig.getAppConfig(this).set(ps);
	}

	public Properties getProperties() {
		return AppConfig.getAppConfig(this).get();
	}

	public void setProperty(String key, String value) {
		AppConfig.getAppConfig(this).set(key, value);
	}

	public String getProperty(String key) {
		String res = AppConfig.getAppConfig(this).get(key);
		return res;
	}

	public void removeProperty(String... key) {
		AppConfig.getAppConfig(this).remove(key);
	}
	
	/**
	 * 获取App唯一标识
	 * 
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if (StringUtils.isEmpty(uniqueID)) {
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}

	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}
	
	/**
	 * 保存登录信息
	 * 
	 * @param username
	 * @param pwd
	 */
	@SuppressWarnings("serial")
	public void saveLoginInfo(final UserInformation user) {
		this.loginUid = user.getUid();
		this.login = true;
		setProperties(new Properties() {
			{
				setProperty("user.uid", String.valueOf(user.getUid()));
				setProperty("user.name", user.getName());
				setProperty("user.face", user.getPortrait());// 用户头像-文件名
				setProperty("user.account", user.getAccount());
				setProperty("user.pwd",
						CyptoUtils.encode("oschinaApp", user.getPwd()));
				setProperty("user.location", user.getLocation());
				setProperty("user.followers",
						String.valueOf(user.getFollowers()));
				setProperty("user.fans", String.valueOf(user.getFans()));
				setProperty("user.score", String.valueOf(user.getScore()));
				setProperty("user.isRememberMe",
						String.valueOf(user.isRememberMe()));// 是否记住我的信息
			}
		});
	}
	
	/**
	 * 获得登录用户的信息
	 * @return
	 */
	public UserInformation getLoginUser() {
		UserInformation user = new UserInformation();
		user.setUid(StringUtils.toInt(getProperty("user.uid"), 0));
		user.setName(getProperty("user.name"));
		user.setPortrait(getProperty("user.face"));
		user.setAccount(getProperty("user.account"));
		user.setPwd(CyptoUtils.decode("oschinaApp", getProperty("user.pwd")));
		user.setLocation(getProperty("user.location"));
		user.setFollowers(StringUtils.toInt(getProperty("user.followers"), 0));
		user.setFans(StringUtils.toInt(getProperty("user.fans"), 0));
		user.setScore(StringUtils.toInt(getProperty("user.score"), 0));
		user.setRememberMe(StringUtils.toBool(getProperty("user.isRememberMe")));
		return user;
	}

	/**
	 * 清除登录信息
	 */
	public void cleanLoginInfo() {
		this.loginUid = 0;
		this.login = false;
		removeProperty("user.uid", "user.name", "user.face", "user.account",
				"user.pwd", "user.location", "user.followers", "user.fans",
				"user.score", "user.isRememberMe");
	}
	
	public int getLoginUid() {
		return loginUid;
	}

	public boolean isLogin() {
		return login;
	}
	
	public static boolean shouldLoadImage() {
		return getPreferences().getBoolean(KEY_LOAD_IMAGE, true);
	}
	
	public static void setSoftKeyboardHeight(int height) {
		Editor editor = getPreferences().edit();
		editor.putInt(KEY_SOFTKEYBOARD_HEIGHT, height);
		apply(editor);
	}

	public static int getSoftKeyboardHeight() {
		return getPreferences().getInt(KEY_SOFTKEYBOARD_HEIGHT, 0);
	}
}
