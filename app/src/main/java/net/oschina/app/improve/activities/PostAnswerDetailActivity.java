package net.oschina.app.improve.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.simple.CommentEX;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.UIHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by thanatos on 16/6/16.
 */
public class PostAnswerDetailActivity extends AppCompatActivity{

    public static final String BUNDLE_KEY = "BUNDLE_KEY";

    @Bind(R.id.iv_portrait) CircleImageView ivPortrait;
    @Bind(R.id.tv_nick) TextView tvNick;
    @Bind(R.id.tv_time) TextView tvTime;
    @Bind(R.id.iv_vote_up) ImageView ivVoteUp;
    @Bind(R.id.iv_vote_down) ImageView ivVoteDown;
    @Bind(R.id.tv_up_count) TextView tvVoteCount;
    @Bind(R.id.webview) WebView mWebView;
    @Bind(R.id.tv_comment) TextView tvCmnCount;
    @Bind(R.id.tv_favorite) TextView tvFavorite;
    @Bind(R.id.tv_share) TextView tvShare;

    private RequestManager reqManager;
    private CommentEX comment;

    public static void show(Context context, CommentEX comment){
        Intent intent = new Intent();
        intent.putExtra(BUNDLE_KEY, comment);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_answer_detail);
        ButterKnife.bind(this);
        reqManager = Glide.with(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("返回");
        }

        initView();
        initData();
    }

    private void initView(){
        // portrait
        if (TextUtils.isEmpty(comment.getAuthorPortrait())){
            ivPortrait.setImageResource(R.drawable.widget_dface);
        }else{
            reqManager.load(comment.getAuthorPortrait()).into(ivPortrait);
        }
        // nick
        tvNick.setText(comment.getAuthor());
        // publish time
        if (!TextUtils.isEmpty(comment.getPubDate()))
            tvTime.setText(StringUtils.friendly_time(comment.getPubDate()));
        // vote state
        switch (comment.getVoteState()){
            case CommentEX.VOTE_STATE_UP:
                ivVoteUp.setSelected(true);
                break;
            case CommentEX.VOTE_STATE_DOWN:
                ivVoteDown.setSelected(true);
        }
        // vote count
        tvVoteCount.setText(String.valueOf(comment.getVoteCount()));
        // comment count
        tvCmnCount.setText(String.valueOf(comment.getReplies().length));

        fillWebView();
    }

    private void fillWebView(){
        if (TextUtils.isEmpty(comment.getContent())) return;

        String content = String.format(
            "<!DOCTYPE html>"
                + "<html>"
                    + "<head>"
                        + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shThemeDefault.css\">"
                        + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shCore.css\">"
                        + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/common_new.css\">"
                    + "</head>"
                    + "<body>"
                        + "<div style='contentstyle' id='article_id'>"
                            + "%s"
                        + "</div>"
                        + "<script type='text/javascript'> var allImgUrls = getAllImgSrc(document.body.innerHTML);</script>"
                        + "<script type=\"text/javascript\" src=\"file:///android_asset/shCore.js\"></script>"
                        + "<script type=\"text/javascript\" src=\"file:///android_asset/brush.js\"></script>"
                        + "<script type=\"text/javascript\" src=\"file:///android_asset/client.js\"></script>"
                        + "<script type=\"text/javascript\" src=\"file:///android_asset/detail_page.js\"></script>"
                        + "<script type=\"text/javascript\">SyntaxHighlighter.all();</script>"
                        + "<script type=\"text/javascript\">function showImagePreview(var url){window.location.url= url;}</script>"
                    + "</body>"
                + "</html>"
            , comment.getContent());
        UIHelper.addWebImageShow(this, mWebView);
        mWebView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
    }

    private void initData(){
        OSChinaApi.getComments(comment.getId(), 2, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String respStr, Throwable throwable) {
                Toast.makeText(PostAnswerDetailActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String respStr) {
                CommentEX comment = AppContext.createGson().fromJson(respStr, CommentEX.class);
                if (comment == null || comment.getId() <= 0)
                    Toast.makeText(PostAnswerDetailActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                PostAnswerDetailActivity.this.comment = comment;
                initView();
            }
        });
    }


}
