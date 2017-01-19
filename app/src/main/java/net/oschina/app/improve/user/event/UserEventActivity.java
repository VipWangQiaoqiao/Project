package net.oschina.app.improve.user.event;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.ui.empty.EmptyLayout;

import butterknife.Bind;

/**
 * Created by haibin
 * on 2017/1/18.
 */

public class UserEventActivity extends BaseBackActivity implements UserEventContract.EmptyView {
    @Bind(R.id.lay_error)
    EmptyLayout mEmptyLayout;

    private UserEventPresenter mPresenter;

    public static void show(Context context, long authorId, String authorName) {
        Intent intent = new Intent(context, UserEventActivity.class);
        intent.putExtra("authorId", authorId);
        if (!TextUtils.isEmpty(authorName))
            intent.putExtra("authorName", authorName);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_user_event;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    mPresenter.onRefreshing();
                }
            }
        });

        UserEventFragment fragment = UserEventFragment.newInstance();
        addFragment(R.id.lay_container, fragment);
        Intent intent = getIntent();
        mPresenter = new UserEventPresenter(fragment,
                this,
                intent.getLongExtra("authorId", 0),
                intent.getStringExtra("authorName"));
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
