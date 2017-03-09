package net.oschina.app.improve.main.banner;

/**
 * Created by haibin
 * on 2016/11/24.
 */

public interface BannerIndicator extends BannerView.OnBannerChangeListener {
    void bindBannerView(BannerView view);

    void setCurrentItem(int currentItem);

    void setOnViewChangeListener(BannerView.OnBannerChangeListener listener);

    void notifyDataSetChange();
}
