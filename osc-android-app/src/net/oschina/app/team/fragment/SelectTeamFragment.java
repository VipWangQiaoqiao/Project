package net.oschina.app.team.fragment;

import java.io.ByteArrayInputStream;
import java.util.List;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.fragment.MyInformationFragment;
import net.oschina.app.team.adapter.SelectTeamAdapter;
import net.oschina.app.team.bean.Team;
import net.oschina.app.team.bean.TeamList;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;

import org.apache.http.Header;
import org.kymjs.kjframe.utils.DensityUtils;
import org.kymjs.kjframe.utils.PreferenceHelper;
import org.kymjs.kjframe.utils.StringUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * 选择团队列表界面
 * 
 * @author kymjs (kymjs123@gmail.com)
 * 
 */
public class SelectTeamFragment extends BaseFragment {

    @InjectView(R.id.team_select_list)
    ListView mList;
    @InjectView(R.id.team_select_title)
    TextView mTvTitle;

    private Activity aty;
    private List<Team> datas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        aty = getActivity();
        View view = View.inflate(aty, R.layout.team_select_team, null);
        ButterKnife.inject(this, view);
        initData();
        initView(view);
        return view;
    }

    @Override
    public void onClick(View v) {}

    @Override
    public void initView(View view) {
        screenAdapter();
        // 初始化团队列表数据
        String cache = PreferenceHelper.readString(getActivity(),
                MyInformationFragment.TEAM_LIST_FILE,
                MyInformationFragment.TEAM_LIST_KEY);
        if (!StringUtils.isEmpty(cache)) {
            datas = TeamList.toTeamList(cache);
            mList.setAdapter(new SelectTeamAdapter(aty, datas));
        }
        getTeamList();
        mList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Bundle bundle = new Bundle();
                // key是个历史遗留问题。。。
                bundle.putInt(MyInformationFragment.TEAM_LIST_KEY, position);
                UIHelper.showTeamMainActivity(aty, bundle);
                // UIHelper.showSimpleBack(aty, SimpleBackPage.DYNAMIC, bundle);
            }
        });
    }

    @Override
    public void initData() {}

    private void screenAdapter() {
        RelativeLayout.LayoutParams params = (LayoutParams) mList
                .getLayoutParams();
        int screenH = DensityUtils.getScreenH(aty);
        params.width = (int) (DensityUtils.getScreenW(aty) * 0.7);
        params.height = (int) (screenH * 0.7);
        params.topMargin = (int) ((screenH - params.height) * 0.6);
        mList.setLayoutParams(params);
    }

    /**
     * 拉取团队列表信息
     */
    private void getTeamList() {
        OSChinaApi.teamList(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                TeamList datas = XmlUtils.toBean(TeamList.class,
                        new ByteArrayInputStream(arg2));
                if (datas != null && datas.getTeams() != null) {
                    PreferenceHelper.write(getActivity(),
                            MyInformationFragment.TEAM_LIST_FILE,
                            MyInformationFragment.TEAM_LIST_KEY,
                            datas.toCacheData());
                    SelectTeamFragment.this.datas = datas.getTeams();
                    mList.setAdapter(new SelectTeamAdapter(aty, datas
                            .getTeams()));
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                    Throwable arg3) {
                AppContext.showToast("网络不好，请稍后重试");
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

}
