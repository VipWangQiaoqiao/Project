package net.oschina.app.fragment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.MessageDetailAdapter;
import net.oschina.app.api.OperationResponseHandler;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.Constants;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.ResultBean;
import net.oschina.app.ui.dialog.CommonDialog;
import net.oschina.app.ui.dialog.DialogHelper;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class MessageDetailFragment extends BaseListFragment<Comment> implements
        OnItemLongClickListener {
    protected static final String TAG = ActiveFragment.class.getSimpleName();
    public static final String BUNDLE_KEY_FID = "BUNDLE_KEY_FID";
    public static final String BUNDLE_KEY_FNAME = "BUNDLE_KEY_FNAME";
    private static final String CACHE_KEY_PREFIX = "message_detail_list";

    private int mFid;
    private String mFName;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mErrorLayout != null) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mFid = args.getInt(BUNDLE_KEY_FID);
            mFName = args.getString(BUNDLE_KEY_FNAME);
            mCatalog = CommentList.CATALOG_MESSAGE;
        }
        IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_LOGOUT);
        getActivity().registerReceiver(mReceiver, filter);

        ((BaseActivity) getActivity()).setActionBarTitle(mFName);

        int mode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        getActivity().getWindow().setSoftInputMode(mode);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    protected MessageDetailAdapter getListAdapter() {
        return new MessageDetailAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + mFid;
    }

    @Override
    protected CommentList parseList(InputStream is) throws Exception {
        CommentList list = XmlUtils.toBean(CommentList.class, is);
        return list;
    }

    @Override
    protected CommentList readList(Serializable seri) {
        CommentList list = ((CommentList) seri);
        return list;
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        mListView.setDivider(null);
        mListView.setDividerHeight(0);
        mListView.setOnItemLongClickListener(this);
        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (AppContext.getInstance().isLogin()) {
                    requestData(false);
                } else {
                    UIHelper.showLoginActivity(getActivity());
                }
            }
        });
    }

    @Override
    protected void requestData(boolean refresh) {
        mErrorLayout.setErrorMessage("");
        if (AppContext.getInstance().isLogin()) {
            super.requestData(refresh);
        } else {
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
        }
    }

    @Override
    protected void sendRequestData() {
        OSChinaApi.getCommentList(mFid, mCatalog, mCurrentPage, mHandler);
    }

    @Override
    protected void executeOnLoadDataSuccess(List<Comment> data) {
        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        if (data == null) {
            data = new ArrayList<Comment>(1);
        }
        if (mAdapter != null) {
            if (mCurrentPage == 0)
                mAdapter.clear();
            mAdapter.addData(data);
            if (data.size() == 0 && mState == STATE_REFRESH) {
                mErrorLayout.setErrorType(EmptyLayout.NODATA);
            } else if (data.size() < getPageSize()) {
                mAdapter.setState(ListBaseAdapter.STATE_OTHER);
            } else {
                mAdapter.setState(ListBaseAdapter.STATE_LOAD_MORE);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        // Message active = (Message) mAdapter.getItem(position - 1);
        // UIHelper.showMessageDetail(context, friendid, friendname);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
            int position, long id) {
        final Comment message = mAdapter.getItem(position);
        final CommonDialog dialog = DialogHelper
                .getPinterestDialogCancelable(getActivity());
        dialog.setItemsWithoutChk(
                getResources().getStringArray(R.array.message_list_options),
                new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
                        dialog.dismiss();
                        switch (position) {
                        case 0:
                            TDevice.copyTextToBoard(HTMLUtil.delHTMLTag(message
                                    .getContent()));
                            break;
                        case 1:
                            handleDeleteMessage(message);
                            break;
                        default:
                            break;
                        }
                    }
                });
        dialog.setNegativeButton(R.string.cancle, null);
        dialog.show();
        return true;
    }

    private void handleDeleteMessage(final Comment message) {
        CommonDialog dialog = DialogHelper
                .getPinterestDialogCancelable(getActivity());
        dialog.setMessage(getString(R.string.confirm_delete_one_message,
                message.getAuthor()));
        dialog.setNegativeButton(R.string.cancle, null);
        dialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showWaitDialog(R.string.progress_submit);
                        OSChinaApi.deleteComment(mFid,
                                CommentList.CATALOG_MESSAGE, message.getId(),
                                message.getAuthorId(),
                                new DeleteMessageOperationHandler(message));
                    }
                });
        dialog.show();
    }

    class DeleteMessageOperationHandler extends OperationResponseHandler {

        public DeleteMessageOperationHandler(Object... args) {
            super(args);
        }

        @Override
        public void onSuccess(int code, ByteArrayInputStream is, Object[] args)
                throws Exception {
            Result res = XmlUtils.toBean(ResultBean.class, is).getResult();
            if (res.OK()) {
                Comment msg = (Comment) args[0];
                mAdapter.removeItem(msg);
                mAdapter.notifyDataSetChanged();
                hideWaitDialog();
                AppContext.showToastShort(R.string.tip_delete_success);
            } else {
                AppContext.showToastShort(res.getErrorMessage());
                hideWaitDialog();
            }
        }

        @Override
        public void onFailure(int code, String errorMessage, Object[] args) {
            AppContext.showToastShort(R.string.tip_delete_faile);
            hideWaitDialog();
        }
    }
}
