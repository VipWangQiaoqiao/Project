package net.oschina.app.improve.main.banner;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;

/**
 * Created by haibin
 * on 17/2/27.
 */
@SuppressWarnings("all")
public abstract class BaseBannerAdapter {
    private final DataSetObservable mObservable = new DataSetObservable();
    private DataSetObserver mViewPagerObserver;
    public static final int POSITION_UNCHANGED = -1;
    public static final int POSITION_NONE = -2;

    public abstract int getCount();


    public abstract View instantiateItem(int position) ;

    public  void destroyItem(BannerView container,int position,View item){
        container.removeView(item);
    }

    public int getItemPosition(Object object) {
        return -1;
    }

    public void notifyDataSetChanged() {
        synchronized(this) {
            if(this.mViewPagerObserver != null) {
                this.mViewPagerObserver.onChanged();
            }
        }

        this.mObservable.notifyChanged();
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        this.mObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        this.mObservable.unregisterObserver(observer);
    }

    void setViewPagerObserver(DataSetObserver observer) {
        synchronized(this) {
            this.mViewPagerObserver = observer;
        }
    }

}
