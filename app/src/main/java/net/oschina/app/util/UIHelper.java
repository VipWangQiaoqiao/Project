package net.oschina.app.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ZoomButtonsController;

import com.dtr.zxing.activity.CaptureActivity;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Active;
import net.oschina.app.bean.Banner;
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.fragment.BrowserFragment;
import net.oschina.app.fragment.QuestionTagFragment;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.detail.general.BlogDetailActivity;
import net.oschina.app.improve.detail.general.EventDetailActivity;
import net.oschina.app.improve.detail.general.NewsDetailActivity;
import net.oschina.app.improve.detail.general.QuestionDetailActivity;
import net.oschina.app.improve.detail.general.SoftwareDetailActivity;
import net.oschina.app.improve.media.ImageGalleryActivity;
import net.oschina.app.improve.tweet.activities.TweetDetailActivity;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.user.activities.UserSendMessageActivity;
import net.oschina.app.improve.user.fragments.UserBlogFragment;
import net.oschina.app.improve.user.fragments.UserQuestionFragment;
import net.oschina.app.improve.utils.URLUtils;
import net.oschina.app.interf.OnWebViewImageListener;
import net.oschina.app.team.adapter.TeamMemberAdapter;
import net.oschina.app.team.bean.Team;
import net.oschina.app.team.bean.TeamActive;
import net.oschina.app.team.bean.TeamDiscuss;
import net.oschina.app.team.bean.TeamIssue;
import net.oschina.app.team.bean.TeamIssueCatalog;
import net.oschina.app.team.bean.TeamMember;
import net.oschina.app.team.bean.TeamProject;
import net.oschina.app.team.fragment.TeamActiveFragment;
import net.oschina.app.team.ui.TeamMainActivity;
import net.oschina.app.team.ui.TeamNewIssueActivity;
import net.oschina.app.ui.DetailActivity;
import net.oschina.app.ui.SimpleBackActivity;
import net.oschina.app.widget.AvatarView;

/**
 * 界面帮助类
 *
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年10月10日 下午3:33:36
 */
public class UIHelper {

    /**
     * 全局web样式
     */
    // 链接样式文件，代码块高亮的处理
    public final static String linkCss = "<script type=\"text/javascript\" " +
            "src=\"file:///android_asset/shCore.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/brush.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/client.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/detail_page" +
            ".js\"></script>"
            + "<script type=\"text/javascript\">SyntaxHighlighter.all();</script>"
            + "<script type=\"text/javascript\">function showImagePreview(var url){window" +
            ".location.url= url;}</script>"
            + "<link rel=\"stylesheet\" type=\"text/css\" " +
            "href=\"file:///android_asset/shThemeDefault.css\">"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shCore" +
            ".css\">"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/common" +
            ".css\">";
    public final static String WEB_STYLE = linkCss;

    public static final String WEB_LOAD_IMAGES = "<script type=\"text/javascript\"> var " +
            "allImgUrls = getAllImgSrc(document.body.innerHTML);</script>";

    private static final String SHOWIMAGE = "ima-api:action=showImage&data=";

    /**
     * 显示登录界面
     *
     * @param context
     */
    public static void showLoginActivity(Context context) {
        LoginActivity.show(context);
    }

    /**
     * 显示Team界面
     *
     * @param context
     */
    public static void showTeamMainActivity(Context context) {
        Intent intent = new Intent(context, TeamMainActivity.class);
        context.startActivity(intent);
    }

    /**
     * 显示新闻详情
     *
     * @param context
     * @param newsId
     */
    public static void showNewsDetail(Context context, long newsId,
                                      int commentCount) {
        net.oschina.app.improve.detail.general.NewsDetailActivity.show(context, newsId);
    }


    /**
     * 显示博客详情
     *
     * @param context
     * @param blogId
     */
    public static void showBlogDetail(Context context, long blogId) {
        net.oschina.app.improve.detail.general.BlogDetailActivity.show(context, blogId);
    }

    /**
     * 显示帖子详情
     *
     * @param context
     * @param postId
     */
    public static void showPostDetail(Context context, long postId, int count) {
        net.oschina.app.improve.detail.general.QuestionDetailActivity.show(context, postId);
    }

