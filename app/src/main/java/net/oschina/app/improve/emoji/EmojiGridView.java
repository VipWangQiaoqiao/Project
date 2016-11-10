package net.oschina.app.improve.emoji;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import net.oschina.app.emoji.DisplayRules;
import net.oschina.app.emoji.EmojiGridAdapter;
import net.oschina.app.emoji.Emojicon;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.emoji.KJEmojiConfig;
import net.oschina.app.emoji.KJEmojiFragment;
import net.oschina.app.emoji.OnEmojiClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haibin
 * on 2016/11/10.
 */

public class EmojiGridView extends GridView {
    private List<Emojicon> datas;
    private EmojiGridAdapter adapter;
    private OnEmojiClickListener listener;
    private EditText mEditText;

    public EmojiGridView(Context context, EditText editText) {
        super(context);
        this.mEditText = editText;
        setNumColumns(7);
        init();
    }

    public void setListener(OnEmojiClickListener listener) {
        this.listener = listener;
    }

    private void init() {
        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (listener != null) {
                    listener.onEmojiClick((Emojicon) parent.getAdapter()
                            .getItem(position));
                }
                InputHelper.input2OSC(mEditText, (Emojicon) parent.getAdapter()
                        .getItem(position));
            }
        });
    }

    public void initData(int type) {
        datas = new ArrayList<>();
        datas = DisplayRules.getAllByType(type);

        adapter = new EmojiGridAdapter(getContext(), datas);
        setAdapter(adapter);
    }
}
