package net.oschina.app.improve.comment;

import android.content.Context;
import android.content.Intent;

import net.oschina.app.R;
import net.oschina.app.improve.activities.BaseBackActivity;

public class CommentsActivity extends BaseBackActivity {

    public static void show(Context context, long id, int type) {
        Intent intent = new Intent(context, CommentsActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_comments;
    }
}
