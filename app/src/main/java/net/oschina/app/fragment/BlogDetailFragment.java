package net.oschina.app.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseDetailFragment;
import net.oschina.app.bean.Blog;
import net.oschina.app.bean.BlogDetail;
import net.oschina.app.bean.Entity;
import net.oschina.app.bean.FavoriteList;
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

public class BlogDetailFragment extends BaseDetailFragment implements
        OnSendClickListener {

    protected static final String TAG = BlogDetailFragment.class
            .getSimpleName();
    private static final String BLOG_CACHE_KEY = "blog_";
    @InjectView(R.id.tv_title)
    TextView mTvTitle;
    @InjectView(R.id.tv_source)
    TextView mTvSource;
    @InjectView(R.id.tv_time)
    TextView mTvTime;
    private WebView mWebView;
    private int mBlogId;
    private Blog mBlog;

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_detail, container,
                false);

        mCommentCount = getActivity().getIntent().getIntExtra("comment_count",
                0);
        mBlogId = getActivity().getIntent().getIntExtra("blog_id", 0);
        ButterKnife.inject(this, view);
        initViews(view);

        return view;
    }

    private void initViews(View view) {
        ((DetailActivity) getActivity()).toolFragment
                .setCommentCount(mCommentCount);
        mEmptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
        mWebView = (WebView) view.findViewById(R.id.webview);
        UIHelper.initWebView(mWebView);
    }

    @Override
    protected boolean hasReportMenu() {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((DetailActivity) getActivity()).toolFragment.showReportButton();
    }

    @Override
    protected void onFavoriteChanged(boolean flag) {
        super.onFavoriteChanged(flag);
        mBlog.setFavorite(flag ? 1 : 0);
        saveCache(mBlog);
    }

    @Override
    protected String getCacheKey() {
        return new StringBuilder(BLOG_CACHE_KEY).append(mBlogId).toString();
    }

    @Override
    protected void sendRequestData() {
        mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        OSChinaApi.getBlogDetail(mBlogId, mHandler);
    }

    @Override
    protected Entity parseData(InputStream is) throws Exception {
        return XmlUtils.toBean(BlogDetail.class, is).getBlog();
    }

    @Override
    protected Entity readData(Serializable seri) {
        return (Blog) seri;
    }

    @Override
    public void onclickWriteComment() {
        super.onclickWriteComment();
        if (mBlog != null)
            UIHelper.showBlogComment(getActivity(), mBlogId,
                    mBlog.getAuthorId());
    }

    @Override
    protected void executeOnLoadDataSuccess(Entity entity) {
        mBlog = (Blog) entity;
        fillUI();
        fillWebViewBody();
        ((DetailActivity) getActivity()).toolFragment
                .setCommentCount(mCommentCount);
    }

    private void fillUI() {
        mTvTitle.setText(mBlog.getTitle());
        mTvSource.setText(mBlog.getAuthor());
        mTvSource.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showUserCenter(getActivity(), mBlog.getAuthorId(),
                        mBlog.getAuthor());
            }
        });
        mTvTime.setText(StringUtils.friendly_time(mBlog.getPubDate()));
        notifyFavorite(mBlog.getFavorite() == 1);
    }

    @Override
    public int getCommentCount() {
        return mBlog.getCommentCount();
    }

    private void fillWebViewBody() {
        StringBuffer body = new StringBuffer();
        body.append(UIHelper.setHtmlCotentSupportImagePreview(mBlog.getBody()));
        body.append(UIHelper.WEB_STYLE).append(UIHelper.WEB_LOAD_IMAGES);
        mWebView.loadDataWithBaseURL(null, body.toString(), "text/html",
                "utf-8", null);
    }

    @Override
    protected int getFavoriteTargetId() {
        return mBlog != null ? mBlog.getId() : -1;
    }

    @Override
    protected int getFavoriteTargetType() {
        return mBlog != null ? FavoriteList.TYPE_BLOG : -1;
    }

    @Override
    protected String getShareUrl() {
        return mBlog != null ? URLsUtils.URL_MOBILE + "blog/" + mBlog.getId()
                : null;
    }

    @Override
    protected String getRepotrUrl() {
        return mBlog != null ? mBlog.getUrl() : "";
    }

    @Override
    protected String getShareTitle() {
        return mBlog != null ? mBlog.getTitle()
                : getString(R.string.share_title_blog);
    }

    @Override
    protected String getShareContent() {
        return mBlog != null ? StringUtils.getSubString(0, 55,
                getFilterHtmlBody(mBlog.getBody())) : "";
    }

    @Override
    protected int getRepotrId() {
        return mBlog != null ? mBlogId : 0;
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
        OSChinaApi.publicBlogComment(mBlogId, AppContext.getInstance()
                .getLoginUid(), str.toString(), mCommentHandler);
    }

    @Override
    public void onClickFlagButton() {}

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
