package net.oschina.app.improve.pay.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import net.oschina.app.R;
import net.oschina.app.util.SimpleTextWatcher;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 打赏 dialog
 * Created by thanatos on 16/10/12.
 */

public class RewardDialog extends Dialog implements View.OnClickListener {

    @Bind(R.id.img_portrait)
    CircleImageView mPortrait;
    @Bind(R.id.tv_nick)
    TextView mNick;
    @Bind(R.id.tv_info)
    TextView mInfo;
    @Bind(R.id.et_input)
    EditText mInput;
    @Bind(R.id.tv_pay_choice)
    TextView mPayChoice;
    @Bind(R.id.btn_reward)
    Button mBtnReward;
    @Bind(R.id.layout_casts)
    LinearLayout mLayoutCasts;

    private String portrait;
    private String nick;
    private OnClickRewardCallback callback;

    public RewardDialog(Context context) {
        super(context);
    }

    public interface OnClickRewardCallback {
        void reward(float cast);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_dialog_reward);
        ButterKnife.bind(this);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        getWindow().setAttributes(params);

        // TODO 支持多平台支付,后期添加微信支付
        mInfo.setText("使用支付宝支付");
        mPayChoice.setVisibility(View.GONE);

        Glide.with(getContext())
                .load(portrait)
                .asBitmap()
                .placeholder(R.mipmap.widget_default_face)
                .error(R.mipmap.widget_default_face)
                .into(mPortrait);
        mNick.setText(nick);

        mBtnReward.setEnabled(false);
        mInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if (TextUtils.isEmpty(s)) {
                    mBtnReward.setEnabled(false);
                    return;
                }
                String mCastStr = s.toString();
                float cast = 0;
                try {
                    cast = Float.valueOf(mCastStr);
                } catch (Exception e) {
                    cast = 0;
                    mInput.setText(null);
                }
                if (cast <= 0) mBtnReward.setEnabled(false);
                else mBtnReward.setEnabled(true);
            }
        });

        int count = mLayoutCasts.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = mLayoutCasts.getChildAt(i);
            view.setOnClickListener(this);
        }
        selectAt(null, count - 1);
    }

    private void selectAt(View v, int index) {
        int count = mLayoutCasts.getChildCount();
        for (int i = 0; i < count; i++) {
            mLayoutCasts.getChildAt(i).setSelected(false);
        }
        if (v == null) {
            mLayoutCasts.getChildAt(index).setSelected(true);
        } else {
            v.setSelected(true);
        }
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    @Override
    public void onClick(View v) {
        Object o = v.getTag();
        if (o == null) return;
        selectAt(v, 0);
        String mCastStr = o.toString();
        int cast;
        try {
            cast = Integer.valueOf(mCastStr);
        } catch (Exception e) {
            cast = -1;
        }
        // 其他金额
        if (cast == -1) {
            mInput.setText(null);
            return;
        }
        mInput.setText(mCastStr);
        mInput.setSelection(mCastStr.length());
    }

    public void setOnClickRewardListener(OnClickRewardCallback callback) {
        this.callback = callback;
    }

    @OnClick(R.id.btn_reward)
    void onClickReward() {
        String mCastStr = mInput.getText().toString();
        float cast = Float.valueOf(mCastStr);
        if (cast < 0.01) {
            Toast.makeText(getContext(), "最低悬赏0.01元", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cast > 9999999.99) {
            Toast.makeText(getContext(), "最高悬赏9999999.99", Toast.LENGTH_SHORT).show();
            return;
        }
        if (callback == null) return;
        callback.reward(cast);
    }

}
