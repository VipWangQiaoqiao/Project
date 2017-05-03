package net.oschina.app.improve.user.sign.up;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.bean.EventSignIn;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.user.sign.InvitationActivity;
import net.oschina.app.improve.user.sign.in.SignInInfoActivity;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.SimplexToast;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 报名信息
 * Created by haibin on 2017/4/13.
 */

public class SignUpFragment extends BaseFragment implements View.OnClickListener, SignUpContract.View {
    @Bind(R.id.tv_event_name)
    TextView mTextEventName;
    @Bind(R.id.tv_name)
    TextView mTextName;
    @Bind(R.id.tv_work)
    TextView mTextWork;
    @Bind(R.id.tv_date)
    TextView mTextDate;
    @Bind(R.id.tv_phone)
    TextView mTextPhone;
    @Bind(R.id.tv_company)
    TextView mTextCompany;
    @Bind(R.id.tv_status)
    TextView mTextStatus;
    @Bind(R.id.tv_remark)
    TextView mTextRemark;
    @Bind(R.id.btn_sign_up)
    Button mBtnSignUp;
    @Bind(R.id.btn_cancel)
    Button mBtnCalcen;
    private SubBean mDetail;
    private String mInvitationImg;
    private SignUpContract.Presenter mPresenter;
    /**
     * mType 从活动详情进来表示查看邀请函==1，从扫一扫进来表示要签到 == 2
     */
    private int mType;

    static SignUpFragment newInstance(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        SignUpFragment fragment = new SignUpFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sign_in;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        mType = bundle.getInt("type", 1);
    }

    @OnClick({R.id.btn_sign_up, R.id.btn_cancel})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_up:
                switch (mType) {
                    case 1:
                        InvitationActivity.show(mContext, mInvitationImg);
                        break;
                    case 2:
                        mPresenter.signUp(mDetail.getId());
                        break;
                }
                break;
            case R.id.btn_cancel:
                DialogHelper.getConfirmDialog(mContext, "", "是否确认取消？", "是", "否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.cancelApply(mDetail.getId());
                    }
                }).show();
                break;
        }
    }

    @Override
    protected void initData() {
        super.initData();
        mBtnSignUp.setText(mType == 1 ? "活动邀请函" : "立即签到");
        mBtnCalcen.setVisibility(mType == 1 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showNetworkError(int strId) {
        if (mContext == null)
            return;
        SimplexToast.show(mContext, strId);
    }

    @Override
    public void showGetDetailSuccess(SubBean detail) {
        mDetail = detail;
        mTextEventName.setText(detail.getTitle());
        HashMap<String, Object> extra = mDetail.getExtra();
        if (extra != null) {
            int eventType = getExtraInt(extra.get("eventType"));
            mBtnSignUp.setVisibility(eventType == 1 ? View.VISIBLE : View.GONE);
            if (eventType != 1) {
                mBtnCalcen.setVisibility(View.VISIBLE);
                mBtnCalcen.setBackgroundResource(R.drawable.selector_event_sign);
                mBtnCalcen.setTextColor(Color.WHITE);
            }
        }
    }

    @Override
    public void showGetApplyInfoSuccess(Map<String, String> map) {
        mTextName.setText(getExtraString("姓名", map));
        mTextWork.setText(getExtraString("职位", map));
        mTextDate.setText(getExtraString("报名时间", map));
        mTextPhone.setText(getExtraString("手机号码", map));
        mTextCompany.setText(getExtraString("公司", map));
        mTextStatus.setText(getExtraString("状态", map));
        mInvitationImg = getExtraString("invitationImg", map);
        String remark = getExtraString("备注", map);
        if (TextUtils.isEmpty(remark)) {
            setGone(R.id.ll_remark);
            return;
        }
        mTextRemark.setText(remark);

    }

    @Override
    public void showSignInSuccess(EventSignIn sign) {
        SignInInfoActivity.show(mContext, mDetail, sign);
    }

    @Override
    public void showSignInFailure(int strId) {
        if (mContext == null)
            return;
        SimplexToast.show(mContext, strId);
    }

    @Override
    public void showCancelApplySuccess(String message) {
        SimplexToast.show(mContext, message);
        Intent intent = new Intent();
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void showCancelApplyFailure(String message) {
        SimplexToast.show(mContext, message);
    }

    private String getExtraString(String key, Map<String, String> map) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return "";
    }

    private int getExtraInt(Object object) {
        return object == null ? 0 : Double.valueOf(object.toString()).intValue();
    }


    @Override
    public void setPresenter(SignUpContract.Presenter presenter) {
        this.mPresenter = presenter;
    }
}
