package net.oschina.app.improve.main.banner;

import android.view.View;

/**
 * Created by haibin
 * on 17/3/1.
 */

public class ScaleTransform implements BannerView.BannerTransformer {
    @Override
    public void curPageTransform(final View cur, final View pre, final View next, boolean isScrollBack, int parentWidth, int currX, int mTotalScrollX, boolean isToLeft) {
        int width = parentWidth / 2;

        int curHeight = cur.getHeight();
        int preHeight = pre.getHeight();

        int absX = Math.abs((mTotalScrollX - currX) % width);
        int diff = curHeight - preHeight;
        float add = Math.abs((float) (width - absX) * (float) diff / width);
        float curSy = (preHeight + add) / (float) curHeight;

        if (isScrollBack) {
            cur.setScaleY(curSy);
        } else {
            absX = parentWidth - Math.abs((mTotalScrollX - currX));
            add = Math.abs((float) absX * (float) diff / parentWidth);
            curSy = (preHeight + add) / (float) curHeight;
            cur.setScaleY(curSy);
            if (isToLeft) {// -1{
                next.setScaleY(1);
            } else {
                pre.setScaleY(1);
            }
        }
    }

    @Override
    public void onScroll(View cur, View pre, View next, int parentWidth, int dx) {
        int width = parentWidth / 2;

        int curHeight = cur.getHeight();
        int preHeight = pre.getHeight();

        float totalSy = (float) preHeight / curHeight;
        int diff = curHeight - preHeight;
        float add = Math.abs((float) dx * (float) diff / width);
        float curSy = (curHeight - add) / (float) curHeight;
        if (dx < 0) {//右滑-1
            if (Math.abs(dx) <= width) {
                if (curSy < totalSy)
                    curSy = totalSy;
                cur.setScaleY(curSy);
            } else {
                add = Math.abs((float) (Math.abs(dx) - width) * (float) diff / width);
                curSy = (preHeight + add) / (float) preHeight;
                pre.setScaleY(curSy);
            }
        }
        if (dx > 0) {//左滑+1
            if (Math.abs(dx) <= width) {
                if (curSy < totalSy)
                    curSy = totalSy;
                cur.setScaleY(curSy);
            } else {
                add = Math.abs((float) (Math.abs(dx) - width) * (float) diff / width);
                curSy = (preHeight + add) / (float) preHeight;
                next.setScaleY(curSy);
            }
        }
    }
}
