package net.oschina.app.fragment;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URLEncoder;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseDetailFragment;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.Entity;
import net.oschina.app.bean.FavoriteList;
import net.oschina.app.bean.Post;
import net.oschina.app.bean.PostDetail;
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
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class PostDetailFragment extends BaseDetailFragment implements
        OnSendClickListener {

    protected static final String TAG = PostDetailFragment.class
            .getSimpleName();
    private static final String POST_CACHE_KEY = "post_";
    @InjectView(R.id.tv_title)
    TextView mTvTitle;
    @InjectView(R.id.tv_source)
    TextView mTvSource;
    @InjectView(R.id.tv_time)
    TextView mTvTime;
    private WebView mWebView;
    private int mPostId;
    private Post mPost;

    @Override
    protected void onFavoriteChanged(boolean flag) {
        super.onFavoriteChanged(flag);
        mPost.setFavorite(flag ? 1 : 0);
        saveCache(mPost);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_detail, container,
                false);
        mCommentCount = getActivity().getIntent().getIntExtra("comment_count",
                0);
        mPostId = getActivity().getIntent().getIntExtra("post_id", 0);
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
        return new StringBuilder(POST_CACHE_KEY).append(mPostId).toString();
    }

    @Override
    protected void sendRequestData() {
        mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        OSChinaApi.getPostDetail(mPostId, mHandler);
    }

    @Override
    protected Entity parseData(InputStream is) throws Exception {
        return XmlUtils.toBean(PostDetail.class, is).getPost();
    }

    @Override
    protected Entity readData(Serializable seri) {
        return (Post) seri;
    }

    @Override
    protected void executeOnLoadDataSuccess(Entity entity) {
        mPost = (Post) entity;
        fillUI();
        fillWebViewBody();
        ((DetailActivity) getActivity()).toolFragment
                .setCommentCount(mCommentCount);
    }

    private void fillUI() {
        mTvTitle.setText(mPost.getTitle());
        mTvSource.setText(mPost.getAuthor());
        mTvTime.setText(StringUtils.friendly_time(mPost.getPubDate()));
        notifyFavorite(mPost.getFavorite() == 1);
    }

    @Override
    public int getCommentCount() {
        return mPost.getAnswerCount();
    }

    private void fillWebViewBody() {
        // 显示标签
        StringBuffer body = new StringBuffer();
        body.append(UIHelper.setHtmlCotentSupportImagePreview(mPost.getBody()));
        body.append(UIHelper.WEB_STYLE).append(UIHelper.WEB_LOAD_IMAGES)
                .append(getPostTags(mPost.getTags()))
                .append("<div style=\"margin-bottom: 80px\" />");
        mWebView.loadDataWithBaseURL(null, body.toString(), "text/html",
                "utf-8", null);
    }

    @SuppressWarnings("deprecation")
    private String getPostTags(Post.Tags taglist) {
        if (taglist == null)
            return "";
        StringBuffer tags = new StringBuffer();
        for (String tag : taglist.getTags()) {
            tags.append(String
                    .format("<a class='tag' href='http://www.oschina.net/question/tag/%s' >&nbsp;%s&nbsp;</a>&nbsp;&nbsp;",
                            URLEncoder.encode(tag), tag));
        }
        return String.format("<div style='margin-top:10px;'>%s</div>", tags);
    }

    @Override
    protected boolean hasReportMenu() {
        return true;
    }

    @Override
    protected int getFavoriteTargetId() {
        return mPost != null ? mPost.getId() : -1;
    }

    @Override
    protected int getFavoriteTargetType() {
        return mPost != null ? FavoriteList.TYPE_POST : -1;
    }

    @Override
    protected String getShareTitle() {
        return mPost != null ? mPost.getTitle()
                : getString(R.string.share_title_post);
    }

    @Override
    protected String getShareContent() {
        return mPost != null ? StringUtils.getSubString(0, 55,
                getFilterHtmlBody(mPost.getBody())) : "";
    }

    @Override
    protected String getShareUrl() {
        return mPost != null ? URLsUtils.URL_MOBILE + "qusetion/"
                + mPost.getId() : null;
    }

    @Override
    protected String getRepotrUrl() {
        return mPost != null ? mPost.getUrl() : "";
    }

    @Override
    protected int getRepotrId() {
        return mPost != null ? mPostId : 0;
    }

    @Override
    public void onclickWriteComment() {
        super.onclickWriteComment();
        UIHelper.showComment(getActivity(), mPostId, CommentList.CATALOG_POST);
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
        OSChinaApi.publicComment(CommentList.CATALOG_POST, mPostId, AppContext
                .getInstance().getLoginUid(), str.toString(), 0,
                mCommentHandler);
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

    @Override
    public void onClickFlagButton() {}
}
