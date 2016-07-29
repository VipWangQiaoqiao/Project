package net.oschina.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import net.oschina.app.ui.MainActivity;
import net.oschina.app.util.TDevice;

import org.kymjs.kjframe.http.KJAsyncTask;
import org.kymjs.kjframe.utils.FileUtils;
import org.kymjs.kjframe.utils.PreferenceHelper;

import java.io.File;

/**
 * 应用启动界面
 */
public class AppStart extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 防止第三方跳转时出现双实例
        Activity aty = AppManager.getActivity(MainActivity.class);
        if (aty != null && !aty.isFinishing()) {
            finish();
        }

        setContentView(R.layout.app_start);
        findViewById(R.id.app_start_view).postDelayed(new Runnable() {
            @Override
            public void run() {
                redirectTo();
            }
        }, 800);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int cacheVersion = PreferenceHelper.readInt(this, "first_install",
                "first_install", -1);
        int currentVersion = TDevice.getVersionCode();
        if (cacheVersion < currentVersion) {
            PreferenceHelper.write(this, "first_install", "first_install",
                    currentVersion);
            cleanKJImageCache();
        }
    }

    private void cleanKJImageCache() {
        final File folder = FileUtils.getSaveFolder("OSChina/imagecache");
        File[] files = folder.listFiles();
        if (files != null && files.length > 0) {
            KJAsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    if (folder.isDirectory()) {

                        for (File file : folder.listFiles()) {
                            file.delete();
                        }
                    }
                }

            });
        }
    }

    /**
     * 跳转到...
     */
    private void redirectTo() {
        Intent uploadLog = new Intent(this, LogUploadService.class);
        startService(uploadLog);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
