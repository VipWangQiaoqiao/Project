package net.oschina.app.improve.media;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.media.adapter.ImageAdapter;
import net.oschina.app.improve.media.adapter.ImageFolderAdapter;
import net.oschina.app.improve.media.bean.Image;
import net.oschina.app.improve.media.bean.ImageFolder;
import net.oschina.app.improve.media.config.CommonUtil;
import net.oschina.app.improve.media.config.ImageConfig;
import net.oschina.app.improve.media.config.ImageLoaderListener;
import net.oschina.app.improve.media.contract.SelectImageContract;
import net.qiujuer.genius.ui.Ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by huanghaibin_dev
 * on 2016/7/15.
 */
public class SelectImageFragment extends BaseFragment implements SelectImageContract.View, View.OnClickListener, ImageLoaderListener {
    @Bind(R.id.rv_image)
    RecyclerView mContentView;
    @Bind(R.id.btn_title_select)
    Button mSelectFolderView;
    @Bind(R.id.toolbar)
    View mToolbar;
    @Bind(R.id.btn_done)
    Button mDoneView;

    private ImageFolderPopupWindow mFolderPopupWindow;
    private ImageFolderAdapter mImageFolderAdapter;
    private ImageAdapter mImageAdapter;

    private List<Image> mSelectedImage;
    private static ImageConfig mConfig;

    private String mCamImageName;
    private LoaderListener mCursorLoader = new LoaderListener();

    private SelectImageContract.Operator mOperator;

    public static SelectImageFragment getInstance(ImageConfig config) {
        mConfig = config;
        return new SelectImageFragment();
    }

