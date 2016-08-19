package net.oschina.app.improve.user.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Notice;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.bean.User;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.notice.NoticeBean;
import net.oschina.app.improve.notice.NoticeManager;
import net.oschina.app.improve.user.activities.UserMessageActivity;
import net.oschina.app.improve.user.activities.UserTweetActivity;
import net.oschina.app.improve.user.bean.UserInfo;
import net.oschina.app.improve.widget.SolarSystemView;
import net.oschina.app.ui.MainActivity;
import net.oschina.app.ui.MyQrodeDialog;
import net.oschina.app.ui.SimpleBackActivity;
import net.oschina.app.ui.dialog.DialogControl;
import net.oschina.app.util.DialogHelp;
import net.oschina.app.util.FileUtil;
import net.oschina.app.util.ImageUtils;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import net.oschina.app.widget.BadgeView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
        EasyPermissions.PermissionCallbacks, NoticeManager.NoticeNotify {

    public static final int ACTION_TYPE_ALBUM = 0;
    public static final int ACTION_TYPE_PHOTO = 1;
    private static final String TAG = "NewUserInfoFragment";
    private final static int CROP = 200;
    private final static String FILE_SAVEPATH = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/OSChina/Portrait/";

    private static final int CAMERA_PERM = 1;

    @Bind(R.id.iv_logo_setting)
    ImageView mIvLogoSetting;
    @Bind(R.id.iv_logo_zxing)
    ImageView mIvLogoZxing;

    @Bind(R.id.iv_portrait)
    CircleImageView mCiOrtrait;
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

    //  private AsyncTask<String, Void, User> mCacheTask;
    @Bind(R.id.user_info_notice_message)
    View mMesView;

    @Bind(R.id.user_info_notice_fans)
    View mFansView;

    private BadgeView mMesCount;
    private boolean mIsWatingLogin;
    private Uri origUri;
    private File protraitFile;
    private Bitmap protraitBitmap;
    private String protraitPath;
    private boolean isChangeFace;
    private UserInfo mUserInfo;
    private TextHttpResponseHandler textHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {

            try {

                Type type = new TypeToken<ResultBean<UserInfo>>() {
                }.getType();

                ResultBean resultBean = AppContext.createGson().fromJson(responseString, type);
                if (resultBean.isSuccess()) {
                    UserInfo userInfo = (UserInfo) resultBean.getResult();

                    Log.d(TAG, "onSuccess: ----->userInfo=" + userInfo.toString());
                    updateView(userInfo);
                    mUserInfo = userInfo;
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
        }
    };
    private net.oschina.app.bean.User mInfo;
    private BadgeView mMesCountForFans;

    private void updateView(UserInfo userInfo) {

        setImageFromNet(mCiOrtrait, userInfo.getPortrait(), R.mipmap.widget_dface);

        mTvName.setText(userInfo.getName());

        switch (userInfo.getGender()) {
            case 0:
                mIvGander.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mIvGander.setVisibility(View.VISIBLE);
                mIvGander.setImageResource(R.mipmap.userinfo_icon_male);
                break;
            case 2:
                mIvGander.setVisibility(View.VISIBLE);
                mIvGander.setImageResource(R.mipmap.userinfo_icon_female);
                break;
            default:
                break;
        }

        mTvSummary.setText(userInfo.getDesc());
        mTvScore.setText(String.format("%s  %d", getString(R.string.user_score), userInfo.getScore()));
        mTvStweetCount.setText(userInfo.getTweetCount() + "");
        mTvFavoriteCount.setText(userInfo.getCollectCount() + "");
        mTvFollowCount.setText(userInfo.getFollowCount() + "");
        mTvFollowerCount.setText(userInfo.getFansCount() + "");

    }

    private void setNotice() {

        if (MainActivity.mNotice != null) {

            Notice notice = MainActivity.mNotice;
            int atmeCount = notice.getAtmeCount();// @我
            int msgCount = notice.getMsgCount();// 留言
            int reviewCount = notice.getReviewCount();// 评论
            int newFansCount = notice.getNewFansCount();// 新粉丝
            int newLikeCount = notice.getNewLikeCount();// 获得点赞
            int activeCount = atmeCount + reviewCount + msgCount + newFansCount + newLikeCount;//
            // 信息总数
            if (activeCount > 0) {
                mMesCount.show();
                mMesCount.setText(" ");
            } else {
                mMesCount.hide();
            }

        } else {
            mMesCount.hide();
        }

    }

    private void sendRequestData() {
        OSChinaApi.getUserInfo(textHandler);
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
                // float y = mCiOrtrait.getY();
                // int ciOrtraitWidth = mCiOrtrait.getWidth();
                int ciOrtraitHeight = mCiOrtrait.getHeight();

                float px = x + +rlShowInfoX + (width >> 1);
                float py = (height >> 1) - ciOrtraitHeight / 2 + 48;
                int radius = (width >> 1);

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
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ------------>");
        NoticeManager.bindNotify(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        NoticeManager.unBindNotify(this);
        Log.d(TAG, "onPause: ------->");
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mMesCount = new BadgeView(getActivity(), mMesView);
        mMesCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        mMesCount.setBadgePosition(BadgeView.POSITION_CENTER);
        mMesCount.setGravity(Gravity.CENTER);
        mMesCount.setBackgroundResource(R.mipmap.notification_bg);
        //mMesCount.hide(true);
        mMesCount.show(true);

        mMesCountForFans = new BadgeView(getActivity(), mFansView);
        mMesCountForFans.setTextSize(TypedValue.COMPLEX_UNIT_SP, 4);
        mMesCountForFans.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        mMesCountForFans.setGravity(Gravity.CENTER);
        mMesCountForFans.setBackgroundResource(R.mipmap.notification_bg);
        mMesCountForFans.show(true);

        initSolar();
    }

    @Override
    protected void initData() {
        super.initData();
        sendRequestData();
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
            if (!AppContext.getInstance().isLogin()) {
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
                    showClickAvatar();
                    break;
                case R.id.rl_show_my_info:
                    //显示我的资料
//                    UIHelper.showUserCenter(getActivity(), AppContext.getInstance()
//                            .getLoginUid(), AppContext.getInstance().getLoginUser()
//                            .getName());
                    UIHelper.showSimpleBack(getActivity(),
                            SimpleBackPage.MY_INFORMATION_DETAIL);
                    break;
                case R.id.ly_tweet:
                    UserTweetActivity.show(getActivity(), AppContext.getInstance().getLoginUid());
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
                    hideMesCount();
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
        DialogHelp.getSelectDialog(getActivity(), "选择操作", getResources().getStringArray(R.array.avatar_option), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    handleSelectPicture();
                } else {
                    UIHelper.showUserAvatar(getActivity(), mInfo.getPortrait());
                }
            }
        }).show();
    }

    private void handleSelectPicture() {
        DialogHelp.getSelectDialog(getActivity(), "选择图片", getResources().getStringArray(R.array.choose_picture),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        goToSelectPicture(i);
                    }
                }).show();
    }

    private void goToSelectPicture(int position) {
        switch (position) {
            case ACTION_TYPE_ALBUM:
                startImagePick();
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
    private void startImagePick() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),
                    ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),
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

    private void startTakePhoto() {
        Intent intent;
        // 判断是否挂载了SD卡
        String savePath = "";
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            savePath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/oschina/Camera/";
            File savedir = new File(savePath);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        }

        // 没有挂载SD卡，无法保存文件
        if (StringUtils.isEmpty(savePath)) {
            AppContext.showToastShort("无法保存照片，请检查SD卡是否挂载");
            return;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        String fileName = "osc_" + timeStamp + ".jpg";// 照片命名
        File out = new File(savePath, fileName);
        Uri uri = Uri.fromFile(out);
        origUri = uri;

        String theLarge = savePath + fileName;

        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
    }

    // 裁剪头像的绝对路径
    private Uri getUploadTempFile(Uri uri) {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File savedir = new File(FILE_SAVEPATH);
            if (!savedir.exists()) {
                boolean mkdirs = savedir.mkdirs();
            }
        } else {
            AppContext.showToast("无法保存上传的头像，请检查SD卡是否挂载");
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(uri);

        // 如果是标准Uri
        if (TextUtils.isEmpty(thePath)) {
            thePath = ImageUtils.getAbsoluteImagePath(getActivity(), uri);
        }
        String ext = FileUtil.getFileFormat(thePath);

        ext = TextUtils.isEmpty(ext) ? "jpg" : ext;
        // 照片命名
        String cropFileName = "osc_crop_" + timeStamp + "." + ext;
        // 裁剪头像的绝对路径
        protraitPath = FILE_SAVEPATH + cropFileName;
        protraitFile = new File(protraitPath);

        return Uri.fromFile(protraitFile);
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

    protected ProgressDialog showWaitDialog(String str) {
        FragmentActivity activity = getActivity();
        if (activity instanceof DialogControl) {
            return ((DialogControl) activity).showWaitDialog(str);
        }
        return null;
    }

    protected void hideWaitDialog() {
        FragmentActivity activity = getActivity();
        if (activity instanceof DialogControl) {
            ((DialogControl) activity).hideWaitDialog();
        }
    }

    /**
     * 上传新照片
     */
    private void uploadNewPhoto() {
        showWaitDialog("正在上传头像...");

        // 获取头像缩略图
        if (!TextUtils.isEmpty(protraitPath) && protraitFile.exists()) {
            protraitBitmap = ImageUtils
                    .loadImgThumbnail(protraitPath, 200, 200);
        } else {
            AppContext.showToast("图像不存在，上传失败");
        }
        if (protraitBitmap != null) {

            try {
                OSChinaApi.updatePortrait(AppContext.getInstance().getLoginUid(), protraitFile,
                        new AsyncHttpResponseHandler() {

                            @Override
                            public void onSuccess(int arg0, Header[] arg1,
                                                  byte[] arg2) {
                                Result res = (Result) XmlUtils.toBean(ResultBean.class, new ByteArrayInputStream(arg2)).getResult();
                                if (res.OK()) {
                                    AppContext.showToast("更换成功");
                                    // 显示新头像
                                    mCiOrtrait.setImageBitmap(protraitBitmap);
                                    isChangeFace = true;
                                } else {
                                    AppContext.showToast(res.getErrorMessage());
                                }
                            }

                            @Override
                            public void onFailure(int arg0, Header[] arg1,
                                                  byte[] arg2, Throwable arg3) {
                                AppContext.showToast("更换头像失败");
                            }

                            @Override
                            public void onFinish() {
                                hideWaitDialog();
                            }

                        });
            } catch (FileNotFoundException e) {
                AppContext.showToast("图像不存在，上传失败");
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnIntent) {
        //  super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;

        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
                startActionCrop(origUri);// 拍照后裁剪
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
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onNoticeArrived(NoticeBean bean) {

        // Log.d(TAG, "onNoticeArrived: ---->bean=" + bean.toString());
        int allCount = bean.getAllCount();
        if (allCount > 0) {
            int fans = bean.getFans();
            int tempCount = Integer.parseInt(mTvFollowerCount.getText().toString(), 2);
            int soCount = tempCount + fans;
            mTvFollowerCount.setText(soCount + "");
            showMesCount();
        }

    }

    /**
     * show MesCount
     */
    private void showMesCount() {
        if (mMesCount != null) {
            boolean shown = mMesCount.isShown();
            if (!shown) {
                mMesCount.show(true);
            }
        }
    }

    /**
     * hide MesCount
     */
    private void hideMesCount() {
        if (mMesCount != null) {
            boolean shown = mMesCount.isShown();
            if (shown) {
                mMesCount.hide(true);
            }
        }
    }

    private class CacheTask extends AsyncTask<String, Void, User> {
        private final WeakReference<Context> mContext;

        private CacheTask(Context context) {
            mContext = new WeakReference<>(context);
        }

        @Override
        protected User doInBackground(String... params) {
            Serializable seri = CacheManager.readObject(mContext.get(),
                    params[0]);
            if (seri == null) {
                return null;
            } else {
                return (User) seri;
            }
        }

        @Override
        protected void onPostExecute(User info) {
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
