package net.oschina.app.improve.utils.parser;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;

import net.oschina.app.improve.git.bean.Project;
import net.oschina.app.improve.git.detail.ProjectDetailActivity;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.util.UIHelper;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 富文本解析工具
 * Created by haibin
 * on 2017/3/21.
 */
@SuppressWarnings("unused")
public abstract class RichTextParser {

    private static final Pattern PatternAtUserWithHtml = Pattern.compile(
            "<a href=['\"]http[s]?://my.oschina.net/(\\w+|u/([0-9]+))['\"][^<>]+>(@([^@<>]+))</a>"
    );

    static final Pattern PatternAtUser = Pattern.compile(
            "@[^@\\s:]+"
    );

    // #Java#
    private static final Pattern PatternSoftwareTagWithHtml = Pattern.compile(
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
    private static final Pattern PatternGit = Pattern.compile(
            "<a\\s+href=\'http[s]?://git\\.oschina\\.net/[^>]*\'[^>]*data-project=\'([0-9]*)\'[^>]*>([^<>]*)</a>"
    );

    // links
    private static final Pattern PatternLinks = Pattern.compile(
            "<a\\s+href=['\"]([^'\"]*)['\"][^<>]*>([^<>]*)</a>"
    );

    // team task
    private static final Pattern PatternTeamTask = Pattern.compile(
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
    private static Spannable assimilate(CharSequence sequence,
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

    /**
     * 判断手机输入合法
     *
     * @param phoneNumber 手机号码
     * @return true|false
     */
    public static boolean machPhoneNum(CharSequence phoneNumber) {
        String regex = "^[1][34578][0-9]\\d{8}$";
        return Pattern.matches(regex, phoneNumber);
    }

    interface OnClickListener {
        void onClick(String str);
    }

    /**
     * 字符串转化为拼音
     * 字符串中英文不转换为拼音
     *
     * @param text      可能含有拼音的字符串
     * @param splitHead 每个中文转化为拼音后头部添加的分割符号
     *                  eg: "V好De","~"->"~V~hao~De"
     * @return 转化为拼音后的字符串
     */
    public static String convertToPinyin(String text, String splitHead) {
        if (TextUtils.isEmpty(text))
            return "";
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

        char[] charArray = text.toCharArray();

        StringBuilder sb = new StringBuilder();
        boolean canAdd = true;
        for (char c : charArray) {
            String temp = Character.toString(c);
            if (temp.matches("[\u4E00-\u9FA5]+")) {
                String py;
                try {
                    String[] pys = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    py = pys[0];
                } catch (Exception e) {
                    e.printStackTrace();
                    py = " ";
                }
                sb.append(splitHead);
                sb.append(py);
                canAdd = true;
            } else {
                if (canAdd) {
                    sb.append(splitHead);
                    canAdd = false;
                }
                sb.append(temp);
            }
        }
        return sb.toString().trim();
    }

    /**
     * 格式化<a href="url" ...>@xxx</a>
     * // http://my.oschina.net/u/user_id
     * // http://my.oschina.net/user_ident
     *
     * @param context context
     * @param content content
     * @return Spannable
     */
    static Spannable parseOnlyAtUser(final Context context, CharSequence content) {
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        Matcher matcher;
        while (true) {
            matcher = PatternAtUserWithHtml.matcher(builder.toString());
            if (matcher.find()) {
                final String group0 = matcher.group(1); // ident 标识 如retrofit
                final String group1 = matcher.group(2); // uid id
                final String group2 = matcher.group(3); // @Nick
                final String group3 = matcher.group(4); // Nick
                builder.replace(matcher.start(), matcher.end(), group2);
                long uid;
                try {
                    uid = group1 == null ? 0 : Integer.valueOf(group1);
                } catch (Exception e) {
                    uid = 0;
                }
                final long _uid = uid;
                ClickableSpan span = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        if (_uid > 0) {
                            OtherUserHomeActivity.show(context, _uid);
                        } else if (!TextUtils.isEmpty(group0)) {
                            OtherUserHomeActivity.show(context, 0, group0);
                        } else {
                            OtherUserHomeActivity.show(context, group3);
                        }
                    }
                };
                builder.setSpan(span, matcher.start(), matcher.start() + group2.length(),
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                continue;
            }
            break;
        }
        return builder;
    }

    /**
     * 格式化git项目标签
     */
    static Spannable parseOnlyGit(final Context context, CharSequence content) {
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        Matcher matcher;
        while (true) {
            matcher = PatternGit.matcher(builder.toString());
            if (matcher.find()) {
                final String group0 = matcher.group(1);
                final String group1 = matcher.group(2);
                builder.replace(matcher.start(), matcher.end(), group1);
                ClickableSpan span = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Project project = new Project();
                        project.setId(Integer.parseInt(group0));
                        ProjectDetailActivity.show(context, project);
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

    /**
     * 格式化话题
     */
    static Spannable parseOnlyTag(final Context context, CharSequence content) {
        return assimilate(content, PatternSoftwareTagWithHtml, 1, 2, new RichTextParser.OnClickListener() {
            @Override
            public void onClick(String str) {
                UIHelper.showUrlRedirect(context, str);
            }
        });
    }

    /**
     * 格式化链接
     */
    static Spannable parseOnlyLink(final Context context, CharSequence content) {
        return assimilate(content, PatternLinks, 1, 2, new RichTextParser.OnClickListener() {
            @Override
            public void onClick(String str) {
                UIHelper.showUrlRedirect(context, str);
            }
        });
    }

    public static boolean checkIsZH(String input) {
        char[] charArray = input.toLowerCase().toCharArray();
        for (char c : charArray) {
            String tempC = Character.toString(c);
            if (tempC.matches("[\u4E00-\u9FA5]+")) {
                return true;
            }
        }
        return false;
    }

    static Spannable parseOnlyTeamTask(final Context context, CharSequence content) {
        return assimilate(content, PatternTeamTask, 1, 2, new OnClickListener() {
            @Override
            public void onClick(String str) {
                UIHelper.openInternalBrowser(context, str);
            }
        });
    }
}
