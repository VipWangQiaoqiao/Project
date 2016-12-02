package net.oschina.app.improve.media.crop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.WindowManager;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseActivity;

import java.io.FileOutputStream;

import butterknife.OnClick;

/**
 * Created by haibin
 * on 2016/12/2.
 */

public class CropActivity extends BaseActivity implements View.OnClickListener {
    private CropLayout mCropLayout;
    private String mPath;

    public static void show(Fragment fragment, String path) {
        Intent intent = new Intent(fragment.getActivity(), CropActivity.class);
        intent.putExtra("path", path);
        fragment.startActivityForResult(intent, 0x04);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_crop;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        mCropLayout = (CropLayout) findViewById(R.id.cropLayout);
    }

    @Override
    protected void initData() {
        super.initData();
        mPath = getIntent().getStringExtra("path");
        getImageLoader().load(mPath).into(mCropLayout.getImageView());
        mCropLayout.setCropWidth(600);
        mCropLayout.setCropHeight(600);
        mCropLayout.start();
    }

    @OnClick({R.id.tv_crop, R.id.tv_cancel})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_crop:
                try {
                    Bitmap bitmap = mCropLayout.cropBitmap();
                    String path = Environment.getExternalStorageDirectory() + "/crop.jpg";
                    FileOutputStream os = new FileOutputStream(path);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_cancel:
                break;
        }
    }
}
