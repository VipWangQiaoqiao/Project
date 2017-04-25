package net.oschina.app.improve.user.sign.in;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.bean.EventSignIn;
import net.oschina.app.improve.bean.SubBean;

import butterknife.Bind;

/**
 * 签到状态信息界面
 * Created by haibin on 2017/4/12.
 */

public class SignInInfoActivity extends BaseBackActivity {
    @Bind(R.id.tv_event_name)
    TextView mTextEventName;
    @Bind(R.id.tv_cost)
    TextView mTextCost;
    @Bind(R.id.tv_cost_msg)
    TextView mTextCostMsg;
    @Bind(R.id.tv_msg)
    TextView mTextMsg;

    public static void show(Context context, SubBean detail, EventSignIn info) {
        Intent intent = new Intent(context, SignInInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("detail", detail);
        bundle.putSerializable("sign_in", info);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_sign_in_info;
    }

    @Override
    protected void initData() {
        super.initData();
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }
        SubBean detail = (SubBean) bundle.getSerializable("detail");
        EventSignIn info = (EventSignIn) bundle.getSerializable("sign_in");
        assert detail != null;
        mTextEventName.setText(detail.getTitle());
        assert info != null;
        mTextCost.setText("￥" + info.getCost() / 100);
        mTextCostMsg.setText(info.getCostMessage());
        mTextMsg.setText(info.getMessage());
    }
}
