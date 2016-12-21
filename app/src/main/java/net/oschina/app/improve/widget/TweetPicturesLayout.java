package net.oschina.app.improve.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.media.ImageGalleryActivity;
import net.oschina.common.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JuQiu
 * on 16/8/26.
 */
public class TweetPicturesLayout extends ViewGroup implements View.OnClickListener {
    private static final int SINGLE_MAX_W = 120;
    private static final int SINGLE_MAX_H = 180;
    private static final int SINGLE_MIN_W = 34;
    private static final int SINGLE_MIN_H = 34;

    private Tweet.Image[] mImages;
    private float mVerticalSpacing;
    private float mHorizontalSpacing;
    private int mColumn;
    private int mMaxPictureSize;

    public TweetPicturesLayout(Context context) {
        this(context, null);
    }

    public TweetPicturesLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TweetPicturesLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TweetPicturesLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final Context context = getContext();
        final Resources resources = getResources();
        final float density = resources.getDisplayMetrics().density;

        int vSpace = (int) (4 * density);
        int hSpace = vSpace;

        if (attrs != null) {
            // Load attributes
            final TypedArray a = context.obtainStyledAttributes(
                    attrs, R.styleable.TweetPicturesLayout, defStyleAttr, defStyleRes);

            // Load clip touch corner radius
            vSpace = a.getDimensionPixelOffset(R.styleable.TweetPicturesLayout_verticalSpace, vSpace);
            hSpace = a.getDimensionPixelOffset(R.styleable.TweetPicturesLayout_horizontalSpace, hSpace);
            setColumn(a.getInt(R.styleable.TweetPicturesLayout_column, 3));
            setMaxPictureSize(a.getDimensionPixelOffset(R.styleable.TweetPicturesLayout_maxPictureSize, 0));
            a.recycle();
        }

