package net.oschina.app.improve.user.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import net.oschina.app.cache.CacheManager;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.bean.UserV2;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.notice.NoticeBean;
import net.oschina.app.improve.notice.NoticeManager;
import net.oschina.app.improve.user.activities.UserFansActivity;
import net.oschina.app.improve.user.activities.UserFollowsActivity;
import net.oschina.app.improve.user.activities.UserMessageActivity;
import net.oschina.app.improve.user.activities.UserTweetActivity;
import net.oschina.app.improve.widget.SolarSystemView;
import net.oschina.app.improve.widget.TitleBar;
import net.oschina.app.interf.OnTabReselectListener;
import net.oschina.app.ui.MyQRCodeDialog;
import net.oschina.app.ui.SimpleBackActivity;
import net.oschina.app.util.DialogHelp;
import net.oschina.app.util.FileUtil;
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
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by fei on 2016/8/15.
 * desc: user mUserInfo module
 */

public class NewUserInfoFragment extends BaseFragment implements View.OnClickListener,
        EasyPermissions.PermissionCallbacks, NoticeManager.NoticeNotify, OnTabReselectListener {

    public static final String CACHE_NAME = "NewUserInfoFragment";

    public static final int ACTION_TYPE_ALBUM = 0;
    public static final int ACTION_TYPE_PHOTO = 1;
    private final static int CROP = 200;
    private final static String FILE_SAVE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            .getAbsolutePath() + File.separator + "开源中国" + File.separator;

    private static final int CAMERA_PERM = 1;

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
    @Bind(R.id.tv_summary)
    TextView mTvSummary;
    @Bind(R.id.tv_score)
    TextView mTvScore;
    @Bind(R.id.user_view_solar_system)
    SolarSystemView mSolarSystem;
    @Bind(R.id.rl_show_my_info)
    LinearLayout mRlShowInfo;


    @Bind(R.id.lay_about_info)
    LinearLayout layAboutCount;
    @Bind(R.id.tv_tweet)
    TextView mTvTweetCount;
    @Bind(R.id.tv_favorite)
    TextView mTvFavoriteCount;
    @Bind(R.id.tv_following)
    TextView mTvFollowCount;
    @Bind(R.id.tv_follower)
    TextView mTvFollowerCount;

    @Bind(R.id.user_info_notice_message)
    View mMesView;

    @Bind(R.id.user_info_notice_fans)
    View mFansView;

    private Uri mOrigUri;
    private File mPortraitFile;

    private net.oschina.app.bean.User mInfo;
    private boolean mIsUploadIcon;
    private ProgressDialog mDialog;

    private int mMaxRadius;
    private int mR;
    private float mPx;
    private float mPy;

    private String mPortraitPath;
    private UserV2 mUserInfo;
    private TextHttpResponseHandler textHandler = new TextHttpResponseHandler() {

        @Override
        public void onStart() {
            super.onStart();
            if (mSolarSystem != null)
                mSolarSystem.accelerate();
            if (mIsUploadIcon) {
                showWaitDialog(R.string.title_update_success_status);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            if (mSolarSystem != null)
                mSolarSystem.decelerate();
            if (mIsUploadIcon)
                Toast.makeText(getActivity(), R.string.title_update_fail_status, Toast.LENGTH_SHORT).show();
            mIsUploadIcon = false;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                if (mSolarSystem != null)
                    mSolarSystem.decelerate();

                Type type = new TypeToken<ResultBean<UserV2>>() {
                }.getType();

                ResultBean resultBean = AppContext.createGson().fromJson(responseString, type);
                if (resultBean.isSuccess()) {
                    UserV2 userInfo = (UserV2) resultBean.getResult();
                    updateView(userInfo);
                    AppOperator.runOnThread(new Runnable() {
                        @Override
                        public void run() {
                            CacheManager.saveObject(getContext(), mUserInfo, CACHE_NAME);
                        }
                    });
                }
                if (mIsUploadIcon) {
                    hideWaitDialog();
                    mIsUploadIcon = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }
        }

    };

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
        // super.initData();

        UserV2 userInfo = (UserV2) CacheManager.readObject(getActivity(), CACHE_NAME);
        if (userInfo != null) {
            updateView(userInfo);
            if (TDevice.hasInternet()) {
                sendRequestData();
            }
        } else {
            if (TDevice.hasInternet()) {
                sendRequestData();
            } else {
                hideView();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mInfo = AppContext.getInstance().getLoginUser();
        mIsUploadIcon = false;
        NoticeManager.bindNotify(this);
        boolean login = AppContext.getInstance().isLogin();
        if (!login) {
            hideView();
        } else {
            if (TDevice.hasInternet()) {
                sendRequestData();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        NoticeManager.unBindNotify(this);
        boolean login = AppContext.getInstance().isLogin();
        if (!login) {
            hideView();
        }
    }

    /**
     * update the view
     *
     * @param userInfo userInfo
     */
    private void updateView(UserV2 userInfo) {

        setImageFromNet(mCirclePortrait, userInfo.getPortrait(), R.mipmap.widget_dface);
        mCirclePortrait.setVisibility(View.VISIBLE);

        mTvName.setText(userInfo.getName());
        mTvName.setVisibility(View.VISIBLE);

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

        mTvSummary.setText(userInfo.getDesc());
        mTvSummary.setVisibility(View.VISIBLE);
        mTvScore.setText(String.format("%s  %s", getString(R.string.user_score), formatCount(userInfo.getStatistics().getScore())));
        mTvScore.setVisibility(View.VISIBLE);
        layAboutCount.setVisibility(View.VISIBLE);
        mTvTweetCount.setText(formatCount(userInfo.getStatistics().getTweet()));
        mTvFavoriteCount.setText(formatCount(userInfo.getStatistics().getCollect()));
        mTvFollowCount.setText(formatCount(userInfo.getStatistics().getFollow()));
        mTvFollowerCount.setText(formatCount(userInfo.getStatistics().getFans()));

//        updateSolar(mPx, mPy);

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

    private void sendRequestData() {
        OSChinaApi.getUserInfo(textHandler);
    }

    /**
     * init solar view
     */
    private void initSolar() {

        if (mRoot != null) {
            mRoot.post(new Runnable() {
                @Override
                public void run() {

                    if (mRlShowInfo == null) return;
                    int width = mRlShowInfo.getWidth();
                    float rlShowInfoX = mRlShowInfo.getX();

                    int height = mFlUserInfoIconContainer.getHeight();
                    float y1 = mFlUserInfoIconContainer.getY();

                    float x = mCirclePortrait.getX();
                    float y = mCirclePortrait.getY();
                    int ciOrtraitWidth = mCirclePortrait.getWidth();

                    mPx = x + +rlShowInfoX + (width >> 1);
                    mPy = y1 + y + (height - y) / 2;
                    mMaxRadius = (int) (mSolarSystem.getHeight() - mPy + 50);
                    mR = (ciOrtraitWidth >> 1);

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
        for (int i = 60, radius = r + i; radius <= maxRadius; i = (int) (i * 1.4), radius += i) {
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
        mCirclePortrait.setImageResource(R.mipmap.widget_dface);
        mTvName.setText("点击头像登录");
        mIvGander.setVisibility(View.INVISIBLE);
        mTvSummary.setVisibility(View.INVISIBLE);
        mTvScore.setVisibility(View.INVISIBLE);
        layAboutCount.setVisibility(View.INVISIBLE);
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
    @OnClick({R.id.iv_logo_setting, R.id.iv_logo_zxing, R.id.iv_portrait, R.id.user_view_solar_system, R.id.ly_tweet,
            R.id.ly_favorite, R.id.ly_following, R.id.ly_follower, R.id.rl_message, R.id.rl_blog, R.id.rl_info_avtivities,
            R.id.rl_team
    })
    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.iv_logo_setting) {
            UIHelper.showSetting(getActivity());
        } else {
            if (!AppContext.getInstance().isLogin()) {
                UIHelper.showLoginActivity(getActivity());
                return;
            }

            switch (id) {
                case R.id.iv_logo_zxing:
                    MyQRCodeDialog dialog = new MyQRCodeDialog(getActivity());
                    dialog.show();
                    break;
                case R.id.iv_portrait:
                    //编辑头像
                    showClickAvatar();
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
                    UserTweetActivity.show(getActivity(), AppContext.getInstance().getLoginUid());
                    break;
                case R.id.ly_favorite:
                    UIHelper.showUserFavorite(getActivity(), AppContext.getInstance()
                            .getLoginUid());
                    break;
                case R.id.ly_following:
                    UserFollowsActivity.show(getActivity(), AppContext.getInstance().getLoginUid());
                    break;
                case R.id.ly_follower:
                    UserFansActivity.show(getActivity(), AppContext.getInstance().getLoginUid());
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

    private void showClickAvatar() {
        if (mInfo == null) {
            AppContext.showToast("");
            return;
        }
        DialogHelp.getSelectDialog(getActivity(), getString(R.string.action_select), getResources().getStringArray(R.array.avatar_option), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    handleSelectPicture();
                } else {
                    if (mUserInfo == null) return;
                    UIHelper.showUserAvatar(getActivity(), mUserInfo.getPortrait());
                }
            }
        }).show();
    }

    /**
     * show select-picture  dialog
     */
    private void handleSelectPicture() {
        DialogHelp.getSelectDialog(getActivity(), "选择图片", getResources().getStringArray(R.array.choose_picture),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        goToSelectPicture(i);
                    }
                }).show();
    }

    /**
     * select picture
     *
     * @param position action position
     */
    private void goToSelectPicture(int position) {
        switch (position) {
            case ACTION_TYPE_ALBUM:
                showImagePick();
                break;
            case ACTION_TYPE_PHOTO:
                startTakePhotoByPermissions();
                break;
            default:
                break;
        }
    }

    /**
     * 选择图片裁剪
     */
    private void showImagePick() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.action_select_picture)),
                    ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.action_select_picture)),
                    ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
        }
    }

    @AfterPermissionGranted(CAMERA_PERM)
    private void startTakePhotoByPermissions() {
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this.getContext(), perms)) {
            try {
                startTakePhoto();
            } catch (Exception e) {
                Toast.makeText(this.getContext(), R.string.permissions_camera_error, Toast.LENGTH_LONG).show();
            }
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(this,
                    getResources().getString(R.string.str_request_camera_message),
                    CAMERA_PERM, perms);
        }
    }

    /**
     * take photo
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    private void startTakePhoto() {
        Intent intent;
        // 判断是否挂载了SD卡
        String savePath = "";
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            savePath = FILE_SAVE_PATH;
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
        }

        // 没有挂载SD卡，无法保存文件
        if (TextUtils.isEmpty(savePath)) {
            AppContext.showToastShort(getString(R.string.title_error_photo));
            return;
        }

        String fileName = "portrait.jpg";// 照片命名
        File out = new File(savePath, fileName);
        Uri uri = Uri.fromFile(out);
        mOrigUri = uri;

        //  String theLarge = savePath + fileName;

        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
    }

    // 裁剪头像的绝对路径
    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    private Uri getUploadTempFile(Uri uri) {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File saveDir = new File(FILE_SAVE_PATH);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
        } else {
            AppContext.showToast("无法保存上传的头像，请检查SD卡是否挂载");
            return null;
        }
        String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(uri);

        // 如果是标准Uri
        if (TextUtils.isEmpty(thePath)) {
            thePath = ImageUtils.getAbsoluteImagePath(getActivity(), uri);
        }
        String ext = FileUtil.getFileFormat(thePath);

        ext = TextUtils.isEmpty(ext) ? "jpg" : ext;
        // 照片命名
        String cropFileName = "portrait." + ext;
        // 裁剪头像的绝对路径
        mPortraitPath = FILE_SAVE_PATH + cropFileName;
        mPortraitFile = new File(mPortraitPath);

        // notify
        Uri notifyUri = Uri.fromFile(mPortraitFile);
        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, notifyUri));

        return notifyUri;
    }

    /**
     * 拍照后裁剪
     *
     * @param data 原始图片
     */
    private void startActionCrop(Uri data) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra("output", this.getUploadTempFile(data));
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", CROP);// 输出图片大小
        intent.putExtra("outputY", CROP);
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
    }

    public ProgressDialog showWaitDialog(int messageId) {
        String message = getResources().getString(messageId);
        if (mDialog == null) {
            mDialog = DialogHelp.getWaitDialog(getActivity(), message);
        }

        mDialog.setMessage(message);
        mDialog.show();

        return mDialog;
    }

    public void hideWaitDialog() {
        ProgressDialog dialog = mDialog;
        if (dialog != null) {
            mDialog = null;
            try {
                dialog.dismiss();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * update the new picture
     */
    private void uploadNewPhoto() {

        // 获取头像缩略图
        if (TextUtils.isEmpty(mPortraitPath) && !mPortraitFile.exists()) {
            AppContext.showToast(getString(R.string.title_icon_null));
        }

        if (mPortraitFile != null) {
            mIsUploadIcon = true;
            OSChinaApi.updateUserIcon(mPortraitFile, textHandler);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnIntent);
        if (resultCode != Activity.RESULT_OK)
            return;

        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
                startActionCrop(mOrigUri);// 拍照后裁剪
                break;
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
                startActionCrop(imageReturnIntent.getData());// 选图后裁剪
                break;
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:
                uploadNewPhoto();
                break;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onNoticeArrived(NoticeBean bean) {

        int allCount = bean.getReview() + bean.getLetter() + bean.getMention();
        if (allCount > 0) {
            if (mMesView != null)
                mMesView.setVisibility(View.VISIBLE);
        } else {
            if (mMesView != null)
                mMesView.setVisibility(View.INVISIBLE);
        }

        int fans = bean.getFans();
        if (fans > 0) {
            if (mFansView != null)
                mFansView.setVisibility(View.VISIBLE);
        } else {
            if (mFansView != null) {
                mFansView.setVisibility(View.INVISIBLE);
            }
        }

    }


    @Override
    public void onTabReselect() {
        if (AppContext.getInstance().isLogin() && TDevice.hasInternet()) {
            sendRequestData();
        }
    }

}
