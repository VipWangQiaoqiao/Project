package net.oschina.app.improve.emoji;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.emoji.OnEmojiClickListener;

/**
 * Created by haibin
 * on 2016/11/10.
 */

public class EmojiView extends LinearLayout {
    private ViewPager mEmpjiPager;
    private TextView mQQText;
    private TextView mEmojiText;
    private View mDel;
    private EditText mEditText;
    private OnEmojiClickListener listener;

    public EmojiView(Context context, EditText editText) {
        super(context);
        this.mEditText = editText;
        LayoutInflater.from(context).inflate(R.layout.layout_emoji, this, true);
    }

    public void setListener(OnEmojiClickListener listener) {
        this.listener = listener;
        init();
    }

    private void init() {
        mEmpjiPager = (ViewPager) findViewById(R.id.vp_emoji);
        mQQText = (TextView) findViewById(R.id.tv_qq);
        mEmojiText = (TextView) findViewById(R.id.tv_emoji);
        mDel = findViewById(R.id.btn_del);

        mQQText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmpjiPager.setCurrentItem(0);
            }
        });

        mEmojiText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmpjiPager.setCurrentItem(1);
            }
        });

        mDel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeleteButtonClick(v);
            }
        });

        mEmpjiPager.setAdapter(new PagerAdapter() {
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
                final EmojiRecyclerView view = new EmojiRecyclerView(getContext(), mEditText);
                view.setListener(listener);
                view.initData(position);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                if (object instanceof EmojiRecyclerView) {
                    container.removeView((EmojiRecyclerView) object);
                }
            }
        });
    }
}
