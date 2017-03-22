package net.oschina.app.improve.utils.parser;

import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;

/**
 * 动弹富文本解析
 * Created by haibin
 * on 2017/3/21.
 */

public class TweetParser extends RichTextParser {
    @Override
    public Spannable parse(Context context, String content) {
        if (TextUtils.isEmpty(content))
            return null;
        return null;
    }
}
