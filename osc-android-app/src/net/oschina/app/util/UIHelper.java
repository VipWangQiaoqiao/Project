package net.oschina.app.util;

import net.oschina.app.ui.LoginActivity;
import android.content.Context;
import android.content.Intent;

/** 
 * 界面帮助类
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年10月10日 下午3:33:36 
 * 
 */
public class UIHelper {
	
	/**
	 * 显示登录界面
	 * @param context
	 */
	public static void showLoginActivity(Context context) {
		Intent intent = new Intent(context, LoginActivity.class);
		context.startActivity(intent);
	}
}
