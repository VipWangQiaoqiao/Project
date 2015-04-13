package net.oschina.app.fragment;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URLEncoder;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseDetailFragment;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.Entity;
import net.oschina.app.bean.FavoriteList;
import net.oschina.app.bean.Post;
import net.oschina.app.bean.PostDetail;
import net.oschina.app.fragment.ToolbarFragment.OnActionClickListener;
import net.oschina.app.fragment.ToolbarFragment.ToolAction;
import net.oschina.app.interf.ToolbarEmojiVisiableControl;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.URLsUtils;
import net.oschina.app.util.XmlUtils;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class PostDetailFragment extends BaseDetailFragment {

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
                break;
            case ACTION_VIEW_COMMENT:
                UIHelper.showComment(getActivity(), mPostId,
                        CommentList.CATALOG_POST);
                break;
            case ACTION_FAVORITE:
                handleFavoriteOrNot();
                break;
            case ACTION_SHARE:
                handleShare();
                break;
            case ACTION_REPORT:
                onReportMenuClick();
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

        mPostId = getActivity().getIntent().getIntExtra("post_id", 0);
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

    // @Override
    // protected void onCommentChanged(int opt, int id, int catalog,
    // boolean isBlog, Comment comment) {
    // }

    @Override
    protected void executeOnLoadDataSuccess(Entity entity) {
        mPost = (Post) entity;
        fillUI();
        fillWebViewBody();
    }

    private void fillUI() {
        mTvTitle.setText(mPost.getTitle());
        mTvSource.setText(mPost.getAuthor());
        mTvTime.setText(StringUtils.friendly_time(mPost.getPubDate()));
        if (mToolBarFragment != null) {
            mToolBarFragment.setCommentCount(mPost.getAnswerCount());
        }
        notifyFavorite(mPost.getFavorite() == 1);
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
    protected void onFavoriteChanged(boolean flag) {
        super.onFavoriteChanged(flag);
        if (mToolBarFragment != null) {
            mToolBarFragment.setFavorite(flag);
        }
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
        // TODO Auto-generated method stub
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
}
