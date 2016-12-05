package net.oschina.app;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.bean.SubTab;
import net.oschina.app.improve.main.MainActivity;
import net.oschina.common.utils.StreamUtil;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 应用启动界面
 */
public class LaunchActivity extends BaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.app_start;
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        /*
        // 防止第三方跳转时出现双实例
        Activity aty = get ;
        if (aty != null && !aty.isFinishing()) {
            finish();
        }
        */
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        //TODO Check use new version
        //int currentVersion = TDevice.getVersionCode();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                redirectTo();
            }
        }, 800);

    }

    private void redirectTo() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void todo() {
        List<SubTab> original = setupOriginalDataSet();
        if (original == null)
            return;
        List<SubTab> active = setupActiveDataSet();
        if (active == null)
            return;
        List<SubTab> newActive = new ArrayList<>();
        for (SubTab tab : active) {
            SubTab newTab = searchSubTab(original, tab);
            if (newTab != null) {
                newActive.add(newTab);
            }
        }

        if (newActive.size() == 0) {
            newActive = setupActiveAssetsDataSet();
            if (newActive == null || newActive.size() == 0)
                return;
        } else {
            List<SubTab> activeAssetsDataSet = setupActiveAssetsDataSet();
            if (activeAssetsDataSet == null)
                return;
            for (SubTab tab : activeAssetsDataSet) {
                if (searchSubTab(newActive, tab) == null)
                    newActive.add(tab);
            }
        }


        Collections.sort(newActive, new Comparator<SubTab>() {
            @Override
            public int compare(SubTab o1, SubTab o2) {
                if (o1.isFixed()) {
                    if (o2.isFixed()) {
                        return o1.getOrder() - o2.getOrder();
                    } else {
                        return -1;
                    }
                } else {
                    if (o2.isFixed()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        });

        String json = AppOperator.getGson().toJson(newActive);
        FileOutputStream fos = null;
        try {
            fos = openFileOutput("sub_tab_active.json",
                    Context.MODE_PRIVATE);
            fos.write(json.getBytes("UTF-8"));
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(fos);
        }
    }

    private SubTab searchSubTab(List<SubTab> original, SubTab oldTab) {
        for (SubTab tab : original) {
            if (tab.getToken().equals(oldTab.getToken()))
                return tab;
        }
        return null;
    }

    public List<SubTab> setupActiveAssetsDataSet() {
        InputStream is = null;
        try {
            byte[] bytes = new byte[1024];
            int length;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            is = getResources().getAssets().open("sub_tab_active.json");
            while ((length = is.read(bytes)) != -1) {
                baos.write(bytes, 0, length);
            }
            return AppOperator.getGson().<ArrayList<SubTab>>fromJson(new String(baos.toByteArray(), "UTF-8"),
                    new TypeToken<ArrayList<SubTab>>() {
                    }.getType());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(is);
        }
        return null;
    }

    public List<SubTab> setupActiveDataSet() {
        InputStream is = null;
        try {
            byte[] bytes = new byte[1024];
            int length;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            File file = getFileStreamPath("sub_tab_active.json");
            if (file.exists()) {
                is = openFileInput("sub_tab_active.json");
            } else {
                return null;
            }
            while ((length = is.read(bytes)) != -1) {
                baos.write(bytes, 0, length);
            }
            return AppOperator.getGson().<ArrayList<SubTab>>fromJson(new String(baos.toByteArray(), "UTF-8"),
                    new TypeToken<ArrayList<SubTab>>() {
                    }.getType());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(is);
        }
        return null;
    }

    public List<SubTab> setupOriginalDataSet() {
        InputStream is = null;
        try {
            is = getResources().getAssets().open("sub_tab_original.json");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int length;
            while ((length = is.read(bytes)) != -1) {
                baos.write(bytes, 0, length);
            }
            return AppOperator.getGson().<ArrayList<SubTab>>fromJson(new String(baos.toByteArray(), "UTF-8"),
                    new TypeToken<ArrayList<SubTab>>() {
                    }.getType());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(is);
        }
        return null;
    }
}
