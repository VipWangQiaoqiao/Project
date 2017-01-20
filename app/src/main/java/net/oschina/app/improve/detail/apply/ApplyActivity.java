package net.oschina.app.improve.detail.apply;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.TDevice;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by haibin
 * on 2016/12/27.
 */

public class ApplyActivity extends BackActivity implements
        View.OnClickListener, ApplyContract.EmptyView {
    @Bind(R.id.searchView)
    SearchView mSearchView;

    @Bind(R.id.emptyLayout)
    EmptyLayout mEmptyLayout;

    @Bind(R.id.ll_search)
    LinearLayout mLinearSearch;

    @Bind(R.id.fl_tool)
    FrameLayout mFrameTool;
    @Bind(R.id.search_src_text)
    EditText mViewSearchEditor;
    private ApplyPresenter mPresenter;
    private String mSearchText;

    public static void show(Context context, long sourceId) {
        Intent intent = new Intent(context, ApplyActivity.class);
        intent.putExtra("sourceId", sourceId);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_apply;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        long sourceId = getIntent().getLongExtra("sourceId", 0);
        if (sourceId == 0) {
            SimplexToast.show(this, "活动不存在");
            finish();
            return;
        }
        mViewSearchEditor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // 阻止点击关闭按钮 collapse icon
                return true;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchView.clearFocus();
                return doSearch(query, false);
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return doSearch(newText, true);
            }
        });
        ApplyFragment fragment = ApplyFragment.newInstance();
        mPresenter = new ApplyPresenter(fragment, this, sourceId);
        addFragment(R.id.fl_content, fragment);
    }

    @OnClick({R.id.ll_search, R.id.tv_cancel})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_search:
                mLinearSearch.setVisibility(View.GONE);
                mSearchView.setVisibility(View.VISIBLE);
                mSearchView.setFocusable(true);
                mSearchView.setFocusableInTouchMode(true);
                mSearchView.requestFocus();
                TDevice.openKeyboard(mSearchView);
                break;
            case R.id.tv_cancel:
                mLinearSearch.setVisibility(View.VISIBLE);
                mSearchView.setVisibility(View.GONE);
                mViewSearchEditor.setText("");
                mPresenter.setFilter("");
                mSearchView.clearFocus();
                TDevice.hideSoftKeyboard(mSearchView);
                break;
        }
    }

    @Override
    public void showGetApplyUserSuccess() {
        mFrameTool.setVisibility(View.VISIBLE);
        mEmptyLayout.setVisibility(View.GONE);
    }

    @Override
    public void showGetApplyUserError(String message) {

    }

    @Override
    public void showSearchError(String message) {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mSearchView.clearFocus();
    }

    private Runnable mSearchRunnable = new Runnable() {
        @Override
        public void run() {
            if (TextUtils.isEmpty(mSearchText))
                return;
            mPresenter.setFilter(mSearchText);
            mPresenter.onRefreshing();
        }
    };

    private boolean doSearch(String query, boolean fromTextChange) {
        mSearchText = query.trim();
        mPresenter.setFilter(mSearchText);
        mLinearSearch.removeCallbacks(mSearchRunnable);
        if (fromTextChange && !TDevice.isWifiOpen()) return false;
        mLinearSearch.postDelayed(mSearchRunnable, fromTextChange ? 2000 : 0);
        return true;
    }
}
