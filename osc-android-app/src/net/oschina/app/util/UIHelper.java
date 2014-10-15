package net.oschina.app.util;

import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import net.oschina.app.AppContext;
import net.oschina.app.bean.News;
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.interf.OnWebViewImageListener;
import net.oschina.app.ui.DetailActivity;
import net.oschina.app.ui.ImagePreviewActivity;
import net.oschina.app.ui.LoginActivity;
import net.oschina.app.ui.SimpleBackActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.webkit.JavascriptInterface;
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
			+ "a.tag {font-size:15px;text-decoration:none;background-color:#cfc;color:#060;border-bottom:1px solid #B1D3EB;border-right:1px solid #B1D3EB;color:#3E6D8E;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;position:relative}"
			+ "</style>";

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
	 * 显示博客详情
	 * 
	 * @param context
	 * @param blogId
	 */
	public static void showBlogDetail(Context context, int blogId) {
		Intent intent = new Intent(context, DetailActivity.class);
		intent.putExtra("blog_id", blogId);
		intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
				DetailActivity.DISPLAY_BLOG);
		context.startActivity(intent);
	}
	
	/**
	 * 显示帖子详情
	 * 
	 * @param context
	 * @param postId
	 */
	public static void showPostDetail(Context context, int postId) {
		Intent intent = new Intent(context, DetailActivity.class);
		intent.putExtra("post_id", postId);
		intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
				DetailActivity.DISPLAY_POST);
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
				showPostDetail(context, StringUtils.toInt(objId));
				break;
			case News.NEWSTYPE_BLOG:
				showBlogDetail(context, StringUtils.toInt(objId));
				break;
			}
		} else {
			showUrlRedirect(context, url);
		}
	}
	
	/**
	 * 添加网页的点击图片展示支持
	 */
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@JavascriptInterface
	public static void addWebImageShow(final Context cxt, WebView wv) {
		wv.getSettings().setJavaScriptEnabled(true);
		wv.addJavascriptInterface(new OnWebViewImageListener() {
			
			@Override
			@JavascriptInterface
			public void onImageClick(String bigImageUrl) {
				if (bigImageUrl != null) {
					UIHelper.showImagePreview(cxt, new String[] { bigImageUrl });
				}
			}
		}, "mWebViewImageListener");
	}
	
	public static String setHtmlCotentSupportImagePreview(String body) {
		// 读取用户设置：是否加载文章图片--默认有wifi下始终加载图片
		if (AppContext.shouldLoadImage() || TDevice.isWifiOpen()) {
			// 过滤掉 img标签的width,height属性
			body = body.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
			body = body.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");
			// 添加点击图片放大支持
			body = body.replaceAll("(<img[^>]+src=\")(\\S+)\"",
					"$1$2\" onClick=\"showImagePreview('$2')\"");
		} else {
			// 过滤掉 img标签
			body = body.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");
		}
		return body;
	}
	
	/**
	 * url跳转
	 * 
	 * @param context
	 * @param url
	 */
	public static void showUrlRedirect(Context context, String url) {
		if (url.startsWith(IAM_API_SHOWIMAGE)) {
			String realUrl = url.substring(IAM_API_SHOWIMAGE.length());
			try {
				JSONObject json = new JSONObject(realUrl);
				int idx = json.optInt("index");
				String[] urls = json.getString("urls").split(",");
				showImagePreview(context, idx, urls);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return;
		}
		URLsUtils urls = URLsUtils.parseURL(url);
		if (urls != null) {
			showLinkRedirect(context, urls.getObjType(), urls.getObjId(),
					urls.getObjKey());
		} else {
			openBrowser(context, url);
		}
	}

	public static void showLinkRedirect(Context context, int objType,
			int objId, String objKey) {
		switch (objType) {
		case URLsUtils.URL_OBJ_TYPE_NEWS:
			showNewsDetail(context, objId);
			break;
		case URLsUtils.URL_OBJ_TYPE_QUESTION:
			showPostDetail(context, objId);
			break;
		case URLsUtils.URL_OBJ_TYPE_QUESTION_TAG:
			//showQuestionListByTag(context, objKey);
			break;
		case URLsUtils.URL_OBJ_TYPE_SOFTWARE:
			//showSoftwareDetail(context, objKey);
			break;
		case URLsUtils.URL_OBJ_TYPE_ZONE:
			//showUserCenter(context, objId, objKey);
			break;
		case URLsUtils.URL_OBJ_TYPE_TWEET:
			//showTweetDetail(context, objId);
			break;
		case URLsUtils.URL_OBJ_TYPE_BLOG:
			showBlogDetail(context, objId);
			break;
		case URLsUtils.URL_OBJ_TYPE_OTHER:
			openBrowser(context, objKey);
			break;
		}
	}

	/**
	 * 打开浏览器
	 * 
	 * @param context
	 * @param url
	 */
	public static void openBrowser(Context context, String url) {
		try {
			Uri uri = Uri.parse(url);
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			context.startActivity(it);
		} catch (Exception e) {
			e.printStackTrace();
			AppContext.showToastShort("无法浏览此网页");
		}
	}
	
	@JavascriptInterface
	public static void showImagePreview(Context context, String[] imageUrls) {
		ImagePreviewActivity.showImagePrivew(context, 0, imageUrls);
	}

	@JavascriptInterface
	public static void showImagePreview(Context context, int index,
			String[] imageUrls) {
		ImagePreviewActivity.showImagePrivew(context, index, imageUrls);
	}
	
	public static void showSimpleBackForResult(Fragment fragment,
			int requestCode, SimpleBackPage page, Bundle args) {
		Intent intent = new Intent(fragment.getActivity(),
				SimpleBackActivity.class);
		intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
		intent.putExtra(SimpleBackActivity.BUNDLE_KEY_ARGS, args);
		fragment.startActivityForResult(intent, requestCode);
	}

	public static void showSimpleBackForResult(Activity context,
			int requestCode, SimpleBackPage page, Bundle args) {
		Intent intent = new Intent(context, SimpleBackActivity.class);
		intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
		intent.putExtra(SimpleBackActivity.BUNDLE_KEY_ARGS, args);
		context.startActivityForResult(intent, requestCode);
	}

	public static void showSimpleBackForResult(Activity context,
			int requestCode, SimpleBackPage page) {
		Intent intent = new Intent(context, SimpleBackActivity.class);
		intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
		context.startActivityForResult(intent, requestCode);
	}

	public static void showSimpleBack(Context context, SimpleBackPage page) {
		Intent intent = new Intent(context, SimpleBackActivity.class);
		intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
		context.startActivity(intent);
	}

	public static void showSimpleBack(Context context, SimpleBackPage page,
			Bundle args) {
		Intent intent = new Intent(context, SimpleBackActivity.class);
		intent.putExtra(SimpleBackActivity.BUNDLE_KEY_ARGS, args);
		intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
		context.startActivity(intent);
	}
}
