package net.oschina.app.improve.emoji;

import android.content.Context;
import android.widget.EditText;

import net.oschina.app.emoji.Emojicon;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.improve.face.FacePanelView;
import net.oschina.app.improve.face.FaceRecyclerView;
import net.oschina.app.util.TDevice;

/**
 * Created by haibin
 * on 2016/11/10.
 */

public class EmojiView extends FacePanelView {
    private EditText mEditText;

    public EmojiView(Context context, EditText editText) {
        super(context);
        this.mEditText = editText;
        setListener(new FacePanelListener() {
            @Override
            public void onDeleteClick() {
                InputHelper.backspace(mEditText);
            }

            @Override
            public void hideSoftKeyboard() {
                TDevice.hideSoftKeyboard(mEditText);
            }

            @Override
            public void onFaceClick(Emojicon v) {
                InputHelper.input2OSC(mEditText, v);
            }
        });
    }

    @Override
    protected FaceRecyclerView createRecyclerView() {
        return new EmojiRecyclerView(getContext(), this);
    }
}
