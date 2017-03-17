package net.oschina.app.improve.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.bitmap.StreamBitmapDecoder;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.bumptech.glide.load.resource.transcode.BitmapToGlideDrawableTranscoder;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;

import net.oschina.app.R;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.util.TLog;

import java.io.IOException;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static android.util.TypedValue.applyDimension;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class PortraitView extends CircleImageView {
    public PortraitView(Context context) {
        super(context);
    }

    public PortraitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PortraitView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public static void setup(ImageView imageView, Author author) {
        if (imageView != null && imageView instanceof PortraitView) {
            ((PortraitView) imageView).setup(author);
        } else {
            TLog.error("PortraitView con't setup with:" +
                    (imageView == null ? "null" : imageView.getClass().getSimpleName()));
        }
    }

    public void setup(Author author) {
        if (author == null)
            return;
        setup(author.getName(), author.getPortrait());
    }

    public void setup(final String name, final String path) {
        final Context context = getContext();
        if (context == null)
            return;
        Glide.with(context)
                .load("")
                .asBitmap()
                .error(R.mipmap.widget_default_face)
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        target.getSize(new SizeReadyCallback() {
                            @Override
                            public void onSizeReady(int width, int height) {
                                Bitmap bitmap = buildSrcFromName(name, width, height);
                                setImageBitmap(bitmap);
                            }
                        });
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(this);
    }

    private Bitmap buildSrcFromName(final String name, int w, int h) {
        if (w == Target.SIZE_ORIGINAL || w <= 0)
            w = 100;
        if (h == Target.SIZE_ORIGINAL || h <= 0)
            h = 100;

        final String firstChar = (TextUtils.isEmpty(name) ? "*" : name.substring(0, 1)).toUpperCase();

        BitmapPool pool = Glide.get(getContext()).getBitmapPool();
        Bitmap bitmap = pool.getDirty(w, h, Bitmap.Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(applyDimension(COMPLEX_UNIT_SP, 40, getResources().getDisplayMetrics()));
        paint.setTypeface(Typeface.SANS_SERIF);


        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLUE);
        canvas.drawText(firstChar, canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f + (paint.getTextSize() / 2.0f), paint);
        return bitmap;
    }


}
