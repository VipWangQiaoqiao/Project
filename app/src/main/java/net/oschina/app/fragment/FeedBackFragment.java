package net.oschina.app.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.util.ImageUtils;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.UIHelper;

import org.kymjs.kjframe.Core;
import org.kymjs.kjframe.bitmap.BitmapCallBack;
import org.kymjs.kjframe.bitmap.DiskImageRequest;
import org.kymjs.kjframe.utils.FileUtils;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class FeedBackFragment extends BaseFragment {
    @Bind(R.id.et_feedback)
    EditText mEtContent;
    @Bind(R.id.iv_img)
    ImageView mImv;
    @Bind(R.id.iv_clear_img)
    ImageView mImClear;
    @Bind(R.id.rb_feedback_error)
    RadioButton mRbError;

    private String imgPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_feedback, null);
        ButterKnife.bind(this, view);
        mImv.setOnClickListener(this);
        mImClear.setOnClickListener(this);
        mRbError.setChecked(true);
        return view;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_clear_img:
                mImv.setImageResource(R.drawable.selector_image_add);
                imgPath = null;
                mImClear.setVisibility(View.GONE);
                break;
            case R.id.iv_img:
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "选择图片"),
                            ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
                } else {
                    intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "选择图片"),
                            ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null &&
                requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                imgPath = ImageUtils.getImagePath(selectedImageUri, getActivity());
                new Core.Builder().view(mImv).url(imgPath).doTask();
                mImClear.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.submit_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.public_menu_send:
                final String data = mEtContent.getText().toString();
                if (StringUtils.isEmpty(data)) {
                    AppContext.showToast("你忘记写建议了");
                } else {
                    final String header = String.format("［Android-主站-%s］%s",
                            mRbError.isChecked() ? getString(R.string.str_feedback_error) :
                                    getString(R.string.str_feedback_function), data);
                    if (!TextUtils.isEmpty(imgPath)) {
                        final String path = FileUtils.getSDCardPath() + "/OSChina/tempfile.jpg";
                        DiskImageRequest req = new DiskImageRequest();
                        req.load(imgPath, 300, 300, new BitmapCallBack() {
                            @Override
                            public void onSuccess(Bitmap bitmap) {
                                super.onSuccess(bitmap);
                                FileUtils.bitmapToFile(bitmap, path);
                                upload(header, new File(path));
                            }
                        });
                    } else {
                        upload(header, null);
                    }
                }
                break;
        }
        return true;
    }

    /**
     * 上传反馈信息
     *
     * @param content
     * @param file
     */
    public void upload(String content, File file) {
        final ProgressDialog dialog = UIHelper.getprogress(getActivity(), "上传中");
        OSChinaApi.feedback(content, file, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                AppContext.showToast("已收到你的建议，谢谢");
                getActivity().finish();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                AppContext.showToast("网络异常，请稍后重试");
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
            }
        });
    }
}
