package net.oschina.app.improve.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.main.MainActivity;
import net.oschina.app.improve.tweet.activities.TweetDetailActivity;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.util.TLog;
import net.oschina.app.util.UIHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * web url 计划 activity
 * Created by huanghaibin_dev
 * on 2016/7/11.
 */

public class SchemeUrlActivity extends BaseBackActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Intent intent = getIntent();
            if (intent != null) {
                //  oscapp://www.oschina.net/launch/app?type=-1(&id=112213)
                String metaData = intent.getDataString();
                TLog.e("meta", metaData);
                long id = 0;
                int type = 0;
                if (metaData != null) {
                    Pattern pattern = Pattern.compile("([^&?]*)");
                    Matcher matcher = pattern.matcher(metaData);
                    while (matcher.find()) {
                        String group = matcher.group();
                        if (!group.contains("www.oschina.net")) {
                            String[] param = group.split("=");
                            if (group.contains("type")) {
                                type = Integer.parseInt(param[1]);
                            } else if (group.contains("id")) {
                                id = Long.parseLong(param[1]);
                            }
                        }
                    }

                    switch (type) {
                        case 20://打开个人中心
                            OtherUserHomeActivity.show(this, id);
                            break;
                        case 100://打开动弹详情
                            TweetDetailActivity.show(this, id);
                            break;
                        default:
                            if (id == 0)//默认启动首页
                                startActivity(new Intent(this, MainActivity.class));
                            else//否则启动各个类型详情
                                UIHelper.showDetail(this, type, id, "");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_scheme_url;
    }
}