    @Override
    public void onAttach(Context context) {
        this.mOperator = (SelectImageContract.Operator) context;
        this.mOperator.setDataView(this);
        super.onAttach(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_select_image;
    }

    @OnClick({R.id.btn_preview, R.id.icon_back, R.id.btn_title_select, R.id.btn_done})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                mOperator.onBack();
                break;
            case R.id.btn_preview:
                if (mSelectedImage.size() > 0) {
                    ImageGalleryActivity.show(getActivity(), CommonUtil.toArray(mSelectedImage), 0, false);
                }
                break;
            case R.id.btn_title_select:
                showPopupFolderList();
                break;
            case R.id.btn_done:
                onSelectComplete();
                break;
        }
    }

    @Override
    protected void initWidget(View view) {
        mContentView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        mContentView.addItemDecoration(new SpaceGridItemDecoration((int) Ui.dipToPx(getResources(), 1)));
        mImageAdapter = new ImageAdapter(getActivity());
        mImageAdapter.setSelectMode(mConfig.getSelectMode());
        mImageFolderAdapter = new ImageFolderAdapter(getActivity());
        if (mConfig == null) mConfig = ImageConfig.Build();
        mImageAdapter.setLoader(this);
        mImageFolderAdapter.setLoader(this);
        mContentView.setAdapter(mImageAdapter);
    }

    @Override
    protected void initData() {
        mSelectedImage = new ArrayList<>();
        if (mConfig.getSelectMode() == ImageConfig.SelectMode.MULTI_MODE) {
            if (mConfig.getSelectedImage() != null && mConfig.getSelectedImage().size() != 0) {
                for (String s : mConfig.getSelectedImage()) {
                    Image image = new Image();
                    image.setSelect(true);
                    image.setPath(s);
                    mSelectedImage.add(image);
                }
            }
        }

        mImageAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, long itemId) {
                if (mConfig.getMediaMode() == ImageConfig.MediaMode.HAVE_CAM_MODE) {
                    if (position != 0) {
                        handleImage(position);
                    } else {
                        if (mSelectedImage.size() < mConfig.getSelectCount()) {
                            mOperator.requestCamera();
                        } else {
                            Toast.makeText(getActivity(), "最多只能选择 " + mConfig.getSelectCount() + " 张图片", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    handleImage(position);
                }
            }
        });

        getActivity().getSupportLoaderManager().initLoader(0, null, mCursorLoader);
    }

    private void handleImage(int position) {
        Image image = mImageAdapter.getItem(position);
        //如果是多选模式
        if (mConfig.getSelectMode() == ImageConfig.SelectMode.MULTI_MODE) {
            if (image.isSelect()) {
                image.setSelect(false);
                mSelectedImage.remove(image);
                mImageAdapter.updateItem(position);
            } else {
                if (mSelectedImage.size() == mConfig.getSelectCount()) {
                    Toast.makeText(getActivity(), "最多只能选择 " + mConfig.getSelectCount() + " 张照片", Toast.LENGTH_SHORT).show();
                } else {
                    image.setSelect(true);
                    mSelectedImage.add(image);
                    mImageAdapter.updateItem(position);
                }
            }
        } else {
            mSelectedImage.add(image);
            handleResult();
        }
        int size = mSelectedImage.size();
        mDoneView.setText("完成(" + size + ")");
        mDoneView.setEnabled(size > 0);
    }

    private void handleResult() {
        if (mConfig.getCallBack() != null && mSelectedImage.size() != 0) {
            mConfig.getCallBack().doBack(CommonUtil.toArrayList(mSelectedImage));
            getActivity().finish();
        }
    }

    /**
     * 完成选择
     */
    public void onSelectComplete() {
        handleResult();
    }

    /**
     * 申请相机权限成功
     */
    @Override
    public void onOpenCameraSuccess() {
        toOpenCamera();
    }

    /**
     * 申请读取存储成功
     */
    @Override
    public void onReadExternalStorageSuccess() {
        getLoaderManager().initLoader(0, null, mCursorLoader);
    }

    @Override
    public void onCameraPermissionDenied() {

    }

    @Override
    public void onExternalStorageDenied() {

    }

    /**
     * 创建弹出的相册
     */
    private void showPopupFolderList() {
        if (mFolderPopupWindow == null) {
            mFolderPopupWindow = new ImageFolderPopupWindow(getActivity());
            mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mFolderPopupWindow.setAdapter(mImageFolderAdapter);
            mFolderPopupWindow.setOutsideTouchable(true);
            mFolderPopupWindow.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(final int position, long itemId) {
                    mImageAdapter.clear();
                    if (mConfig != null && mConfig.getMediaMode() == ImageConfig.MediaMode.HAVE_CAM_MODE) {
                        if (position != 0) {
                            Image cam = new Image();
                            mImageAdapter.addItem(cam);
                        }
                    }
                    mImageAdapter.addAll(mImageFolderAdapter.getItem(position).getImages());
                    mContentView.scrollToPosition(0);
                    mFolderPopupWindow.dismiss();
                }
            });
        }
        mFolderPopupWindow.showAsDropDown(mToolbar);
    }

    /**
     * 打开相机
     */
    private void toOpenCamera() {
        // 判断是否挂载了SD卡
        mCamImageName = null;
        String savePath = "";
        if (CommonUtil.hasSDCard()) {
            savePath = CommonUtil.getCameraPath();
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
        }

        // 没有挂载SD卡，无法保存文件
        if (TextUtils.isEmpty(savePath)) {
            Toast.makeText(getActivity(), "无法保存照片，请检查SD卡是否挂载", Toast.LENGTH_LONG).show();
            return;
        }

        mCamImageName = CommonUtil.getSaveImageFullName();
        File out = new File(savePath, mCamImageName);
        Uri uri = Uri.fromFile(out);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent,
                0x03);
    }

    /**
     * 拍照完成通知系统添加照片
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param data        data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0x03 && mCamImageName != null) {
            Uri localUri = Uri.fromFile(new File(CommonUtil.getCameraPath() + mCamImageName));
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            getActivity().sendBroadcast(localIntent);
        }
    }

    @Override
    public void displayImage(ImageView iv, String path) {
        getImgLoader().load(path)
                .placeholder(R.color.grey_200)
                .error(R.mipmap.ic_default_image)
                .into(iv);
    }

    private class LoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {
        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.MINI_THUMB_MAGIC,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id == 0) {
                //数据库光标加载器
                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        null, null, IMAGE_PROJECTION[2] + " DESC");//倒叙排列
                return cursorLoader;
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                ArrayList<Image> images = new ArrayList<>();
                if (mConfig != null && mConfig.getMediaMode() == ImageConfig.MediaMode.HAVE_CAM_MODE) {
                    Image cam = new Image();
                    images.add(cam);
                }
                List<ImageFolder> imageFolders = new ArrayList<>();

                ImageFolder defaultFolder = new ImageFolder();
                defaultFolder.setName("全部照片");
                defaultFolder.setPath("");
                imageFolders.add(defaultFolder);

                int count = data.getCount();
                if (count > 0) {
                    data.moveToFirst();
                    do {
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        int id = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                        String thumbPath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                        String bucket = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));

                        Image image = new Image();
                        image.setPath(path);
                        image.setName(name);
                        image.setDate(dateTime);
                        image.setId(id);
                        image.setThumbPath(thumbPath);
                        image.setFolderName(bucket);
                        images.add(image);

                        //如果是新拍的照片
                        if (mCamImageName != null && mCamImageName.toLowerCase().equals(image.getName().toLowerCase())) {
                            image.setSelect(true);
                            mSelectedImage.add(image);
                        }

                        //如果是被选中的图片
                        if (mSelectedImage.size() > 0) {
                            for (Image i : mSelectedImage) {
                                if (i.getPath().equals(image.getPath())) {
                                    image.setSelect(true);
                                }
                            }
                        }

                        File imageFile = new File(path);
                        File folderFile = imageFile.getParentFile();
                        ImageFolder folder = new ImageFolder();
                        folder.setName(folderFile.getName());
                        folder.setPath(folderFile.getAbsolutePath());
                        if (!imageFolders.contains(folder)) {
                            ArrayList<Image> imageList = new ArrayList<>();
                            imageList.add(image);
                            folder.setImages(imageList);
                            folder.setAlbumPath(image.getPath());//默认相册封面
                            imageFolders.add(folder);
                        } else {
                            // 更新
                            ImageFolder f = imageFolders.get(imageFolders.indexOf(folder));
                            f.getImages().add(image);
                        }


                    } while (data.moveToNext());
                }
                mImageAdapter.resetItem(images);
                defaultFolder.setImages(images);
                if (mConfig.getMediaMode() == ImageConfig.MediaMode.HAVE_CAM_MODE) {
                    defaultFolder.setAlbumPath(images.size() > 1 ? images.get(1).getPath() : null);
                } else {
                    defaultFolder.setAlbumPath(images.size() > 0 ? images.get(0).getPath() : null);
                }
                mImageFolderAdapter.resetItem(imageFolders);

                //删除掉不存在的，在于用户选择了相片，又去相册删除
                if (mSelectedImage.size() > 0) {
                    List<Image> rs = new ArrayList<>();
                    for (Image i : mSelectedImage) {
                        File f = new File(i.getPath());
                        if (!f.exists()) {
                            rs.add(i);
                        }
                    }
                    mSelectedImage.removeAll(rs);
                }


                //btn_preview.setText("预览(" + mSelectedImage.size() + ")");
                if (mConfig != null && mConfig.getSelectMode() == ImageConfig.SelectMode.SINGLE_MODE && mCamImageName != null) {
                    handleResult();
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}
