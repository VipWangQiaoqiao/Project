package net.oschina.app.viewpagefragment;

import net.oschina.app.R;
import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.bean.SoftwareDec;
import net.oschina.app.fragment.SoftwareCatalogListFragment;
import net.oschina.app.fragment.SoftwareListFragment;
import android.os.Bundle;
import android.view.View;

public class OpensourceSoftwareFragment extends BaseViewPagerFragment {
	
	public static OpensourceSoftwareFragment newInstance(){
		return new OpensourceSoftwareFragment();
	}
	
	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		String[] title = getResources().getStringArray(R.array.opensourcesoftware);
		adapter.addTab(title[0], "software_catalog", SoftwareCatalogListFragment.class, null);
		adapter.addTab(title[1], "software_recommend", SoftwareListFragment.class, getBundle(SoftwareDec.CATALOG_RECOMMEND));
		adapter.addTab(title[2], "software_latest", SoftwareListFragment.class, getBundle(SoftwareDec.CATALOG_TIME));
		adapter.addTab(title[3], "software_hot", SoftwareListFragment.class, getBundle(SoftwareDec.CATALOG_VIEW));
		adapter.addTab(title[4], "software_china", SoftwareListFragment.class, getBundle(SoftwareDec.CATALOG_LIST_CN));
	}
	
	private Bundle getBundle(String catalog) {
		Bundle bundle = new Bundle();
		bundle.putString(BaseListFragment.BUNDLE_SOFTWARE, catalog);
		return bundle;
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