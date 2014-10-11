package net.oschina.app.util;

import java.util.regex.Pattern;

import net.oschina.app.AppContext;
import net.oschina.app.bean.News;
import net.oschina.app.interf.OnWebViewImageListener;
import net.oschina.app.ui.DetailActivity;
import net.oschina.app.ui.LoginActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

/** 
 * 界面帮助类
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年10月10日 下午3:33:36 
 * 
 */
public class UIHelper {
	
	/** 表情图片匹配 */
	private static Pattern facePattern = Pattern
			.compile("\\[{1}([0-9]\\d*)\\]{1}");

	/** 全局web样式 */
	// 链接样式文件，代码块高亮的处理
	public final static String linkCss = "<script type=\"text/javascript\" src=\"file:///android_asset/shCore.js\"></script>"
			+ "<script type=\"text/javascript\" src=\"file:///android_asset/brush.js\"></script>"
			+ "<script type=\"text/javascript\" src=\"file:///android_asset/client.js\"></script>"
			+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shThemeDefault.css\">"
			+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shCore.css\">"
			+ "<script type=\"text/javascript\">SyntaxHighlighter.all();</script>"
			+ "<script type=\"text/javascript\">function showImagePreview(var url){window.location.url= url;}</script>";
	public final static String WEB_STYLE = linkCss
			+ "<style>* {font-size:16px;line-height:20px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;} "
			+ "img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#fff;padding:2px;} "
			+ "pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;overflow: auto;} "
			+ "a.tag {font-size:15px;text-decoration:none;background-color:#bbd6f3;border-bottom:2px solid #3E6D8E;border-right:2px solid #7F9FB6;color:#284a7b;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;}</style>";

	public static final String WEB_LOAD_IMAGES = "<script type=\"text/javascript\"> var allImgUrls = getAllImgSrc(document.body.innerHTML);</script>";

	private static final String IAM_API_SHOWIMAGE = "ima-api:action=showImage&data=";
	
	
	/**
	 * 显示登录界面
	 * @param context
	 */
	public static void showLoginActivity(Context context) {
		Intent intent = new Intent(context, LoginActivity.class);
		context.startActivity(intent);
	}
	
	/**
	 * 显示新闻详情
	 * 
	 * @param context
	 * @param newsId
	 */
	public static void showNewsDetail(Context context, int newsId) {
		Intent intent = new Intent(context, DetailActivity.class);
		intent.putExtra("news_id", newsId);
		intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
				DetailActivity.DISPLAY_NEWS);
		context.startActivity(intent);
	}
	
	/**
	 * 新闻超链接点击跳转
	 * 
	 * @param context
	 * @param newsId
	 * @param newsType
	 * @param objId
	 */
	public static void showNewsRedirect(Context context, News news) {
		String url = news.getUrl();
		// url为空-旧方法
		if (StringUtils.isEmpty(url)) {
			int newsId = news.getId();
			int newsType = news.getNewType().type;
			String objId = news.getNewType().attachment;
			switch (newsType) {
			case News.NEWSTYPE_NEWS:
				showNewsDetail(context, newsId);
				break;
			case News.NEWSTYPE_SOFTWARE:
				//showSoftwareDetail(context, objId);
				break;
			case News.NEWSTYPE_POST:
				//showQuestionDetail(context, StringUtils.toInt(objId));
				break;
			case News.NEWSTYPE_BLOG:
				//showBlogDetail(context, StringUtils.toInt(objId));
				break;
			}
		} else {
			//showUrlRedirect(context, url);
		}
	}
	
	/**
	 * 设置显示图片，并支持点击查看
	 * @param body
	 * @return
	 */
	public static String setHtmlCotentSupportImagePreview(String body) {
		// 读取用户设置：是否加载文章图片--默认有wifi下始终加载图片
		if (TDevice.isWifiOpen()) {
			// 过滤掉 img标签的width,height属性
			body = body.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
			body = body.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");
			// 添加点击图片放大支持
			body = body.replaceAll("(<img[^>]+src=\")(\\S+)\"",
					"$1$2\" onClick=\"showImagePreview('$2')\"");
			// mWebViewImageListener.onImageClick
		} else {
			// 过滤掉 img标签
			body = body.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");
		}
		return body;
	}
	
	/**
	 * 添加网页的点击图片展示支持
	 */
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	public static void addWebImageShow(final Context cxt, WebView wv) {
		wv.getSettings().setJavaScriptEnabled(true);
		wv.addJavascriptInterface(new OnWebViewImageListener() {

			@Override
			public void onImageClick(String bigImageUrl) {
				if (bigImageUrl != null) {
					//UIHelper.showImagePreview(cxt, new String[] { bigImageUrl });
				}
			}
		}, "mWebViewImageListener");
	}
}
