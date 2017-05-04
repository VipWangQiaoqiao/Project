package net.oschina.app.improve.main.update;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.bean.Version;

import butterknife.Bind;

/**
 * 在线更新对话框
 * Created by haibin on 2017/5/4.
 */

public class UpdateActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.tv_update_info)
    TextView mTextUpdateInfo;

    public static void show(Fragment fragment, Version version) {
        Intent intent = new Intent(fragment.getActivity(), UpdateActivity.class);
        intent.putExtra("version", version);
        fragment.startActivityForResult(intent, 0x01);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_update;
    }

    @Override
    public void onClick(View v) {

    }
}
