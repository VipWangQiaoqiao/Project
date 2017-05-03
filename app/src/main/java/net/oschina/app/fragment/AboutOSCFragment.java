package net.oschina.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.Setting;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.common.admin.Boss;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutOSCFragment extends BaseFragment {

    @Bind(R.id.tv_version_name)
    TextView mTvVersionName;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        view.findViewById(R.id.tv_grade).setOnClickListener(this);
        view.findViewById(R.id.tv_oscsite).setOnClickListener(this);
        view.findViewById(R.id.tv_knowmore).setOnClickListener(this);
    }

    @Override
    public void initData() {
        mTvVersionName.setText(TDevice.getVersionName());
    }

    @Override
    @OnClick(R.id.img_portrait)
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.tv_grade:
                TDevice.openAppInMarket(getActivity());
                break;
            case R.id.tv_oscsite:
                UIHelper.openInternalBrowser(getActivity(), "https://www.oschina.net");
                break;
            case R.id.tv_knowmore:
                UIHelper.openInternalBrowser(getActivity(),
                        "https://www.oschina.net/home/aboutosc");
                break;
            case R.id.img_portrait:
                Boss.verifyApp(getContext());
                Setting.updateSystemConfigTimeStamp(getContext());
                break;
            default:
                break;
        }
    }
}
