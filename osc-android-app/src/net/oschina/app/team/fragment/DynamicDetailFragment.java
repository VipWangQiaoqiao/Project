package net.oschina.app.team.fragment;

import net.oschina.app.R;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.bean.Active;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DynamicDetailFragment extends BaseFragment {

    private Active data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.frag_dynamic_detail, null);
        initData();
        initView(root);
        return root;
    }

    @Override
    public void initData() {
        super.initData();
        Bundle bundle = getArguments();
        data = (Active) bundle
                .getSerializable(DynamicFragment.DYNAMIC_FRAGMENT_KEY);
    }
}
