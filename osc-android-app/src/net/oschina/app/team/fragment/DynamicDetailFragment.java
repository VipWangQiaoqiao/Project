package net.oschina.app.team.fragment;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.team.bean.TeamActive;
import net.oschina.app.team.bean.TeamActiveDetail;
import net.oschina.app.ui.SimpleBackActivity;
import net.oschina.app.util.XmlUtils;
import net.oschina.app.widget.AvatarView;

import org.apache.http.Header;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

public class DynamicDetailFragment extends BaseFragment {

    private AvatarView img_head;
    private TextView tv_name;
    private TextView tv_active;
    private TextView tv_content;
    private LinearLayout ll_event_list;
    private TextView tv_client;
    private TextView tv_date;

    private TeamActiveDetail data;
    private TeamActive active;
    private int teamId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.frag_dynamic_detail, null);
        initData();
        initView(root);
        return root;
    }

    @Override
    public void initData() {
        super.initData();
        Bundle bundle = getActivity().getIntent().getBundleExtra(
                SimpleBackActivity.BUNDLE_KEY_ARGS);
        active = (TeamActive) bundle
                .getSerializable(DynamicFragment.DYNAMIC_FRAGMENT_KEY);
        teamId = bundle.getInt(DynamicFragment.DYNAMIC_FRAGMENT_TEAM_KEY, 0);
    }

    @Override
    public void initView(View v) {
        super.initView(v);
        img_head = (AvatarView) v.findViewById(R.id.event_listitem_userface);
        tv_name = (TextView) v.findViewById(R.id.event_listitem_username);
        tv_active = (TextView) v.findViewById(R.id.event_listitem_active);
        tv_content = (TextView) v.findViewById(R.id.event_listitem_content);
        ll_event_list = (LinearLayout) v
                .findViewById(R.id.event_listitem_commits_list);
        tv_client = (TextView) v.findViewById(R.id.event_listitem_client);
        tv_date = (TextView) v.findViewById(R.id.event_listitem_date);

        img_head.setAvatarUrl(active.getAuthor().getPortrait());
        tv_name.setText(active.getAuthor().getName());
        ll_event_list.setVisibility(View.GONE);
        // tv_active.setText(data.getBody().getDetail());
        tv_content.setText(Html.fromHtml(active.getBody().getDetail()));
        tv_date.setText(active.getCreateTime());
        // tv_client.setText("");

        initNetContent();
    }

    private void initNetContent() {
        OSChinaApi.getDynamicDetail(active.getId(), teamId, AppContext
                .getInstance().getLoginUid(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                data = XmlUtils.toBean(TeamActiveDetail.class, arg2);

                tv_content.setText(Html.fromHtml(data.getTeamActive().getBody()
                        .getTitle()));
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                    Throwable arg3) {

            }
        });
    }
}
