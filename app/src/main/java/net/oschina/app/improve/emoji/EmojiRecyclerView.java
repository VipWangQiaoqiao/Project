package net.oschina.app.improve.emoji;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.EditText;

import net.oschina.app.emoji.DisplayRules;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.emoji.OnEmojiClickListener;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;

/**
 * Created by haibin
 * on 2017/1/20.
 */

 class EmojiRecyclerView extends RecyclerView {
    private EmojiAdapter mAdapter;
    private OnEmojiClickListener listener;
    private EditText mEditText;

     EmojiRecyclerView(Context context, EditText editText) {
        super(context);
        setLayoutManager(new GridLayoutManager(context, 7));
        mAdapter = new EmojiAdapter(context);
        setAdapter(mAdapter);
        this.mEditText = editText;
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, long itemId) {
                if (listener != null) {
                    listener.onEmojiClick(mAdapter.getItem(position));
                }
                InputHelper.input2OSC(mEditText, mAdapter.getItem(position));
            }
        });
    }

    public void initData(int type) {
        mAdapter.addAll(DisplayRules.getAllByType(type));
    }

    public void setListener(OnEmojiClickListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        ViewParent parent = this;
        while (!((parent = parent.getParent()) instanceof ViewPager)) ;
        parent.requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }
}
