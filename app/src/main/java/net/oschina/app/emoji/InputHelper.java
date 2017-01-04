/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oschina.app.emoji;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.widget.EditText;

import net.oschina.app.util.TDevice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Emoji 表情解析类
 */
public class InputHelper {
    /**
     * 删除Emoji表情
     *
     * @param editText
     */
    public static void backspace(EditText editText) {
        if (editText == null) {
            return;
        }
        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0,
                0, KeyEvent.KEYCODE_ENDCALL);
        editText.dispatchKeyEvent(event);
    }

    /**
     * 获取name对应的资源
     */
    public static int getEmojiResId(String name) {
        Integer res = DisplayRules.getMapAll().get(name);
        if (res != null) {
            return res;
        } else {
            return -1;
        }
    }

    /**
     * Support OSChina Client，due to the need to support both 2 Format<br>
     * (I'm drunk, I go home)
     */
    public static Spannable displayEmoji(Resources res, CharSequence s) {
        return displayEmoji(res, s, (int) TDevice.spToPx(res, 20));
    }

    public static Spannable displayEmoji(Resources res, CharSequence s, int size) {
        return displayEmoji(res, new SpannableString(s), size);
    }

    public static Spannable displayEmoji(Resources res, Spannable spannable) {
        return displayEmoji(res, spannable, (int) TDevice.spToPx(res, 20));
    }

    public static Spannable displayEmoji(Resources res, Spannable spannable, int size) {
        String str = spannable.toString();

        if (!str.contains(":") && !str.contains("[")) {
            return spannable;
        }

        if (size == 0)
            size = (int) TDevice.spToPx(res, 20);

        Pattern pattern = Pattern.compile("(\\[[^\\[\\]:\\s\\n]+\\])|(:[^:\\[\\]\\s\\n]+:)");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            String emojiStr = matcher.group();
            if (TextUtils.isEmpty(emojiStr)) continue;
            int resId = getEmojiResId(emojiStr);
            if (resId <= 0) continue;
            Drawable drawable = res.getDrawable(resId);
            if (drawable == null) continue;
            drawable.setBounds(0, 0, size, size);

            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
            spannable.setSpan(span, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannable;
    }

    /**
     * 输入Emoji表情到 EditText
     *
     * @param editText EditText
     * @param emojicon Emojicon
     */
    public static void input2OSC(EditText editText, Emojicon emojicon) {
        if (editText == null || emojicon == null) {
            return;
        }
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        if (start < 0) {
            // 没有多选时，直接在当前光标处添加
            editText.append(displayEmoji(editText.getResources(),
                    emojicon.getRemote(), (int) editText.getTextSize()));
        } else {
            // 将已选中的部分替换为表情(当长按文字时会多选刷中很多文字)
            Spannable str = displayEmoji(editText.getResources(),
                    emojicon.getRemote(), (int) editText.getTextSize());
            editText.getText().replace(Math.min(start, end),
                    Math.max(start, end), str, 0, str.length());
        }
    }
}
