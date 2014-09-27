package net.oschina.app.base;

import android.view.View;

public class BaseTabFragment extends BaseFragment {

	public static interface TabChangedListener {

		public abstract boolean isCurrent(BaseTabFragment fragment);
	}

	private TabChangedListener mListener;

	public BaseTabFragment() {
	}

	public final void a(TabChangedListener listener) {
		mListener = listener;
	}

	protected final boolean e() {
		return mListener.isCurrent(this);
	}

	public void f() {
	}

	public void g() {
	}

	public void h() {
	}

	public void i() {
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		
	}
}
