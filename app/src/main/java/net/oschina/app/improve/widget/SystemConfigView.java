package net.oschina.app.improve.widget;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import net.oschina.app.BuildConfig;
import net.oschina.app.R;
import net.oschina.app.Setting;
import net.oschina.app.api.ApiHttpClient;
import net.oschina.app.util.TDevice;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */

public class SystemConfigView extends LinearLayout {

    public static void show(ViewGroup root) {
        if ((System.currentTimeMillis() - Setting.getSystemConfigTimeStamp(root.getContext())) < 4000) {
            SystemConfigView v = new SystemConfigView(root.getContext());
            root.addView(v);
        }
    }

    public SystemConfigView(Context context) {
        super(context);
        inflate(context, R.layout.lay_system_config, this);
        initUrlConfig((RadioGroup) findViewById(R.id.radio_group));
    }


    private void initUrlConfig(RadioGroup group) {
        String serverUrls = BuildConfig.API_SERVER_URL;
        String curServerUrl = Setting.getServerUrl(getContext());
        serverUrls = TextUtils.isEmpty(serverUrls) ? curServerUrl : serverUrls;
        final String[] urls = serverUrls.split(";");

        for (int i = 0; i < urls.length; i++) {
            String url = urls[i];
            if (TextUtils.isEmpty(url))
                continue;
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, (int) TDevice.dipToPx(getResources(), 4),
                    0, (int) TDevice.dipToPx(getResources(), 4));


            RadioButton button = new RadioButton(getContext());
            button.setLayoutParams(params);
            button.setText(url);
            button.setChecked(url.equals(curServerUrl));
            button.setId(i);
            button.setTag(url);
            button.setButtonDrawable(R.drawable.ic_selector_checkbox);
            button.setPadding(0, 0, 0, 0);
            group.addView(button);
        }

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Object obj = group.findViewById(checkedId).getTag();
                if (obj != null && obj instanceof String) {
                    Setting.updateServerUrl(getContext(), (String) obj);
                    ApiHttpClient.init((Application) getContext().getApplicationContext());
                }
            }
        });
    }


}
