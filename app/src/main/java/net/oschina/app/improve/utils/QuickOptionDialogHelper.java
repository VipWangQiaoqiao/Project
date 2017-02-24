package net.oschina.app.improve.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import net.oschina.app.R;
import net.oschina.app.util.TDevice;
import net.oschina.common.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */

public class QuickOptionDialogHelper {
    private List<String> items = new ArrayList<>();
    private List<Runnable> runnableList = new ArrayList<>();
    private Context context;

    public static QuickOptionDialogHelper with(Context context) {
        QuickOptionDialogHelper helper = new QuickOptionDialogHelper();
        helper.context = context;
        return helper;
    }

    public QuickOptionDialogHelper addCopy(final String text) {
        if (TextUtils.isEmpty(text))
            return this;
        addOther(R.string.copy, new Runnable() {
            @Override
            public void run() {
                TDevice.copyTextToBoard(text);
            }
        });
        return this;
    }

    public QuickOptionDialogHelper addOther(@StringRes int id, Runnable runnable) {
        items.add(context.getString(id));
        runnableList.add(runnable);
        return this;
    }

    public QuickOptionDialogHelper addOther(boolean isNeed, @StringRes int id, Runnable runnable) {
        if (!isNeed)
            return this;
        return addOther(id, runnable);
    }


    public void show() {
        if (items.size() == 0)
            return;
        DialogHelper.getSelectDialog(context, CollectionUtil.toArray(items, String.class), "取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        doWork(i);
                    }
                }).show();
    }

    private void doWork(int index) {
        if (index >= 0 && index < runnableList.size()) {
            runnableList.get(index).run();
        }
    }
}
