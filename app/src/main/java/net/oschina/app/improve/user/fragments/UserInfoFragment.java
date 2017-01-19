package net.oschina.app.improve.user.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.media.SelectImageActivity;
import net.oschina.app.improve.media.config.SelectOptions;
import net.oschina.app.improve.notice.NoticeBean;
import net.oschina.app.improve.notice.NoticeManager;
import net.oschina.app.improve.user.activities.UserFansActivity;
import net.oschina.app.improve.user.activities.UserFollowsActivity;
import net.oschina.app.improve.user.activities.UserMessageActivity;
import net.oschina.app.improve.user.activities.UserTweetActivity;
import net.oschina.app.improve.user.collection.UserCollectionActivity;
import net.oschina.app.improve.user.event.UserEventActivity;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.SolarSystemView;
import net.oschina.app.improve.widget.TitleBar;
import net.oschina.app.interf.OnTabReselectListener;
import net.oschina.app.ui.MyQRCodeDialog;
import net.oschina.app.util.ImageUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by fei on 2016/8/15.
 * <p>
 * 用户个人界面
 */

public class UserInfoFragment extends BaseFragment implements View.OnClickListener,
        EasyPermissions.PermissionCallbacks, NoticeManager.NoticeNotify, OnTabReselectListener {

    @Bind(R.id.iv_logo_setting)
    ImageView mIvLogoSetting;
    @Bind(R.id.iv_logo_zxing)
    ImageView mIvLogoZxing;
    @Bind(R.id.user_info_head_container)
    FrameLayout mFlUserInfoHeadContainer;


    @Bind(R.id.iv_portrait)
    CircleImageView mCirclePortrait;
    @Bind(R.id.iv_gender)
    ImageView mIvGander;
    @Bind(R.id.user_info_icon_container)
    FrameLayout mFlUserInfoIconContainer;

    @Bind(R.id.tv_nick)
    TextView mTvName;
    @Bind(R.id.tv_score)
    TextView mTvScore;
    @Bind(R.id.user_view_solar_system)
    SolarSystemView mSolarSystem;
    @Bind(R.id.rl_show_my_info)
    LinearLayout mRlShowInfo;


    @Bind(R.id.about_line)
    View mAboutLine;

    @Bind(R.id.lay_about_info)
    LinearLayout mLayAboutCount;
    @Bind(R.id.tv_tweet)
    TextView mTvTweetCount;
    @Bind(R.id.tv_favorite)
    TextView mTvFavoriteCount;
    @Bind(R.id.tv_following)
    TextView mTvFollowCount;
    @Bind(R.id.tv_follower)
    TextView mTvFollowerCount;

    @Bind(R.id.user_info_notice_message)
    TextView mMesView;

    @Bind(R.id.user_info_notice_fans)
    TextView mFansView;

    private boolean mIsUploadIcon;
    private ProgressDialog mDialog;

    private int mMaxRadius;
    private int mR;
    private float mPx;
    private float mPy;

    private File mCacheFile;

    private User mUserInfo;

    private TextHttpResponseHandler requestUserInfoHandler = new TextHttpResponseHandler() {

        @Override
        public void onStart() {
            super.onStart();
            if (mSolarSystem != null) mSolarSystem.accelerate();
            if (mIsUploadIcon) {
                showWaitDialog(R.string.title_updating_user_avatar);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString
                , Throwable throwable) {
            if (mIsUploadIcon) {
                Toast.makeText(getActivity(), R.string.title_update_fail_status, Toast.LENGTH_SHORT).show();
                deleteCacheImage();
            }
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                Type type = new TypeToken<ResultBean<User>>() {
                }.getType();

                ResultBean resultBean = AppOperator.createGson().fromJson(responseString, type);
                if (resultBean.isSuccess()) {
                    User userInfo = (User) resultBean.getResult();
                    updateView(userInfo);
                    //缓存用户信息
                    AccountHelper.updateUserCache(userInfo);
                }
                if (mIsUploadIcon) {
                    deleteCacheImage();
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if (mSolarSystem != null) mSolarSystem.decelerate();
            if (mIsUploadIcon) mIsUploadIcon = false;
            if (mDialog != null && mDialog.isShowing()) mDialog.dismiss();
        }
    };


    /**
     * delete the cache image file for upload action
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void deleteCacheImage() {
        File file = this.mCacheFile;
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_user_home;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        measureTitleBarHeight();

        if (mFansView != null)
            mFansView.setVisibility(View.INVISIBLE);

        initSolar();

    }

    @Override
    protected void initData() {
        super.initData();
        NoticeManager.bindNotify(this);
        requestUserCache();
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsUploadIcon = false;
        if (AccountHelper.isLogin()) {
            User user = AccountHelper.getUser();
            updateView(user);
        } else {
            hideView();
        }
    }

    /**
     * if user isLogin,request user cache,
     * And then request user info and update user info
     */
    private void requestUserCache() {
        if (AccountHelper.isLogin()) {
            User user = AccountHelper.getUser();
            updateView(user);
            sendRequestData();
        } else {
            hideView();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!AccountHelper.isLogin()) {
            hideView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NoticeManager.unBindNotify(this);
    }

    /**
     * update the view
     *
     * @param userInfo userInfo
     */
    private void updateView(User userInfo) {

        setImageFromNet(mCirclePortrait, userInfo.getPortrait(), R.mipmap.widget_default_face);
        mCirclePortrait.setVisibility(View.VISIBLE);

        mTvName.setText(userInfo.getName());
        mTvName.setVisibility(View.VISIBLE);
        mTvName.setTextSize(20.0f);

        switch (userInfo.getGender()) {
            case 0:
                mIvGander.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mIvGander.setVisibility(View.VISIBLE);
                mIvGander.setImageResource(R.mipmap.ic_male);
                break;
            case 2:
                mIvGander.setVisibility(View.VISIBLE);
                mIvGander.setImageResource(R.mipmap.ic_female);
                break;
            default:
                break;
        }

        mTvScore.setText(String.format(
                "%s  %s",
                getString(R.string.user_score),
                formatCount(userInfo.getStatistics().getScore()))
        );
        mTvScore.setVisibility(View.VISIBLE);
        mAboutLine.setVisibility(View.VISIBLE);
        mLayAboutCount.setVisibility(View.VISIBLE);
        mTvTweetCount.setText(formatCount(userInfo.getStatistics().getTweet()));
        mTvFavoriteCount.setText(formatCount(userInfo.getStatistics().getCollect()));
        mTvFollowCount.setText(formatCount(userInfo.getStatistics().getFollow()));
        mTvFollowerCount.setText(formatCount(userInfo.getStatistics().getFans()));

        mUserInfo = userInfo;
    }

    /**
     * format count
     *
     * @param count count
     * @return formatCount
     */
    private String formatCount(long count) {

        if (count > 1000) {
            int a = (int) (count / 100);
            int b = a % 10;
            int c = a / 10;
            String str;
            if (c <= 9 && b != 0)
                str = c + "." + b;
            else
                str = String.valueOf(c);

            return str + "k";
        } else {
            return String.valueOf(count);
        }

    }

    /**
     * requestData
     */
    private void sendRequestData() {
        if (TDevice.hasInternet() && AccountHelper.isLogin())
            OSChinaApi.getUserInfo(requestUserInfoHandler);
    }

    /**
     * init solar view
     */
    private void initSolar() {
        View root = this.mRoot;
        if (root != null) {
            root.post(new Runnable() {
                @Override
                public void run() {

                    if (mRlShowInfo == null) return;
                    int width = mRlShowInfo.getWidth();
                    float rlShowInfoX = mRlShowInfo.getX();

                    int height = mFlUserInfoIconContainer.getHeight();
                    float y1 = mFlUserInfoIconContainer.getY();

                    float x = mCirclePortrait.getX();
                    float y = mCirclePortrait.getY();
                    int portraitWidth = mCirclePortrait.getWidth();

                    mPx = x + +rlShowInfoX + (width >> 1);
                    mPy = y1 + y + (height - y) / 2;
                    mMaxRadius = (int) (mSolarSystem.getHeight() - mPy + 250);
                    mR = (portraitWidth >> 1);

                    updateSolar(mPx, mPy);

                }
            });
        }
    }

    /**
     * update solar
     *
     * @param px float
     * @param py float
     */
    private void updateSolar(float px, float py) {

        SolarSystemView solarSystemView = mSolarSystem;
        Random random = new Random(System.currentTimeMillis());
        int maxRadius = mMaxRadius;
        int r = mR;
        solarSystemView.clear();
        for (int i = 40, radius = r + i; radius <= maxRadius; i = (int) (i * 1.4), radius += i) {
            SolarSystemView.Planet planet = new SolarSystemView.Planet();
            planet.setClockwise(random.nextInt(10) % 2 == 0);
            planet.setAngleRate((random.nextInt(35) + 1) / 1000.f);
            planet.setRadius(radius);
            solarSystemView.addPlanets(planet);

        }
        solarSystemView.setPivotPoint(px, py);
    }

    /**
     *
     */
    private void hideView() {
        mCirclePortrait.setImageResource(R.mipmap.widget_default_face);
        mTvName.setText(R.string.user_hint_login);
        mTvName.setTextSize(16.0f);
        mIvGander.setVisibility(View.INVISIBLE);
        mTvScore.setVisibility(View.INVISIBLE);
        mLayAboutCount.setVisibility(View.GONE);
        mAboutLine.setVisibility(View.GONE);
    }

    /**
     * measureTitleBarHeight
     */
    private void measureTitleBarHeight() {
        if (mRlShowInfo != null) {
            mRlShowInfo.setPadding(mRlShowInfo.getLeft(),
                    TitleBar.getExtPaddingTop(getResources()),
                    mRlShowInfo.getRight(), mRlShowInfo.getBottom());
        }
    }

    @SuppressWarnings("deprecation")
    @OnClick({
            R.id.iv_logo_setting, R.id.iv_logo_zxing, R.id.iv_portrait,
            R.id.user_view_solar_system, R.id.ly_tweet, R.id.ly_favorite,
            R.id.ly_following, R.id.ly_follower, R.id.rl_message, R.id.rl_blog,
            R.id.rl_info_question, R.id.rl_info_activities, R.id.rl_team
    })
    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.iv_logo_setting) {
            UIHelper.showSetting(getActivity());
        } else {

            if (!AccountHelper.isLogin()) {
                LoginActivity.show(getActivity());
                return;
            }

            switch (id) {
                case R.id.iv_logo_zxing:
                    MyQRCodeDialog dialog = new MyQRCodeDialog(getActivity());
                    dialog.show();
                    break;
                case R.id.iv_portrait:
                    //查看头像 or 更换头像
                    showAvatarOperation();
                    break;
                case R.id.user_view_solar_system:
                    //显示我的资料
                    if (mUserInfo != null) {
                        Bundle userBundle = new Bundle();
                        userBundle.putSerializable("user_info", mUserInfo);
                        UIHelper.showSimpleBack(getActivity(),
                                SimpleBackPage.MY_INFORMATION_DETAIL, userBundle);
                    }
                    break;
                case R.id.ly_tweet:
                    UserTweetActivity.show(getActivity(), AccountHelper.getUserId());
                    break;
                case R.id.ly_favorite:
                    UserCollectionActivity.show(getActivity());
                    break;
                case R.id.ly_following:
                    UserFollowsActivity.show(getActivity(), AccountHelper.getUserId());
                    break;
                case R.id.ly_follower:
                    UserFansActivity.show(getActivity(), AccountHelper.getUserId());
                    break;
                case R.id.rl_message:
                    UserMessageActivity.show(getActivity());
                    break;
                case R.id.rl_blog:
                    UIHelper.showUserBlog(getActivity(), AccountHelper.getUserId());
                    break;
                case R.id.rl_info_question:
                    UIHelper.showUserQuestion(getActivity(), AccountHelper.getUserId());
                    break;
                case R.id.rl_info_activities:
                    UserEventActivity.show(mContext, AccountHelper.getUserId(), "");
                    break;
                case R.id.rl_team:
                    UIHelper.showTeamMainActivity(getActivity());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 更换头像 or 查看头像
     */
    private void showAvatarOperation() {
        if (!AccountHelper.isLogin()) {
            LoginActivity.show(getActivity());
        } else {
            DialogHelper.getSelectDialog(getActivity(),
                    getString(R.string.action_select),
                    getResources().getStringArray(R.array.avatar_option), "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case 0:
                                    SelectImageActivity.show(getContext(), new SelectOptions.Builder()
                                            .setSelectCount(1)
                                            .setHasCam(true)
                                            .setCrop(700, 700)
                                            .setCallback(new SelectOptions.Callback() {
                                                @Override
                                                public void doSelected(String[] images) {
                                                    String path = images[0];
                                                    uploadNewPhoto(new File(path));
                                                }
                                            }).build());
                                    break;

                                case 1:
                                    if (mUserInfo == null
                                            || TextUtils.isEmpty(mUserInfo.getPortrait())) return;
                                    UIHelper.showUserAvatar(getActivity(), mUserInfo.getPortrait());
                                    break;
                            }
                        }
                    }).show();
        }
    }

    /**
     * take photo
     */
    private void startTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
    }

    public ProgressDialog showWaitDialog(int messageId) {
        String message = getResources().getString(messageId);
        if (mDialog == null) {
            mDialog = DialogHelper.getProgressDialog(getActivity(), message);
        }

        mDialog.setMessage(message);
        mDialog.show();

        return mDialog;
    }

    /**
     * update the new picture
     */
    private void uploadNewPhoto(File file) {
        // 获取头像缩略图
        if (file == null || !file.exists() || file.length() == 0) {
            AppContext.showToast(getString(R.string.title_icon_null));
        } else {
            mIsUploadIcon = true;
            this.mCacheFile = file;
            OSChinaApi.updateUserIcon(file, requestUserInfoHandler);
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        try {
            startTakePhoto();
        } catch (Exception e) {
            Toast.makeText(this.getContext(), R.string.permissions_camera_error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this.getContext(), R.string.permissions_camera_error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onNoticeArrived(NoticeBean bean) {
        if (mMesView != null) {
            int allCount = bean.getReview() + bean.getLetter() + bean.getMention();
            mMesView.setVisibility(allCount > 0 ? View.VISIBLE : View.GONE);
            mMesView.setText(String.valueOf(allCount));
        }
        if (mFansView != null) {
            int fans = bean.getFans();
            mFansView.setVisibility(fans > 0 ? View.VISIBLE : View.GONE);
            mFansView.setText(String.valueOf(fans));
        }
    }


    @Override
    public void onTabReselect() {
        sendRequestData();
    }

}
