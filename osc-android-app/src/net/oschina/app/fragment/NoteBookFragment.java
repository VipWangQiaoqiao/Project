package net.oschina.app.fragment;

import java.util.ArrayList;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.NotebookAdapter;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.bean.NotebookData;
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.db.NoteDatabase;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.DragGridView;
import net.oschina.app.widget.DragGridView.OnDeleteListener;
import net.oschina.app.widget.DragGridView.OnMoveListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 便签列表界面
 * 
 * @author kymjs(kymjs123@gmail.com)
 * 
 */
public class NoteBookFragment extends BaseFragment implements
        OnItemClickListener, OnRefreshListener {

    @InjectView(R.id.frag_note_list)
    DragGridView mList;
    @InjectView(R.id.frag_note_trash)
    ImageView mImgTrash;
    @InjectView(R.id.swiperefreshlayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private NoteDatabase noteDb;
    private ArrayList<NotebookData> datas;
    private NotebookAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_note, container,
                false);
        ButterKnife.inject(this, rootView);
        initData();
        initView(rootView);
        return rootView;
    }

    @Override
    public void initData() {
        noteDb = new NoteDatabase(getActivity());
        datas = noteDb.query();// 查询操作，忽略耗时
        if (datas != null) {
            adapter = new NotebookAdapter(getActivity(), datas);
        }
    }

    @Override
    public void initView(View view) {
        mList.setAdapter(adapter);
        mList.setOnItemClickListener(this);
        mList.setTrashView(mImgTrash);
        mList.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mList.setOnDeleteListener(new OnDeleteListener() {
            @Override
            public void onDelete(int position) {
                delete(datas.get(position));
            }
        });
        mList.setOnMoveListener(new OnMoveListener() {
            @Override
            public void startMove() {
                mSwipeRefreshLayout.setEnabled(false);
            }

            @Override
            public void endMove() {
                setListCanPull();
                if (adapter.getDataChange()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            noteDb.reset(adapter.getDatas());
                        }
                    }).start();
                }
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
    }

    @Override
    public void onResume() {
        super.onResume();
        refurbish();
        setListCanPull();
    }

    /*************** private method *********************/

    @Override
    public void onRefresh() {
        if (mState == STATE_REFRESH) {
            return;
        }

        // 设置顶部正在刷新
        mList.setSelection(0);
        setSwipeRefreshLoadingState();

        /* !!! 设置耗时操作 !!! */

        refurbish();
        setSwipeRefreshLoadedState();
    }

    /**
     * 设置顶部正在加载的状态
     */
    private void setSwipeRefreshLoadingState() {
        mState = STATE_REFRESH;
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            // 防止多次重复刷新
            mSwipeRefreshLayout.setEnabled(false);
        }
        mList.setDragEnable(false);
    }

    /**
     * 设置顶部加载完毕的状态
     */
    private void setSwipeRefreshLoadedState() {
        mState = STATE_NOMORE;
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);
        }
        mList.setDragEnable(true);
    }

    /**
     * 刷新列表数据
     */
    private void refurbish() {
        datas = noteDb.query();
        if (datas != null && adapter != null) {
            adapter.refurbishData(datas);
        }
    }

    /**
     * 删除数据
     * 
     * @param data
     */
    private void delete(NotebookData data) {
        noteDb.delete(data.getId());
        if (datas != null && adapter != null) {
            adapter.refurbishData(datas);
            mList.setAdapter(adapter);
        }
    }

    /**
     * 未登陆用户不能下拉同步，只能使用本地存储
     */
    private void setListCanPull() {
        if (!AppContext.getInstance().isLogin()) {
            mSwipeRefreshLayout.setEnabled(false);
        } else {
            mSwipeRefreshLayout.setEnabled(true);
        }
    }

    /*************** function method *********************/

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Bundle bundle = new Bundle();
        bundle.putInt(NoteEditFragment.NOTE_FROMWHERE_KEY,
                NoteEditFragment.NOTEBOOK_ITEM);
        bundle.putSerializable(NoteEditFragment.NOTE_KEY, datas.get(position));
        UIHelper.showSimpleBack(getActivity(), SimpleBackPage.NOTE_EDIT, bundle);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.pub_tweet_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.public_menu_send:
            Bundle bundle = new Bundle();
            bundle.putInt(NoteEditFragment.NOTE_FROMWHERE_KEY,
                    NoteEditFragment.NOTEBOOK_FRAGMENT);
            UIHelper.showSimpleBack(getActivity(), SimpleBackPage.NOTE_EDIT,
                    bundle);
            break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {}
}
