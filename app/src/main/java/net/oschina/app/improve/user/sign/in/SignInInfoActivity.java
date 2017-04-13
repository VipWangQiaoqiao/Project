package net.oschina.app.improve.user.sign.in;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BackActivity;

import butterknife.Bind;

/**
 * 签到信息界面
 * Created by haibin on 2017/4/12.
 */

public class SignInInfoActivity extends BackActivity {
    @Bind(R.id.tv_event_name)
    TextView mTextEventName;
    @Bind(R.id.tv_cost)
    TextView mTextCost;
    @Bind(R.id.tv_cost_msg)
    TextView mTextCostMsg;

    public static void show(Context context) {
        Intent intent = new Intent(context, SignInInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_sign_in_info;
    }
}
