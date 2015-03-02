package net.oschina.app.team.fragment;

import java.util.ArrayList;
import java.util.List;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.ApiHttpClient;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.bean.NotebookData;
import net.oschina.app.bean.NotebookDataList;
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.bean.User;
import net.oschina.app.db.NoteDatabase;
import net.oschina.app.team.adapter.NotebookAdapter;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.KJAnimations;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import net.oschina.app.widget.KJDragGridView;
import net.oschina.app.widget.KJDragGridView.OnDeleteListener;
import net.oschina.app.widget.KJDragGridView.OnMoveListener;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.http.core.KJAsyncTask;
import org.kymjs.kjframe.http.core.KJAsyncTask.OnFinishedListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 便签列表界面
 * 
 * @author kymjs (https://github.com/kymjs)
 */
public class NoteBookFragment extends BaseFragment implements
        OnItemClickListener, OnRefreshListener {

    @InjectView(R.id.frag_note_list)
    KJDragGridView mGrid;
    @InjectView(R.id.frag_note_trash)
    ImageView mImgTrash;
    @InjectView(R.id.swiperefreshlayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.error_layout)
    EmptyLayout mEmptyLayout;

    private NoteDatabase noteDb;
    private ArrayList<NotebookData> datas;
    private NotebookAdapter adapter;
    private User user;
    private Activity aty;

    /**
     * 用来做更进一步人性化的防手抖策略时使用<br>
     * 比如由于手抖动上下拉菜单时拉动一部分，但又没有达到可刷新的时候，暂停一段时间，这个时候用户的逻辑应该是想移动item的。<br>
     * （这手抽的也太厉害了吧，这里为了效率就算了，没必要那么复杂）<br>
     * 其实应该还有一种根据setOnFocusChangeListener来改写的方法，我没有尝试。
     */
    // private static final Handler mHandler = new Handler() {
    // @Override
    // public void handleMessage(android.os.Message msg) {
    // mList.setDragEnable(true);
    // };
    // };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_note, container,
                false);
        aty = getActivity();
        ButterKnife.inject(this, rootView);
        initData();
        initView(rootView);
        return rootView;
    }

    @Override
    public void initData() {
        user = AppContext.getInstance().getLoginUser();
        noteDb = new NoteDatabase(getActivity());
        datas = noteDb.query();// 查询操作，忽略耗时
        if (datas != null) {
            adapter = new NotebookAdapter(aty, datas);
        }

        if (datas != null && !datas.isEmpty()) {
            mEmptyLayout.setVisibility(View.GONE);
        } else {
            mEmptyLayout.setVisibility(View.VISIBLE);
            mEmptyLayout.setErrorType(EmptyLayout.NODATA);
            mEmptyLayout.setNoDataContent("暂无便签，请添加或下拉同步");
        }
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void initView(View view) {
        mGrid.setAdapter(adapter);
        mGrid.setOnItemClickListener(this);
        mGrid.setTrashView(mImgTrash);
        mGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mGrid.setOnDeleteListener(new OnDeleteListener() {
            @Override
            public void onDelete(int position) {
                delete(position);
            }
        });
        mGrid.setOnMoveListener(new OnMoveListener() {
            @Override
            public void startMove() {
                mSwipeRefreshLayout.setEnabled(false);
                mImgTrash.startAnimation(KJAnimations.getTranslateAnimation(0,
                        0, mImgTrash.getTop(), 0, 500));
                mImgTrash.setVisibility(View.VISIBLE);
            }

            @Override
            public void finishMove() {
                setListCanPull();
                mImgTrash.setVisibility(View.INVISIBLE);
                mImgTrash.startAnimation(KJAnimations.getTranslateAnimation(0,
                        0, 0, mImgTrash.getTop(), 500));
                if (adapter.getDataChange()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            noteDb.reset(adapter.getDatas());
                        }
                    }).start();
                }
            }

            @Override
            public void cancleMove() {}
        });
        mSwipeRefreshLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    mState = STATE_PRESSNONE;
                    mGrid.setDragEnable(false);
                    // 如果你愿意还可以进一步人性化处理，请看mHandler注释
                    // mHandler.sendMessageDelayed(Message.obtain(), 400);
                } else {
                    mGrid.setDragEnable(true);
                }
                return false;
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
        mGrid.setSelection(0);
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
        mGrid.setDragEnable(false);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            // 防止多次重复刷新
            mSwipeRefreshLayout.setEnabled(false);
        }
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
        mGrid.setDragEnable(true);
    }

    /**
     * 使用自带缓存功能的网络请求，防止多次刷新造成的流量损耗以及服务器压力
     * 
     * @param i
     */
    private void refurbish() {
        datas = noteDb.query();
        if (datas != null && adapter != null) {
            adapter.refurbishData(datas);
        }
        if (user.getUid() != 0) { // 未登录时不请求网络
            HttpConfig config = new HttpConfig();
            config.setCookieString(AppContext.getInstance().getProperty(
                    AppConfig.CONF_COOKIE));
            KJHttp kjh = new KJHttp(config);

            HttpParams params = new HttpParams();
            params.put("uid", user.getUid() + "");
            kjh.get("http://" + ApiHttpClient.HOST
                    + "/action/api/team_sticky_list", params,
                    new HttpCallBack() {
                        @Override
                        public void onSuccess(final String t) {
                            super.onSuccess(t);
                            NotebookDataList dataList = XmlUtils.toBean(
                                    NotebookDataList.class, t.getBytes());
                            if (dataList != null) {
                                doSynchronize(dataList.getList());
                            }
                        }
                    });
        }

        if (datas != null && !datas.isEmpty()) {
            mEmptyLayout.setVisibility(View.GONE);
        } else {
            mEmptyLayout.setVisibility(View.VISIBLE);
            mEmptyLayout.setErrorType(EmptyLayout.NODATA);
            mEmptyLayout.setNoDataContent("暂无便签，请添加或下拉同步");
        }
    }

    /**
     * 删除数据
     * 
     * @param data
     */
    private void delete(int index) {
        noteDb.delete(datas.get(index).getId());
        datas.remove(index);
        if (datas != null && adapter != null) {
            adapter.refurbishData(datas);
            mGrid.setAdapter(adapter);
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

    /**
     * 处理云端数据与本地数据的同步逻辑
     * 
     * @param cloudDatas
     *            云端的数据列表
     */
    private void doSynchronize(final List<NotebookData> cloudDatas) {
        // 设置线程完成时的相应方法
        KJAsyncTask.setOnFinishedListener(new OnFinishedListener() {
            @Override
            public void onPostExecute() {
                // 在UI线程更新视图
                super.onPostExecute();
                if (datas != null && adapter != null) {
                    adapter.refurbishData(datas);
                }
            }
        });
        // 使用并发的方式启动线程
        KJAsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                for (NotebookData data : cloudDatas) {
                    if (data != null) {
                        noteDb.merge(data); // 首先将云端数据合并到本地
                    }
                }
                datas = noteDb.query(); // 合并完成后更新适配器数据缓存
            }
        });
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
        inflater.inflate(R.menu.notebook_menu, menu);
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
    public void onDestroy() {
        super.onDestroy();
        noteDb.destroy();
    }

    @Override
    public void onClick(View v) {}
}