package net.oschina.app.improve.search;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.widget.LinearLayout;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseActivity;

import butterknife.Bind;

/**
 * V2版的Search View, 科科~
 * Created by thanatos on 16/10/18.
 */

public class V2SearchActivity extends BaseActivity {

    @Bind(R.id.view_root) LinearLayout mViewRoot;
    @Bind(R.id.layout_tab) TabLayout mLayoutTab;
    @Bind(R.id.view_pager) ViewPager mViewPager;
    @Bind(R.id.view_searcher) SearchView mViewSearch;

    @Override
    protected int getContentView() {
        return R.layout.activity_v2_search;
    }
}
