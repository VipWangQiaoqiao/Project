package net.oschina.app.improve.notice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.oschina.app.service.NoticeUtils;

/**
 * Created by JuQiu
 * on 16/8/19.
 */
public class NoticeAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("NoticeAlarmReceiver", "onReceived");
        NoticeUtils.requestNotice(context);
    }
}
