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
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.oschina.app.R;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.media.ImageGalleryActivity;

/**
 * Created by JuQiu
 * on 16/8/26.
 */
public class TweetPicturesLayout extends ViewGroup implements View.OnClickListener {
    private Tweet.Image[] mImages;
    private float mVerticalSpacing;
    private float mHorizontalSpacing;
    private int mColumn;

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

    public void setImage(Tweet.Image[] images) {
        if (mImages == images)
            return;
        removeAllImage();
        mImages = images;
        if (images != null && images.length > 0) {
            LayoutInflater inflater = LayoutInflater.from(this.getContext());
            RequestManager requestManager = Glide.with(getContext());
            for (int i = 0; i < images.length; i++) {
                View view = inflater.inflate(R.layout.lay_tweet_image_item, this, false);
                view.setTag(i);
                view.setOnClickListener(this);
                String path = images[i].getThumb();
                BitmapRequestBuilder builder = requestManager.load(path)
                        .asBitmap()
                        .centerCrop()
                        .placeholder(R.color.grey_50)
                        .error(R.mipmap.ic_split_graph);

                if (path.toLowerCase().endsWith("gif")) {
                    builder = builder.diskCacheStrategy(DiskCacheStrategy.SOURCE);
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

    public void removeAllImage() {
        removeAllViews();
        mImages = null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int selfWidth = resolveSize(paddingLeft + paddingRight, widthMeasureSpec);
        final int childCount = getChildCount();

        // Not have child we can only need padding size
        if (childCount == 0) {
            setMeasuredDimension(selfWidth, resolveSize(paddingTop + paddingBottom, heightMeasureSpec));
            return;
        }

        // Get child size
        final float contentWidth = selfWidth - paddingRight - paddingLeft - mHorizontalSpacing * (mColumn - 1);
        final int childSize = (int) (contentWidth / mColumn);

        // Measure all child
        for (int i = 0; i < childCount; ++i) {
            View childView = getChildAt(i);
            childView.measure(MeasureSpec.makeMeasureSpec(childSize, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(childSize, MeasureSpec.EXACTLY));
        }

        int lines = (int) (childCount / (float) mColumn + 0.9);

        int wantedHeight = (int) (lines * childSize +
                mVerticalSpacing * (lines - 1) +
                paddingBottom + paddingTop);
        wantedHeight = resolveSize(wantedHeight, heightMeasureSpec);
        setMeasuredDimension(selfWidth, wantedHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int myWidth = r - l;

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();

        int childLeft = paddingLeft;
        int childTop = paddingTop;

        int lineHeight = 0;

        for (int i = 0, childCount = getChildCount(); i < childCount; ++i) {
            View childView = getChildAt(i);

            if (childView.getVisibility() == View.GONE) {
                continue;
            }

            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();

            lineHeight = Math.max(childHeight, lineHeight);

            if (childLeft + childWidth + paddingRight > myWidth) {
                childLeft = paddingLeft;
                childTop += mVerticalSpacing + lineHeight;
                lineHeight = childHeight;
            }
            childView.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            childLeft += childWidth + mHorizontalSpacing;
        }
    }

    @Override
    public void onClick(View v) {
        Tweet.Image[] images = mImages;
        if (images == null)
            return;

        Object obj = v.getTag();
        if (obj == null || !(obj instanceof Integer))
            return;

        int index = (int) obj;
        if (index < 0)
            index = 0;
        if (index >= images.length)
            index = images.length - 1;

        ImageGalleryActivity.show(getContext(), Tweet.Image.getImagePath(images), index);
    }
}
