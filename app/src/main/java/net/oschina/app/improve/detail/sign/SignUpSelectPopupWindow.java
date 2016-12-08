package net.oschina.app.improve.detail.sign;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;

/**
 * Created by haibin
 * on 2016/12/7.
 */

public class SignUpSelectPopupWindow extends PopupWindow implements View.OnAttachStateChangeListener,
        BaseRecyclerAdapter.OnItemClickListener {
    private StringAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Callback mCallback;

    public SignUpSelectPopupWindow(Context context, Callback callback) {
        super(LayoutInflater.from(context).inflate(R.layout.event_sign_up_popup_selected, null),
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mCallback = callback;

        setAnimationStyle(R.style.popup_anim_style_alpha);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        setFocusable(true);

        View content = getContentView();
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        content.addOnAttachStateChangeListener(this);

        mRecyclerView = (RecyclerView) content.findViewById(R.id.rv_select);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

    }

    @Override
    public void onViewAttachedToWindow(View v) {
        if (mCallback != null)
            mCallback.onShow();
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        if (mCallback != null)
            mCallback.onDismiss();
    }

    @Override
    public void onItemClick(int position, long itemId) {
        if (mCallback != null)
            mCallback.onSelect(this, mAdapter.getItem(position));
    }

    public void setAdapter(StringAdapter adapter) {
        this.mAdapter = adapter;
        mRecyclerView.setAdapter(adapter);
        mAdapter.setOnItemClickListener(this);
    }

    public interface Callback {
        void onSelect(SignUpSelectPopupWindow popupWindow, String value);

        void onDismiss();

        void onShow();
    }
}
