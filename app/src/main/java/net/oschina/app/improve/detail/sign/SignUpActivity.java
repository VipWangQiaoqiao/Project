package net.oschina.app.improve.detail.sign;

import android.content.Intent;
import android.support.v4.app.Fragment;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;

/**
 * 新版活动报名页面,动态请求活动报名参数，按服务器返回的顺序inflate各自对应的View
 * Created by haibin
 * on 2016/12/5.
 */

public class SignUpActivity extends BaseBackActivity implements SignUpContract.EmptyView {

    private SignUpFragment mFragment;

    public static void show(Fragment fragment, long sourceId) {
        Intent intent = new Intent(fragment.getActivity(), SignUpActivity.class);
        intent.putExtra("sourceId", sourceId);
        fragment.startActivityForResult(intent, 0x01);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_sign_up;
    }

    @Override
    public void hideEmptyLayout() {

    }

    @Override
    public void showErrorLayout(int errorType) {

    }
}
