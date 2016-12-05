package net.oschina.app.improve.detail.sign;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;

/**
 * 新版活动报名页面,动态请求活动报名参数，按服务器返回的顺序inflate各自对应的View
 * Created by haibin
 * on 2016/12/5.
 */

public class SignUpActivity extends BaseBackActivity {
    @Override
    protected int getContentView() {
        return R.layout.activity_sign_up;
    }
}
