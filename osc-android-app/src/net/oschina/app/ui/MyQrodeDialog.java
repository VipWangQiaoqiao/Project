package net.oschina.app.ui;

import com.google.zxing.WriterException;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.util.QrCodeUtils;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class MyQrodeDialog extends Dialog {

	private ImageView mIvCode;
	
    private MyQrodeDialog(Context context, boolean flag,
            OnCancelListener listener) {
        super(context, flag, listener);
    }

	@SuppressLint("InflateParams")
	private MyQrodeDialog(Context context, int defStyle) {
		super(context, defStyle);
		View contentView = getLayoutInflater().inflate(
				R.layout.dialog_my_qr_code, null);
		mIvCode = (ImageView) contentView.findViewById(R.id.iv_qr_code);
		try {
			mIvCode.setImageBitmap(QrCodeUtils.Create2DCode(String.format("http://my.oschina.net/u/%s", AppContext.getInstance().getLoginUid())));
		} catch (WriterException e) {
			e.printStackTrace();
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MyQrodeDialog.this.dismiss();
                return true;
            }
        });
        super.setContentView(contentView);
    }


	public MyQrodeDialog(Context context) {
		this(context, R.style.quick_option_dialog);
	}
	
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setGravity(Gravity.CENTER);
    }
}
