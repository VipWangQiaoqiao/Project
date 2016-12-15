package net.oschina.app.improve.widget.banner;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

/**
 * Created by haibin
 * on 2016/11/26.
 */
public class ZoomTransformer implements PageTransformer {
    private static final float MIN_SCALE = 0.85f;

    private static final float MIN_ALPHA = 0.5f;

    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        if (position < -1) {
            view.setAlpha(0);
            view.setTranslationX(0);
        } else if (position <= 1) {
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)
                    / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
        } else {
            view.setAlpha(0);
            view.setTranslationX(0);
        }
    }
}