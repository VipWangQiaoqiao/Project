package net.oschina.app.improve.main.subcription;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;

import java.lang.reflect.Type;

/**
 * Created by haibin
 * on 2016/10/26.
 */

public class SubFragment extends BaseRecyclerViewFragment<SubBean> {
    @Override
    protected BaseRecyclerAdapter<SubBean> getRecyclerAdapter() {
        return new SubBeanAdapter(getActivity(), BaseRecyclerAdapter.BOTH_HEADER_FOOTER);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<SubBean>>>() {
        }.getType();
    }
}
