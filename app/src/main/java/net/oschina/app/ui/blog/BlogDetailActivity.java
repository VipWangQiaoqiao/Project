package net.oschina.app.ui.blog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.base.ResultBean;
import net.oschina.app.bean.blog.BlogDetail;
import net.oschina.app.util.FontSizeUtils;
import net.oschina.app.util.ThemeSwitchUtils;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

public class BlogDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private RequestManager mImgLoader;
    private long mId;
    private WebView mWebView;
    private TextView mTVAuthorName;
    private TextView mTVPubDate;
    private TextView mTVTitle;
    private TextView mTVCommentCount;
    private ImageView mIVLabelRecommend;
    private ImageView mIVLabelOriginate;
    private ImageView mIVAuthorPortrait;
    private Button mBtnFlow;
    private EditText mETInput;

    private ImageView mIVFlow;
    private ImageView mIVShare;

    private LinearLayout mLayAbouts;
    private LinearLayout mLayComments;

    public static void show(Context context, long id) {
        Intent intent = new Intent(context, BlogDetailActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(false);
        }

        mId = getIntent().getLongExtra("id", 0);
        if (mId == 0)
            finish();
        else {
            initView();
            initData();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_blog_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mImgLoader = Glide.with(this);

        mWebView = (WebView) findViewById(R.id.webview);
        UIHelper.initWebView(mWebView);

        mTVAuthorName = (TextView) findViewById(R.id.tv_name);
        mTVPubDate = (TextView) findViewById(R.id.tv_pub_date);
        mTVTitle = (TextView) findViewById(R.id.tv_title);
        mTVCommentCount = (TextView) findViewById(R.id.tv_comment_count);

        mIVLabelRecommend = (ImageView) findViewById(R.id.iv_label_recommend);
        mIVLabelOriginate = (ImageView) findViewById(R.id.iv_label_originate);
        mIVAuthorPortrait = (ImageView) findViewById(R.id.iv_avatar);

        mBtnFlow = (Button) findViewById(R.id.btn_flow);
        mETInput = (EditText) findViewById(R.id.et_input);

        mIVFlow = (ImageView) findViewById(R.id.iv_flow);
        mIVShare = (ImageView) findViewById(R.id.iv_share);

        mLayAbouts = (LinearLayout) findViewById(R.id.lay_blog_detail_about);
        mLayComments = (LinearLayout) findViewById(R.id.lay_blog_detail_comment);

        mIVFlow.setOnClickListener(this);
        mIVShare.setOnClickListener(this);
        mETInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


                    return true;
                }
                return false;
            }
        });
    }

    private void initData() {
        OSChinaApi.getBlogDetail(mId, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<BlogDetail>>() {
                    }.getType();

                    ResultBean<BlogDetail> resultBean = AppContext.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        handleData(resultBean.getResult());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    private final static String linkCss = "<script type=\"text/javascript\" " +
            "src=\"file:///android_asset/shCore.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/brush.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/client.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/detail_page" +
            ".js\"></script>"
            + "<script type=\"text/javascript\">SyntaxHighlighter.all();</script>"
            + "<script type=\"text/javascript\">function showImagePreview(var url){window" +
            ".location.url= url;}</script>"
            + "<link rel=\"stylesheet\" type=\"text/css\" " +
            "href=\"file:///android_asset/shThemeDefault.css\">"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shCore" +
            ".css\">"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/common_new" +
            ".css\">";

    private String getWebViewBody(BlogDetail blog) {
        return linkCss + UIHelper.WEB_LOAD_IMAGES +
                ThemeSwitchUtils.getWebViewBodyString() +
                UIHelper.setHtmlCotentSupportImagePreview(blog.getBody()) +
                "</div></body>";
    }

    private void handleData(BlogDetail blog) {
        String body = getWebViewBody(blog);
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL("", body, "text/html", "UTF-8", "");
            // 显示存储的字体大小
            mWebView.loadUrl(FontSizeUtils.getSaveFontSize());
        }

        mTVAuthorName.setText(blog.getAuthor());
        mImgLoader.load(blog.getAuthorPortrait()).error(R.drawable.widget_dface).into(mIVAuthorPortrait);

        mTVPubDate.setText(blog.getPubDate());
        mTVTitle.setText(blog.getTitle());


        mIVLabelRecommend.setVisibility(blog.isRecommend() ? View.VISIBLE : View.GONE);
        mIVLabelOriginate.setImageDrawable(blog.isOriginal() ?
                getResources().getDrawable(R.drawable.ic_label_originate) :
                getResources().getDrawable(R.drawable.ic_label_reprint));

        if (blog.getAuthorRelation() == 3) {
            mBtnFlow.setEnabled(true);
            mBtnFlow.setText("关注");
        } else {
            mBtnFlow.setEnabled(false);
            mBtnFlow.setText("已关注");
        }

        for (BlogDetail.About about : blog.getAbouts()) {
            View aboutLay = getLayoutInflater().inflate(R.layout.item_blog_detail_about_lay, mLayAbouts, true);
            ((TextView) aboutLay.findViewById(R.id.tv_title)).setText(about.title);
            ((TextView) aboutLay.findViewById(R.id.tv_info_view)).setText(about.viewCount);
            ((TextView) aboutLay.findViewById(R.id.tv_info_comment)).setText(about.commentCount);
        }

        mTVCommentCount.setText(String.format("评论(%s)", blog.getCommentCount()));
        for (BlogDetail.Comment comment : blog.getComments()) {
            View aboutLay = getLayoutInflater().inflate(R.layout.item_blog_detail_comment_lay, mLayComments, true);
            mImgLoader.load(comment.authorPortrait).error(R.drawable.widget_dface)
                    .into(((ImageView) aboutLay.findViewById(R.id.iv_avatar)));

            ((TextView) aboutLay.findViewById(R.id.tv_name)).setText(comment.author);
            ((TextView) aboutLay.findViewById(R.id.tv_pub_date)).setText(comment.pubDate);
            ((TextView) aboutLay.findViewById(R.id.tv_content)).setText(comment.content);

            aboutLay.findViewById(R.id.btn_comment).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    private void submitComment(long id, String content) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_flow: {

            }
            break;
            case R.id.iv_share: {

            }
            break;
        }
    }
}
