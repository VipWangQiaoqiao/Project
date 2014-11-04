package net.oschina.app.fragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import net.oschina.app.R;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.util.UIHelper;

/** 
 * 发现页面
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年11月4日 下午3:34:07 
 * 
 */

public class ExploreFragment extends BaseFragment {
	
	@InjectView(R.id.rl_active)View mRlActive;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_explore, null);
		ButterKnife.inject(this, view);
		initView(view);
		return view;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.rl_active:
			UIHelper.showMyActive(getActivity());
			break;
		default:
			break;
		}
	}

	@Override
	public void initView(View view) {
		mRlActive.setOnClickListener(this);
	}

	@Override
	public void initData() {

	}

}
