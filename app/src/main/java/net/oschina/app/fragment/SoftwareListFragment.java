package net.oschina.app.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import net.oschina.app.adapter.SoftwareAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.bean.Entity;
import net.oschina.app.bean.ListEntity;
import net.oschina.app.bean.SoftwareDec;
import net.oschina.app.bean.SoftwareList;
import net.oschina.app.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

public class SoftwareListFragment extends BaseListFragment<SoftwareDec> {

    public static final String BUNDLE_SOFTWARE = "BUNDLE_SOFTWARE";

    private static final String CACHE_KEY_PREFIX = "softwarelist_";

    private String softwareType = "recommend";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            softwareType = args.getString(BUNDLE_SOFTWARE);
        }
    }

    @Override
    protected SoftwareAdapter getListAdapter() {
        return new SoftwareAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + softwareType;
    }

    @Override
    protected SoftwareList parseList(InputStream is) throws Exception {
        SoftwareList list = XmlUtils.toBean(SoftwareList.class, is);
        return list;
    }

    @Override
    protected ListEntity readList(Serializable seri) {
        return ((SoftwareList) seri);
    }

    @Override
    protected void sendRequestData() {
        OSChinaApi.getSoftwareList(softwareType, mCurrentPage, mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        SoftwareDec softwaredec = mAdapter.getItem(position);
        if (softwaredec != null) {
            // String ident = softwaredec.getUrl().substring(softwaredec.getUrl().lastIndexOf("/") + 1);
            int softwareDecId = softwaredec.getId();
            //SoftwareDetailActivity.show(getActivity(), ident);
            net.oschina.app.improve.detail.general.SoftwareDetailActivity.show(getActivity(), softwareDecId);
            // 放入已读列表
            saveToReadedList(view, SoftwareList.PREF_READED_SOFTWARE_LIST,
                    softwaredec.getName());
        }
    }

    @Override
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null) {
            for (int i = 0; i < s; i++) {
                if (((SoftwareDec) enity).getName().equals(
                        ((SoftwareDec) data.get(i)).getName())) {
                    return true;
                }
            }
        }
        return false;
    }

}
