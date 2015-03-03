package net.oschina.app.service;

import java.util.ArrayList;

import net.oschina.app.bean.NotebookData;
import net.oschina.app.db.NoteDatabase;

import org.kymjs.kjframe.utils.SystemTool;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 一个文件云同步解决方案（便签同步）
 * 
 * @author kymjs (https://github.com/kymjs)
 * 
 */
public class CloudSynchronizeService extends Service {

    private NoteDatabase noteDb;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void stop() {
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        noteDb = new NoteDatabase(this);
        ArrayList<NotebookData> localDatas = noteDb.query();
        @SuppressWarnings("unchecked")
        ArrayList<NotebookData> cloudDatas = (ArrayList<NotebookData>) intent
                .getSerializableExtra("cloudDatas");

        // 同步逻辑太麻烦，要么用二重循环由客户端判断差异文件，要么浪费流量把本地文件全部上传交由服务器判断。
        // 这里我采用两种逻辑：WiFi环境下客户端直接提交全部文件，交由服务器判断。
        // GPRS环境下客户端判断差异文件，并编辑服务器端文件达到同步
        if (SystemTool.isWiFi(this)) {
            // doSynchronizeWithWIFI(localDatas, cloudDatas);//服务器判断暂未实现
            doSynchronizeWithGPRS(localDatas, cloudDatas);
        } else {
            doSynchronizeWithGPRS(localDatas, cloudDatas);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * GPRS环境下：使用二重循环遍历差异文件并更新云端文件达到同步
     * 
     * @param localDatas
     *            本地数据
     * @param cloudDatas
     *            云端数据
     */
    private void doSynchronizeWithGPRS(ArrayList<NotebookData> localDatas,
            ArrayList<NotebookData> cloudDatas) {

        // 假设5个相同数据
        ArrayList<NotebookData> waitSync = new ArrayList<NotebookData>(
                localDatas.size() - cloudDatas.size() + 5);

        for (NotebookData localData : localDatas) {
            for (NotebookData cloudData : cloudDatas) {
                if (!localData.equals(cloudData)) {
                    waitSync.add(localData);
                }
            }
        }

        if (localDatas.size() > 0) {

        }
    }

    /**
     * WIFI环境下：客户端直接提交全部文件，交由服务器做同步判断
     * 
     * @param localDatas
     *            本地数据
     * @param cloudDatas
     *            云端数据
     */
    private void doSynchronizeWithWIFI(ArrayList<NotebookData> localDatas,
            ArrayList<NotebookData> cloudDatas) {}
}
