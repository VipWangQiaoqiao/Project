package net.oschina.app.improve.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by thanatosx on 2016/12/23.
 */

public class BottomDialog extends BottomSheetDialog {

    private BottomSheetBehavior behavior;


    public BottomDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initialize(view);
    }

    @Override
    public void show() {
        super.show();
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void initialize(View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        behavior = (BottomSheetBehavior) params.getBehavior();
    }
}
