package net.oschina.app.improve.main;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 异常信息界面
 * Created by haibin on 2017/5/8.
 */

public class ErrorActivity extends BaseBackActivity implements View.OnClickListener {
    @Bind(R.id.tv_crash_info)
    TextView mTextCrashInfo;

    public static void show(Context context, String message) {
        if (message == null)
            return;
        Intent intent = new Intent(context, ErrorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("message", message);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_error;
    }

    @Override
    protected void initData() {
        super.initData();
        mTextCrashInfo.setText(getIntent().getStringExtra("message"));
    }

    @OnClick({R.id.btn_restart})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_restart:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
