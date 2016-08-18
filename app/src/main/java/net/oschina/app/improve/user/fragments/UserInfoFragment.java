package net.oschina.app.improve.user.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.bean.Constants;
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.user.activities.UserMessageActivity;
import net.oschina.app.improve.widget.SolarSystemView;
import net.oschina.app.ui.MyQrodeDialog;
import net.oschina.app.ui.SimpleBackActivity;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.AvatarView;
import net.oschina.app.widget.BadgeView;

import java.io.Serializable;
import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by fei on 2016/8/15.
 * desc: user info module
 */

public class UserInfoFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "UserInfoFragment";

    @Bind(R.id.iv_logo_setting)
    ImageView mIvLogoSetting;
    @Bind(R.id.iv_logo_zxing)
    ImageView mIvLogoZxing;

    @Bind(R.id.iv_portrait)
    AvatarView mCiOrtrait;
    @Bind(R.id.iv_gender)
    ImageView mIvGander;
    @Bind(R.id.tv_nick)
    TextView mTvName;
    @Bind(R.id.tv_summary)
    TextView mTvSummary;
    @Bind(R.id.tv_score)
    TextView mTvScore;
    @Bind(R.id.user_view_solar_system)
    SolarSystemView mSolarSystem;

    @Bind(R.id.rl_show_my_info)
    LinearLayout mRlShowInfo;

    @Bind(R.id.tv_tweet)
    TextView mTvStweetCount;
    @Bind(R.id.tv_favorite)
    TextView mTvFavoriteCount;
    @Bind(R.id.tv_following)
    TextView mTvFollowCount;
    @Bind(R.id.tv_follower)
    TextView mTvFollowerCount;

    private BadgeView mMesCount;

    private boolean mIsWatingLogin;
    private boolean refresh;


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case Constants.INTENT_ACTION_LOGOUT:

                    mIsWatingLogin = true;
                    steupUser();
                    mMesCount.hide();

                    break;
                case Constants.INTENT_ACTION_USER_CHANGE:
                    requestData(true);
                    break;
                case Constants.INTENT_ACTION_NOTICE:
                    setNotice();
                    break;
            }
        }
    };
    private AsyncTask<String, Void, net.oschina.app.bean.User> mCacheTask;
    private net.oschina.app.bean.User mInfo;


    private void setNotice() {

    }

    private void requestData(boolean b) {
        if (AppContext.getInstance().isLogin()) {
            mIsWatingLogin = false;
            String key = getCacheKey();
            if (refresh || TDevice.hasInternet()
                    && (!CacheManager.isExistDataCache(getActivity(), key))) {
                sendRequestData();
            } else {
                readCacheData(key);
            }
        } else {
            mIsWatingLogin = true;
        }
        steupUser();
    }

    private void readCacheData(String key) {
        cancelReadCacheTask();
        mCacheTask = new CacheTask(getActivity()).execute(key);
    }

    private void cancelReadCacheTask() {
        if (mCacheTask != null) {
            mCacheTask.cancel(true);
            mCacheTask = null;
        }
    }

    private void sendRequestData() {


    }

    private void steupUser() {
        if (mIsWatingLogin) {
            // mUserContainer.setVisibility(View.GONE);
            // mUserUnLogin.setVisibility(View.VISIBLE);
            //   layUserinfo.setVisibility(View.GONE);
        } else {
            // mUserContainer.setVisibility(View.VISIBLE);
            // mUserUnLogin.setVisibility(View.GONE);
            //layUserinfo.setVisibility(View.VISIBLE);
        }
    }

    private String getCacheKey() {
        return "my_information" + AppContext.getInstance().getLoginUid();
    }

    /**
     * init solar view
     */
    private void initSolar() {
        mRlShowInfo.post(new Runnable() {
            @Override
            public void run() {

                int width = mRlShowInfo.getWidth();
                int height = mRlShowInfo.getHeight();
                float rlShowInfoX = mRlShowInfo.getX();
                // float rlShowInfoY = mRlShowInfo.getY();
                float x = mCiOrtrait.getX();
                float y = mCiOrtrait.getY();
                // int ciOrtraitWidth = mCiOrtrait.getWidth();
                int ciOrtraitHeight = mCiOrtrait.getHeight();

                float px = x + +rlShowInfoX + (width >> 1);
                float py = (height >> 1) - ciOrtraitHeight - y / 2 + 28;
                int radius = (width >> 1) - 20;

                SolarSystemView.Planet planet1 = new SolarSystemView.Planet();
                planet1.setClockwise(true);
                planet1.setAngleRate(0.015F);
                planet1.setRadius(radius / 4);

                SolarSystemView.Planet planet2 = new SolarSystemView.Planet();
                planet2.setClockwise(false);
                planet2.setAngleRate(0.02F);
                planet2.setRadius(radius / 4 * 2);

                SolarSystemView.Planet planet3 = new SolarSystemView.Planet();
                planet3.setClockwise(true);
                planet3.setAngleRate(0.01F);
                planet3.setRadius(radius / 4 * 3);

                SolarSystemView.Planet planet4 = new SolarSystemView.Planet();
                planet4.setClockwise(false);
                planet4.setAngleRate(0.02F);
                planet4.setRadius(radius);

                mSolarSystem.addPlanets(planet1);
                mSolarSystem.addPlanets(planet2);
                mSolarSystem.addPlanets(planet3);
                mSolarSystem.addPlanets(planet4);
                mSolarSystem.setPivotPoint(px, py);
            }
        });
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_LOGOUT);
        filter.addAction(Constants.INTENT_ACTION_USER_CHANGE);
        getActivity().registerReceiver(mReceiver, filter);

        requestData(true);
        mInfo = AppContext.getInstance().getLoginUser();
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        initSolar();
    }


    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_user_home;
    }

    @OnClick({R.id.iv_logo_setting, R.id.iv_logo_zxing, R.id.iv_portrait, R.id.rl_show_my_info, R.id.ly_tweet,
            R.id.ly_favorite, R.id.ly_following, R.id.ly_follower, R.id.rl_message, R.id.rl_blog, R.id.rl_info_avtivities,
            R.id.rl_team
    })
    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.iv_logo_setting) {
            UIHelper.showSetting(getActivity());
        } else {
            if (mIsWatingLogin) {
                UIHelper.showLoginActivity(getActivity());
                return;
            }

            switch (id) {
                case R.id.iv_logo_zxing:
                    MyQrodeDialog dialog = new MyQrodeDialog(getActivity());
                    dialog.show();
                    break;
                case R.id.iv_portrait:
                    //编辑头像

                    break;
                case R.id.rl_show_my_info:
                    //显示我的资料
                    UIHelper.showUserCenter(getActivity(), AppContext.getInstance()
                            .getLoginUid(), AppContext.getInstance().getLoginUser()
                            .getName());
                    break;
                case R.id.ly_tweet:

                    break;
                case R.id.ly_favorite:
                    UIHelper.showUserFavorite(getActivity(), AppContext.getInstance()
                            .getLoginUid());
                    break;
                case R.id.ly_following:
                    UIHelper.showFriends(getActivity(), AppContext.getInstance()
                            .getLoginUid(), 0);
                    break;
                case R.id.ly_follower:
                    UIHelper.showFriends(getActivity(), AppContext.getInstance()
                            .getLoginUid(), 1);
                    break;
                case R.id.rl_message:
                    UserMessageActivity.show(getActivity());
                    break;
                case R.id.rl_blog:
                    UIHelper.showUserBlog(getActivity(), AppContext.getInstance()
                            .getLoginUid());
                    break;
                case R.id.rl_info_avtivities:
                    Bundle bundle = new Bundle();
                    bundle.putInt(SimpleBackActivity.BUNDLE_KEY_ARGS, 1);
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.MY_EVENT, bundle);
                    break;
                case R.id.rl_team:
                    UIHelper.showTeamMainActivity(getActivity());
                    break;
                default:
                    break;
            }
        }
    }

    private class CacheTask extends AsyncTask<String, Void, net.oschina.app.bean.User> {
        private final WeakReference<Context> mContext;

        private CacheTask(Context context) {
            mContext = new WeakReference<>(context);
        }

        @Override
        protected net.oschina.app.bean.User doInBackground(String... params) {
            Serializable seri = CacheManager.readObject(mContext.get(),
                    params[0]);
            if (seri == null) {
                return null;
            } else {
                return (net.oschina.app.bean.User) seri;
            }
        }

        @Override
        protected void onPostExecute(net.oschina.app.bean.User info) {
            super.onPostExecute(info);
            if (info != null) {
                mInfo = info;
                // mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                // } else {
                // mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                //fillUI();
            }
        }
    }

    private class SaveCacheTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> mContext;
        private final Serializable seri;
        private final String key;

        private SaveCacheTask(Context context, Serializable seri, String key) {
            mContext = new WeakReference<>(context);
            this.seri = seri;
            this.key = key;
        }

        @Override
        protected Void doInBackground(Void... params) {
            CacheManager.saveObject(mContext.get(), seri, key);
            return null;
        }
    }


}
