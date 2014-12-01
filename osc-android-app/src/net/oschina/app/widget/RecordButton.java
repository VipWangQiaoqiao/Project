package net.oschina.app.widget;

import java.io.File;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.widget.RecordButtonUtil.OnPlayListener;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * 录音专用Button，可弹出自定义的录音dialog。需要配合{@link #RecordButtonUtil}使用
 * 
 * @author kymjs(kymjs123@gmail.com)
 * 
 */
public class RecordButton extends Button {
    private static final int MIN_INTERVAL_TIME = 1000; // 录音最短时间
    private static final int MAX_INTERVAL_TIME = 60000; // 录音最长时间

    private boolean mIsCancel = false; // 手指抬起或划出时判断是否主动取消录音

    private int mYpositon = 0;// 录音按钮top值
    private long mStartTime;// 录音起始时间

    private static Dialog mRecordDialog;

    private String mAudioFile = null;
    private OnFinishedRecordListener mFinishedListerer;
    private static OnVolumeChangeListener mVolumeListener;

    private RecordButtonUtil mAudioUtil;
    private ObtainDecibelThread mThread;
    private Handler mVolumeHandler; // 用于更新录音音量大小的图片

    public RecordButton(Context context) {
        super(context);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mVolumeHandler = new ShowVolumeHandler();
        mAudioUtil = new RecordButtonUtil();
        int[] location = new int[2];
        getLocationOnScreen(location);
        mYpositon = location[1];
        initSavePath();
    }

    // 调用该方法设置录音文件存储点
    private void initSavePath() {
        mAudioFile = RecordButtonUtil.AUDOI_DIR;
        File file = new File(mAudioFile);
        if (!file.exists()) {
            file.mkdirs();
        }
        mAudioFile += File.separator + System.currentTimeMillis() + ".amr";
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mAudioFile == null) {
            return false;
        }
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            initlization();
            break;
        case MotionEvent.ACTION_UP:
            if (!mIsCancel) {
                finishRecord();
            } else {
                cancelRecord();
            }
            mIsCancel = false;
            break;
        case MotionEvent.ACTION_MOVE:// 当手指移动到view外面，会cancel
            if (event.getY() < mYpositon) {
                mIsCancel = false;
            }
            break;
        }
        return true;
    }

    /**
     * 初始化 dialog和录音器
     */
    private void initlization() {
        mStartTime = System.currentTimeMillis();
        if (mRecordDialog == null) {
            mRecordDialog = new Dialog(getContext());
            // WindowManager.LayoutParams params = mRecordDialog.getWindow()
            // .getAttributes();
            // int px = 200;
            // params.width = px;
            // params.height = px;
            // mRecordDialog.getWindow().setAttributes(params);
            mRecordDialog.setOnDismissListener(onDismiss);
        }
        mRecordDialog.show();
        startRecording();
    }

    /**
     * 录音完成（达到最长时间或用户决定录音完成）
     */
    private void finishRecord() {
        stopRecording();
        mRecordDialog.dismiss();
        long intervalTime = System.currentTimeMillis() - mStartTime;
        if (intervalTime < MIN_INTERVAL_TIME) {
            AppContext.showToastShort(R.string.record_sound_short);
            File file = new File(mAudioFile);
            file.delete();
            return;
        }
        if (mFinishedListerer != null) {
            mFinishedListerer.onFinishedRecord(mAudioFile,
                    (int) ((System.currentTimeMillis() - mStartTime) / 1000));
        }
    }

    // 用户手动取消录音
    private void cancelRecord() {
        stopRecording();
        mRecordDialog.dismiss();
        File file = new File(mAudioFile);
        file.delete();
        if (mFinishedListerer != null) {
            mFinishedListerer.onCancleRecord();
        }
    }

    // 开始录音
    private void startRecording() {
        mAudioUtil.setAudioPath(mAudioFile);
        mAudioUtil.recordAudio();
        mThread = new ObtainDecibelThread();
        mThread.start();

    }

    // 停止录音
    private void stopRecording() {
        if (mThread != null) {
            mThread.exit();
            mThread = null;
        }
        if (mAudioUtil != null) {
            mAudioUtil.stopRecord();
        }
    }

    /******************************* public method ****************************************/

    /**
     * 获取最近一次录音的文件路径
     * 
     * @return
     */
    public String getCurrentAudioPath() {
        return mAudioFile;
    }

    /**
     * 设置录音时显示的dialog
     * 
     * @param dialog
     */
    public void setRecordDialog(Dialog dialog) {
        mRecordDialog = dialog;
    }

    /**
     * 设置要播放的声音的路径
     * 
     * @param path
     */
    public void setAudioPath(String path) {
        mAudioUtil.setAudioPath(path);
    }

    /**
     * 开始播放
     */
    public void startPlay() {
        mAudioUtil.startPlay();
    }

    /**
     * 删除当前文件
     */
    public void delete() {
        File file = new File(mAudioFile);
        file.delete();
    }

    /**
     * 结束录音的监听器
     * 
     * @param listener
     */
    public void setOnFinishedRecordListener(OnFinishedRecordListener listener) {
        mFinishedListerer = listener;
    }

    /**
     * dialog中音量改变的回调方法
     * 
     * @param l
     */
    public void setOnVolumeChangeListener(OnVolumeChangeListener l) {
        mVolumeListener = l;
    }

    /**
     * 播放结束监听器
     * 
     * @param l
     */
    public void setOnPlayListener(OnPlayListener l) {
        mAudioUtil.setOnPlayListener(l);
    }

    /******************************* inline class ****************************************/

    private class ObtainDecibelThread extends Thread {
        private volatile boolean running = true;

        public void exit() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (System.currentTimeMillis() - mStartTime >= MAX_INTERVAL_TIME) {
                    finishRecord();
                }
                if (mAudioUtil == null || !running) {
                    break;
                }
                int volumn = mAudioUtil.getVolumn();
                if (volumn != 0) {
                    mVolumeHandler.sendEmptyMessage(volumn);
                }
            }
        }
    }

    private final OnDismissListener onDismiss = new OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            stopRecording();
        }
    };

    static class ShowVolumeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            /* 当前音量 */
            if (mVolumeListener != null) {
                mVolumeListener.onVolumeChange(mRecordDialog, msg.what);
            }
        }
    }

    /**
     * 音量改变的监听器
     */
    public interface OnVolumeChangeListener {
        void onVolumeChange(Dialog dialog, int volume);
    }

    public interface OnFinishedRecordListener {

        /**
         * 用户手动取消
         */
        public void onCancleRecord();

        /**
         * 录音完成
         * 
         * @param audioPath
         * @param recordTime
         */
        public void onFinishedRecord(String audioPath, int recordTime);
    }
}
