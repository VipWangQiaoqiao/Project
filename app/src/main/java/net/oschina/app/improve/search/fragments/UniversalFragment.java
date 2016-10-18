package net.oschina.app.improve.search.fragments;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.search.bean.SearchResultArticle;

import java.lang.reflect.Type;

/**
 * 博客、软件、资讯、问答的搜索界面
 * Created by thanatos on 16/10/18.
 */

public class UniversalFragment extends BaseRecyclerViewFragment<SearchResultArticle>{


    @Override
    protected BaseRecyclerAdapter<SearchResultArticle> getRecyclerAdapter() {
        // TODO add adapter
        return null;
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<SearchResultArticle>>(){}.getType();
    }
}
