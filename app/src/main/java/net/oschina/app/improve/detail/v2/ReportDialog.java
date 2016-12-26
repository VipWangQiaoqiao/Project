package net.oschina.app.improve.detail.v2;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.Report;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.SimplexToast;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2016/12/26.
 */
@SuppressWarnings("all")
public class ReportDialog {
    public static AlertDialog create(final Context context,
                                     final long id,
                                     final String href,
                                     final byte type) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_report_view, null);
        TextView textLink = (TextView) view.findViewById(R.id.tv_link);
        final TextView textType = (TextView) view.findViewById(R.id.tv_report_type);
        final EditText editText = (EditText) view.findViewById(R.id.et_report);
        final String[] reason = context.getResources().getStringArray(R.array.report);
        textType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.getSelectDialog(context,
                        reason,
                        "取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                textType.setTag(which);
                                textType.setText(reason[which]);
                            }
                        }).show();
            }
        });
        textLink.setText(href);
        textType.setText("广告");
        textType.setTag(1);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.App_Theme_Dialog_Alert)
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int tag = Integer.parseInt(textType.getTag().toString());
                        if (tag == 0 && TextUtils.isEmpty(editText.getText().toString().trim())) {
                            SimplexToast.show(context, "请填写其它原因");
                            return;
                        }
                        OSChinaApi.report(id,
                                type,
                                href,
                                Integer.parseInt(textType.getTag().toString()),
                                editText.getText().toString(),
                                new TextHttpResponseHandler() {
                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                        SimplexToast.show(context, "举报失败");
                                    }

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                        try {
                                            Type t = new TypeToken<ResultBean<Report>>() {
                                            }.getType();
                                            ResultBean<Report> resultBean = new Gson().fromJson(responseString, t);
                                            if (resultBean != null) {
                                                SimplexToast.show(context, resultBean.isSuccess() ? "举报成功" : resultBean.getMessage());
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton("取消", null);
        return builder.create();
    }
}
