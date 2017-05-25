package net.oschina.app.improve.detail.v2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.comment.CommentsActivity;
import net.oschina.app.improve.comment.OnCommentClickListener;
import net.oschina.app.improve.detail.db.Behavior;
import net.oschina.app.improve.detail.db.DBManager;
import net.oschina.app.improve.dialog.ShareDialog;
import net.oschina.app.improve.tweet.service.TweetPublishService;
import net.oschina.app.improve.user.activities.UserSelectFriendsActivity;
import net.oschina.app.improve.widget.CommentShareView;
import net.oschina.app.improve.widget.adapter.OnKeyArrivedListenerAdapter;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 新版本详情页实现
 * Created by haibin
 * on 2016/11/30.
 */

public abstract class DetailActivity extends BackActivity implements
        DetailContract.EmptyView, Runnable,
        OnCommentClickListener {

    protected String mCommentHint;
    protected DetailPresenter mPresenter;
    protected EmptyLayout mEmptyLayout;
    protected DetailFragment mDetailFragment;
    protected ShareDialog mAlertDialog;
    protected TextView mCommentCountView;
    protected long mStay;//该界面停留时间
    private long mStart;
    protected Behavior mBehavior;
    private boolean isInsert;

    protected CommentBar mDelegation;

    protected SubBean mBean;
    protected String mIdent;

    protected long mCommentId;
    protected long mCommentAuthorId;
    protected boolean mInputDoubleEmpty = false;

    @Override
    protected int getContentView() {
        return R.layout.activity_detail_v2;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        DBManager.from(getApplicationContext())
                .create(Behavior.class);
        CommentShareView.clearShareImage();
        if (!TDevice.hasWebView(this)) {
            finish();
            return;
        }
        if (TextUtils.isEmpty(mCommentHint))
            mCommentHint = getString(R.string.pub_comment_hint);
        LinearLayout layComment = (LinearLayout) findViewById(R.id.ll_comment);
        mEmptyLayout = (EmptyLayout) findViewById(R.id.lay_error);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    mPresenter.getDetail();
                }
            }
        });
        mBean = (SubBean) getIntent().getSerializableExtra("sub_bean");
        mIdent = getIntent().getStringExtra("ident");
        mDetailFragment = getDetailFragment();
        addFragment(R.id.lay_container, mDetailFragment);
        mPresenter = new DetailPresenter(mDetailFragment, this, mBean, mIdent);
        if (!mPresenter.isHideCommentBar()) {
            mDelegation = CommentBar.delegation(this, layComment);
            mDelegation.setCommentHint(mCommentHint);
            mDelegation.getBottomSheet().getEditText().setHint(mCommentHint);
            mDelegation.setFavDrawable(mBean.isFavorite() ? R.drawable.ic_faved : R.drawable.ic_fav);

            mDelegation.setFavListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!AccountHelper.isLogin()) {
                        LoginActivity.show(DetailActivity.this);
                        return;
                    }
                    mPresenter.favReverse();
                }
            });

            mDelegation.getBottomSheet().setMentionListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((AccountHelper.isLogin())) {
                        UserSelectFriendsActivity.show(DetailActivity.this, mDelegation.getBottomSheet().getEditText());
                    } else {
                        LoginActivity.show(DetailActivity.this, 1);
                    }
                }
            });

            mDelegation.setShareListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toShare(mBean.getTitle(), mBean.getBody(), mBean.getHref());
                }
            });

            mDelegation.getBottomSheet().getEditText().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        handleKeyDel();
                    }
                    return false;
                }
            });
            mDelegation.getBottomSheet().setCommitListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // showDialog("正在提交评论...");
                    if (mDelegation == null) return;
                    mDelegation.getBottomSheet().dismiss();
                    mDelegation.setCommitButtonEnable(false);
                    mPresenter.addComment(mBean.getId(),
                            mBean.getType(),
                            mDelegation.getBottomSheet().getCommentText(),
                            0,
                            mCommentId,
                            mCommentAuthorId);
                }
            });
            mDelegation.getBottomSheet().getEditText().setOnKeyArrivedListener(new OnKeyArrivedListenerAdapter(this));
        }
        mEmptyLayout.post(new Runnable() {
            @Override
            public void run() {
                mPresenter.getCache();
                mPresenter.getDetail();
            }
        });
        if (mToolBar != null)
            mToolBar.setOnTouchListener(new OnDoubleTouchListener() {
                @Override
                void onMultiTouch(View v, MotionEvent event, int touchCount) {
                    if (touchCount == 2) {
                        mPresenter.scrollToTop();
                    }
                }
            });

        if (AccountHelper.isLogin() &&
                DBManager.from(getApplicationContext())
                        .getCount(Behavior.class) >= 15) {
            mPresenter.uploadBehaviors(DBManager.from(getApplicationContext())
                    .limit(15, 0)
                    .get(Behavior.class)
            );
        }
    }

    private void initBehavior() {
        if (AccountHelper.isLogin() && mBean.getType() != News.TYPE_EVENT && !isInsert) {
            mBehavior = new Behavior();
            mBehavior.setUser(AccountHelper.getUserId());
            mBehavior.setUserName(AccountHelper.getUser().getName());
            mBehavior.setNetwork(getNetwork());
            mBehavior.setUrl(mBean.getHref());
            mBehavior.setOperateType(mBean.getType());
            mBehavior.setOperateTime(System.currentTimeMillis());
            mStart = mBehavior.getOperateTime();
            mBehavior.setOperation("read");
            mBehavior.setDevice(android.os.Build.MODEL);
            mBehavior.setVersion(TDevice.getVersionName());
            mBehavior.setOs(android.os.Build.VERSION.RELEASE);
            isInsert = DBManager.from(getApplicationContext())
                    .insert(mBehavior);
        }
    }

    @SuppressWarnings("deprecation")
    private String getNetwork() {
        ConnectivityManager connect = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connect == null)
            return "null";
        NetworkInfo activeNetInfo = connect.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return "null";
        }
        NetworkInfo wifiInfo = connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null) {
            NetworkInfo.State state = wifiInfo.getState();
            if (state != null)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return "WIFI";
                }
        }
        NetworkInfo networkInfo = connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo.State state = networkInfo.getState();
        if (null != state)
            if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                switch (activeNetInfo.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                    case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                    case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return "2G";
                    case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return "3G";
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return "4G";
                }
            }
        return "null";
    }

    @Override
    public void hideEmptyLayout() {
        mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        if (mCommentCountView != null) {
            mCommentCountView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommentsActivity.show(DetailActivity.this, mBean.getId(), mBean.getType(), OSChinaApi.COMMENT_NEW_ORDER, mBean.getTitle());
                }
            });
        }
    }

    @Override
    public void showErrorLayout(int errorType) {
        mEmptyLayout.setErrorType(errorType);
    }

    @Override
    public void showGetDetailSuccess(SubBean bean) {
        this.mBean = bean;
        initBehavior();
        if (mDelegation != null)
            mDelegation.setFavDrawable(mBean.isFavorite() ? R.drawable.ic_faved : R.drawable.ic_fav);
        if (mCommentCountView != null && mBean.getStatistics() != null) {
            mCommentCountView.setText(String.valueOf(mBean.getStatistics().getComment()));
        }
    }

    @Override
    public void run() {
        hideEmptyLayout();
        mDetailFragment.onPageFinished();
    }


    @Override
    public void showFavReverseSuccess(boolean isFav, int favCount, int strId) {
        if (mBehavior != null) {
            mBehavior.setIsCollect(isFav ? 1 : 0);
            DBManager.from(getApplicationContext())
                    .where("operate_time=?", String.valueOf(mBehavior.getOperateTime()))
                    .update(mBehavior);
        }
        if (mDelegation != null) {
            mDelegation.setFavDrawable(isFav ? R.drawable.ic_faved : R.drawable.ic_fav);
        }
    }

    @Override
    public void showCommentSuccess(Comment comment) {
        //hideDialog();
        if (mBehavior != null) {
            mBehavior.setIsComment(1);
            DBManager.from(getApplicationContext())
                    .where("operate_time=?", String.valueOf(mBehavior.getOperateTime()))
                    .update(mBehavior);
        }
        if (mDelegation == null)
            return;
        if (mDelegation.getBottomSheet().isSyncToTweet()) {
            TweetPublishService.startActionPublish(this,
                    mDelegation.getBottomSheet().getCommentText(), null,
                    About.buildShare(mBean.getId(), mBean.getType()));
        }
        mDelegation.getBottomSheet().dismiss();
        mDelegation.setCommitButtonEnable(true);
        AppContext.showToastShort(R.string.pub_comment_success);
        mDelegation.getCommentText().setHint(mCommentHint);
        mDelegation.getBottomSheet().getEditText().setText("");
        mDelegation.getBottomSheet().getEditText().setHint(mCommentHint);
        mDelegation.getBottomSheet().dismiss();

    }

    @Override
    public void showCommentError(String message) {
        //hideDialog();
        AppContext.showToastShort(R.string.pub_comment_failed);
        mDelegation.setCommitButtonEnable(true);
    }

    @Override
    public void showUploadBehaviorsSuccess(int index) {
        DBManager.from(getApplicationContext())
                .where("id<=?", String.valueOf(index))
                .delete(Behavior.class);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        MenuItem item = menu.findItem(R.id.menu_scroll_comment);
        if (item != null) {
            View action = item.getActionView();
            if (action != null) {
                View tv = action.findViewById(R.id.tv_comment_count);
                if (tv != null) {
                    mCommentCountView = (TextView) tv;
                    if (mBean.getStatistics() != null)
                        mCommentCountView.setText(mBean.getStatistics().getComment() + "");
                }
            }
        }
        return true;
    }


    @SuppressWarnings({"LoopStatementThatDoesntLoop", "SuspiciousMethodCalls"})
    protected boolean toShare(String title, String content, String url) {
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(url) || mBean == null)
            return false;

        String imageUrl = null;
        List<SubBean.Image> images = mBean.getImages();
        switch (mBean.getType()) {
            case News.TYPE_EVENT:
                if (images != null && images.size() > 0) {
                    imageUrl = images.get(0).getHref();
                }
                break;
            case News.TYPE_SOFTWARE:
                if (images != null && images.size() > 0) {
                    imageUrl = images.get(0).getThumb();
                    if (imageUrl != null && imageUrl.contains("logo/default.png")) {
                        imageUrl = null;
                    }
                    break;
                }
            default:
                String regex = "<img src=\"([^\"]+)\"";

                Pattern pattern = Pattern.compile(regex);

                Matcher matcher = pattern.matcher(content);

                while (matcher.find()) {
                    imageUrl = matcher.group(1);
                    break;
                }
                break;
        }
        content = content.trim();
        if (content.length() > 55) {
            content = HTMLUtil.delHTMLTag(content);
            if (content.length() > 55)
                content = StringUtils.getSubString(0, 55, content);
        } else {
            content = HTMLUtil.delHTMLTag(content);
        }
        if (TextUtils.isEmpty(content))
            content = "";

        // 分享
        if (mAlertDialog == null) {
            mAlertDialog = new
                    ShareDialog(this, mBean.getId())
                    .type(mBean.getType())
                    .title(title)
                    .content(content)
                    .imageUrl(imageUrl)//如果没有图片，即url为null，直接加入app默认分享icon
                    .url(url).with();
        }
        mAlertDialog.show();
        if (mBehavior != null) {
            mBehavior.setIsShare(1);
            DBManager.from(getApplicationContext())
                    .where("operate_time=?", String.valueOf(mBehavior.getOperateTime()))
                    .update(mBehavior);
        }
        return true;
    }

    @Override
    public void onClick(View view, Comment comment) {
        mCommentId = comment.getId();
        mCommentAuthorId = comment.getAuthor().getId();
        mDelegation.getCommentText().setHint(String.format("%s %s", getResources().getString(R.string.reply_hint), comment.getAuthor().getName()));
        mDelegation.getBottomSheet().show(String.format("%s %s", getResources().getString(R.string.reply_hint), comment.getAuthor().getName()));
    }

    protected void handleKeyDel() {
        if (mCommentId != mBean.getId()) {
            if (TextUtils.isEmpty(mDelegation.getBottomSheet().getCommentText())) {
                if (mInputDoubleEmpty) {
                    mCommentId = mBean.getId();
                    mCommentAuthorId = 0;
                    mDelegation.setCommentHint(mCommentHint);
                    mDelegation.getBottomSheet().getEditText().setHint(mCommentHint);
                } else {
                    mInputDoubleEmpty = true;
                }
            } else {
                mInputDoubleEmpty = false;
            }
        }
    }

    protected int getExtraInt(Object object) {
        return object == null ? 0 : Double.valueOf(object.toString()).intValue();
    }

    protected abstract DetailFragment getDetailFragment();

    @Override
    public void finish() {
        if (mEmptyLayout.getErrorState() == EmptyLayout.HIDE_LAYOUT)
            DetailCache.addCache(mBean);
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mStart != 0)
            mStart = System.currentTimeMillis();
        if (mAlertDialog == null)
            return;
        mAlertDialog.hideProgressDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mStay += (System.currentTimeMillis() - mStart) / 1000;
        if (mBehavior != null) {
            mBehavior.setStay(mStay);
            DBManager.from(getApplicationContext())
                    .where("operate_time=?", String.valueOf(mBehavior.getOperateTime()))
                    .update(mBehavior);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && mBehavior != null) {
            mBehavior.setIsComment(1);
            DBManager.from(getApplicationContext())
                    .where("operate_time=?", String.valueOf(mBehavior.getOperateTime()))
                    .update(mBehavior);
        }
    }

    private abstract class OnDoubleTouchListener implements View.OnTouchListener {
        private long lastTouchTime = 0;
        private AtomicInteger touchCount = new AtomicInteger(0);
        private Runnable mRun = null;
        private Handler mHandler;

        OnDoubleTouchListener() {
            mHandler = new Handler(getMainLooper());
        }

        void removeCallback() {
            if (mRun != null) {
                mHandler.removeCallbacks(mRun);
                mRun = null;
            }
        }

        @Override
        public boolean onTouch(final View v, final MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                final long now = System.currentTimeMillis();
                lastTouchTime = now;

                touchCount.incrementAndGet();
                removeCallback();

                mRun = new Runnable() {
                    @Override
                    public void run() {
                        if (now == lastTouchTime) {
                            onMultiTouch(v, event, touchCount.get());
                            touchCount.set(0);
                        }
                    }
                };

                mHandler.postDelayed(mRun, getMultiTouchInterval());
            }
            return true;
        }


        int getMultiTouchInterval() {
            return 400;
        }


        abstract void onMultiTouch(View v, MotionEvent event, int touchCount);
    }
}
