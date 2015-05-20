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
import net.oschina.app.emoji.OnSendClickListener;
import net.oschina.app.ui.DetailActivity;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.URLsUtils;
import net.oschina.app.util.XmlUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class NewsDetailFragment extends BaseDetailFragment implements
        OnSendClickListener {

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
    private News mNews;

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_detail, container,
                false);
        mCommentCount = getActivity().getIntent().getIntExtra("comment_count",
                0);
        mNewsId = getActivity().getIntent().getIntExtra("news_id", 0);
        ButterKnife.inject(this, view);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mEmptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
        ((DetailActivity) getActivity()).toolFragment
                .setCommentCount(mCommentCount);
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
            }
        }
    }

    @Override
    protected void executeOnLoadDataSuccess(Entity entity) {
        mNews = (News) entity;
        fillUI();
        fillWebViewBody();
        ((DetailActivity) getActivity()).setCommentCount(mCommentCount);
    }

    private void fillUI() {
        mTvTitle.setText(mNews.getTitle());
        mTvSource.setText(mNews.getAuthor());
        mTvSource.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showUserCenter(getActivity(), mNews.getAuthorId(),
                        mNews.getAuthor());
            }
        });
        mTvTime.setText(StringUtils.friendly_time(mNews.getPubDate()));
        Log.i("kymjs", "newsdetail==128==" + mNews.getFavorite());
        notifyFavorite(mNews.getFavorite() == 1);
    }

    @Override
    public int getCommentCount() {
        return mCommentCount;
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
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, body.toString(), "text/html",
                    "utf-8", null);
        }
    }

    @Override
    protected void onFavoriteChanged(boolean flag) {
        super.onFavoriteChanged(flag);
        mNews.setFavorite(flag ? 1 : 0);
        saveCache(mNews);
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
        return mNews != null ? mNews.getTitle()
                : getString(R.string.share_title_news);
    }

    @Override
    protected String getShareContent() {
        return mNews != null ? StringUtils.getSubString(0, 55,
                getFilterHtmlBody(mNews.getBody())) : "";
    }

    @Override
    protected String getShareUrl() {
        return mNews != null ? URLsUtils.URL_MOBILE + "news/" + mNews.getId()
                : null;
    }

    @Override
    public void onClickSendButton(Editable str) {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_network_error);
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }
        if (TextUtils.isEmpty(str)) {
            AppContext.showToastShort(R.string.tip_comment_content_empty);
            return;
        }
        showWaitDialog(R.string.progress_submit);
        OSChinaApi.publicComment(CommentList.CATALOG_NEWS, mNewsId, AppContext
                .getInstance().getLoginUid(), str.toString(), 0,
                mCommentHandler);
    }

    @Override
    public void onClickFlagButton() {}

    @Override
    public void onclickWriteComment() {
        super.onclickWriteComment();
        if (mNews != null)
            UIHelper.showComment(getActivity(), mNewsId,
                    CommentList.CATALOG_NEWS);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        sendRequestData();
        return super.onOptionsItemSelected(item);
    }
}
