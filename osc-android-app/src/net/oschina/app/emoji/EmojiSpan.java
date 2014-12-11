package net.oschina.app.emoji;

import net.oschina.app.AppContext;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;

public class EmojiSpan extends DynamicDrawableSpan {
    private final String value;
    private Drawable mDrawable;
    private final int mSize;
    private final int type;

    public EmojiSpan(String value, int size, int type) {
        this.value = value;
        this.mSize = size;
        this.type = type;
    }

    @Override
    public Drawable getDrawable() {
        if (mDrawable == null) {
            try {
                Emoji emoji = null;
                if (type == 0) {
                    emoji = EmojiHelper.getEmoji(value);
                } else {
                    emoji = EmojiHelper.getEmojiByNumber(value);
                }
                if (emoji != null) {
                    mDrawable = AppContext.getInstance().getResources()
                            .getDrawable(emoji.getResId());
                    // 获取drawable不同分辨率下的大小
                    int size = mDrawable.getIntrinsicWidth() - 20;
                    mDrawable.setBounds(0, 0, size, size);
                }
            } catch (Exception e) {
                // swallow
            }
        }
        return mDrawable;
    }
}
