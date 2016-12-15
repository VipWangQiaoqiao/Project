package net.oschina.app.improve.detail.general;

import net.oschina.app.R;
import net.oschina.app.improve.detail.v2.DetailFragment;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class EventDetailFragment extends DetailFragment {

    public static EventDetailFragment newInstance() {
        EventDetailFragment fragment = new EventDetailFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_event_detail_v2;
    }
}
