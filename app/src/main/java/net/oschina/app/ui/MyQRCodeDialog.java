package net.oschina.app.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.zxing.WriterException;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.util.ImageUtils;
import net.oschina.app.util.QrCodeUtils;

import java.io.File;

public class MyQRCodeDialog extends Dialog {

    private ImageView mIvCode;
    private Bitmap bitmap;

    private MyQRCodeDialog(Context context, boolean flag,
                           OnCancelListener listener) {
        super(context, flag, listener);
    }

    @SuppressLint("InflateParams")
    private MyQRCodeDialog(final Context context, int defStyle) {
        super(context, defStyle);
        View contentView = getLayoutInflater().inflate(
                R.layout.dialog_my_qr_code, null);
        mIvCode = (ImageView) contentView.findViewById(R.id.iv_qr_code);
        try {
            bitmap = QrCodeUtils.Create2DCode(String.format(
                    "http://my.oschina.net/u/%s", AccountHelper.getUserId()));
            mIvCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mIvCode.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dismiss();
                try {
                    String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    File file = new File(sdPath + File.separator + "OSChina"
                            + File.separator);
                    if (!file.exists()) file.mkdirs();
                    file = new File(file.getAbsoluteFile(), "qrcode.png");
                    if (file.exists()) file.delete();
                    ImageUtils.saveImageToSD(context, file.getAbsolutePath(), bitmap, 100);
                    AppContext.showToast("二维码已保存到oschina文件夹下");
                } catch (Exception e) {
                    e.printStackTrace();
                    AppContext.showToast("SD卡不可写，二维码保存失败");
                }

                return false;
            }
        });

        contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MyQRCodeDialog.this.dismiss();
                return false;
            }
        });
        super.setContentView(contentView);
    }

    public MyQRCodeDialog(Context context) {
        this(context, R.style.App_Theme_Tweet_Main);
    }


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setGravity(Gravity.CENTER);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        p.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(p);
    }
}
