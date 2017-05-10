package net.oschina.app.improve.git.gist.detail;

import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseFragment;

/**
 * 代码片段详情
 * Created by haibin on 2017/5/10.
 */

public class GistDetailFragment extends BaseFragment {

    public static GistDetailFragment newInstance() {
        GistDetailFragment fragment = new GistDetailFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_gist_detail;
    }
}
