package net.oschina.app.improve.utils.parser;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 富文本解析工具
 * Created by haibin
 * on 2017/3/21.
 */
@SuppressWarnings("unused")
public abstract class RichTextParser {

    static final Pattern PatternAtUserWithHtml = Pattern.compile(
            "<a href=['\"]http[s]?://my.oschina.net/(\\w+|u/([0-9]+))['\"][^<>]+>@[^@<>]+</a>"
    );
    static final Pattern PatternAtUser = Pattern.compile(
            "@[^@\\s:]+"
    );

    // #Java#
    static final Pattern PatternSoftwareTagWithHtml = Pattern.compile(
            "<a\\s+href=['\"]([^'\"]*)['\"][^<>]*>(#[^#@<>\\s]+#)</a>"
    );
    static final Pattern PatternSoftwareTag = Pattern.compile(
            "#([^#@<>\\s]+)#"
    );

    // @user links
    @Deprecated
    static final Pattern PatternAtUserAndLinks = Pattern.compile(
            "<a\\s+href=['\"]http://my\\.oschina\\.net/([0-9a-zA-Z_]+)['\"][^<>]*>(@[^@<>\\s]+)</a>" +
                    "|<a href=['\"]([^'\"]*)['\"][^<>]*>([^<>]*)</a>"
    );

    // git tag
    static final Pattern PatternGit = Pattern.compile(
            "<a\\s+href=\'http[s]?://git\\.oschina\\.net/[^>]*\'[^>]*data-project=\'([0-9]*)\'[^>]*>([^<>]*)</a>"
    );

    // links
    static final Pattern PatternLinks = Pattern.compile(
            "<a\\s+href=['\"]([^'\"]*)['\"][^<>]*>([^<>]*)</a>"
    );

    // team task
    static final Pattern PatternTeamTask = Pattern.compile(
            "<a\\s+style=['\"][^'\"]*['\"]\\s+href=['\"]([^'\"]*)['\"][^<>]*>([^<>]*)</a>"
    );

    // html task
    static final Pattern PatternHtml = Pattern.compile(
            "<[^<>]+>([^<>]+)</[^<>]+>"
    );

    /**
     * 解析
     *
     * @param context context
     * @param content content
     * @return Spannable
     */
    public abstract Spannable parse(Context context, String content);

    /**
     * @param sequence       文本
     * @param pattern        正则
     * @param usedGroupIndex 使用的组号
     * @param showGroupIndex 显示的组号
     * @param listener       点击回掉
     * @return 匹配后的文本
     */
    static Spannable assimilate(CharSequence sequence,
                                Pattern pattern,
                                int usedGroupIndex,
                                int showGroupIndex,
                                final OnClickListener listener) {
        SpannableStringBuilder builder = new SpannableStringBuilder(sequence);
        Matcher matcher;
        while (true) {
            matcher = pattern.matcher(builder.toString());
            if (matcher.find()) {
                final String group0 = matcher.group(usedGroupIndex);
                final String group1 = matcher.group(showGroupIndex);
                builder.replace(matcher.start(), matcher.end(), group1);
                ClickableSpan span = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        listener.onClick(group0);
                    }
                };
                builder.setSpan(span, matcher.start(), matcher.start() + group1.length(),
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                continue;
            }
            break;
        }
        return builder;
    }

    interface OnClickListener {
        void onClick(String str);
    }
}
