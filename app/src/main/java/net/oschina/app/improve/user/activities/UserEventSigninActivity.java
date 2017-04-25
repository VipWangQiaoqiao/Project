package net.oschina.app.improve.user.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
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
import net.oschina.app.improve.bean.EventSignIn;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.user.sign.in.SignInInfoActivity;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.utils.parser.RichTextParser;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.TDevice;
import net.oschina.common.adapter.TextWatcherAdapter;

import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

/**
 * Created by fei
 * on 2016/11/30.
 * change by fei
 * on 2016/12/15
 * desc:活动签到
 */

public class UserEventSigninActivity extends BaseBackActivity {

    public static final String EVENT_ID_KEY = "event_id_key";

    @Bind(R.id.iv_event_img)
    ImageView mIvImg;
    @Bind(R.id.tv_event_title)
    TextView mTvTitle;
    @Bind(R.id.tv_event_author)
    TextView mTvAuthor;
    @Bind(R.id.tv_event_type)
    TextView mTvType;

    @Bind(R.id.ck_check)
    CheckBox mCkLabel;

    @Bind(R.id.line)
    View mLine;

    @Bind(R.id.lay_input_bg)
    LinearLayout mLayInputBg;

    @Bind(R.id.et_signin)
    EditText mEtSignin;

    @Bind(R.id.lay_container_user_info)
    LinearLayout mLayUserInfo;

    @Bind(R.id.tv_signin_notice)
    TextView mTvNotice;

    @Bind(R.id.tv_cost_notice)
    TextView mTvCost;

    @Bind(R.id.bt_signin_submit)
    Button mBtSubmit;

    @Bind(R.id.lay_error)
    EmptyLayout mEmptyLayout;

    private long mId;
    private int mRequestType = 0x01;//0x01 请求活动详情  0x02 匹配当前账户信息是否是报名账户  0x03 签到
    private ProgressDialog mDialog;

    private EventDetail mEventDetail = null;

    /**
     * show signinActivity
     *
     * @param context context
     */
    public static void show(Context context, long sourceId) {
        Intent intent = new Intent(context, UserEventSigninActivity.class);
        intent.putExtra(EVENT_ID_KEY, sourceId);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main_signin;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mId = bundle.getLong(EVENT_ID_KEY, 0);
        return mId > 0;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initWidget() {
        super.initWidget();

        mEtSignin.addTextChangedListener(new TextWatcherAdapter() {
            @SuppressWarnings("deprecation")
            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();
                boolean machPhoneNum = RichTextParser.machPhoneNum(input);
                mLayInputBg.setActivated(true);
                if (machPhoneNum) {
                    mBtSubmit.setEnabled(true);
                    mLayInputBg.setBackgroundResource(R.drawable.bg_signin_input_ok);
                } else {
                    if (s.length() <= 0) {
                        mLayInputBg.setBackgroundResource(R.drawable.bg_signin_input_ok);
                    } else {
                        mLayInputBg.setBackgroundResource(R.drawable.bg_signin_input_error);
                    }
                    mBtSubmit.setEnabled(false);
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
                submitToSignIn();
            }
        });

        mIvImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                net.oschina.app.improve.detail.general.EventDetailActivity.show(UserEventSigninActivity.this, mId);
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initData() {
        super.initData();

        //检查网络
        if (!checkNetIsAvailable()) {
            showError(EmptyLayout.NETWORK_ERROR);
            return;
        }

        requestData();
    }

    private boolean checkNetIsAvailable() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(getString(R.string.tip_network_error));
            showError(EmptyLayout.NETWORK_ERROR);
            return false;
        }
        return true;
    }

    private void requestData() {
        int requestType = mRequestType;
        switch (requestType) {
            case 0x01:
                requestEventDetail(mId);
                break;
            case 0x02:
                requestApplyInfo(mEventDetail, mId);
                break;
            default:
                break;
        }
    }

