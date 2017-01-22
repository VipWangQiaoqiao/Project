package net.oschina.app.improve.utils;

import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Pair;
import android.view.View;

import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.UIHelper;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String 处理工具类
 */
public class AssimilateUtils {

    // @thanatosx
    // http://my.oschina.net/u/user_id
    // http://my.oschina.net/user_ident
    public static final Pattern PatternAtUserWithHtml = Pattern.compile(
            "<a\\s+href=['\"]http[s]?://my\\.oschina\\.[a-z]+/([0-9a-zA-Z_]+" +
                    "|u/([0-9]+))['\"][^<>]*>(@([^@<>\\s]+))</a>"
    );
    public static final Pattern PatternAtUser = Pattern.compile(
            "@[^@\\s:]+"
    );

    // #Java#
    public static final Pattern PatternSoftwareTagWithHtml = Pattern.compile(
            "<a\\s+href=['\"]([^'\"]*)['\"][^<>]*>(#[^#@<>\\s]+#)</a>"
    );
    public static final Pattern PatternSoftwareTag = Pattern.compile(
            "#([^#@<>\\s]+)#"
    );

    // @user links
    @Deprecated
    public static final Pattern PatternAtUserAndLinks = Pattern.compile(
            "<a\\s+href=['\"]http://my\\.oschina\\.net/([0-9a-zA-Z_]+)['\"][^<>]*>(@[^@<>\\s]+)</a>" +
                    "|<a href=['\"]([^'\"]*)['\"][^<>]*>([^<>]*)</a>"
    );

    // links
    public static final Pattern PatternLinks = Pattern.compile(
            "<a\\s+href=['\"]([^'\"]*)['\"][^<>]*>([^<>]*)</a>"
    );

    // team task
    public static final Pattern PatternTeamTask = Pattern.compile(
            "<a\\s+style=['\"][^'\"]*['\"]\\s+href=['\"]([^'\"]*)['\"][^<>]*>([^<>]*)</a>"
    );

    // html task
    public static final Pattern PatternHtml = Pattern.compile(
            "<[^<>]+>([^<>]+)</[^<>]+>"
    );

    private interface Action1 {
        void call(String str);
    }

    /**
     * 通常使用的过滤逻辑
     *
     * @param context {@link Context}
     * @param content Content String
     * @return String
     */
    public static Spannable assimilate(Context context, String content) {
        if (TextUtils.isEmpty(content)) return null;
        content = HTMLUtil.rollbackReplaceTag(content);
        Spannable spannable = assimilateOnlyAtUser(context, content);
        spannable = assimilateOnlyTag(context, spannable);
        spannable = assimilateOnlyLink(context, spannable);
        spannable = InputHelper.displayEmoji(context.getResources(), spannable);
        return spannable;
    }

    /**
     * 高亮@User
     *
     * @param context Context
     * @param content string
     * @return
     */
    public static Spannable highlightAtUser(Context context, CharSequence content) {
        return highlightAtUser(context, new SpannableString(content));
    }

