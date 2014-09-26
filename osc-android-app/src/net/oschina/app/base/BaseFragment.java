package net.oschina.app.base;

import butterknife.ButterKnife;
import net.oschina.app.AppContext;
import net.oschina.app.interf.BaseFragmentInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 碎片基类
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年9月25日 上午11:18:46
 *
 */
public abstract class BaseFragment extends Fragment implements android.view.View.OnClickListener, BaseFragmentInterface {
	protected static final int STATE_NONE = 0;
	protected static final int STATE_REFRESH = 1;
	protected static final int STATE_LOADMORE = 2;
	protected int mState = STATE_NONE;
	
	protected LayoutInflater mInflater;
	
	public AppContext getApplication() {
		return (AppContext) getActivity().getApplication();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.mInflater = inflater;
		View view = inflater.inflate(getLayoutId(), null);
		// 通过注解绑定控件
		ButterKnife.inject(this, view);
		initView();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	protected int getLayoutId() {
		return 0;
	}
	
	protected View inflateView(int resId) {
		return this.mInflater.inflate(resId, null);
	}
}
