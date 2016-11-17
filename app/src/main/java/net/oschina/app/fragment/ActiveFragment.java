package net.oschina.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import net.oschina.app.R;
import net.oschina.app.adapter.ActiveAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.bean.Active;
import net.oschina.app.bean.ActiveList;
import net.oschina.app.bean.Constants;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;

/**
 * 动态fragment
 *
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @author kymjs (https://github.com/kymjs)
 * @created 2014年10月22日 下午3:35:43
 */
public class ActiveFragment extends BaseListFragment<Active> implements
        OnItemLongClickListener {

    protected static final String TAG = ActiveFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "active_list";
    private boolean mIsWatingLogin; // 还没登陆

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mErrorLayout != null) {
                mIsWatingLogin = true;
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_LOGOUT);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        if (mIsWatingLogin) {
            mCurrentPage = 0;
            mState = STATE_REFRESH;
            requestData(false);
        }
        super.onResume();
    }

    @Override
    protected ActiveAdapter getListAdapter() {
        return new ActiveAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + mCatalog + AccountHelper.getUserId();
    }

    @Override
    protected ActiveList parseList(InputStream is) {
        return XmlUtils.toBean(ActiveList.class, is);
    }

    @Override
    protected ActiveList readList(Serializable seri) {
        return ((ActiveList) seri);
    }

    @Override
    public void initView(View view) {
        if (mCatalog == ActiveList.CATALOG_LASTEST) {
            setHasOptionsMenu(true);
        }
        super.initView(view);
        mListView.setOnItemLongClickListener(this);
        mListView.setOnItemClickListener(this);
        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountHelper.isLogin()) {
                    mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    requestData(false);
                } else {
                    UIHelper.showLoginActivity(getActivity());
                }
            }
        });
        if (AccountHelper.isLogin()) {

        }
    }

    @Override
    protected void requestData(boolean refresh) {
        if (AccountHelper.isLogin()) {
            mIsWatingLogin = false;
            super.requestData(refresh);
        } else {
            mIsWatingLogin = true;
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
        }
    }

    @Override
    protected void sendRequestData() {
        OSChinaApi.getActiveList((int) AccountHelper.getUserId(),
                mCatalog, mCurrentPage, mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Active active = mAdapter.getItem(position);
        if (active != null)
            UIHelper.showActiveRedirect(view.getContext(), active);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        final Active active = mAdapter.getItem(position);
        if (active == null)
            return false;
        String[] items = new String[]{getResources().getString(R.string.copy)};
        DialogHelper.getSelectDialog(getActivity(), items, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TDevice.copyTextToBoard(HTMLUtil.delHTMLTag(active.getMessage()));
            }
        }).show();
        return true;
    }

    @Override
    protected long getAutoRefreshTime() {
        // 最新动态，即是好友圈
        if (mCatalog == ActiveList.CATALOG_LASTEST) {
            return 5 * 60;
        }
        return super.getAutoRefreshTime();
    }
}
