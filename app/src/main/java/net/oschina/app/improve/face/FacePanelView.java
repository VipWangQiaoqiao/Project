package net.oschina.app.improve.face;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.oschina.app.R;
import net.oschina.app.emoji.DisplayRules;
import net.oschina.app.emoji.Emojicon;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */

public class FacePanelView extends LinearLayout implements View.OnClickListener, FaceRecyclerView.OnFaceClickListener {
    private ViewPager mPager;
    private FacePanelListener mListener;

    public FacePanelView(Context context) {
        super(context);
        init();
    }

    public FacePanelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FacePanelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FacePanelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        inflate(getContext(), R.layout.lay_face_panel, this);

        mPager = (ViewPager) findViewById(R.id.view_pager);
        findViewById(R.id.tv_qq).setOnClickListener(this);
        findViewById(R.id.tv_emoji).setOnClickListener(this);
        findViewById(R.id.btn_del).setOnClickListener(this);

        mPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {
                final FaceRecyclerView view = createRecyclerView();
                bindRecyclerViewData(view, position);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                if (object instanceof FaceRecyclerView) {
                    container.removeView((FaceRecyclerView) object);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_qq:
                mPager.setCurrentItem(0);
                break;
            case R.id.tv_emoji:
                mPager.setCurrentItem(1);
                break;
            case R.id.btn_del:
                onDeleteClick();
                break;
        }
    }

    protected FaceRecyclerView createRecyclerView() {
        return new FaceRecyclerView(getContext(), this);
    }

    protected FaceRecyclerView bindRecyclerViewData(FaceRecyclerView view, final int position) {
        view.setData(DisplayRules.getAllByType(position));
        return view;
    }

    protected void onDeleteClick() {
        FacePanelListener listener = mListener;
        if (listener != null)
            listener.onDeleteClick();
    }

    @Override
    public void onFaceClick(Emojicon v) {
        FacePanelListener listener = mListener;
        if (listener != null)
            listener.onFaceClick(v);
    }

    public void setListener(FacePanelListener listener) {
        mListener = listener;
    }

    public interface FacePanelListener extends FaceRecyclerView.OnFaceClickListener {
        void onDeleteClick();
    }

}
