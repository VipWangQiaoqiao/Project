package net.oschina.app.util;

import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.zbar.lib.CaptureActivity;

import net.oschina.app.AppContext;
import net.oschina.app.AppManager;
import net.oschina.app.R;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.bean.Active;
import net.oschina.app.bean.Constants;
import net.oschina.app.bean.News;
import net.oschina.app.bean.Notice;
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.fragment.CommentFrament;
import net.oschina.app.fragment.FriendsFragment;
import net.oschina.app.fragment.QuestionTagFragment;
import net.oschina.app.fragment.SoftWareTweetsFrament;
import net.oschina.app.interf.OnWebViewImageListener;
import net.oschina.app.service.NoticeService;
import net.oschina.app.ui.DetailActivity;
import net.oschina.app.ui.ImagePreviewActivity;
import net.oschina.app.ui.LoginActivity;
import net.oschina.app.ui.SimpleBackActivity;
import net.oschina.app.ui.dialog.CommonDialog;
import net.oschina.app.viewpagefragment.FriendsViewPagerFragment;
import net.oschina.app.widget.AvatarView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * 界面帮助类
 * 
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
			+ "a.tag {font-size:15px;text-decoration:none;background-color:#cfc;color:#060;border-bottom:1px solid #B1D3EB;border-right:1px solid #B1D3EB;color:#3E6D8E;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;position:relative}</style>";

	public static final String WEB_LOAD_IMAGES = "<script type=\"text/javascript\"> var allImgUrls = getAllImgSrc(document.body.innerHTML);</script>";

	private static final String IAM_API_SHOWIMAGE = "ima-api:action=showImage&data=";

	/**
	 * 显示登录界面
	 * 
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
	 * 显示相关Tag帖子列表
	 * 
	 * @param context
	 * @param tag
	 */
	public static void showPostListByTag(Context context, String tag) {
		Bundle args = new Bundle();
		args.putString(QuestionTagFragment.BUNDLE_KEY_TAG, tag);
		showSimpleBack(context, SimpleBackPage.QUESTION_TAG, args);
	}

	/**
	 * 显示动弹详情
	 * 
	 * @param context
	 * @param id
	 */
	public static void showTweetDetail(Context context, int tweetid) {
		Intent intent = new Intent(context, DetailActivity.class);
		intent.putExtra("tweet_id", tweetid);
		intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
				DetailActivity.DISPLAY_TWEET);
		context.startActivity(intent);
	}

	/**
	 * 显示软件详情
	 * 
	 * @param context
	 * @param ident
	 */
	public static void showSoftwareDetail(Context context, String ident) {
		Intent intent = new Intent(context, DetailActivity.class);
		intent.putExtra("ident", ident);
		intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
				DetailActivity.DISPLAY_SOFTWARE);
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
				showSoftwareDetail(context, objId);
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
	 * 动态点击跳转到相关新闻、帖子等
	 * 
	 * @param context
	 * @param id
	 * @param catalog
	 *            0其他 1新闻 2帖子 3动弹 4博客
	 */
	public static void showActiveRedirect(Context context, Active active) {
		String url = active.getUrl();
		// url为空-旧方法
		if (StringUtils.isEmpty(url)) {
			int id = active.getObjectId();
			int catalog = active.getCatalog();
			switch (catalog) {
			case Active.CATALOG_OTHER:
				// 其他-无跳转
				break;
			case Active.CATALOG_NEWS:
				showNewsDetail(context, id);
				break;
			case Active.CATALOG_POST:
				showPostDetail(context, id);
				break;
			case Active.CATALOG_TWEET:
				showTweetDetail(context, id);
				break;
			case Active.CATALOG_BLOG:
				showBlogDetail(context, id);
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
			showPostListByTag(context, objKey);
			break;
		case URLsUtils.URL_OBJ_TYPE_SOFTWARE:
			showSoftwareDetail(context, objKey);
			break;
		case URLsUtils.URL_OBJ_TYPE_ZONE:
			showUserCenter(context, objId, objKey);
			break;
		case URLsUtils.URL_OBJ_TYPE_TWEET:
			showTweetDetail(context, objId);
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

		if (StringUtils.isImgUrl(url)) {
			ImagePreviewActivity.showImagePrivew(context, 0,
					new String[] { url });
			return;
		}
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

	public static void showComment(Context context, int id, int catalog) {
		Bundle args = new Bundle();
		args.putInt(CommentFrament.BUNDLE_KEY_ID, id);
		args.putInt(CommentFrament.BUNDLE_KEY_CATALOG, catalog);
		showSimpleBack(context, SimpleBackPage.COMMENT, args);
	}

	public static void showSoftWareTweets(Context context, int id) {
		Bundle args = new Bundle();
		args.putInt(SoftWareTweetsFrament.BUNDLE_KEY_ID, id);
		showSimpleBack(context, SimpleBackPage.SOFTWARE_TWEETS, args);
	}

	public static void showBlogComment(Context context, int id, int ownerId) {
		Bundle args = new Bundle();
		args.putInt(CommentFrament.BUNDLE_KEY_ID, id);
		args.putInt(CommentFrament.BUNDLE_KEY_OWNER_ID, ownerId);
		args.putBoolean(CommentFrament.BUNDLE_KEY_BLOG, true);
		showSimpleBack(context, SimpleBackPage.COMMENT, args);
	}

	public static SpannableString parseActiveAction(int objecttype,
			int objectcatalog, String objecttitle) {
		String title = "";
		int start = 0;
		int end = 0;
		if (objecttype == 32 && objectcatalog == 0) {
			title = "加入了开源中国";
		} else if (objecttype == 1 && objectcatalog == 0) {
			title = "添加了开源项目 " + objecttitle;
		} else if (objecttype == 2 && objectcatalog == 1) {
			title = "在讨论区提问：" + objecttitle;
		} else if (objecttype == 2 && objectcatalog == 2) {
			title = "发表了新话题：" + objecttitle;
		} else if (objecttype == 3 && objectcatalog == 0) {
			title = "发表了博客 " + objecttitle;
		} else if (objecttype == 4 && objectcatalog == 0) {
			title = "发表一篇新闻 " + objecttitle;
		} else if (objecttype == 5 && objectcatalog == 0) {
			title = "分享了一段代码 " + objecttitle;
		} else if (objecttype == 6 && objectcatalog == 0) {
			title = "发布了一个职位：" + objecttitle;
		} else if (objecttype == 16 && objectcatalog == 0) {
			title = "在新闻 " + objecttitle + " 发表评论";
		} else if (objecttype == 17 && objectcatalog == 1) {
			title = "回答了问题：" + objecttitle;
		} else if (objecttype == 17 && objectcatalog == 2) {
			title = "回复了话题：" + objecttitle;
		} else if (objecttype == 17 && objectcatalog == 3) {
			title = "在 " + objecttitle + " 对回帖发表评论";
		} else if (objecttype == 18 && objectcatalog == 0) {
			title = "在博客 " + objecttitle + " 发表评论";
		} else if (objecttype == 19 && objectcatalog == 0) {
			title = "在代码 " + objecttitle + " 发表评论";
		} else if (objecttype == 20 && objectcatalog == 0) {
			title = "在职位 " + objecttitle + " 发表评论";
		} else if (objecttype == 101 && objectcatalog == 0) {
			title = "回复了动态：" + objecttitle;
		} else if (objecttype == 100) {
			title = "更新了动态";
		}
		SpannableString sp = new SpannableString(title);
		// 设置标题字体大小、高亮
		if (!StringUtils.isEmpty(objecttitle)) {
			start = title.indexOf(objecttitle);
			if (objecttitle.length() > 0 && start > 0) {
				end = start + objecttitle.length();
				sp.setSpan(new AbsoluteSizeSpan(14, true), start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				sp.setSpan(
						new ForegroundColorSpan(Color.parseColor("#0e5986")),
						start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		return sp;
	}

	/**
	 * 组合动态的回复文本
	 * 
	 * @param name
	 * @param body
	 * @return
	 */
	public static SpannableStringBuilder parseActiveReply(String name,
			String body) {
		Spanned span = Html.fromHtml(body.trim());
		SpannableStringBuilder sp = new SpannableStringBuilder(name + "：");
		sp.append(span);
		// 设置用户名字体加粗、高亮
		// sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
		// name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new ForegroundColorSpan(Color.parseColor("#576B95")), 0,
				name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return sp;
	}

	/**
	 * 发送App异常崩溃报告
	 * 
	 * @param cont
	 * @param crashReport
	 */
	public static void sendAppCrashReport(final Context context,
			final String crashReport) {
		CommonDialog dialog = new CommonDialog(context);

		dialog.setTitle(R.string.app_error);
		dialog.setMessage(R.string.app_error_message);
		dialog.setPositiveButton(R.string.submit_report,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 发送异常报告
						TDevice.sendEmail(context, crashReport,
								"zhangdeyi@oschina.net");
						// 退出
						AppManager.getAppManager().AppExit(context);
					}
				});
		dialog.setNegativeButton(R.string.cancle,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 退出
						AppManager.getAppManager().AppExit(context);
					}
				});
		dialog.show();
	}

	/**
	 * 发送通知广播
	 * 
	 * @param context
	 * @param notice
	 */
	public static void sendBroadCast(Context context, Notice notice) {
		if (!((AppContext) context.getApplicationContext()).isLogin()
				|| notice == null)
			return;
		TLog.log("NOTICE", "发送通知广播");
		Intent intent = new Intent(Constants.INTENT_ACTION_NOTICE);
		Bundle bundle = new Bundle();
		bundle.putSerializable("notice_bean", notice);
		bundle.putInt("atmeCount", notice.getAtmeCount());
		bundle.putInt("msgCount", notice.getMsgCount());
		bundle.putInt("reviewCount", notice.getReviewCount());
		bundle.putInt("newFansCount", notice.getNewFansCount());
		intent.putExtras(bundle);
		context.sendBroadcast(intent);
	}

	/**
	 * 发送通知广播
	 * 
	 * @param context
	 */
	public static void sendBroadcastForNotice(Context context) {
		Intent intent = new Intent(NoticeService.INTENT_ACTION_BROADCAST);
		context.sendBroadcast(intent);
	}

	/**
	 * 显示用户中心页面
	 * 
	 * @param context
	 * @param uid
	 * @param hisuid
	 * @param hisname
	 */
	public static void showUserCenter(Context context, int hisuid,
			String hisname) {
		Bundle args = new Bundle();
		args.putInt("his_id", hisuid);
		args.putString("his_name", hisname);
		showSimpleBack(context, SimpleBackPage.USER_CENTER, args);
	}

	/**
	 * 显示用户的博客列表
	 * 
	 * @param context
	 * @param uid
	 */
	public static void showUserBlog(Context context, int uid) {
		Bundle args = new Bundle();
		args.putInt(BaseListFragment.BUNDLE_KEY_CATALOG, uid);
		showSimpleBack(context, SimpleBackPage.USER_BLOG, args);
	}

	/**
	 * 显示用户头像大图
	 * 
	 * @param context
	 * @param avatarUrl
	 */
	public static void showUserAvatar(Context context, String avatarUrl) {
		if (StringUtils.isEmpty(avatarUrl)) {
			return;
		}
		String url = AvatarView.getLargeAvatar(avatarUrl);
		ImagePreviewActivity.showImagePrivew(context, 0, new String[] { url });
	}

	/**
	 * 显示登陆用户的个人中心页面
	 * 
	 * @param context
	 */
	public static void showMyInformation(Context context) {
		showSimpleBack(context, SimpleBackPage.MY_INFORMATION);
	}

	/**
	 * 显示我的所有动态
	 * 
	 * @param context
	 */
	public static void showMyActive(Context context) {
		showSimpleBack(context, SimpleBackPage.MY_ACTIVE);
	}

	/**
	 * 显示扫一扫界面
	 * 
	 * @param context
	 */
	public static void showScanActivity(Context context) {
		Intent intent = new Intent(context, CaptureActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 显示用户的消息中心
	 * 
	 * @param context
	 */
	public static void showMyMes(Context context) {
		showSimpleBack(context, SimpleBackPage.MY_MES);
	}

	/**
	 * 显示用户的关注/粉丝列表
	 * 
	 * @param context
	 */
	public static void showFriends(Context context, int uid, int tabIdx) {
		Bundle args = new Bundle();
		args.putInt(FriendsViewPagerFragment.BUNDLE_KEY_TABIDX, tabIdx);
		args.putInt(FriendsFragment.BUNDLE_KEY_UID, uid);
		showSimpleBack(context, SimpleBackPage.MY_FRIENDS, args);
	}
}
