package net.oschina.app.improve.app;

import android.support.annotation.NonNull;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */

public class ParentLinkedHolder<T> {
    public T item;
    private ParentLinkedHolder<T> parentLinkedHolder;

    public ParentLinkedHolder(@NonNull T item) {
        this.item = item;
    }

    public T get() {
        return item;
    }

    public boolean hasParent() {
        return parentLinkedHolder != null;
    }

    public void addParent(ParentLinkedHolder<T> holder) {
        parentLinkedHolder = holder;
    }

    public ParentLinkedHolder<T> putParent() {
        ParentLinkedHolder<T> holder = parentLinkedHolder;
        parentLinkedHolder = null;
        return holder;
    }
}
