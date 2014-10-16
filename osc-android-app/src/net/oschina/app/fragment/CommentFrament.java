package net.oschina.app.fragment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.CommentAdapter;
import net.oschina.app.adapter.CommentAdapter.OnOperationListener;
import net.oschina.app.api.OperationResponseHandler;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.ListEntity;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.ResultBean;
import net.oschina.app.emoji.EmojiFragment;
import net.oschina.app.emoji.EmojiFragment.EmojiTextListener;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class CommentFrament extends BaseListFragment implements
		OnOperationListener, EmojiTextListener, OnItemLongClickListener {

	public static final String BUNDLE_KEY_CATALOG = "BUNDLE_KEY_CATALOG";
	public static final String BUNDLE_KEY_BLOG = "BUNDLE_KEY_BLOG";
	public static final String BUNDLE_KEY_ID = "BUNDLE_KEY_ID";
	public static final String BUNDLE_KEY_OWNER_ID = "BUNDLE_KEY_OWNER_ID";
	protected static final String TAG = NewsFragment.class.getSimpleName();
	private static final String BLOG_CACHE_KEY_PREFIX = "blogcomment_list";
	private static final String CACHE_KEY_PREFIX = "comment_list";
	private static final int REQUEST_CODE = 0x10;
	private static final String COMMENT_SCREEN = "comment_screen";

	private int mId, mOwnerId;
	private boolean mIsBlogComment;

	private EmojiFragment mEmojiFragment;

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
		activity.findViewById(R.id.emoji_container).setVisibility(View.GONE);
	}

	protected int getLayoutRes() {
		return R.layout.fragment_pull_refresh_listview;
	}
	
	@Override
	public void initView(View view) {
		super.initView(view);
		mListView.setOnItemLongClickListener(this);
	}

	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			mCatalog = args.getInt(BUNDLE_KEY_CATALOG, 0);
			mId = args.getInt(BUNDLE_KEY_ID, 0);
			mOwnerId = args.getInt(BUNDLE_KEY_OWNER_ID, 0);
			mIsBlogComment = args.getBoolean(BUNDLE_KEY_BLOG, false);
		}

		if (!mIsBlogComment && mCatalog == CommentList.CATALOG_POST) {
			((BaseActivity) getActivity())
					.setActionBarTitle(R.string.post_answer);
		}

		int mode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
				| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
		getActivity().getWindow().setSoftInputMode(mode);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE 
				&& resultCode == Activity.RESULT_OK) {
			//if (mState == STATE_NONE) {
				//mCurrentPage = 0;
				//mState = STATE_REFRESH;
				//requestData(true);
			//} 
			Comment comment = data.getParcelableExtra(Comment.BUNDLE_KEY_COMMENT);
			if(comment != null) {
				mAdapter.addItem(0,comment);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected ListBaseAdapter getListAdapter() {
		return new CommentAdapter(this);
	}

	@Override
	protected String getCacheKeyPrefix() {
		String str = mIsBlogComment ? BLOG_CACHE_KEY_PREFIX : CACHE_KEY_PREFIX;
		return new StringBuilder(str).append("_").append(mId).append("_Owner")
				.append(mOwnerId).toString();
	}

	@Override
	protected ListEntity parseList(InputStream is) throws Exception {
		if (mIsBlogComment) {
//			return BlogCommentList.parse(is);
			return null;
		} else {
			return XmlUtils.toBean(CommentList.class, is);//CommentList.parse(is);
		}
	}

	@Override
	protected ListEntity readList(Serializable seri) {
//		if (mIsBlogComment)
//			return ((BlogCommentList) seri);
		return ((CommentList) seri);
	}

	@Override
	public boolean onBackPressed() {
		if (mEmojiFragment != null) {
			return mEmojiFragment.onBackPressed();
		}
		return super.onBackPressed();
	}

	@Override
	protected void sendRequestData() {
		if (mIsBlogComment) {
			OSChinaApi.getBlogCommentList(mId, mCurrentPage, mHandler);
		} else {
			OSChinaApi.getCommentList(mId, mCatalog, mCurrentPage, mHandler);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
//		final Comment comment = (Comment) mAdapter.getItem(position - 1);
//		if (comment == null)
//			return;
//		final CommonDialog dialog = DialogHelper
//				.getPinterestDialogCancelable(getActivity());
//		String[] items = null;
//		if (AppContext.instance().isLogin()
//				&& AppContext.instance().getLoginUid() == comment.getAuthorId()) {
//			items = new String[] { getString(R.string.reply),
//					getString(R.string.delete) };
//			dialog.setTitle(R.string.operation);
//			dialog.setItemsWithoutChk(items, new OnItemClickListener() {
//
//				@Override
//				public void onItemClick(AdapterView<?> parent, View view,
//						int position, long id) {
//					dialog.dismiss();
//					if (position == 0) {
//						handleReplyComment(comment);
//					} else if (position == 1) {
//						handleDeleteComment(comment);
//					}
//				}
//
//			});
//			dialog.setNegativeButton(R.string.cancle, null);
//			dialog.show();
//		} else {
//			handleReplyComment(comment);
//		}
	}

	private void handleReplyComment(Comment comment) {
		if (!AppContext.getInstance().isLogin()) {
			UIHelper.showLoginActivity(getActivity());
			return;
		}
//		UIHelper.showReplyCommentForResult(this, REQUEST_CODE,
//				mIsBlogComment, mId, mCatalog, comment);
		
	}

	private void handleDeleteComment(Comment comment) {
//		if (!AppContext.getInstance().isLogin()) {
//			UIHelper.showLoginActivity(getActivity());
//			return;
//		}
//		AppContext.showToastShort(R.string.deleting);
//		if (mIsBlogComment) {
//			OSChinaApi.deleteBlogComment(AppContext.getInstance().getLoginUid(), mId,
//					comment.getId(), comment.getAuthorId(), mOwnerId,
//					new DeleteOperationResponseHandler(comment));
//		} else {
//			OSChinaApi.deleteComment(mId, mCatalog, comment.getId(), comment
//					.getAuthorId(), new DeleteOperationResponseHandler(comment));
//		}
	}

//	@Override
//	public void onMoreClick(final Comment comment) {
//	}

	@Override
	public void onSendClick(String text) {
	}

	class DeleteOperationResponseHandler extends OperationResponseHandler {

		DeleteOperationResponseHandler(Object... args) {
			super(args);
		}

		@Override
		public void onSuccess(int code, ByteArrayInputStream is, Object[] args) {
			try {
				Result res = XmlUtils.toBean(ResultBean.class, is).getResult();
				if (res.OK()) {
//					AppContext.showToastShort(R.string.delete_success);
//					mAdapter.removeItem(args[0]);
				} else {
					AppContext.showToastShort(res.getErrorMessage());
				}
			} catch (Exception e) {
				e.printStackTrace();
				onFailure(code, e.getMessage(), args);
			}
		}

		@Override
		public void onFailure(int code, String errorMessage, Object[] args) {
//			AppContext.showToastShort(R.string.delete_faile);
		}
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
//		final Comment item = (Comment) mAdapter.getItem(position - 1);
//		if (item == null)
//			return false;
//		String[] items = new String[] { getResources().getString(R.string.copy) };
//		final CommonDialog dialog = DialogHelper
//				.getPinterestDialogCancelable(getActivity());
//		dialog.setNegativeButton(R.string.cancle, null);
//		dialog.setItemsWithoutChk(items, new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				dialog.dismiss();
//				TDevice.copyTextToBoard(HTMLSpirit.delHTMLTag(item
//						.getContent()));
//			}
//		});
//		dialog.show();
		return true;
	}

	@Override
	public void onMoreClick(Comment comment) {
		
	}
}
