package net.oschina.app.improve.widget.adapter;

import net.oschina.common.widget.RichEditText;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */

public class OnKeyArrivedListenerAdapter implements RichEditText.OnKeyArrivedListener {
    @Override
    public boolean onMentionKeyArrived() {
        return false;
    }

    @Override
    public boolean onTopicKeyArrived() {
        return false;
    }
}
