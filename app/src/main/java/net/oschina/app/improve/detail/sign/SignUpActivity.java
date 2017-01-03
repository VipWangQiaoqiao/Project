package net.oschina.app.improve.detail.sign;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.ui.empty.EmptyLayout;

import butterknife.Bind;

/**
 * 新版活动报名页面,动态请求活动报名参数，按服务器返回的顺序inflate各自对应的View
 * Created by haibin
 * on 2016/12/5.
 */

public class SignUpActivity extends BaseBackActivity implements SignUpContract.EmptyView {

    @Bind(R.id.error_layout)
    EmptyLayout mEmptyLayout;

    private SignUpFragment mFragment;

    private SignUpPresenter mPresenter;

    private long mSourceId;

    public static void show(Fragment fragment, long sourceId) {
        Intent intent = new Intent(fragment.getActivity(), SignUpActivity.class);
        intent.putExtra("sourceId", sourceId);
        fragment.startActivityForResult(intent, 0x01);
    }

    public static void show(Activity activity, long sourceId) {
        Intent intent = new Intent(activity, SignUpActivity.class);
        intent.putExtra("sourceId", sourceId);
        activity.startActivityForResult(intent, 0x01);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_sign_up;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mSourceId = getIntent().getLongExtra("sourceId", 0);
        mFragment = SignUpFragment.newInstance(mSourceId);
        addFragment(R.id.fl_content, mFragment);
        mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    mPresenter.getSignUpOptions(mSourceId);
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();

        if (mSourceId <= 0) {
            SimplexToast.show(this, "活动资源不存在");
            finish();
        }
        mPresenter = new SignUpPresenter(mFragment, this);
        mPresenter.getSignUpOptions(mSourceId);
    }

    @Override
    public void hideEmptyLayout() {
        mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
    }

    @Override
    public void showErrorLayout(int errorType) {
        mEmptyLayout.setErrorType(errorType);
    }
}
