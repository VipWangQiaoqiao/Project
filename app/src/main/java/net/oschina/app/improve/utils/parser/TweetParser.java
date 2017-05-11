package net.oschina.app.improve.utils.parser;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import net.oschina.app.emoji.InputHelper;
import net.oschina.app.util.HTMLUtil;

import java.util.regex.Matcher;

/**
 * 动弹富文本解析
 * Created by haibin
 * on 2017/3/21.
 */

public class TweetParser extends RichTextParser {
    private static TweetParser mInstance = new TweetParser();

    public static TweetParser getInstance() {
        return mInstance;
    }

    @Override
    public Spannable parse(Context context, String content) {
        if (TextUtils.isEmpty(content))
            return null;
        content = HTMLUtil.rollbackReplaceTag(content);
        Spannable spannable = parseOnlyAtUser(context, content);
        spannable = parseOnlyGist(context, spannable);
        spannable = parseOnlyGit(context, spannable);
        spannable = parseOnlyTag(context, spannable);
        spannable = parseOnlyLink(context, spannable);
        spannable = InputHelper.displayEmoji(context.getResources(), spannable);
        return spannable;
    }

    /**
     * 清空HTML标签
     */
    public Spannable clearHtmlTag(CharSequence content) {
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        Matcher matcher;
        while (true) {
            matcher = PatternHtml.matcher(builder.toString());
            if (matcher.find()) {
                String str = matcher.group(1);
                builder.replace(matcher.start(), matcher.end(), str);
                continue;
            }
            break;
        }
        return builder;
    }
}
