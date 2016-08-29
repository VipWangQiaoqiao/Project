package net.oschina.app.improve.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
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

/**
 * Created by JuQiu
 * on 16/8/26.
 */
public class TweetPicturesView extends ViewGroup {
    private Tweet.Image[] mImages;
    private float mVerticalSpacing;
    private float mHorizontalSpacing;

    public TweetPicturesView(Context context) {
        this(context, null);
    }

    public TweetPicturesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TweetPicturesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TweetPicturesView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
                    attrs, R.styleable.FlowLayout, defStyleAttr, defStyleRes);

            // Load clip touch corner radius
            vSpace = a.getDimensionPixelOffset(R.styleable.FlowLayout_verticalSpace, vSpace);
            hSpace = a.getDimensionPixelOffset(R.styleable.FlowLayout_horizontalSpace, hSpace);
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


    public void setImage(Tweet.Image[] images) {
        if (mImages == images)
            return;
        removeAllImage();
        mImages = images;
        if (images != null && images.length > 0) {
            LayoutInflater inflater = LayoutInflater.from(this.getContext());
            RequestManager requestManager = Glide.with(getContext());
            for (int i = 0; i < images.length; i++) {
                ImageView imageView = (ImageView) inflater.inflate(R.layout.lay_tweet_image_item, this, false);
                //imageView.setTag(R.id.iv_tweet_image, i);
                //imageView.setTag(R.id.iv_tweet_face, position);
                //imageView.setOnClickListener(imageClickListener);
                String path = images[i].getThumb();
                BitmapRequestBuilder builder = requestManager.load(path)
                        .asBitmap()
                        .centerCrop()
                        .placeholder(R.color.grey_50)
                        .error(R.mipmap.ic_split_graph);
                if (path.toLowerCase().endsWith("gif")) {
                    builder = builder.diskCacheStrategy(DiskCacheStrategy.SOURCE);
                }
                builder.into(imageView);
                addView(imageView);
            }
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }

    public void addImage(Tweet.Image image) {

    }

    public void removeAllImage() {
        removeAllViews();
        mImages = null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int selfWidth = resolveSize(0, widthMeasureSpec);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int childLeft = paddingLeft;
        int childTop = paddingTop;
        int lineHeight = 0;
        final int contentWidth = selfWidth - paddingRight - paddingLeft;

        //通过计算每一个子控件的高度，得到自己的高度
        for (int i = 0, childCount = getChildCount(); i < childCount; ++i) {
            View childView = getChildAt(i);
            LayoutParams childLayoutParams = childView.getLayoutParams();
            childView.measure(
                    getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight,
                            childLayoutParams.width),
                    getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom,
                            childLayoutParams.height));
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();
            lineHeight = Math.max(childHeight, lineHeight);

            childLeft += childWidth;
            if (childLeft > contentWidth) {
                childLeft = childWidth;
                childTop += mVerticalSpacing + lineHeight;
                lineHeight = childHeight;
            } else {
                childLeft += mHorizontalSpacing;
            }
        }

        int wantedHeight = childTop + lineHeight + paddingBottom;
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

        //根据子控件的宽高，计算子控件应该出现的位置。
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
            Log.e("TAG", "i:" + i + " l:" + childLeft + " t:" + childTop + " r:" + (childLeft + childWidth) + " b:" + (childTop + childHeight));
            childLeft += childWidth + mHorizontalSpacing;
        }

        Log.e("TAG", "w:" + getWidth() + " h:" + getHeight());
    }
}
