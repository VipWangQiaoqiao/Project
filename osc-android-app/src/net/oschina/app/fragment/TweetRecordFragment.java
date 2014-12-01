package net.oschina.app.fragment;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.bean.Tweet;
import net.oschina.app.service.ServerTaskUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.RecordButton;
import net.oschina.app.widget.RecordButton.OnFinishedRecordListener;
import net.oschina.app.widget.RecordButton.OnVolumeChangeListener;
import net.oschina.app.widget.RecordDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 语音动弹发布界面
 * 
 * @author kymjs(kymjs123@gmail.com)
 * 
 */
public class TweetRecordFragment extends BaseFragment {

    @InjectView(R.id.tweet_btn_record)
    RecordButton mBtnRecort;
    @InjectView(R.id.button1)
    Button button1;

    @Override
    public void onClick(View v) {}

    @Override
    public void initView(View view) {
        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnRecort.setAudioPath(mBtnRecort.getCurrentAudioPath());
                mBtnRecort.startPlay();
            }
        });

        mBtnRecort.setOnFinishedRecordListener(new OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(String audioPath, int recordTime) {
                handleSubmit(audioPath);
            }

            @Override
            public void onCancleRecord() {
                UIHelper.toast("录音取消");
            }
        });

        RecordDialog dialog = new RecordDialog(getActivity());
        mBtnRecort.setRecordDialog(dialog);
        mBtnRecort.setOnVolumeChangeListener(new OnVolumeChangeListener() {
            @Override
            public void onVolumeChange(Dialog d, int volume) {
                RecordDialog dialog = (RecordDialog) d;
                switch (volume) {
                case 6:
                    break;
                case 5:
                    break;
                case 4:
                    break;
                case 3:
                    break;
                case 2:
                    break;
                case 1:
                    break;
                default:
                    break;
                }
            }
        });
    }

    @Override
    public void initData() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.item_tweet_pub_record,
                container, false);
        ButterKnife.inject(this, rootView);
        initView(rootView);
        return rootView;
    }

    /**
     * 发布动弹
     */
    private void handleSubmit(String audioPath) {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_network_error);
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }

        Tweet tweet = new Tweet();
        tweet.setAuthorid(AppContext.getInstance().getLoginUid());
        tweet.setAudioPath(audioPath);
        tweet.setBody("#语音动弹#");
        ServerTaskUtils.pubTweet(getActivity(), tweet);
    }
}
