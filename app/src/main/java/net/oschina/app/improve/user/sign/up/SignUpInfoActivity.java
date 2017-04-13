package net.oschina.app.improve.user.sign.up;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.bean.EventDetail;

import butterknife.Bind;

/**
 * 活动报名签到，包括报名信息
 * Created by haibin on 2017/4/11.
 */

public class SignUpInfoActivity extends BackActivity {
    @Bind(R.id.tv_name)
    TextView mTextName;
    @Bind(R.id.tv_work)
    TextView mTextWork;
    @Bind(R.id.tv_gender)
    TextView mTextGender;
    @Bind(R.id.tv_date)
    TextView mTextDate;
    @Bind(R.id.tv_email)
    TextView mTextEmail;
    @Bind(R.id.tv_phone)
    TextView mTextPhone;
    @Bind(R.id.tv_company)
    TextView mTextCompany;
    @Bind(R.id.tv_status)
    TextView mTextStatus;
    @Bind(R.id.tv_remark)
    TextView mTextRemark;

    public static void show(Context context, EventDetail detail) {
        Intent intent = new Intent(context, SignUpInfoActivity.class);
        intent.putExtra("detail", detail);
        context.startActivity(intent);
    }

    public static void show(Context context, long id) {
        Intent intent = new Intent(context, SignUpInfoActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_sign_info;
    }

    @Override
    protected void initData() {
        super.initData();
        EventDetail detail = (EventDetail) getIntent().getSerializableExtra("detail");
        if (detail == null) {
            return;
        }
        long id = getIntent().getLongExtra("id", 0);
    }
}
