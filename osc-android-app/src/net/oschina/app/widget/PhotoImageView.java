package net.oschina.app.widget;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnFinishListener;
import android.content.Context;
import android.util.AttributeSet;

/**
 * 这里的代码全是白费，fuck，通过改动PhotoView源码才能成功。见PhotoViewAttacher类onTouch方法
 * 
 * @author kymjs (https://github.com/kymjs)
 * 
 */
public class PhotoImageView extends PhotoView {

    // private static OnFinishListener listener;
    // long previousTouch = 0;
    // static boolean effect; // 有效
    // static boolean isDoubleClick = false; // 双击

    public PhotoImageView(Context context) {
        super(context);
    }

    @Override
    public void setOnFinishListener(OnFinishListener l) {
        super.setOnFinishListener(l);
    }

    public PhotoImageView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public PhotoImageView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
    }
    //
    // public void setOnFinishListener(OnFinishListener l) {
    // this.listener = l;
    // }
    //
    // /**
    // * 首次按下开始计时<br>
    // * 200毫秒以内触发up表示计时有效，开始真正计时<br>
    // * 500毫秒以内再次触发down取消计时<br>
    // * 取消计时以后up事件标识计时失效<br>
    // */
    // @Override
    // public boolean onTouchEvent(MotionEvent event) {
    // long downTime = 0;
    // long upTime = 0;
    // switch (event.getAction()) {
    // case MotionEvent.ACTION_DOWN:
    // downTime = System.currentTimeMillis();
    // effect = false;
    // if (downTime - previousTouch < 500) {
    // cancleTime();
    // isDoubleClick = true;
    // }
    // break;
    // case MotionEvent.ACTION_UP:
    // upTime = System.currentTimeMillis();
    // if (upTime - downTime < 200) {
    // effect = true;
    // }
    // if (!isDoubleClick) {
    // doTime();
    // } else {
    // isDoubleClick = false;
    // }
    // previousTouch = upTime;
    // break;
    // default:
    // break;
    // }
    // return super.onTouchEvent(event);
    // }
    //
    // /**
    // * 开始计时
    // */
    // private void doTime() {
    // if (effect) {
    // handle.postDelayed(send, 500);
    // }
    // }
    //
    // /**
    // * 取消计时
    // */
    // private void cancleTime() {
    // handle.removeCallbacks(send);
    // }
    //
    // public interface OnFinishListener {
    // void onClick();
    // }
    //
    // static Handler handle = new Handler() {
    // @Override
    // public void handleMessage(android.os.Message msg) {
    // if (listener != null) {
    // listener.onClick();
    // }
    // KJLoger.debug("关闭了");
    // };
    // };
    //
    // Runnable send = new Runnable() {
    // @Override
    // public void run() {
    // handle.sendEmptyMessage(0x00000078);
    // }
    // };
}
