package net.oschina.app.improve.main.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import net.oschina.app.AppConfig;
import net.oschina.app.R;
import net.oschina.app.improve.main.MainActivity;
import net.oschina.app.improve.utils.StreamUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 下载服务
 * Created by haibin
 * on 2016/10/19.
 */
@SuppressWarnings("unused")
public class DownloadService extends Service {
    public static boolean isDownload = false;
    private Handler mHandler;
    private String mUrl;
    private String mTitle = "正在下载%s";
    private String saveFileName = AppConfig.DEFAULT_SAVE_FILE_PATH;
    private NotificationManager mNotificationManager;
    private Notification mNotification;

    public static void startService(Context context, String downloadUrl) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra("url", downloadUrl);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isDownload = true;
        mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mUrl = intent.getStringExtra("url");
        File file = new File(AppConfig.DEFAULT_SAVE_FILE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        String apkFile = saveFileName;
        final File saveFile = new File(apkFile);
        new Thread() {
            @Override
            public void run() {
                try {
                    downloadUpdateFile(mUrl, saveFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    public long downloadUpdateFile(String downloadUrl, File saveFile) throws Exception {
        int downloadCount = 0;
        int currentSize = 0;
        long totalSize = 0;
        int updateTotalSize = 0;

        HttpURLConnection httpConnection = null;
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            URL url = new URL(downloadUrl);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection
                    .setRequestProperty("User-Agent", "PacificHttpClient");
            if (currentSize > 0) {
                httpConnection.setRequestProperty("RANGE", "bytes="
                        + currentSize + "-");
            }
            httpConnection.setConnectTimeout(10000);
            httpConnection.setReadTimeout(20000);
            updateTotalSize = httpConnection.getContentLength();
            if (httpConnection.getResponseCode() == 404) {
                throw new Exception("fail!");
            }
            is = httpConnection.getInputStream();
            fos = new FileOutputStream(saveFile, false);
            byte buffer[] = new byte[1024];
            int readSize = 0;
            while ((readSize = is.read(buffer)) > 0) {

                fos.write(buffer, 0, readSize);
                totalSize += readSize;
                if ((downloadCount == 0)
                        || (int) (totalSize * 100 / updateTotalSize) - 4 > downloadCount) {
                    downloadCount += 4;
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    msg.arg1 = downloadCount;
                    mHandler.sendMessage(msg);
                }
            }

            mHandler.sendEmptyMessage(0);
            isDownload = false;

        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
            StreamUtils.close(is, fos);
            stopService();
        }
        return totalSize;
    }

    private void setUpNotification() {
        int icon = R.mipmap.ic_notification;
        CharSequence tickerText = "准备下载";
        long when = System.currentTimeMillis();
        mNotification = new Notification(icon, tickerText, when);

        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        RemoteViews contentView = new RemoteViews(getPackageName(),
                R.layout.download_notification_show);
        contentView.setTextViewText(R.id.tv_download_state, mTitle);
        mNotification.contentView = contentView;

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotification.contentIntent = contentIntent;
        mNotificationManager.notify(0, mNotification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void stopService() {
        Intent intent = new Intent(getApplicationContext(), DownloadService.class);
        stopService(intent);
    }

    @Override
    public void onDestroy() {
        isDownload = false;
        super.onDestroy();
    }
}
