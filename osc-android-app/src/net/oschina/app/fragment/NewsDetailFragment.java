package net.oschina.app.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseDetailFragment;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.Entity;
import net.oschina.app.bean.FavoriteList;
import net.oschina.app.bean.News;
import net.oschina.app.bean.News.Relative;
import net.oschina.app.bean.NewsDetail;
import net.oschina.app.emoji.EmojiFragment;
import net.oschina.app.emoji.EmojiFragment.EmojiTextListener;
import net.oschina.app.fragment.ToolbarFragment.OnActionClickListener;
import net.oschina.app.fragment.ToolbarFragment.ToolAction;
import net.oschina.app.interf.EmojiFragmentControl;
import net.oschina.app.interf.ToolbarEmojiVisiableControl;
import net.oschina.app.interf.ToolbarFragmentControl;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.URLsUtils;
import net.oschina.app.util.XmlUtils;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class NewsDetailFragment extends BaseDetailFragment implements
        ToolbarFragmentControl, EmojiTextListener, EmojiFragmentControl {

    protected static final String TAG = NewsDetailFragment.class
            .getSimpleName();
    private static final String NEWS_CACHE_KEY = "news_";
    @InjectView(R.id.tv_title)
    TextView mTvTitle;
    @InjectView(R.id.tv_source)
    TextView mTvSource;
    @InjectView(R.id.tv_time)
    TextView mTvTime;
    private int mNewsId;
    private int mCommentCount;
    private News mNews;
    private EmojiFragment mEmojiFragment;
    private ToolbarFragment mToolBarFragment;

    private final OnClickListener mMoreListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Activity act = getActivity();
            if (act != null && act instanceof ToolbarEmojiVisiableControl) {
                ((ToolbarEmojiVisiableControl) act).toggleToolbarEmoji();
            }
        }
    };

    private final OnActionClickListener mActionListener = new OnActionClickListener() {

        @Override
        public void onActionClick(ToolAction action) {
            switch (action) {
            case ACTION_CHANGE:
                Activity act = getActivity();
                if (act != null && act instanceof ToolbarEmojiVisiableControl) {
                    ((ToolbarEmojiVisiableControl) act).toggleToolbarEmoji();
                }
                break;
            case ACTION_WRITE_COMMENT:
                act = getActivity();
                if (act != null && act instanceof ToolbarEmojiVisiableControl) {
                    ((ToolbarEmojiVisiableControl) act).toggleToolbarEmoji();
                }
                mEmojiFragment.showKeyboardIfNoEmojiGrid();
                break;
            case ACTION_VIEW_COMMENT:
                if (mNews != null)
                    UIHelper.showComment(getActivity(), mNewsId,
                            CommentList.CATALOG_NEWS);
                break;
            case ACTION_FAVORITE:
                handleFavoriteOrNot();
                break;
            case ACTION_SHARE:
                handleShare();
                break;
            default:
                break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_detail, container,
                false);

        mNewsId = getActivity().getIntent().getIntExtra("news_id", 0);
        mCommentCount = getActivity().getIntent().getIntExtra("comment_count", 0);
        ButterKnife.inject(this, view);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mEmptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);

        mWebView = (WebView) view.findViewById(R.id.webview);
        UIHelper.initWebView(mWebView);
    }

    @Override
    protected String getCacheKey() {
        return new StringBuilder(NEWS_CACHE_KEY).append(mNewsId).toString();
    }

    @Override
    protected void sendRequestData() {
        mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        OSChinaApi.getNewsDetail(mNewsId, mHandler);
    }

    @Override
    protected Entity parseData(InputStream is) throws Exception {
        return XmlUtils.toBean(NewsDetail.class, is).getNews();
    }

    @Override
    protected Entity readData(Serializable seri) {
        return (News) seri;
    }

    @Override
    protected void onCommentChanged(int opt, int id, int catalog,
            boolean isBlog, Comment comment) {
        if (id == mNewsId && catalog == CommentList.CATALOG_NEWS && !isBlog) {
            if (Comment.OPT_ADD == opt && mNews != null) {
                mNews.setCommentCount(mNews.getCommentCount() + 1);
                if (mToolBarFragment != null) {
                    mToolBarFragment.setCommentCount(mNews.getCommentCount());
                }
            }
        }
    }

    @Override
    protected void executeOnLoadDataSuccess(Entity entity) {
        mNews = (News) entity;
        fillUI();
        fillWebViewBody();
    }

    private void fillUI() {
        mTvTitle.setText(mNews.getTitle());
        mTvSource.setText(mNews.getAuthor());
        mTvTime.setText(StringUtils.friendly_time(mNews.getPubDate()));
        if (mToolBarFragment != null) {
        	if (mCommentCount <= 0) {
        		mCommentCount = mNews.getCommentCount();
        	}
        	mToolBarFragment.setCommentCount(mCommentCount);
        }
        notifyFavorite(mNews.getFavorite() == 1);
    }

    private void fillWebViewBody() {

        StringBuffer body = new StringBuffer();
        body.append(UIHelper.setHtmlCotentSupportImagePreview(mNews.getBody()));

        body.append(UIHelper.WEB_STYLE).append(UIHelper.WEB_LOAD_IMAGES);

        // 更多关于***软件的信息
        String softwareName = mNews.getSoftwareName();
        String softwareLink = mNews.getSoftwareLink();
        if (!StringUtils.isEmpty(softwareName)
                && !StringUtils.isEmpty(softwareLink))
            body.append(String
                    .format("<div id='oschina_software' style='margin-top:8px;color:#FF0000;font-weight:bold'>更多关于:&nbsp;<a href='%s'>%s</a>&nbsp;的详细信息</div>",
                            softwareLink, softwareName));

        // 相关新闻
        if (mNews != null && mNews.getRelatives() != null
                && mNews.getRelatives().size() > 0) {
            String strRelative = "";
            for (Relative relative : mNews.getRelatives()) {
                strRelative += String.format(
                        "<a href='%s' style='text-decoration:none'>%s</a><p/>",
                        relative.url, relative.title);
            }
            body.append("<p/><div style=\"height:1px;width:100%;background:#DADADA;margin-bottom:10px;\"/>"
                    + String.format("<br/> <b>相关资讯</b> <div><p/>%s</div>",
                            strRelative));
        }
        body.append("<br/>");
        mWebView.loadDataWithBaseURL(null, body.toString(), "text/html",
                "utf-8", null);
    }

    @Override
    protected void onFavoriteChanged(boolean flag) {
        mNews.setFavorite(flag ? 1 : 0);
        if (mToolBarFragment != null) {
            mToolBarFragment.setFavorite(flag);
        }
        saveCache(mNews);
    }

    @Override
    public void setToolBarFragment(ToolbarFragment fragment) {
        mToolBarFragment = fragment;
        mToolBarFragment.setOnActionClickListener(mActionListener);
        mToolBarFragment.setActionVisiable(ToolAction.ACTION_CHANGE, true);
        mToolBarFragment.setActionVisiable(ToolAction.ACTION_FAVORITE, true);
        mToolBarFragment.setActionVisiable(ToolAction.ACTION_WRITE_COMMENT,
                true);
        mToolBarFragment
                .setActionVisiable(ToolAction.ACTION_VIEW_COMMENT, true);
        mToolBarFragment.setActionVisiable(ToolAction.ACTION_SHARE, true);
    }

    @Override
    public void setEmojiFragment(EmojiFragment fragment) {
        mEmojiFragment = fragment;
        mEmojiFragment.setEmojiTextListener(this);
        mEmojiFragment.setButtonMoreVisibility(View.VISIBLE);
        mEmojiFragment.setButtonMoreClickListener(mMoreListener);
    }

    @Override
    public void onSendClick(String text) {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_network_error);
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }
        if (TextUtils.isEmpty(text)) {
            AppContext.showToastShort(R.string.tip_comment_content_empty);
            mEmojiFragment.requestFocusInput();
            return;
        }
        showWaitDialog(R.string.progress_submit);
        OSChinaApi.publicComment(CommentList.CATALOG_NEWS, mNewsId, AppContext
                .getInstance().getLoginUid(), text, 0, mCommentHandler);
    }

    @Override
    protected void commentPubSuccess(Comment comment) {
        super.commentPubSuccess(comment);
        mEmojiFragment.reset();
        UIHelper.sendBroadCastCommentChanged(getActivity(), false, mNewsId,
                CommentList.CATALOG_NEWS, Comment.OPT_ADD, comment);
    }

    @Override
    protected int getFavoriteTargetId() {
        return mNews != null ? mNews.getId() : -1;
    }

    @Override
    protected int getFavoriteTargetType() {
        return mNews != null ? FavoriteList.TYPE_NEWS : -1;
    }

    @Override
    protected String getShareTitle() {
        return getString(R.string.share_title_news);
    }

    @Override
    protected String getShareContent() {
        return mNews != null ? mNews.getTitle() : null;
    }

    @Override
    protected String getShareUrl() {
        return mNews != null ? URLsUtils.URL_MOBILE + "news/" + mNews.getId() : null;
    }
}
