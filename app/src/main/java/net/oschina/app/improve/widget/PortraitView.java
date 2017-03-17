package net.oschina.app.improve.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;

import net.oschina.app.R;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.util.TLog;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class PortraitView extends CircleImageView {
    private static final String TAG = PortraitView.class.getSimpleName();

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

    public void setup(final String name, String path) {
        final Context context = getContext();
        if (context == null)
            return;

        if ("http://www.oschina.net/img/portrait.gif".equalsIgnoreCase(path)) {
            path = "";
        }

        Glide.with(context)
                .load(path)
                .asBitmap()
                .error(R.mipmap.widget_default_face)
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        target.getSize(new SizeReadyCallback() {
                            @Override
                            public void onSizeReady(int width, int height) {
                                final String firstChar = (TextUtils.isEmpty(name) ? "*" : name.substring(0, 1)).toUpperCase();
                                Bitmap bitmap = buildSrcFromName(firstChar, width, height);
                                setScaleType(ScaleType.CENTER_CROP);
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

    @SuppressWarnings("ResourceAsColor")
    private Bitmap buildSrcFromName(final String firstChar, int w, int h) {
        if (w == Target.SIZE_ORIGINAL || w <= 0)
            w = 100;
        if (h == Target.SIZE_ORIGINAL || h <= 0)
            h = 100;

        final int size = Math.max(Math.min(Math.min(w, h), 320), 160);
        final float fontSize = size * 0.5f;
        log("size:" + size + " fontSize:" + fontSize);

        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);

        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(fontSize);
        paint.setTypeface(Typeface.SANS_SERIF);
        Typeface typeface = getFont(getContext(), "Numans-Regular.otf");
        if (typeface != null)
            paint.setTypeface(typeface);

        Rect rect = new Rect();
        paint.getTextBounds(firstChar, 0, 1, rect);
        int fontHeight = rect.height();
        log(rect.toString());

        int fontHalfH = fontHeight >> 1;
        int centerX = bitmap.getWidth() >> 1;
        int centerY = bitmap.getHeight() >> 1;

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(getBackgroundColor(firstChar));
        canvas.drawText(firstChar, centerX, centerY + fontHalfH, paint);

        paint.setStrokeWidth(6);
        paint.setColor(Color.BLACK);
        canvas.drawPoint(centerX, centerY, paint);

        paint.setColor(Color.BLACK);
        canvas.drawPoint(centerX, centerY + fontHalfH, paint);

        return bitmap;
    }

    private static int getBackgroundColor(String firstChar) {
        int hashCode = firstChar.hashCode();
        int len = Color.COLORS.length;
        return Color.COLORS[hashCode % (len - 1)];
    }

    public static Typeface getFont(Context context, String fontFile) {
        String fontPath = "fonts/" + fontFile;

        try {
            return Typeface.createFromAsset(context.getAssets(), fontPath);
        } catch (Exception var4) {
            log("Font file at " + fontPath + " cannot be found or the file is not a valid font file.");
            return null;
        }
    }

    private static void log(String args) {
        TLog.e(TAG, args);
    }

    static class Color {
        static final int WHITE = -1;
        static final int BLACK = -16777216;
        static final int RED = -1762269;
        static final int PINK = -1499549;
        static final int PURPLE = -6543440;
        static final int DEEP_PURPLE = -10011977;
        static final int INDIGO = -12627531;
        static final int BLUE = -11110404;
        static final int LIGHT_PINK = -16537100;
        static final int CYAN = -16728876;
        static final int TEAL = -16738680;
        static final int GREEN = -14312668;
        static final int LIGHT_GREEN = -7617718;
        static final int LIME = -3285959;
        static final int YELLOW = -5317;
        static final int AMBER = -16121;
        static final int ORANGE = -26624;
        static final int DEEP_ORANGE = -43230;
        static final int BROWN = -8825528;
        static final int GREY = -6381922;
        static final int BLUE_GREY = -10453621;
        static final int[] COLORS = new int[]{RED, PINK, PURPLE, DEEP_PURPLE, INDIGO,
                BLUE, LIGHT_PINK, CYAN, TEAL, GREEN, LIGHT_GREEN, LIME, YELLOW, AMBER,
                ORANGE, DEEP_ORANGE, BROWN, GREY, BLUE_GREY};
    }

}
