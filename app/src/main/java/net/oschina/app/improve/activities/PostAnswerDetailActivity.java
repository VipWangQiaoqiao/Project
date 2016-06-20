package net.oschina.app.improve.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.improve.bean.BlogDetail;
import net.oschina.app.improve.bean.simple.Comment;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by thanatos on 16/6/16.
 */
public class PostAnswerDetailActivity extends AppCompatActivity{

    public static final String BUNDLE_KEY = "BUNDLE_KEY";

    @Bind(R.id.iv_portrait) CircleImageView ivPortrait;
    @Bind(R.id.tv_nick) TextView tvNick;
    @Bind(R.id.tv_time) TextView tvTime;
    @Bind(R.id.iv_vote_up) TextView ivVoewUp;
    @Bind(R.id.iv_vote_down) TextView ivVoewDown;
    @Bind(R.id.tv_up_count) TextView tvUpDown;
    @Bind(R.id.webview) WebView mWebView;
    @Bind(R.id.tv_comment) TextView tvComment;
    @Bind(R.id.tv_favorite) TextView tvFavorite;
    @Bind(R.id.tv_share) TextView tvShare;

    private RequestManager reqManager;

    public static void show(Context context, Comment comment){
        Intent intent = new Intent();
        intent.putExtra(BUNDLE_KEY, comment);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_answer_detail);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("返回");
        }

        initView();
        initData();
    }

    private void initView(){

    }

    private void initData(){

    }


}
