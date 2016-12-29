package net.oschina.app.improve.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

import net.oschina.app.improve.widget.listenerAdapter.TextWatcherAdapter;
import net.oschina.app.util.TLog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 一个简单的富文本编辑器
 * 实现了@(AT)和##的Tag匹配功能，
 * 具有Tag删除判断，和光标定位判断；预防用户胡乱篡改
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class RichEditText extends android.support.v7.widget.AppCompatEditText {
    private static final String TAG = RichEditText.class.getName();

    public RichEditText(Context context) {
        super(context);
        init();
    }

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        addTextChangedListener(tagSpanTextWatcher);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new ZanyInputConnection(super.onCreateInputConnection(outAttrs),
                true);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        TLog.e(TAG, "onSelectionChanged:" + selStart + " " + selEnd);
        Editable message = getText();

        if (selStart == selEnd) {
            TagSpan[] list = message.getSpans(selStart - 1, selStart, TagSpan.class);
            if (list.length > 0) {
                // Get first tag
                TagSpan span = list[0];
                int spanStart = message.getSpanStart(span);
                int spanEnd = message.getSpanEnd(span);
                TLog.e(TAG, "onSelectionChanged#a:" + spanStart + " " + spanEnd);
                // Check index
                if (Math.abs(selStart - spanStart) > Math.abs(selStart - spanEnd)) {
                    Selection.setSelection(message, spanEnd);
                    replaceCacheTagSpan(message, span, false);
                    return;
                } else {
                    Selection.setSelection(message, spanStart);
                }
            }
        } else {
            TagSpan[] list = message.getSpans(selStart, selEnd, TagSpan.class);
            if (list.length == 0)
                return;
            int start = selStart;
            int end = selEnd;
            for (TagSpan span : list) {
                int spanStart = message.getSpanStart(span);
                int spanEnd = message.getSpanEnd(span);

                if (spanStart < start)
                    start = spanStart;

                if (spanEnd > end)
                    end = spanEnd;
            }
            if (start != selStart || end != selEnd) {
                Selection.setSelection(message, start, end);
                TLog.e(TAG, "onSelectionChanged#b:" + start + " " + end);
            }
        }

        replaceCacheTagSpan(message, null, false);
    }

    private class ZanyInputConnection extends InputConnectionWrapper {

        ZanyInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            TLog.e(TAG, "ZanyInputConnection#sendKeyEvent:" + event.toString());
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                if (!RichEditText.this.tagSpanTextWatcher.checkKeyDel())
                    return false;
            }
            return super.sendKeyEvent(event);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
            if (beforeLength == 1 && afterLength == 0) {
                // backspace
                return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                        && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }

            return super.deleteSurroundingText(beforeLength, afterLength);
        }

        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            TLog.e(TAG, "ZanyInputConnection#commitText:" + text + " " + newCursorPosition);
            text = checkCommitWithCacheTagSpan(text);
            super.commitText(" ", 1);
            return super.commitText(text, newCursorPosition);
        }
    }

    private void replaceCacheTagSpan(Editable message, TagSpan span, boolean targetDelState) {
        if (tagSpanTextWatcher != null) {
            tagSpanTextWatcher.replaceSpan(message, span, targetDelState);
        }
    }

    private CharSequence checkCommitWithCacheTagSpan(CharSequence text) {
        if (tagSpanTextWatcher != null) {
            text = tagSpanTextWatcher.checkCommit(text);
        }
        return text;
    }

    private TagSpanTextWatcher tagSpanTextWatcher = new TagSpanTextWatcher();

    private class TagSpanTextWatcher extends TextWatcherAdapter {
        private TagSpan willDelSpan;

        void replaceSpan(Editable message, TagSpan span, boolean targetDelState) {
            if (span != null)
                span.setPreDeleteState(targetDelState, message);

            if (willDelSpan != span) {
                // When different
                TagSpan cacheSpan = willDelSpan;
                if (cacheSpan != null) {
                    cacheSpan.setPreDeleteState(false, message);
                }
                willDelSpan = span;
            }
        }

        boolean checkKeyDel() {
            int selStart = getSelectionStart();
            int selEnd = getSelectionEnd();
            Editable message = getText();
            TLog.e(TAG, "TagSpanTextWatcher#checkKeyDel:" + selStart + " " + selEnd);
            if (selStart == selEnd) {
                int start = selStart - 1;
                int count = 1;

                start = start < 0 ? 0 : start;

                int end = start + count;
                TagSpan[] list = message.getSpans(start, end, TagSpan.class);

                if (list.length > 0) {
                    // Only get first
                    final TagSpan span = list[0];
                    final TagSpan cacheSpan = willDelSpan;

                    if (span == cacheSpan) {
                        if (span.isPreDeleteState)
                            return true;
                        else {
                            span.setPreDeleteState(true, message);
                            return false;
                        }
                    }
                }
            }
            // Replace cache tag to null
            replaceSpan(message, null, false);
            return true;
        }

        CharSequence checkCommit(CharSequence s) {
            if (willDelSpan != null) {
                willDelSpan.isPreDeleteState = false;
                willDelSpan = null;
                if (!" ".equals(s.subSequence(0, 1))) {
                    s = " " + s;
                    TLog.e(TAG, "TagSpanTextWatcher#checkCommit#Sapce:" + s);
                }
            }
            return s;
        }

        @Override
        public void afterTextChanged(Editable s) {
            final TagSpan span = willDelSpan;
            TLog.e(TAG, "TagSpanTextWatcher#afterTextChanged#span:" + (span == null ? "null" : span.toString()));
            if (span != null && span.isPreDeleteState) {
                int start = s.getSpanStart(span);
                int end = s.getSpanEnd(span);

                // Remove the span
                s.removeSpan(span);

                // Remove the remaining emoticon text.
                if (start != end) {
                    s.delete(start, end);
                }
            }
            // Set tag to null
            willDelSpan = null;
        }
    }

    @SuppressLint("ParcelCreator")
    private static class TagSpan extends ForegroundColorSpan {
        private String value;
        private boolean isPreDeleteState;

        TagSpan(String value) {
            super(0xFF24cf5f);
            this.value = value;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            //TLog.e(TAG, "TagSpan:updateDrawState:" + isPreDeleteState);
            ds.setFakeBoldText(true);
            if (isPreDeleteState) {
                ds.setColor(0xFFFFFFFF);
                ds.bgColor = 0xFF24cf5f;
            } else {
                super.updateDrawState(ds);
            }
        }

        void setPreDeleteState(boolean willDelete, Editable message) {
            if (willDelete == isPreDeleteState)
                return;
            isPreDeleteState = willDelete;
            int cacheSpanStart = message.getSpanStart(this);
            int cacheSpanEnd = message.getSpanEnd(this);
            if (cacheSpanStart >= 0 && cacheSpanEnd >= cacheSpanStart) {
                message.setSpan(this, cacheSpanStart, cacheSpanEnd,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "TagSpan{" +
                    "value='" + value + '\'' +
                    ", isPreDeleteState=" + isPreDeleteState +
                    '}';
        }
    }

    public static Spannable matchMention(Spannable spannable) {
        String text = spannable.toString();

        Pattern pattern = Pattern.compile("@([^@^\\s^:^,^;^'，'^'；'^>^<]{1,})");//@([^@^\\s^:]{1,})([\\s\\:\\,\\;]{0,1})");//@.+?[\\s:]
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String str = matcher.group();
            int matcherStart = matcher.start();
            int matcherEnd = matcher.end();
            spannable.setSpan(new TagSpan(str), matcherStart, matcherEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            TLog.e(TAG, "matchMention:" + str + " " + matcherStart + " " + matcherEnd);
        }

        return spannable;
    }

    public static Spannable matchTopic(Spannable spannable) {
        String text = spannable.toString();

        Pattern pattern = Pattern.compile("#.+?#");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String str = matcher.group();
            int matcherStart = matcher.start();
            int matcherEnd = matcher.end();
            spannable.setSpan(new TagSpan(str), matcherStart, matcherEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            TLog.e(TAG, "matchTopic:" + str + " " + matcherStart + " " + matcherEnd);
        }

        return spannable;
    }

}
