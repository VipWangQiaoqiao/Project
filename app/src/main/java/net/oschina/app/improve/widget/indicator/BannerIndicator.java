package net.oschina.app.improve.widget.indicator;

/**
 * Created by haibin
 * on 2016/11/24.
 */

public interface BannerIndicator extends BannerView.OnViewChangeListener {
    void bindBannerView(BannerView view);

    void setCurrentItem(int currentItem);

    void setOnViewChangeListener(BannerView.OnViewChangeListener listener);

    void notifyDataSetChange();
}
