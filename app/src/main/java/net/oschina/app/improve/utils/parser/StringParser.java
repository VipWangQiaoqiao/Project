package net.oschina.app.improve.utils.parser;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import net.oschina.app.emoji.InputHelper;

/**
 * 只显示文本，不需要富文本点击,如消息、私信列表
 * Created by haibin
 * on 2017/3/22.
 */

public class StringParser extends RichTextParser {
    private static StringParser mInstance = new StringParser();

    public static StringParser getInstance() {
        return mInstance;
    }

    @Override
    public Spannable parse(Context context, String content) {
        String text ;
        if (TextUtils.isEmpty(content)) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            text = "[图片]";
            builder.append(text);
            return builder;
        }
        text = content.replaceAll("[\n\\s]+", " ").replaceAll("<[^<>]+>([^<>]*)</[^<>]+>", "$1");
        return InputHelper.displayEmoji(context.getResources(), text);
    }
}