    /**
     * 显示活动详情
     *
     * @param context
     * @param eventId
     */
    public static void showEventDetail(Context context, long eventId) {
        net.oschina.app.improve.detail.general.EventDetailActivity.show(context, eventId);
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

    public static void showSoftwareDetailById(Context context, int id) {
        net.oschina.app.improve.detail.general.SoftwareDetailActivity.show(context, id);
    }

    /**
     * show detail  method
     *
     * @param context context
     * @param type    type
     * @param id      id
     */
    public static void showDetail(Context context, int type, long id, String href) {
        switch (type) {
            case OSChinaApi.CATALOG_ALL:
                //新闻链接
                showUrlRedirect(context, id, href);
                break;
            case OSChinaApi.CATALOG_SOFTWARE:
                //软件推荐
                SoftwareDetailActivity.show(context, id);
                //UiUtil.showSoftwareDetailById(context, (int) id);
                break;
            case OSChinaApi.CATALOG_QUESTION:
                //问答
                QuestionDetailActivity.show(context, id);
                break;
            case OSChinaApi.CATALOG_BLOG:
                //博客
                BlogDetailActivity.show(context, id);
                break;
            case OSChinaApi.CATALOG_TRANSLATION:
                //4.翻译
                NewsDetailActivity.show(context, id, News.TYPE_TRANSLATE);
                break;
            case OSChinaApi.CATALOG_EVENT:
                //活动
                EventDetailActivity.show(context, id);
                break;
            case OSChinaApi.CATALOG_TWEET:
                // 动弹
                TweetDetailActivity.show(context, id);
                break;
            default:
                //6.资讯
                NewsDetailActivity.show(context, id);
                break;
        }
    }

    public static void showBannerDetail(Context context, Banner banner) {
        long newsId = banner.getId();
        switch (banner.getType()) {
            case Banner.BANNER_TYPE_URL:
                showNewsDetail(context, Integer.parseInt(String.valueOf(newsId)), 0);
                break;
            case Banner.BANNER_TYPE_SOFTWARE:
                showSoftwareDetailById(context, Integer.parseInt(String.valueOf(newsId)));
                break;
            case Banner.BANNER_TYPE_POST:
                showPostDetail(context, StringUtils.toInt(String.valueOf(newsId)),
                        0);
                break;
            case Banner.BANNER_TYPE_BLOG:
                showBlogDetail(context, StringUtils.toLong(String.valueOf(newsId)));
                break;
            case Banner.BANNER_TYPE_EVENT:
                net.oschina.app.improve.detail.general.EventDetailActivity.show(context, newsId);
                break;
            case Banner.BANNER_TYPE_NEWS:
                NewsDetailActivity.show(context, newsId);
            default:
                showUrlRedirect(context, banner.getHref());
                break;
        }
    }

    /**
     * 动态点击跳转到相关新闻、帖子等
     *
     * @param context context
     * @param active  动态实体类
     *                0其他 1新闻 2帖子 3动弹 4博客
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
                    showNewsDetail(context, id, active.getCommentCount());
                    break;
                case Active.CATALOG_POST:
                    showPostDetail(context, id, active.getCommentCount());
                    break;
                case Active.CATALOG_TWEET:
                    TweetDetailActivity.show(context, id);
//                    showTweetDetail(context, null, id);
                    break;
                case Active.CATALOG_BLOG:
                    showBlogDetail(context, id);
                    break;
                default:
                    break;
            }
        } else {
            showUrlRedirect(context, url);
        }
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    public static void initWebView(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setDefaultFontSize(14);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        int sysVersion = Build.VERSION.SDK_INT;
        if (sysVersion >= 11) {
            settings.setDisplayZoomControls(false);
        } else {
            ZoomButtonsController zbc = new ZoomButtonsController(webView);
            zbc.getZoomControls().setVisibility(View.GONE);
        }
        //webView.setWebViewClient(UiUtil.getWebViewClient());
    }

    /**
     * 添加网页的点击图片展示支持
     */
    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    @JavascriptInterface
    public static void addWebImageShow(final Context cxt, WebView wv) {
        wv.getSettings().setJavaScriptEnabled(true);
        wv.addJavascriptInterface(new OnWebViewImageListener() {
            @Override
            @JavascriptInterface
            public void showImagePreview(String bigImageUrl) {
                if (bigImageUrl != null && !StringUtils.isEmpty(bigImageUrl)) {
                    ImageGalleryActivity.show(cxt, bigImageUrl);
                }
            }
        }, "mWebViewImageListener");
    }

    public static String setHtmlCotentSupportImagePreview(String body) {
        // 读取用户设置：是否加载文章图片--默认有wifi下始终加载图片
        if (AppContext.get(AppConfig.KEY_LOAD_IMAGE, true)
                || TDevice.isWifiOpen()) {
            // 过滤掉 img标签的width,height属性
            body = body.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
            body = body.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");
            // 添加点击图片放大支持
            // 添加点击图片放大支持
            body = body.replaceAll("(<img[^>]+src=\")(\\S+)\"",
                    "$1$2\" onClick=\"showImagePreview('$2')\"");
        } else {
            // 过滤掉 img标签
            body = body.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");
        }

        // 过滤table的内部属性
        body = body.replaceAll("(<table[^>]*?)\\s+border\\s*=\\s*\\S+", "$1");
        body = body.replaceAll("(<table[^>]*?)\\s+cellspacing\\s*=\\s*\\S+", "$1");
        body = body.replaceAll("(<table[^>]*?)\\s+cellpadding\\s*=\\s*\\S+", "$1");

        return body;
    }

    private static void showUrlRedirect(Context context, long id, String url) {
        if (url == null && id > 0) {
            net.oschina.app.improve.detail.general.NewsDetailActivity.show(context, id);
            return;
        }

        URLUtils.parseUrl(context, url);
    }

    /**
     * url跳转
     *
     * @param context
     * @param url
     */
    public static void showUrlRedirect(Context context, String url) {
        showUrlRedirect(context, 0, url);
    }

    /**
     * 打开内置浏览器
     *
     * @param context
     * @param url
     */
    public static void openInternalBrowser(Context context, String url) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString(BrowserFragment.BROWSER_KEY, url);
            showSimpleBack(context, SimpleBackPage.BROWSER, bundle);
        } catch (Exception e) {
            e.printStackTrace();
            openExternalBrowser(context, url);
        }
    }

    /**
     * 打开外置的浏览器
     *
     * @param context
     * @param url
     */
    public static void openExternalBrowser(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(Intent.createChooser(intent, "选择打开的应用"));
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
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#008000")), 0,
                name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sp;
    }

    /**
     * 显示用户中心页面
     *
     * @param context
     * @param hisuid
     * @param hisuid
     * @param hisname
     */
    public static void showUserCenter(Context context, long hisuid,
                                      String hisname) {
        if (hisuid == 0 && hisname.equalsIgnoreCase("匿名")) {
            AppContext.showToast("提醒你，该用户为非会员");
            return;
        }
        OtherUserHomeActivity.show(context, hisuid);
    }

    /**
     * 显示用户的博客列表
     *
     * @param context
     * @param uid
     */
    public static void showUserBlog(Context context, long uid) {
        Bundle args = new Bundle();
        args.putLong(UserBlogFragment.BUNDLE_KEY_USER_ID, uid);
        showSimpleBack(context, SimpleBackPage.USER_BLOG, args);
    }

    /**
     * 显示用户的问答列表
     *
     * @param context context
     * @param uid     authorId
     */
    public static void showUserQuestion(Context context, long uid) {
        Bundle args = new Bundle();
        args.putLong(UserQuestionFragment.BUNDLE_KEY_AUTHOR_ID, uid);
        showSimpleBack(context, SimpleBackPage.MY_QUESTION, args);
    }

    /**
     * 显示用户头像大图
     *
     * @param context
     * @param avatarUrl
     */
    public static void showUserAvatar(Context context, String avatarUrl) {
        if (TextUtils.isEmpty(avatarUrl)) {
            return;
        }
        String url = AvatarView.getLargeAvatar(avatarUrl);
        ImageGalleryActivity.show(context, url);
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
     * 显示留言对话页面
     *
     * @param context
     * @param friendid
     * @param friendid
     */
    public static void showMessageDetail(Context context, int friendid,
                                         String friendname) {
        User user = new User();
        user.setId(friendid);
        user.setName(friendname);
        UserSendMessageActivity.show(context, user);
    }

    /**
     * 显示设置界面
     *
     * @param context
     */
    public static void showSetting(Context context) {
        showSimpleBack(context, SimpleBackPage.SETTING);
    }

    /**
     * 显示关于界面
     *
     * @param context
     */
    public static void showAboutOSC(Context context) {
        showSimpleBack(context, SimpleBackPage.ABOUT_OSC);
    }

    /**
     * 清除app缓存
     */
    public static void clearAppCache(boolean showToast) {
        final Handler handler = showToast ? new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    AppContext.showToastShort("缓存清除成功");
                } else {
                    AppContext.showToastShort("缓存清除失败");
                }
            }
        } : null;
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    AppContext.getInstance().clearAppCache();
                    msg.what = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                if (handler != null)
                    handler.sendMessage(msg);
            }
        });
    }

    public static void showCreateNewIssue(Context context, Team team,
                                          TeamProject project, TeamIssueCatalog catalog) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("team", team);
        if (project != null) {
            bundle.putSerializable("project", project);
        }
        if (catalog != null) {
            bundle.putSerializable("catalog", catalog);
        }
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(context, TeamNewIssueActivity.class);
        context.startActivity(intent);
    }

    /***
     * 显示任务详情
     *
     * @param context
     * @param team
     * @param issue
     * @return void
     * @author 火蚁 2015-1-30 下午2:59:57
     */
    public static void showTeamIssueDetail(Context context, Team team,
                                           TeamIssue issue, TeamIssueCatalog catalog) {
        Intent intent = new Intent(context, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("teamid", team.getId());
        bundle.putInt("issueid", issue.getId());
        bundle.putSerializable("team", team);
        bundle.putSerializable("issue", issue);
        bundle.putSerializable("issue_catalog", catalog);
        bundle.putInt(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_TEAM_ISSUE_DETAIL);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 显示讨论贴详情
     *
     * @param context
     * @param team
     * @param discuss
     * @return void
     * @author 火蚁 2015-2-2 下午6:37:53
     */
    public static void showTeamDiscussDetail(Context context, Team team,
                                             TeamDiscuss discuss) {
        Intent intent = new Intent(context, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("teamid", team.getId());
        bundle.putInt("discussid", discuss.getId());
        bundle.putInt(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_TEAM_DISCUSS_DETAIL);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 显示周报详情
     *
     * @param context
     * @param data
     */
    public static void showDiaryDetail(Context context, Bundle data) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("diary", data);
        intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_TEAM_DIARY);
        context.startActivity(intent);
    }

    public static void showTeamMemberInfo(Context context, int teamId,
                                          TeamMember teamMember) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TeamMemberAdapter.TEAM_MEMBER_KEY, teamMember);
        bundle.putInt(TeamMemberAdapter.TEAM_ID_KEY, teamId);
        UIHelper.showSimpleBack(context, SimpleBackPage.TEAM_USER_INFO, bundle);
    }

    /***
     * 显示团队动态详情
     *
     * @param contex
     * @param teamId
     * @param active
     * @return void
     * @author 火蚁 2015-3-13 下午5:34:50
     */
    public static void showTeamActiveDetail(Context contex, int teamId,
                                            TeamActive active) {
        Intent intent = new Intent(contex, DetailActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable(TeamActiveFragment.DYNAMIC_FRAGMENT_KEY, active);
        bundle.putInt(TeamActiveFragment.DYNAMIC_FRAGMENT_TEAM_KEY, teamId);
        bundle.putInt(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_TEAM_TWEET_DETAIL);
        intent.putExtras(bundle);
        contex.startActivity(intent);
    }
}
