package net.oschina.app.improve.widget.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;

import net.oschina.app.improve.tweet.activities.TweetTopicActivity;
import net.oschina.common.widget.RichEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */

public class OnKeyArrivedListenerAdapter implements RichEditText.OnKeyArrivedListener {

    @Override
    public boolean onMentionKeyArrived(RichEditText editText) {
        Editable msg = editText.getText();
        String msgStr = msg.toString();
        int selStartIndex = editText.getSelectionStart();

        if (TextUtils.isEmpty(msgStr.trim()) || selStartIndex <= 0
                || TextUtils.isEmpty(msgStr.substring(selStartIndex - 1, selStartIndex).trim())) {
            skipMention(editText);
        }

        return true;
    }

    @Override
    public boolean onTopicKeyArrived(RichEditText editText) {
        Editable msg = editText.getText();
        String msgStr = msg.toString();
        int selStartIndex = editText.getSelectionStart();
        int selEndIndex = editText.getSelectionEnd();

        if (TextUtils.isEmpty(msgStr.trim()) || selStartIndex <= 0) {
            skipTopic(editText);
            return true;
        }

        int startIndex = 0;
        RichEditText.TagSpan[] spans = msg.getSpans(0, selStartIndex, RichEditText.TagSpan.class);
        if (spans.length > 0) {
            startIndex = msg.getSpanEnd(spans[spans.length - 1]);
        }

        boolean isMatcher = false;
        String tagStr = msgStr.substring(startIndex, selStartIndex) + "#";
        Pattern pattern = Pattern.compile(RichEditText.MATCH_TOPIC);
        Matcher matcher = pattern.matcher(tagStr);
        while (matcher.find()) {
            String str = matcher.group();
            int matcherStart = matcher.start() + startIndex;
            int matcherEnd = matcher.end() + startIndex;
            if (matcherEnd == selStartIndex + 1)
                msg.replace(selStartIndex, selEndIndex, "#");
            msg.setSpan(new RichEditText.TagSpan(str), matcherStart, matcherEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            isMatcher = true;
        }
        if (isMatcher) {
            editText.setSelection(selEndIndex);
            return false;
        }

        skipTopic(editText);
        return true;
    }

    public void skipMention(RichEditText editText) {
        Context context = editText.getContext();
        if (context != null)
            TweetTopicActivity.show(context, editText);
    }

    public void skipTopic(RichEditText editText) {
        Context context = editText.getContext();
        if (context != null)
            TweetTopicActivity.show(context, editText);
    }
}
