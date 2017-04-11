package net.oschina.app.improve.user.sign;

import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BackActivity;

import butterknife.Bind;

/**
 * 报名信息
 * Created by haibin on 2017/4/11.
 */

public class SignInfoActivity extends BackActivity {
    @Bind(R.id.tv_name)
    TextView mTextName;
    @Bind(R.id.tv_work)
    TextView mTextWork;
    @Bind(R.id.tv_gender)
    TextView mTextGender;
    @Bind(R.id.tv_date)
    TextView mTextDate;
    @Bind(R.id.tv_email)
    TextView mTextEmail;
    @Bind(R.id.tv_phone)
    TextView mTextPhone;
    @Bind(R.id.tv_company)
    TextView mTextCompany;
    @Bind(R.id.tv_status)
    TextView mTextStatus;
    @Bind(R.id.tv_remark)
    TextView mTextRemark;
    @Override
    protected int getContentView() {
        return R.layout.activity_sign_info;
    }

}
