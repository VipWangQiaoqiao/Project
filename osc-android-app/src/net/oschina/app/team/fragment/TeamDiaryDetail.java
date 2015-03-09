package net.oschina.app.team.fragment;

import net.oschina.app.R;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.team.bean.TeamDiary;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.InjectView;

/**
 * 周报详情<br>
 * 逻辑介绍：用Listview来显示评论内容，在ListView的HeadView中添加本周报的详细内容与周报列表的item。
 * 周报的详细内容通过动态添加addView的方式
 * 
 * @author kymjs (https://github.com/kymjs)
 */
public class TeamDiaryDetail extends BaseFragment {

    private static final String CACHE_KEY_PREFIX = "team_diary_detail_";

    @InjectView(R.id.listview)
    ListView mList;

    private TeamDiary diaryData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = View.inflate(getActivity(),
                R.layout.fragment_pull_refresh_listview, null);
        initData();
        initView(rootView);
        return rootView;
    }

    @Override
    public void initData() {
        super.initData();
        Bundle bundle = getArguments();
        if (bundle != null) {
            diaryData = (TeamDiary) bundle
                    .getSerializable(TeamDiaryPagerFragment.DIARYDETAIL_KEY);
        } else {
            Log.e("debug", getClass().getSimpleName() + "diaryData初始化异常");
        }
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        initHeaderView();
    }

    private View initHeaderView() {
        View headerView = inflateView(R.layout.frag_dynamic_detail);
        return headerView;
    }
}
