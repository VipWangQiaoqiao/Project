package net.oschina.app.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseDetailFragment;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.Entity;
import net.oschina.app.bean.FavoriteList;
import net.oschina.app.bean.Software;
import net.oschina.app.bean.SoftwareDetail;
import net.oschina.app.bean.Tweet;
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
import net.oschina.app.util.XmlUtils;

import org.kymjs.kjframe.KJBitmap;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 软件详情页面
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年11月21日 上午10:41:45
 * 
 */
public class SoftwareDetailFragment extends BaseDetailFragment implements
	ToolbarFragmentControl, EmojiTextListener, EmojiFragmentControl {

    protected static final String TAG = SoftwareDetailFragment.class
	    .getSimpleName();
    private static final String SOFTWARE_CACHE_KEY = "software_";

    @InjectView(R.id.tv_software_license)
    TextView mTvLicense;

    @InjectView(R.id.tv_software_language)
    TextView mTvLanguage;

    @InjectView(R.id.tv_software_os)
    TextView mTvOs;

    @InjectView(R.id.tv_software_recordtime)
    TextView mTvRecordTime;

    @InjectView(R.id.iv_recommended)
    ImageView mIvRecommended;

    @InjectView(R.id.tv_title)
    TextView mTvTitle;

    @InjectView(R.id.tv_software_author)
    TextView mTvAuthor;

    @InjectView(R.id.ll_author)
    View llAuthor;

    @InjectView(R.id.line_author)
    View lineAuthor;

    @InjectView(R.id.webview)
    WebView mWebView;

    @InjectView(R.id.iv_logo)
    ImageView mIvLogo;
    private String mIdent;
    private Software mSoftware;
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
		if (mSoftware != null)
		    UIHelper.showSoftWareTweets(getActivity(),
			    mSoftware.getId());
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
	View view = inflater.inflate(R.layout.fragment_software_detail,
		container, false);

	mIdent = getActivity().getIntent().getStringExtra("ident");
	ButterKnife.inject(this, view);
	initViews(view);

	return view;
    }

    private void initViews(View view) {
	mEmptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);

	UIHelper.initWebView(mWebView);

	view.findViewById(R.id.btn_software_index).setOnClickListener(this);
	view.findViewById(R.id.btn_software_download).setOnClickListener(this);
	view.findViewById(R.id.btn_software_document).setOnClickListener(this);
	mTvAuthor.setOnClickListener(this);
    }

    @Override
    protected String getCacheKey() {
	return new StringBuilder(SOFTWARE_CACHE_KEY).append(mIdent).toString();
    }

    @Override
    protected void sendRequestData() {
	mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
	OSChinaApi.getSoftwareDetail(mIdent, mHandler);
    }

    @Override
    protected Entity parseData(InputStream is) throws Exception {
	return XmlUtils.toBean(SoftwareDetail.class, is).getSoftware();
    }

    @Override
    protected Entity readData(Serializable seri) {
	return (Software) seri;
    }

    @Override
    protected void executeOnLoadDataSuccess(Entity entity) {
	mSoftware = (Software) entity;
	fillUI();
	fillWebViewBody();
    }

    private void fillUI() {

	if (mSoftware.getAuthor() != null
		&& !StringUtils.isEmpty(mSoftware.getAuthor())) {
	    mTvAuthor.setText(mSoftware.getAuthor());
	} else {
	    llAuthor.setVisibility(View.GONE);
	    lineAuthor.setVisibility(View.GONE);
	}

	if (mSoftware.getRecommended() > 0) {
	    mIvRecommended.setVisibility(View.VISIBLE);
	}

	mTvTitle.setText(mSoftware.getTitle());
	mTvLicense.setText(mSoftware.getLicense());
	mTvLanguage.setText(mSoftware.getLanguage());
	mTvOs.setText(mSoftware.getOs());
	mTvRecordTime.setText(mSoftware.getRecordtime());
	if (mToolBarFragment != null) {
	    mToolBarFragment.setCommentCount(mSoftware.getTweetCount());
	}
	KJBitmap.create().display(mIvLogo, mSoftware.getLogo(),
		R.drawable.widget_dface);
	notifyFavorite(mSoftware.getFavorite() == 1);
    }

    private void fillWebViewBody() {
	StringBuffer body = new StringBuffer(
		UIHelper.setHtmlCotentSupportImagePreview(mSoftware.getBody()));
	body.append(UIHelper.WEB_STYLE).append(UIHelper.WEB_LOAD_IMAGES);
	mWebView.loadDataWithBaseURL(null, body.toString(), "text/html",
		"utf-8", null);
    }

    @Override
    public void onClick(View v) {
	switch (v.getId()) {
	case R.id.btn_software_index:
	    UIHelper.openBrowser(v.getContext(), mSoftware.getHomepage());
	    break;
	case R.id.btn_software_download:
	    UIHelper.openBrowser(v.getContext(), mSoftware.getDownload());
	    break;
	case R.id.btn_software_document:
	    UIHelper.openBrowser(v.getContext(), mSoftware.getDocument());
	    break;
	case R.id.tv_software_author:
	    UIHelper.showUserCenter(getActivity(), mSoftware.getAuthorId(),
		    mSoftware.getAuthor());
	    break;
	default:
	    break;
	}
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
    protected void onFavoriteChanged(boolean flag) {
	mSoftware.setFavorite(flag ? 1 : 0);
	if (mToolBarFragment != null) {
	    mToolBarFragment.setFavorite(flag);
	}
    }

    @Override
    protected int getFavoriteTargetId() {
	return mSoftware != null ? mSoftware.getId() : -1;
    }

    @Override
    protected int getFavoriteTargetType() {
	return mSoftware != null ? FavoriteList.TYPE_SOFTWARE : -1;
    }

    @Override
    protected String getShareTitle() {
	return mSoftware != null ? mSoftware.getTitle() : getString(R.string.share_title_soft);
    }

    @Override
    protected String getShareContent() {
	return mSoftware != null ? StringUtils.getSubString(0, 50, getFilterHtmlBody(mSoftware.getBody())) : "";
    }

    @Override
    protected String getShareUrl() {
	return mSoftware != null ? mSoftware.getUrl().replace("http://www",
		"http://m") : "";
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
	Tweet tweet = new Tweet();
	tweet.setAuthorid(AppContext.getInstance().getLoginUid());
	tweet.setBody(text);
	showWaitDialog(R.string.progress_submit);
	OSChinaApi.pubSoftWareTweet(tweet, mSoftware.getId(), mCommentHandler);
    }

    @Override
    protected void commentPubSuccess(Comment comment) {
	super.commentPubSuccess(comment);
	mEmojiFragment.reset();
    }
}
