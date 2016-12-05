package net.oschina.app.improve.detail.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.bean.Event;
import net.oschina.app.improve.bean.EventDetail;
import net.oschina.app.improve.bean.EventSignin;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.utils.AssimilateUtils;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.ui.empty.EmptyLayout;

import java.lang.reflect.Type;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

import static net.oschina.app.R.id.tv_event_type;
import static net.oschina.app.improve.app.AppOperator.createGson;

/**
 * Created by fei
 * on 2016/11/30.
 * desc:活动签到
 */

public class EventSigninActivity extends BaseBackActivity {
    public static final String EVENT_ID_KEY = "event_id_key";

    @Bind(R.id.iv_event_img)
    ImageView mIvImg;
    @Bind(R.id.tv_event_title)
    TextView mTvTitle;
    @Bind(R.id.tv_event_author)
    TextView mTvAuthor;
    @Bind(tv_event_type)
    TextView mTvType;
    @Bind(R.id.tv_event_counts)
    TextView mTvCounts;

    @Bind(R.id.lay_input)
    LinearLayout mLayInput;
    @Bind(R.id.iv_check)
    ImageView mIvCheck;
    @Bind(R.id.tv_event_start_date)
    TextView mTvLabel;

    @Bind(R.id.lay_input_bg)
    LinearLayout mLayInputBg;

    @Bind(R.id.et_signin)
    EditText mEtSignin;

    @Bind(R.id.tv_signin_notice)
    TextView mTvNotice;

    @Bind(R.id.bt_signin_submit)
    Button mBtSubmit;

    @Bind(R.id.lay_error)
    EmptyLayout mEmptyLayout;

    private long mId;
    private int mRequestType = 0x01;//0x01 请求活动详情 0x02 签到
    private ProgressDialog mDialog;

