package net.oschina.app.improve.comment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.activities.BaseBackActivity;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.CommentEX;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.UIHelper;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 问答的评论详情
 * Created by thanatos on 16/6/16.
 */
public class QuestionAnswerDetailActivity extends BaseBackActivity{

    public static final String BUNDLE_KEY = "BUNDLE_KEY";

    @Bind(R.id.iv_portrait) CircleImageView ivPortrait;
    @Bind(R.id.tv_nick) TextView tvNick;
    @Bind(R.id.tv_time) TextView tvTime;
    @Bind(R.id.iv_vote_up) ImageView ivVoteUp;
    @Bind(R.id.iv_vote_down) ImageView ivVoteDown;
    @Bind(R.id.tv_up_count) TextView tvVoteCount;
    @Bind(R.id.webview) WebView mWebView;
    @Bind(R.id.layout_container) LinearLayout mLyaoutContainer;

    private CommentEX comment;

    public static void show(Context context, CommentEX comment){
        Intent intent = new Intent(context, QuestionAnswerDetailActivity.class);
        intent.putExtra(BUNDLE_KEY, comment);
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        comment = (CommentEX) getIntent().getSerializableExtra(BUNDLE_KEY);
        return !(comment == null || comment.getId() <= 0) && super.initBundle(bundle);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_post_answer_detail;
    }

    protected void initWidget(){
        // portrait
        if (TextUtils.isEmpty(comment.getAuthorPortrait())){
            ivPortrait.setImageResource(R.drawable.widget_dface);
        }else{
            getImageLoader().load(comment.getAuthorPortrait()).into(ivPortrait);
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

        fillWebView();
    }

    private void fillWebView(){
        if (TextUtils.isEmpty(comment.getContent())) return;
        String html = HTMLUtil.setupWebContent(comment.getContent(), true, true);
        UIHelper.addWebImageShow(this, mWebView);
        mWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    }

    protected void initData(){
        OSChinaApi.getComment(comment.getId(), 2, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String respStr, Throwable throwable) {
                Toast.makeText(QuestionAnswerDetailActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String respStr) {
                ResultBean<CommentEX> result = AppContext.createGson().fromJson(respStr,
                        new TypeToken<ResultBean<CommentEX>>(){}.getType());
                if (result.isSuccess()){
                    CommentEX cmn = result.getResult();
                    if (cmn != null && cmn.getId() > 0){
                        comment = cmn;
                        initWidget();
                        return;
                    }
                }
                Toast.makeText(QuestionAnswerDetailActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.layout_vote) void onClickVote(){

    }


}
