package net.oschina.app.team.fragment;

import net.oschina.app.R;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.team.adapter.TeamMemberAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 团队成员界面
 * 
 * @author kymjs (kymjs123@gmail.com)
 * 
 */
public class TeamMemberFragment extends BaseFragment {

    @InjectView(R.id.fragment_team_grid)
    GridView mGrid;

    private Activity aty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_team_member,
                container, false);
        aty = getActivity();
        ButterKnife.inject(this, rootView);
        initData();
        initView(rootView);
        return rootView;
    }

    @Override
    public void onClick(View v) {}

    @Override
    public void initView(View view) {
        mGrid.setAdapter(new TeamMemberAdapter(aty));
        mGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

            }
        });
    }

    @Override
    public void initData() {}
}
