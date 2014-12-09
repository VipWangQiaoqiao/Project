package net.oschina.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import net.oschina.app.R;
import net.oschina.app.base.BaseFragment;

/** 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年9月25日 下午2:53:01 
 * 
 */

public class FragmentTest extends BaseFragment {
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(getLayoutId(), null);
		return view;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_settings;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initView(View view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

}
