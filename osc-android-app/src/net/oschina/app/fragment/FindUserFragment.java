package net.oschina.app.fragment;

import java.io.ByteArrayInputStream;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpResponseHandler;

import butterknife.ButterKnife;
import butterknife.InjectView;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.bean.UserInformation;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.XmlUtils;
import net.oschina.app.widget.AvatarView;

/** 
 * 查找用户界面
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年12月8日 下午3:50:20 
 * 
 */

public class FindUserFragment extends BaseFragment {
	
	private SearchView mSearchView;
	
	@InjectView(R.id.ll_user_info) View mUserInfo;
	
	@InjectView(R.id.iv_acatar) AvatarView mIvAvatar;
	
	@InjectView(R.id.tv_username) TextView mTvName;
	
	@InjectView(R.id.loading) View mLoading;
	
	private AsyncHttpResponseHandler mHandle = new AsyncHttpResponseHandler() {
		
		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			mLoading.setVisibility(View.GONE);
			UserInformation user = XmlUtils.toBean(UserInformation.class, new ByteArrayInputStream(arg2));
			
		}
		
		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
			mLoading.setVisibility(View.GONE);
			AppContext.showToast("加载失败");
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_find_user, null);
		ButterKnife.inject(this, view);
		setHasOptionsMenu(true);
		initView(view);
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.search_menu, menu);
		MenuItem search=menu.findItem(R.id.search_content);
		mSearchView=(SearchView) search.getActionView();
		mSearchView.setIconifiedByDefault(false);
		setSearch();
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	private void setSearch() {
		mSearchView.setQueryHint("输入用户昵称");
		TextView textView = (TextView) mSearchView.findViewById(R.id.search_src_text);
		textView.setTextColor(Color.WHITE);
		
		mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String arg0) {
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String arg0) {
				search(arg0);
				return false;
			}
		});
	}
	
	private void search(String nickName) {
		if (nickName == null || StringUtils.isEmpty(nickName)) {
			return;
		}
		mLoading.setVisibility(View.VISIBLE);
		mUserInfo.setVisibility(View.GONE);
		OSChinaApi.findUser(nickName, mHandle);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.ll_user_info:
			break;
		default:
			break;
		}
	}

	@Override
	public void initView(View view) {
		mUserInfo.setOnClickListener(this);
	}

	@Override
	public void initData() {
		
	}
}