    /**
     * @param context   Context
     * @param spannable string
     * @return
     * @see #highlightAtUser(Context, Spannable)
     */
    public static Spannable highlightAtUser(Context context, Spannable spannable) {
        String str = spannable.toString();
        Matcher matcher = PatternAtUser.matcher(str);
        while (matcher.find()) {
            ForegroundColorSpan span = new ForegroundColorSpan(0XFF6888AD);
            spannable.setSpan(span, matcher.start(), matcher.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    /**
     * 与 {@link #highlightAtUser(Context, Spannable)} 不同的是这个方法是针对<a>标签包裹的@User
     *
     * @param context Context
     * @param content string
     * @return
     */
    public static Spannable assimilateOnlyAtUser(final Context context, CharSequence content) {
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        Matcher matcher;
        while (true) {
            matcher = PatternAtUserWithHtml.matcher(builder.toString());
            if (matcher.find()) {
                final String group0 = matcher.group(1); // ident
                final String group1 = matcher.group(2); // uid
                final String group2 = matcher.group(3); // @Nick
                final String group3 = matcher.group(4); // Nick
                builder.replace(matcher.start(), matcher.end(), group2);
                long uid = 0;
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
     * @param context Context
     * @param content string
     * @return
     */
    public static Spannable assimilateOnlyTag(final Context context, CharSequence content) {
        return assimilate(content, PatternSoftwareTagWithHtml, 1, 2, new Action1() {
            @Override
            public void call(String str) {
                UIHelper.showUrlRedirect(context, str);
            }
        });
    }

    /**
     * 格式化link链接
     * <p>
     * 注意: 最好在最后处理这个过程,否则有些需要特殊处理的就会被它格式化掉
     *
     * @param context Context
     * @param content CharSequence
     * @return A spannable object
     */
    public static Spannable assimilateOnlyLink(final Context context, CharSequence content) {
        return assimilate(content, PatternLinks, 1, 2, new Action1() {
            @Override
            public void call(String str) {
                UIHelper.showUrlRedirect(context, str);
            }
        });
    }

    public static Spannable assimilateOnlyTeamTask(final Context context, CharSequence content) {
        return assimilate(content, PatternTeamTask, 1, 2, new Action1() {
            @Override
            public void call(String str) {
                UIHelper.openInternalBrowser(context, str);
            }
        });
    }

    public static Spannable clearHtmlTag(CharSequence content) {
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

    @Deprecated
    public static Spannable assimilateTagAndAtUser(final Context context, String content) {

        // 现将#software#的标签去掉
        content = content.replaceAll("<a[^<>]*>(#[^#@<>\\s]+#)</a>", "$1");

        Matcher matcher;
        Map<String, Pair<Integer, Integer>> maps = new HashMap<>();

        while (true) {
            matcher = PatternAtUserAndLinks.matcher(content);
            if (matcher.find()) {
                String group1 = matcher.group(1);
                String group2 = matcher.group(2);
                String group3 = matcher.group(3);
                String group4 = matcher.group(4);
                if (group1 != null && group2 != null) {
                    content = matcher.replaceFirst(group2);
                    maps.put(group1, new Pair<>(matcher.start(), matcher.start() + group2.length()));
                } else if (group3 != null && group4 != null) {
                    content = matcher.replaceFirst(group4);
                    maps.put(group3, new Pair<>(matcher.start(), matcher.start() + group4.length()));
                } else {
                    content = matcher.replaceFirst("");
                }
                continue;
            }
            break;
        }

        Spannable spannable = new SpannableString(content);

        for (final Map.Entry<String, Pair<Integer, Integer>> entry : maps.entrySet()) {
            final String substr = entry.getKey();
            final Pair<Integer, Integer> pair = entry.getValue();
            ClickableSpan span = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    if (substr.startsWith("http://") || substr.startsWith("https://")) {
                        UIHelper.openInternalBrowser(context, substr);
                    } else {
                        OtherUserHomeActivity.show(context, substr);
                    }
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ds.linkColor);
                    ds.setUnderlineText(false);
                }
            };
            spannable.setSpan(span, pair.first, pair.second, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        //再处理#software#
        matcher = PatternSoftwareTag.matcher(content);
        while (matcher.find()) {
            final String tag = matcher.group(1);
            ClickableSpan span = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Bundle bundle = new Bundle();
                    bundle.putString("topic", tag);
                    UIHelper.showSimpleBack(context, SimpleBackPage.TWEET_TOPIC_LIST, bundle);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ds.linkColor);
                    ds.setUnderlineText(false);
                }
            };
            spannable.setSpan(span, matcher.start(), matcher.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    /**
     * 本地化处理
     *
     * @param content 处理内容
     * @param pattern 匹配规则
     * @param index0  使用的组号
     * @param index1  显示的组号
     * @param action  回调函数
     * @return Spannable
     */
    @Deprecated
    private static Spannable assimilate(String content, Pattern pattern, int index0, int index1,
                                        final Action1 action) {
        Matcher matcher;
        Map<String, Pair<Integer, Integer>> maps = new HashMap<>();

        while (true) {
            matcher = pattern.matcher(content);
            if (matcher.find()) {
                String group0 = matcher.group(index0);
                String group1 = matcher.group(index1);
                content = matcher.replaceFirst(group1);
                maps.put(group0, new Pair<>(matcher.start(), matcher.start() + group1.length()));
                continue;
            }
            break;
        }

        Spannable spannable = new SpannableString(content);

        for (final Map.Entry<String, Pair<Integer, Integer>> entry : maps.entrySet()) {
            final String substr = entry.getKey();
            final Pair<Integer, Integer> pair = entry.getValue();
            ClickableSpan span = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    action.call(substr);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ds.linkColor);
                    ds.setUnderlineText(false);
                }
            };
            spannable.setSpan(span, pair.first, pair.second, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return spannable;
    }

    /**
     * 本地化处理
     *
     * @param sequence 处理内容
     * @param pattern  匹配规则
     * @param index0   使用的组号
     * @param index1   显示的组号
     * @param action   回调函数
     * @return Spannable
     */
    private static Spannable assimilate(CharSequence sequence, Pattern pattern, int index0,
                                        int index1, final Action1 action) {
        SpannableStringBuilder builder = new SpannableStringBuilder(sequence);
        Matcher matcher;
        while (true) {
            matcher = pattern.matcher(builder.toString());
            if (matcher.find()) {
                final String group0 = matcher.group(index0);
                final String group1 = matcher.group(index1);
                builder.replace(matcher.start(), matcher.end(), group1);
                ClickableSpan span = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        action.call(group0);
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


    public static boolean machPhoneNum(CharSequence phoneNumber) {

        String regex = "^[1][34578][0-9]\\d{8}$";
        // Pattern pattern = Pattern.compile(regex);
        // pattern.matcher(phoneNumber).matches();

        //第二种就是对一种的一种封装
        return Pattern.matches(regex, phoneNumber);
    }

    public static boolean machEmail(CharSequence email) {
        String regex = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
        return Pattern.matches(regex, email);
    }


    /**
     * string 2 pinyin
     *
     * @param input   string
     * @param isLabel isLabel
     * @return pinyin
     */
    public static String returnPinyin(String input, boolean isLabel) {

        StringBuilder sb = new StringBuilder(0);

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

        char[] charArray = input.toLowerCase().toCharArray();

        for (char c : charArray) {
            String tempC = Character.toString(c);
            if (tempC.matches("[\u4E00-\u9FA5]+")) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    sb.append(temp[0]);
                    if (isLabel)
                        break;
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                }
            } else {
                sb.append(tempC);
                if (isLabel)
                    break;
            }
        }

        return sb.toString().toUpperCase();
    }

    /**
     * string 2 pinyin
     *
     * @param input     string
     * @param needSpace 是否需要空格
     * @return pinyin
     */
    public static String returnPinyin4(String input, boolean needSpace) {

        StringBuilder sb = new StringBuilder(0);

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

        char[] charArray = input.toLowerCase().toCharArray();

        for (char c : charArray) {
            String tempC = Character.toString(c);
            if (tempC.matches("[\u4E00-\u9FA5]+")) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    sb.append(temp[0]);
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                }
            } else {
                sb.append(tempC);
            }
            if (needSpace)
                sb.append(" ");
        }

        return sb.toString().trim().toUpperCase();
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


    /**
     * 获取最后一个汉字的坐标
     *
     * @param text 可能含有拼音的字符串
     * @return 如果未查询到返回-1，否则返回最后一个汉字所在的坐标
     */
    public static int lastIndexOfChinese(String text) {
        char[] charArray = text.toCharArray();
        int index = -1;
        for (int i = 0; i < charArray.length; i++) {
            String tempC = Character.toString(charArray[i]);
            if (tempC.matches("[\u4E00-\u9FA5]+")) {
                index = i;
            }
        }
        return index;
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


}
