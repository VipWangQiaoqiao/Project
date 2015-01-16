package net.oschina.app.team.fragment;

import net.oschina.app.R;
import net.oschina.app.base.BaseFragment;

import org.kymjs.kjframe.utils.SystemTool;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Team任务界面
 * 
 * @author kymjs (kymjs123@gmail.com)
 * 
 */
public class MyIssueFragment extends BaseFragment {

    @InjectView(R.id.team_myissue_ing)
    RelativeLayout mRlIng;
    @InjectView(R.id.team_myissue_will)
    RelativeLayout mRlWill;
    @InjectView(R.id.team_myissue_ed)
    RelativeLayout mRlEd;
    @InjectView(R.id.team_myissue_all)
    RelativeLayout mRlAll;
    @InjectView(R.id.myissue_title)
    LinearLayout mLlTitle;

    @InjectView(R.id.team_myissue_ing_num)
    TextView mTvIng;
    @InjectView(R.id.team_myissue_will_num)
    TextView mTvWill;
    @InjectView(R.id.team_myissue_ed_num)
    TextView mTvEd;
    @InjectView(R.id.team_myissue_all_num)
    TextView mTvAll;

    @InjectView(R.id.team_myissue_name)
    TextView mTvName;
    @InjectView(R.id.team_myissue_date)
    TextView mTvDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_team_issue,
                container, false);
        ButterKnife.inject(this, rootView);
        initData();
        initView(rootView);
        return rootView;
    }

    @Override
    public void initView(View view) {
        mRlIng.setOnClickListener(this);
        mRlWill.setOnClickListener(this);
        mRlEd.setOnClickListener(this);
        mRlAll.setOnClickListener(this);

        mTvIng.setText("1");
        mTvWill.setText("2");
        mTvEd.setText("3");
        mTvAll.setText("4");
        mTvName.setText("你是谁，上午好！");
        mTvDate.setText(SystemTool.getDataTime("yyyy年MM月dd日"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.team_myissue_ing:
            break;
        case R.id.team_myissue_will:
            break;
        case R.id.team_myissue_ed:
            break;
        case R.id.team_myissue_all:
            break;
        }
    }

    @Override
    public void initData() {}

}
