package net.oschina.app.improve.user.sign.up;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.ui.empty.EmptyLayout;

import butterknife.Bind;

/**
 * 活动报名签到界面，包括报名信息
 * Created by haibin on 2017/4/11.
 */

public class SignUpInfoActivity extends BaseBackActivity implements SignUpContract.EmptyView {
    private SignUpContract.Presenter mPresenter;
    @Bind(R.id.emptyLayout)
    EmptyLayout mEmptyLayout;

    public static void show(Activity activity, long id, int type) {
        Intent intent = new Intent(activity, SignUpInfoActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("type", type);
        activity.startActivityForResult(intent, 0x02);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_sign_info;
    }

    @Override
    protected void initData() {
        super.initData();
        final long id = getIntent().getLongExtra("id", 0);
        final int type = getIntent().getIntExtra("type", 1);
        SignUpFragment fragment = SignUpFragment.newInstance(type);
        addFragment(R.id.fl_content, fragment);
        mPresenter = new SignUpPresenter(fragment, this);
        mPresenter.getEventDetail(id);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    mPresenter.getEventDetail(id);
                }
            }
        });
    }

    @Override
    public void hideEmptyLayout() {
        if (mEmptyLayout != null)
            mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
    }

    @Override
    public void showErrorLayout(int errorType) {
        if (mEmptyLayout != null)
            mEmptyLayout.setErrorType(errorType);
    }
}
