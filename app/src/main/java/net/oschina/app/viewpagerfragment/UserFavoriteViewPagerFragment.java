package net.oschina.app.viewpagerfragment;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import net.oschina.app.R;
import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.improve.user.fragments.NewUserFavoriteFragment;

/**
 * 用户收藏页
 */
public class UserFavoriteViewPagerFragment extends BaseViewPagerFragment {

    public static UserFavoriteViewPagerFragment newInstance() {
        return new UserFavoriteViewPagerFragment();
    }

    @Override
    protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {

        FrameLayout generalActionBar = (FrameLayout) mRoot.findViewById(R.id.general_actionbar);
        generalActionBar.setVisibility(View.GONE);

        String[] title = getResources().getStringArray(R.array.userfavorite);
        adapter.addTab(title[0], "favorite_all", NewUserFavoriteFragment.class, getBundle(OSChinaApi.CATALOG_ALL));
        adapter.addTab(title[1], "favorite_software", NewUserFavoriteFragment.class, getBundle(OSChinaApi.CATALOG_SOFTWARE));
        adapter.addTab(title[2], "favorite_question", NewUserFavoriteFragment.class, getBundle(OSChinaApi.CATALOG_QUESTION));
        adapter.addTab(title[3], "favorite_blogs", NewUserFavoriteFragment.class, getBundle(OSChinaApi.CATALOG_BLOG));
        adapter.addTab(title[4], "favorite_translation", NewUserFavoriteFragment.class, getBundle(OSChinaApi.CATALOG_TRANSALITON));
        //adapter.addTab(title[5], "favotite_event", NewUserFavoriteFragment.class, getBundle(OSChinaApi.CATALOG_EVENT));
        adapter.addTab(title[6], "favorite_news", NewUserFavoriteFragment.class, getBundle(OSChinaApi.CATALOG_NEWS));

    }

    private Bundle getBundle(int favoriteType) {
        Bundle bundle = new Bundle();
        bundle.putInt(NewUserFavoriteFragment.CATALOG_TYPE, favoriteType);
        return bundle;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void initData() {

    }

}
