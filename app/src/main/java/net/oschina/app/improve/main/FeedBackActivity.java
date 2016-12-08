package net.oschina.app.improve.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.bean.Message;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.media.SelectImageActivity;
import net.oschina.app.improve.media.config.SelectOptions;
import net.oschina.app.improve.utils.PicturesCompressor;

import java.io.File;
import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2016/10/14.
 */

public class FeedBackActivity extends BaseBackActivity implements View.OnClickListener {

    @Bind(R.id.rb_error)
    RadioButton rb_error;

    @Bind(R.id.et_feed_back)
    EditText et_feed_back;

    @Bind(R.id.iv_add)
    ImageView iv_add;

    @Bind(R.id.iv_clear_img)
    ImageView iv_clear_img;

    private String mFilePath = "";
    private String mFeedbackStr = "[Android-主站-%s]";
    private ProgressDialog mDialog;

    public static void show(Context context) {
        context.startActivity(new Intent(context, FeedBackActivity.class));
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_feed_back;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        rb_error.setChecked(true);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @OnClick({R.id.iv_add, R.id.iv_clear_img})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                String content = getFeedBackContent();
                if (TextUtils.isEmpty(content) && TextUtils.isEmpty(mFilePath)) {
                    return;
                }
                content = String.format(mFeedbackStr, rb_error.isChecked() ? "程序错误" : "功能建议") + content;
                File file = new File(mFilePath);
                if (file.exists()) {
                    compress(mFilePath, new Run(content));
                } else {
                    addFeedBack(content, null);
                }
                break;
            case R.id.iv_add:
                openImageSelector();
                break;
            case R.id.iv_clear_img:
                iv_add.setImageResource(R.mipmap.ic_tweet_add);
                iv_clear_img.setVisibility(View.GONE);
                mFilePath = "";
                break;
        }
    }

    public void openImageSelector() {
        SelectImageActivity.show(this, new SelectOptions.Builder()
                .setHasCam(false)
                .setSelectCount(1)
                .setCallback(new SelectOptions.Callback() {
                    @Override
                    public void doSelected(String[] images) {
                        mFilePath = images[0];
                        getImageLoader().load(mFilePath).into(iv_add);
                        iv_clear_img.setVisibility(View.VISIBLE);
                    }
                }).build());
    }

    /**
     * 添加反馈，走系统私信接口
     *
     * @param content content
     * @param file    file
     */
    private void addFeedBack(String content, File file) {
        getDialog("反馈中...").show();
        OSChinaApi.pubMessage(2609904, content, file, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(FeedBackActivity.this, "网络错误，请重试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Type type = new TypeToken<ResultBean<Message>>() {
                }.getType();
                try {
                    ResultBean<Message> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        Toast.makeText(FeedBackActivity.this, "谢谢您的反馈", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(FeedBackActivity.this, resultBean.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mDialog.dismiss();
            }
        });
    }

    private void compress(final String oriPath, final Run runnable) {
        final String path = getFilesDir() + "/message/" + getFileName(oriPath);
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                if (PicturesCompressor.compressImage(oriPath, path, 512 * 1024)) {
                    runnable.setPath(path);
                    runOnUiThread(runnable);
                }
            }
        });
    }

    private String getFileName(String filePath) {
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    private class Run implements Runnable {
        private String path;
        private String content;

        public Run(String content) {
            this.content = content;
        }

        @Override
        public void run() {
            File file = new File(path);
            addFeedBack(content, file);
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public String getFeedBackContent() {
        return et_feed_back.getText().toString().trim();
    }

    public ProgressDialog getDialog(String message) {
        if (mDialog == null) {
            mDialog = new ProgressDialog(this);
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
        }
        mDialog.setMessage(message);
        return mDialog;
    }
}
