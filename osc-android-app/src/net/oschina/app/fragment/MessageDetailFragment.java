package net.oschina.app.fragment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.MessageDetailAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.Constants;
import net.oschina.app.bean.ListEntity;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.ResultBean;
import net.oschina.app.emoji.EmojiFragment;
import net.oschina.app.emoji.EmojiFragment.EmojiTextListener;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;

import org.apache.http.Header;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.loopj.android.http.AsyncHttpResponseHandler;

public class MessageDetailFragment extends BaseListFragment implements
		EmojiTextListener {
	protected static final String TAG = ActiveFragment.class.getSimpleName();
	public static final String BUNDLE_KEY_FID = "BUNDLE_KEY_FID";
	public static final String BUNDLE_KEY_FNAME = "BUNDLE_KEY_FNAME";
	private static final String CACHE_KEY_PREFIX = "message_detail_list";
	private boolean mIsWatingLogin;

	private int mFid;
	private String mFName;
	private EmojiFragment mEmojiFragment;

	private AsyncHttpResponseHandler mPublicHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				ResultBean resb = XmlUtils.toBean(ResultBean.class, new ByteArrayInputStream(arg2));
				Result res = resb.getResult();
				if (res.OK()) {
					AppContext
							.showToastShort(R.string.tip_message_public_success);
					mAdapter.addItem(resb.getComment());
					mEmojiFragment.reset();
				} else {
					AppContext.showToastShort(res.getErrorMessage());
				}
			} catch (Exception e) {
				e.printStackTrace();
				onFailure(arg0, arg1, arg2, e);
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			AppContext.showToastShort(R.string.tip_message_public_faile);
		}
	};

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

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
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		BaseActivity act = ((BaseActivity) activity);
		FragmentTransaction trans = act.getSupportFragmentManager()
				.beginTransaction();
		mEmojiFragment = new EmojiFragment();
		mEmojiFragment.setEmojiTextListener(this);
		trans.replace(R.id.emoji_container, mEmojiFragment);
		trans.commit();
	}

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
	protected ListBaseAdapter getListAdapter() {
		return new MessageDetailAdapter();
	}

	@Override
	protected String getCacheKeyPrefix() {
		return CACHE_KEY_PREFIX + mFid;
	}

	@Override
	protected ListEntity parseList(InputStream is) throws Exception {
		CommentList list = XmlUtils.toBean(CommentList.class, is);
		list.sortList();
		return list;
	}

	@Override
	protected ListEntity readList(Serializable seri) {
		CommentList list = ((CommentList) seri);
		list.sortList();
		return list;
	}

	@Override
	public void initView(View view) {
		super.initView(view);
		mListView.setDivider(null);
		mListView.setDividerHeight(0);
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
		OSChinaApi.getCommentList(mFid, mCatalog, mCurrentPage, mHandler);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// Message active = (Message) mAdapter.getItem(position - 1);
		// UIHelper.showMessageDetail(context, friendid, friendname);
	}

	@Override
	public void onSendClick(String text) {
		if (!AppContext.getInstance().isLogin()) {
			UIHelper.showLoginActivity(getActivity());
			return;
		}
		if (TextUtils.isEmpty(text)) {
			mEmojiFragment.requestFocusInput();
			AppContext.showToastShort(R.string.tip_content_empty);
			return;
		}
		OSChinaApi.publicMessage(AppContext.getInstance().getLoginUid(), mFid, text,
				mPublicHandler);
	}
}
