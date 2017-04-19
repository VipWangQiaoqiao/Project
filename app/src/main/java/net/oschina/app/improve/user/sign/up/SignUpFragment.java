package net.oschina.app.improve.user.sign.up;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.bean.EventDetail;
import net.oschina.app.improve.bean.EventSignIn;
import net.oschina.app.improve.user.sign.InvitationActivity;
import net.oschina.app.improve.user.sign.in.SignInInfoActivity;
import net.oschina.app.improve.widget.SimplexToast;

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
    private EventDetail mDetail;
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

    @OnClick({R.id.btn_sign_up})
    @Override
    public void onClick(View v) {
        switch (mType) {
            case 1:
                InvitationActivity.show(mContext, mInvitationImg);
                break;
            case 2:
                mPresenter.signUp(mDetail.getId());
                break;
        }
    }

    @Override
    protected void initData() {
        super.initData();
        mBtnSignUp.setText(mType == 1 ? "活动邀请函" : "立即签到");
    }

    @Override
    public void showNetworkError(int strId) {
        if (mContext == null)
            return;
        SimplexToast.show(mContext, strId);
    }

    @Override
    public void showGetDetailSuccess(EventDetail detail) {
        mDetail = detail;
        mTextEventName.setText(detail.getTitle());
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
        if(TextUtils.isEmpty(remark)){
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

    private String getExtraString(String key, Map<String, String> map) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return "";
    }


    @Override
    public void setPresenter(SignUpContract.Presenter presenter) {
        this.mPresenter = presenter;
    }
}