    private void submitToSignIn() {
        String phone = mEtSignin.getText().toString().trim();
        OSChinaApi.eventSignin(mId, phone, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showFocusWaitDialog(R.string.state_submit);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                hideWaitDialog();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppContext.showToastShort(R.string.state_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //签到成功更新数据
                ResultBean<EventSignIn> signinResultBean = AppOperator.createGson().fromJson(responseString,
                        new TypeToken<ResultBean<EventSignIn>>() {
                        }.getType());
                if (signinResultBean.isSuccess()) {
                    EventSignIn eventSignin = signinResultBean.getResult();
                    updateSigninView(eventSignin);
                }
            }
        });
    }

    private void requestEventDetail(final long sourceId) {
        //1.第一次初始化活动详情数据
        OSChinaApi.getEventDetail(sourceId, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                showError(EmptyLayout.NETWORK_ERROR);
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ResultBean<EventDetail> resultBean = AppOperator.createGson().fromJson(responseString,
                        new TypeToken<ResultBean<EventDetail>>() {
                        }.getType());

                if (resultBean.isSuccess()) {
                    EventDetail eventDetail = resultBean.getResult();

                    if (eventDetail.getId() <= 0) {
                        AppContext.showToastShort(getString(R.string.event_null_hint));
                        showError(EmptyLayout.NODATA);
                        return;
                    }

                    mEventDetail = eventDetail;

                    if (AccountHelper.isLogin()) {
                        if (!checkNetIsAvailable()) return;
                        mRequestType = 0x02;
                        //2.如果是登录状态，需要匹配是否是该账户的报名信息
                        requestApplyInfo(eventDetail, sourceId);
                    } else {
                        mRequestType = 0x03;
                        updateDetailView(eventDetail);
                        mLayInputBg.setActivated(true);
                        mBtSubmit.setEnabled(false);
                        hideLoading();
                    }
                } else {
                    showError(EmptyLayout.NODATA);
                }
            }
        });
    }

    private void requestApplyInfo(final EventDetail eventDetail, long sourceId) {
        OSChinaApi.syncSignUserInfo(sourceId, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                showError(EmptyLayout.NETWORK_ERROR);
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                ResultBean<Map<String, String>> mapResultBean = AppOperator.createGson().fromJson(responseString,
                        new TypeToken<ResultBean<Map<String, String>>>() {
                        }.getType());

                mRequestType = 0x03;
                int code = mapResultBean.getCode();

                switch (code) {
                    case 0:
                        //code=0，请求不到相关数据，直接使用手机号报名（ps：有可能使用的是其他账户或者直接手机号报名的情况）
                        updateDetailView(eventDetail);
                        setTelVisible(View.VISIBLE);
                        mLayInputBg.setActivated(true);
                        mBtSubmit.setEnabled(false);
                        hideLoading();
                        break;
                    case 1:
                        //code=1，请求成功
                        Map<String, String> userInfoMap = mapResultBean.getResult();
                        if (userInfoMap != null && userInfoMap.size() > 0) {
                            //显示相关报名的用户数据
                            updateDetailView(eventDetail);
                            setTelVisible(View.GONE);
                            updateUserInfoView(userInfoMap);
                            mBtSubmit.setEnabled(true);
                            hideLoading();
                        } else {
                            //报名所填相关用户数据全为null,但是却是报名了的
                            updateDetailView(eventDetail);
                            setTelVisible(View.GONE);
                            mBtSubmit.setEnabled(true);
                            hideLoading();
                        }
                        break;
                    case 404:
                        //code=404,当前登录的用户未报名该活动，返回相关数据为null
                        updateDetailView(eventDetail);
                        setTelVisible(View.VISIBLE);
                        mLayInputBg.setActivated(true);
                        mBtSubmit.setEnabled(false);
                        hideLoading();
                        AppContext.showToastShort(mapResultBean.getMessage());
                        break;
                    default:
                        AppContext.showToastShort(mapResultBean.getMessage());
                        showError(EmptyLayout.NODATA);
                        break;
                }
            }
        });
    }

    private void setTelVisible(int visible) {
        mCkLabel.setVisibility(visible);
        mLine.setVisibility(View.INVISIBLE);
        mLayInputBg.setVisibility(visible);
    }

    @SuppressLint("DefaultLocale")
    private void updateDetailView(EventDetail eventDetail) {

        if (eventDetail.getImg() != null)
            getImageLoader().load(eventDetail.getImg()).into(mIvImg);

        mTvTitle.setText(eventDetail.getTitle());

        if (eventDetail.getAuthor() != null)
            mTvAuthor.setText(String.format("%s %s", getString(R.string.signin_event_author), eventDetail.getAuthor()));

        int typeStr = R.string.osc_site;

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

        mTvType.setText(String.format("%s：%s", getString(R.string.signin_event_type_hint), getString(typeStr)));

        if (!AccountHelper.isLogin()) {
            setTelVisible(View.VISIBLE);
        } else {
            setTelVisible(View.GONE);
        }
    }


    private void updateUserInfoView(Map<String, String> userInfo) {

        Set<Map.Entry<String, String>> entries = userInfo.entrySet();

        for (Map.Entry<String, String> next : entries) {

            String key = next.getKey();
            String value = next.getValue();

            if (TextUtils.isEmpty(value) || TextUtils.isEmpty(key)) {
                continue;
            }

            @SuppressLint("InflateParams") View rootView = getLayoutInflater().inflate(R.layout.lay_signin_user_info, null, false);
            TextView tvKey = (TextView) rootView.findViewById(R.id.tv_key);
            tvKey.setText(String.format("%s:", key));
            TextView tvValue = (TextView) rootView.findViewById(R.id.tv_value);
            tvValue.setText(value);
            mLayUserInfo.addView(rootView);
        }
    }


    /**
     * update event signin view
     *
     * @param eventSignin eventSignin
     */
    private void updateSigninView(EventSignIn eventSignin) {
        int optStatus = eventSignin.getOptStatus();
        switch (optStatus) {
            case 0x01://签到成功
                SubBean bean = new SubBean();
                bean.setTitle(mEventDetail.getTitle());
                SignInInfoActivity.show(this,bean,eventSignin);
                finish();
//                return;
//                mBtSubmit.setEnabled(false);
//                mCkLabel.setVisibility(View.GONE);
//                mLayInputBg.setVisibility(View.GONE);
//                mLayUserInfo.setVisibility(View.GONE);
//                mTvNotice.setVisibility(View.VISIBLE);
//                mTvNotice.setText(eventSignin.getMessage());
//                if (!TextUtils.isEmpty(eventSignin.getCostMessage())) {
//                    mTvCost.setVisibility(View.VISIBLE);
//                    mTvCost.setText(eventSignin.getCostMessage());
//                }
                break;
            case 0x03://活动已结束／活动报名已截止
            case 0x04://您已签到
                SubBean bean1 = new SubBean();
                bean1.setTitle(mEventDetail.getTitle());
                SignInInfoActivity.show(this,bean1,eventSignin);
                finish();

//                mCkLabel.setVisibility(View.GONE);
//                mLayInputBg.setVisibility(View.GONE);
//                mTvNotice.setVisibility(View.VISIBLE);
//                mTvNotice.setText(eventSignin.getMessage());
//                mLayUserInfo.setVisibility(View.GONE);
//                mBtSubmit.setEnabled(false);
//                if (!TextUtils.isEmpty(eventSignin.getCostMessage())) {
//                    mTvCost.setVisibility(View.VISIBLE);
//                    mTvCost.setText(eventSignin.getCostMessage());
//                }
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
