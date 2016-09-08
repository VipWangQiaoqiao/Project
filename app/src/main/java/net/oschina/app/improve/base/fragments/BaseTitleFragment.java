package net.oschina.app.improve.base.fragments;

import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewStub;

import net.oschina.app.R;
import net.oschina.app.improve.widget.TitleBar;

/**
 * Created by JuQiu
 * on 16/9/5.
 */
public abstract class BaseTitleFragment extends BaseFragment {

    TitleBar mTitleBar;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base_title;
    }

    @Override
    protected void onBindViewBefore(View root) {
        super.onBindViewBefore(root);
        // on before onBindViewBefore call
        ViewStub stub = (ViewStub) root.findViewById(R.id.lay_content);
        stub.setLayoutResource(getContentLayoutId());
        stub.inflate();
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        // not null
        mTitleBar = (TitleBar) root.findViewById(R.id.nav_title_bar);
        mTitleBar.setTitle(getTitleRes());
        mTitleBar.setIcon(getIconRes());
        mTitleBar.setIconOnClickListener(getIconClickListener());
    }

    protected abstract
    @LayoutRes
    int getContentLayoutId();

    protected abstract
    @StringRes
    int getTitleRes();

    protected
    @DrawableRes
    int getIconRes() {
        return 0;
    }

    protected View.OnClickListener getIconClickListener() {
        return null;
    }
}
