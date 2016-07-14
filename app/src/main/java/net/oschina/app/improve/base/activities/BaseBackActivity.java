package net.oschina.app.improve.base.activities;

import android.support.v7.app.ActionBar;

/**
 * Created by JuQiu
 * on 16/6/20.
 */

public abstract class BaseBackActivity extends BaseActivity {
    @Override
    protected void initWindow() {
        super.initWindow();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(false);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
