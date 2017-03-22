package net.oschina.app.improve.utils.parser;

import android.content.Context;
import android.text.Spannable;

/**
 * 之显示文本，不需要富文本点击,如消息、私信列表
 * Created by haibin
 * on 2017/3/22.
 */

public class StringParser extends RichTextParser {
    @Override
    public Spannable parse(Context context, String content) {
        return null;
    }
}