        setVerticalSpacing(vSpace);
        setHorizontalSpacing(hSpace);
    }

    public void setHorizontalSpacing(float pixelSize) {
        mHorizontalSpacing = pixelSize;
    }

    public void setVerticalSpacing(float pixelSize) {
        mVerticalSpacing = pixelSize;
    }

    public void setColumn(int column) {
        if (column < 1)
            column = 1;
        if (column > 20)
            column = 20;
        mColumn = column;
    }

    public void setMaxPictureSize(int maxPictureSize) {
        if (maxPictureSize < 0)
            maxPictureSize = 0;
        mMaxPictureSize = maxPictureSize;
    }

    public void setImage(Tweet.Image[] images) {
        if (mImages == images)
            return;

        // 移除布局
        removeAllImage();

        // 过滤掉不合法的数据
        if (images != null) {
            List<Tweet.Image> isOkImages = new ArrayList<>();
            for (Tweet.Image image : images) {
                if (Tweet.Image.check(image))
                    isOkImages.add(image);
            }
            images = CollectionUtil.toArray(isOkImages, Tweet.Image.class);
        }

        // 赋值
        mImages = images;

        if (mImages != null && mImages.length > 0) {
            LayoutInflater inflater = LayoutInflater.from(this.getContext());
            RequestManager requestManager = Glide.with(getContext());
            for (int i = 0; i < mImages.length; i++) {
                Tweet.Image image = mImages[i];
                if (!Tweet.Image.check(image))
                    continue;

                View view = inflater.inflate(R.layout.lay_tweet_image_item, this, false);
                view.setTag(i);
                view.setOnClickListener(this);
                String path = image.getThumb();
                BitmapRequestBuilder builder = requestManager.load(path)
                        .asBitmap()
                        .centerCrop()
                        //.placeholder(R.color.grey_50)
                        .error(R.mipmap.ic_split_graph);

                if (path.toLowerCase().endsWith("gif")) {
                    //builder = builder.diskCacheStrategy(DiskCacheStrategy.SOURCE);
                    view.findViewById(R.id.iv_is_gif).setVisibility(VISIBLE);
                }
                addView(view);
                builder.into((ImageView) view.findViewById(R.id.iv_picture));
            }

            // all do requestLayout
            if (getVisibility() == VISIBLE) {
                requestLayout();
            } else {
                setVisibility(View.VISIBLE);
            }
        } else {
            setVisibility(View.GONE);
        }
    }

    public void setImage(String[] images) {
        if (images == null || images.length == 0) return;
        Tweet.Image[] ims = new Tweet.Image[images.length];
        for (int i = 0; i < images.length; i++) {
            ims[i] = Tweet.Image.create(images[i]);
        }
        setImage(ims);
    }

    public void removeAllImage() {
        removeAllViews();
        mImages = null;
    }

    private int getMaxChildSize(int size) {
        if (mMaxPictureSize == 0)
            return size;
        else
            return Math.min(mMaxPictureSize, size);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int selfWidth = resolveSize(paddingLeft + paddingRight, widthMeasureSpec);
        int wantedHeight = paddingBottom + paddingTop;
        final int childCount = getChildCount();


        //noinspection StatementWithEmptyBody
        if (childCount == 0) {
            // Not have child we can only need padding size
        } else if (childCount == 1) {
            Tweet.Image image = mImages[0];
            if (Tweet.Image.check(image)) {
                int imageW = image.getW();
                int imageH = image.getH();
                imageW = imageW <= 0 ? 100 : imageW;
                imageH = imageH <= 0 ? 100 : imageH;

                float density = getResources().getDisplayMetrics().density;
                // Get max width and height
                float maxContentW = Math.min(selfWidth - paddingRight - paddingLeft, density * SINGLE_MAX_W);
                float maxContentH = density * SINGLE_MAX_H;

                int childW, childH;

                float hToW = imageH / (float) imageW;
                if (hToW > (maxContentH / maxContentW)) {
                    childH = (int) maxContentH;
                    childW = (int) (maxContentH / hToW);
                } else {
                    childW = (int) maxContentW;
                    childH = (int) (maxContentW * hToW);
                }
                // Check the width and height below Min values
                int minW = (int) (SINGLE_MIN_W * density);
                if (childW < minW)
                    childW = minW;
                int minH = (int) (SINGLE_MIN_H * density);
                if (childH < minH)
                    childH = minH;

                View child = getChildAt(0);
                if (child != null) {
                    child.measure(MeasureSpec.makeMeasureSpec(childW, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(childH, MeasureSpec.EXACTLY));
                    wantedHeight += childH;
                }
            }
        } else {
            // Measure all child
            final float maxContentWidth = selfWidth - paddingRight - paddingLeft - mHorizontalSpacing * (mColumn - 1);
            // Get child size
            final int childSize = getMaxChildSize((int) (maxContentWidth / mColumn));

            for (int i = 0; i < childCount; ++i) {
                View childView = getChildAt(i);
                childView.measure(MeasureSpec.makeMeasureSpec(childSize, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(childSize, MeasureSpec.EXACTLY));
            }

            int lines = (int) (childCount / (float) mColumn + 0.9);
            wantedHeight += (int) (lines * childSize + mVerticalSpacing * (lines - 1));
        }

        setMeasuredDimension(selfWidth, resolveSize(wantedHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        float childCount = getChildCount();
        if (childCount > 0) {
            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();

            if (childCount == 1) {
                View childView = getChildAt(0);
                int childWidth = childView.getMeasuredWidth();
                int childHeight = childView.getMeasuredHeight();
                childView.layout(paddingLeft, paddingTop, paddingLeft + childWidth, paddingTop + childHeight);
            } else {
                int mWidth = r - l;
                int paddingRight = getPaddingRight();

                int lineHeight = 0;
                int childLeft = paddingLeft;
                int childTop = paddingTop;

                for (int i = 0; i < childCount; ++i) {
                    View childView = getChildAt(i);

                    if (childView.getVisibility() == View.GONE) {
                        continue;
                    }

                    int childWidth = childView.getMeasuredWidth();
                    int childHeight = childView.getMeasuredHeight();

                    lineHeight = Math.max(childHeight, lineHeight);

                    if (childLeft + childWidth + paddingRight > mWidth) {
                        childLeft = paddingLeft;
                        childTop += mVerticalSpacing + lineHeight;
                        lineHeight = childHeight;
                    }
                    childView.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
                    childLeft += childWidth + mHorizontalSpacing;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        Tweet.Image[] images = mImages;
        if (images == null || images.length <= 0)
            return;

        Object obj = v.getTag();
        if (obj == null || !(obj instanceof Integer))
            return;

        int index = (int) obj;
        if (index < 0)
            index = 0;
        if (index >= images.length)
            index = images.length - 1;

        Tweet.Image image = images[index];
        if (!Tweet.Image.check(image))
            return;

        String[] paths = Tweet.Image.getImagePath(images);
        if (paths == null || paths.length <= 0)
            return;

        ImageGalleryActivity.show(getContext(), paths, index);
    }
}
