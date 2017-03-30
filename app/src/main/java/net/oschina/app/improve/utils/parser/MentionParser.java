package net.oschina.app.improve.utils.parser;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import net.oschina.app.emoji.InputHelper;

/**
 * AT界面
 * Created by haibin
 * on 2017/3/30.
 */

public class MentionParser extends RichTextParser {
    private static MentionParser mInstanse = new MentionParser();

    public static MentionParser getInstance() {
        return mInstanse;
    }

    @Override
    public Spannable parse(Context context, String text) {
        String content;
        if (TextUtils.isEmpty(text)) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            return builder;
        }
        content = text.replaceAll("[\n\\s]+", " ").replaceAll("&nbsp;", " ");
        Spannable spannable = parseOnlyAtUser(context, content);
        spannable = parseOnlyTag(context, spannable);
        spannable = parseOnlyLink(context, spannable);
        spannable = parseOnlyTeamTask(context, spannable);
        spannable = InputHelper.displayEmoji(context.getResources(), spannable);
        return spannable;
    }
}
