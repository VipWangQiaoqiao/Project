package net.oschina.app.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseDetailFragment;
import net.oschina.app.bean.Blog;
import net.oschina.app.bean.BlogDetail;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.Entity;
import net.oschina.app.bean.FavoriteList;
import net.oschina.app.emoji.EmojiFragment;
import net.oschina.app.emoji.EmojiFragment.EmojiTextListener;
import net.oschina.app.fragment.ToolbarFragment.OnActionClickListener;
import net.oschina.app.fragment.ToolbarFragment.ToolAction;
import net.oschina.app.interf.EmojiFragmentControl;
import net.oschina.app.interf.ToolbarEmojiVisiableControl;
import net.oschina.app.interf.ToolbarFragmentControl;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.StringUtil;
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

public class BlogDetailFragment extends BaseDetailFragment implements
        EmojiTextListener, EmojiFragmentControl, ToolbarFragmentControl {

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
                if (mBlog != null)
                    UIHelper.showBlogComment(getActivity(), mBlogId,
                            mBlog.getAuthorId());
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

        mBlogId = getActivity().getIntent().getIntExtra("blog_id", 0);
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
    protected boolean hasReportMenu() {
        return true;
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

    // @Override
    // protected void onCommentChanged(int opt, int id, int catalog,
    // boolean isBlog, Comment comment) {
    // if (id == mBlogId && isBlog) {
    // if (Comment.OPT_ADD == opt && mBlog != null) {
    // mBlog.setCommentCount(mBlog.getCommentCount() + 1);
    // // if (mTvCommentCount != null) {
    // // mTvCommentCount.setVisibility(View.VISIBLE);
    // // mTvCommentCount.setText(getString(R.string.comment_count,
    // // mBlog.getCommentCount()));
    // // }
    // if (mToolBarFragment != null) {
    // mToolBarFragment.setCommentCount(mBlog.getCommentCount());
    // }
    // }
    // }
    // }

    @Override
    protected void executeOnLoadDataSuccess(Entity entity) {
        mBlog = (Blog) entity;
        fillUI();
        fillWebViewBody();
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
        mTvTime.setText(StringUtil.friendly_time(mBlog.getPubDate()));
        if (mToolBarFragment != null) {
            mToolBarFragment.setCommentCount(mBlog.getCommentCount());
        }
        notifyFavorite(mBlog.getFavorite() == 1);
    }

    private void fillWebViewBody() {
        StringBuffer body = new StringBuffer();
        body.append(UIHelper.setHtmlCotentSupportImagePreview(mBlog.getBody()));
        body.append(UIHelper.WEB_STYLE).append(UIHelper.WEB_LOAD_IMAGES);
        mWebView.loadDataWithBaseURL(null, body.toString(), "text/html",
                "utf-8", null);
    }

    @Override
    public void setEmojiFragment(EmojiFragment fragment) {
        mEmojiFragment = fragment;
        mEmojiFragment.setEmojiTextListener(this);
        mEmojiFragment.setButtonMoreVisibility(View.VISIBLE);
        mEmojiFragment.setButtonMoreClickListener(mMoreListener);
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
        mToolBarFragment.setActionVisiable(ToolAction.ACTION_REPORT, true);
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
        OSChinaApi.publicBlogComment(mBlogId, AppContext.getInstance()
                .getLoginUid(), text, mCommentHandler);
    }

    @Override
    protected void commentPubSuccess(Comment comment) {
        super.commentPubSuccess(comment);
        mEmojiFragment.reset();
    }

    @Override
    protected void onFavoriteChanged(boolean flag) {
        super.onFavoriteChanged(flag);
        if (mToolBarFragment != null) {
            mToolBarFragment.setFavorite(flag);
        }
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
		return mBlog != null ? URLsUtils.URL_MOBILE + "blog/" + mBlog.getId() : null;
	}
	
	@Override
	protected String getRepotrUrl() {
		return mBlog != null ? mBlog.getUrl() : "";
	}
    @Override
    protected String getShareTitle() {
        return mBlog != null ? mBlog.getTitle() : getString(R.string.share_title_blog);
    }

    @Override
    protected String getShareContent() {
    	return mBlog != null ? StringUtil.getSubString(0, 55, getFilterHtmlBody(mBlog.getBody())) : "";
    }

    @Override
    protected int getRepotrId() {
        return mBlog != null ? mBlogId : 0;
    }
}
