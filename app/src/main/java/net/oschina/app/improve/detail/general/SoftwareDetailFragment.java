package net.oschina.app.improve.detail.general;

import net.oschina.app.R;
import net.oschina.app.improve.detail.v2.DetailFragment;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class SoftwareDetailFragment extends DetailFragment {
    public static SoftwareDetailFragment newInstance() {
        SoftwareDetailFragment fragment = new SoftwareDetailFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_software_detail_v2;
    }
}