    private TextHttpResponseHandler mHandler = new TextHttpResponseHandler() {
        @Override
        public void onStart() {
            super.onStart();
            if (mRequestType == 0x02) {
                showFocusWaitDialog(R.string.state_submit);
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if (mRequestType == 0x02) {
                hideWaitDialog();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            if (mRequestType == 0x01) {
                showError(EmptyLayout.NETWORK_ERROR);
            } else if (mRequestType == 0x02) {
                AppContext.showToastShort(R.string.state_network_error);
//                EventSignin eventSignin = new EventSignin();
//                eventSignin.setOptStatus(3);
//                eventSignin.setMessage("活动已结束／活动报名已截止");
//                eventSignin.setCost(50);
//                updateSigninView(eventSignin);
            }
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            //第一次初始化数据
            if (mRequestType == 0x01) {
                ResultBean<EventDetail> resultBean = createGson().fromJson(responseString, EventTypeToken());
                if (resultBean.isSuccess()) {
                    hideLoading();
                    EventDetail eventDetail = resultBean.getResult();
                    updateView(eventDetail);
                    mRequestType = 0x02;
                } else {
                    showError(EmptyLayout.NODATA);
                }
            } else if (mRequestType == 0x02) {
                //签到成功更新数据
                mRequestType = 0x02;
                ResultBean<EventSignin> resultBean = AppOperator.createGson().fromJson(responseString, EventSigninTypeToken());
                if (resultBean.isSuccess()) {
                    EventSignin eventSignin = resultBean.getResult();
                    updateSigninView(eventSignin);
                } else {
                    //showError(EmptyLayout.NODATA);
                }
            }
        }
    };


    /**
     * show signinActivity
     *
     * @param context context
     */
    public static void show(Context context, long sourceId) {
        Intent intent = new Intent(context, EventSigninActivity.class);
        intent.putExtra(EVENT_ID_KEY, sourceId);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main_signin;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mIvCheck.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    mEtSignin.setEnabled(false);
                    mIvCheck.setImageResource(R.mipmap.form_checkbox_normal);
                    v.setTag(null);

                    mTvLabel.setTextColor(getResources().getColor(R.color.text_secondary_color));
                    mEtSignin.setTextColor(getResources().getColor(R.color.text_secondary_color));
                    mLayInputBg.setActivated(false);
                    if (!AccountHelper.isLogin()) {
                        mBtSubmit.setEnabled(false);
                        mBtSubmit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
                        mBtSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
                    } else {
                        mBtSubmit.setEnabled(true);
                        mBtSubmit.setTextColor(getResources().getColor(R.color.white));
                        mBtSubmit.setBackgroundResource(R.drawable.bg_login_submit);
                    }
                } else {
                    mEtSignin.setEnabled(true);
                    mEtSignin.setTextColor(getResources().getColor(R.color.text_title_color));
                    mTvLabel.setTextColor(getResources().getColor(R.color.text_title_color));
                    mIvCheck.setImageResource(R.mipmap.form_checkbox_checked);
                    v.setTag(true);
                    mLayInputBg.setActivated(true);
                    if (!AccountHelper.isLogin()) {
                        String phone = mEtSignin.getText().toString().trim();
                        if (AssimilateUtils.machPhoneNum(phone)) {
                            mBtSubmit.setEnabled(true);
                            mBtSubmit.setTextColor(getResources().getColor(R.color.white));
                            mBtSubmit.setBackgroundResource(R.drawable.bg_login_submit);
                        } else {
                            mBtSubmit.setEnabled(false);
                            mBtSubmit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
                            mBtSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
                        }
                    } else {
                        String phone = mEtSignin.getText().toString().trim();
                        if (AssimilateUtils.machPhoneNum(phone)) {
                            mBtSubmit.setEnabled(true);
                            mBtSubmit.setTextColor(getResources().getColor(R.color.white));
                            mBtSubmit.setBackgroundResource(R.drawable.bg_login_submit);
                        } else {
                            mBtSubmit.setEnabled(false);
                            mBtSubmit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
                            mBtSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
                        }
                    }
                }
            }
        });

        mEtSignin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressWarnings("deprecation")
            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();
                boolean machPhoneNum = AssimilateUtils.machPhoneNum(input);
                mLayInputBg.setActivated(true);
                if (machPhoneNum) {
                    mBtSubmit.setEnabled(true);
                    mBtSubmit.setTextColor(getResources().getColor(R.color.white));
                    mBtSubmit.setBackgroundResource(R.drawable.bg_login_submit);
                    mLayInputBg.setBackgroundResource(R.drawable.bg_signin_input_ok);
                } else {
                    if (s.length() <= 0) {
                        mLayInputBg.setBackgroundResource(R.drawable.bg_signin_input_ok);
                    } else {
                        mLayInputBg.setBackgroundResource(R.drawable.bg_signin_input_error);
                    }
                    mBtSubmit.setEnabled(false);
                    mBtSubmit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
                    mBtSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);

                }
            }
        });

        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmptyLayout emptyLayout = mEmptyLayout;
                if (emptyLayout != null && emptyLayout.getErrorState() != EmptyLayout.HIDE_LAYOUT) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    requestData();
                }
            }
        });

        mBtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = null;

                if (!AccountHelper.isLogin())
                    phone = mEtSignin.getText().toString().trim();

                OSChinaApi.eventSignin(mId, phone, mHandler);
            }
        });

        mIvImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventDetailActivity.show(EventSigninActivity.this, mId);
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initData() {
        super.initData();

        Intent intent = getIntent();
        if (intent == null) return;

        mId = intent.getLongExtra(EVENT_ID_KEY, 0);

        if (AccountHelper.isLogin()) {
            mLayInputBg.setActivated(false);
            mBtSubmit.setEnabled(true);
            mBtSubmit.setTextColor(getResources().getColor(R.color.white));
            mBtSubmit.setBackgroundResource(R.drawable.bg_login_submit);
            mEtSignin.setEnabled(false);
            mIvCheck.setTag(null);
            mIvCheck.setImageResource(R.mipmap.form_checkbox_normal);
            mTvLabel.setTextColor(getResources().getColor(R.color.text_secondary_color));
        } else {
            mLayInputBg.setActivated(true);
            mEtSignin.setEnabled(true);
            mBtSubmit.setEnabled(false);
            mBtSubmit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
            mBtSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);

            mIvCheck.setTag(true);
            mIvCheck.setImageResource(R.mipmap.form_checkbox_checked);
            mTvLabel.setTextColor(getResources().getColor(R.color.text_title_color));
        }

        requestData();
    }

    private void requestData() {
        OSChinaApi.getEventDetail(mId, mHandler);
    }


    private Type EventTypeToken() {
        return new TypeToken<ResultBean<EventDetail>>() {
        }.getType();
    }

    private Type EventSigninTypeToken() {
        return new TypeToken<ResultBean<EventSignin>>() {
        }.getType();
    }

    @SuppressLint("DefaultLocale")
    private void updateView(EventDetail eventDetail) {

        if (eventDetail.getImg() != null)
            getImageLoader().load(eventDetail.getImg()).into(mIvImg);

        mTvTitle.setText(eventDetail.getTitle());

        if (eventDetail.getAuthor() != null)
            mTvAuthor.setText(String.format("%s %s", getString(R.string.signin_event_author), eventDetail.getAuthor()));

        int typeStr = R.string.oscsite;
        switch (eventDetail.getType()) {
            case Event.EVENT_TYPE_OSC:
                typeStr = R.string.event_type_osc;
                break;
            case Event.EVENT_TYPE_TEC:
                typeStr = R.string.event_type_tec;
                break;
            case Event.EVENT_TYPE_OTHER:
                typeStr = R.string.event_type_other;
                break;
            case Event.EVENT_TYPE_OUTSIDE:
                typeStr = R.string.event_type_outside;
                break;
        }

        mTvType.setText(String.format("%s：%s", getString(R.string.signin_event_type_hint), getResources().getString(typeStr)));
        mTvCounts.setText(String.format("%d%s", eventDetail.getViewCount(), getString(R.string.signin_event_counts_hint)));
    }


    private void updateSigninView(EventSignin eventSignin) {
        int optStatus = eventSignin.getOptStatus();
        switch (optStatus) {
            case 0x01://签到成功
            case 0x03://活动已结束／活动报名已截止
            case 0x04://您已签到
                mLayInputBg.setVisibility(View.GONE);
                mLayInput.setVisibility(View.GONE);
                mTvNotice.setVisibility(View.VISIBLE);
                mTvNotice.setText(eventSignin.getMessage());
                break;
            case 0x02://活动进行中未报名
                AppContext.showToastShort(eventSignin.getMessage());
                break;
            default:
                break;
        }


    }

    public void hideLoading() {
        final EmptyLayout emptyLayout = mEmptyLayout;
        if (emptyLayout == null)
            return;
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_alpha_to_hide);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        emptyLayout.startAnimation(animation);
    }

    private void showError(int type) {
        EmptyLayout layout = mEmptyLayout;
        if (layout != null) {
            layout.setErrorType(type);
        }
    }


    /**
     * show WaitDialog
     *
     * @return progressDialog
     */
    private ProgressDialog showWaitDialog(@StringRes int messageId) {
        if (mDialog == null) {
            if (messageId <= 0) {
                mDialog = DialogHelper.getProgressDialog(this, true);
            } else {
                String message = getResources().getString(messageId);
                mDialog = DialogHelper.getProgressDialog(this, message, true);
            }
        }
        mDialog.show();

        return mDialog;
    }

    /**
     * show FocusWaitDialog
     *
     * @return progressDialog
     */
    private ProgressDialog showFocusWaitDialog(@StringRes int messageId) {

        String message = getResources().getString(messageId);
        if (mDialog == null) {
            mDialog = DialogHelper.getProgressDialog(this, message, false);//DialogHelp.getWaitDialog(this, message);
        }
        mDialog.show();

        return mDialog;
    }

    /**
     * hide waitDialog
     */
    private void hideWaitDialog() {
        ProgressDialog dialog = mDialog;
        if (dialog != null) {
            mDialog = null;
            try {
                dialog.cancel();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
