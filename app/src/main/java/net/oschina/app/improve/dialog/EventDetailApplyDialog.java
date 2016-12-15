package net.oschina.app.improve.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.bean.EventApplyData;
import net.oschina.app.improve.bean.EventDetail;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.ui.dialog.CommonDialog;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EventDetailApplyDialog extends CommonDialog implements
        View.OnClickListener {

    @Bind(R.id.et_name)
    EditText mName;

    @Bind(R.id.rb_male)
    RadioButton rb_male;

    private String[] genders;

    @Bind(R.id.et_phone)
    EditText mMobile;

    @Bind(R.id.et_company)
    EditText mCompany;

    @Bind(R.id.et_job)
    EditText mJob;

    @Bind(R.id.tv_remarks_tip)
    TextView mTvRemarksTip;// 备注提示

    @Bind(R.id.tv_remarks_selecte)
    TextView mTvRemarksSelected;// 备注选择

    private EventDetail mEvent;

    private EventDetailApplyDialog(Context context, boolean flag, OnCancelListener listener) {
        super(context, flag, listener);
    }

    private EventDetailApplyDialog(Context context, int defStyle, EventDetail event) {
        super(context, defStyle);
        View shareView = View.inflate(context, R.layout.dialog_event_detail_apply, null);
        ButterKnife.bind(this, shareView);
        setContent(shareView, 0);
        this.mEvent = event;
        initView();
    }

    private void initView() {
        genders = getContext().getResources().getStringArray(R.array.gender);

        rb_male.setChecked(true);

        if (mEvent.getRemark() != null) {
            mTvRemarksTip.setVisibility(View.VISIBLE);
            mTvRemarksTip.setText(mEvent.getRemark().getTip());
            mTvRemarksSelected.setVisibility(View.VISIBLE);

            mTvRemarksSelected.setOnClickListener(this);
            String[] selects = mEvent.getRemark().getSelect().split(",");
            mTvRemarksSelected.setText(selects.length > 0 ? selects[0] : mEvent.getRemark().getSelect());
        }
    }

    public EventDetailApplyDialog(Context context, EventDetail event) {
        this(context, R.style.dialog_bottom, event);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_remarks_selecte:
                selectRemarkSelect();
                break;
            default:
                break;
        }
    }


    private void selectRemarkSelect() {
        List<String> stringList = Arrays.asList(mEvent.getRemark().getSelect().split(","));
        final String[] remarkSelects = new String[stringList.size()];
        for (int i = 0; i < stringList.size(); i++) {
            remarkSelects[i] = stringList.get(i);
        }
        DialogHelper.getSelectDialog(getContext(), remarkSelects, "取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mTvRemarksSelected.setText(remarkSelects[i]);
            }
        }).show();
    }

    public EventApplyData getApplyData() {
        String name = mName.getText().toString();
        String gender = genders[rb_male.isChecked() ? 0 : 1];
        String phone = mMobile.getText().toString();
        String company = mCompany.getText().toString();
        String job = mJob.getText().toString();
        String remark = mTvRemarksSelected.getText().toString();

        if (TextUtils.isEmpty(name)) {
            AppContext.showToast("请填写姓名");
            return null;
        }

        if (TextUtils.isEmpty(phone)) {
            AppContext.showToast("请填写电话");
            return null;
        }

        Pattern pattern = Pattern.compile("^1(3[0-9]|4[57]|5[0-35-9]|7[01678]|8[0-9])[0-9]{7}[1-9]");
        if (!pattern.matcher(phone).find()) {
            AppContext.showToast("请填写正确的手机号码");
            return null;
        }

        if (mEvent.getRemark() != null && TextUtils.isEmpty(remark)) {
            AppContext.showToast("请" + mEvent.getRemark().getTip());
            return null;
        }

        EventApplyData data = new EventApplyData();

        data.setName(name);
        data.setGender(gender);
        data.setPhone(phone);
        data.setCompany(company);
        data.setJob(job);
        data.setRemark(remark);
        dismiss();
        return data;
    }
}
