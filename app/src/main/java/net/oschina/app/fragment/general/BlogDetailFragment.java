package net.oschina.app.fragment.general;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.bean.blog.BlogDetail;
import net.oschina.app.contract.BlogDetailContract;
import net.oschina.app.fragment.base.BaseFragment;
import net.oschina.app.util.FontSizeUtils;
import net.oschina.app.util.UIHelper;

/**
 * Created by qiujuer
 * on 16/5/26.
 */

public class BlogDetailFragment extends BaseFragment implements View.OnClickListener, BlogDetailContract.View {
    private final static String TAG = BlogDetailFragment.class.getName();
    private RequestManager mImgLoader;
    private long mId;
    private WebView mWebView;
    private TextView mTVAuthorName;
    private TextView mTVPubDate;
    private TextView mTVTitle;
    private ImageView mIVLabelRecommend;
    private ImageView mIVLabelOriginate;
    private ImageView mIVAuthorPortrait;
    private ImageView mIVFav;
    private Button mBtnRelation;
    private EditText mETInput;

    private LinearLayout mLayAbouts;
    private LinearLayout mLayComments;

    private long mCommentId;

    private BlogDetailContract.Operator mOperator;


    public static BlogDetailFragment instantiate(BlogDetailContract.Operator operator, BlogDetail detail) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("key", detail);
        BlogDetailFragment fragment = new BlogDetailFragment();
        fragment.setArguments(bundle);
        fragment.mOperator = operator;
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_general_blog_detail;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        WebView view = mWebView;
        if (view != null) {
            view.removeAllViews();
            view.destroy();
        }
    }

    @Override
    protected void initWidget(View root) {
        mImgLoader = Glide.with(this);

        mWebView = (WebView) root.findViewById(R.id.webview);
        UIHelper.initWebView(mWebView);

        mTVAuthorName = (TextView) root.findViewById(R.id.tv_name);
        mTVPubDate = (TextView) root.findViewById(R.id.tv_pub_date);
        mTVTitle = (TextView) root.findViewById(R.id.tv_title);

        mIVLabelRecommend = (ImageView) root.findViewById(R.id.iv_label_recommend);
        mIVLabelOriginate = (ImageView) root.findViewById(R.id.iv_label_originate);
        mIVAuthorPortrait = (ImageView) root.findViewById(R.id.iv_avatar);
        mIVFav = (ImageView) root.findViewById(R.id.iv_fav);

        mBtnRelation = (Button) root.findViewById(R.id.btn_relation);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBtnRelation.setElevation(0);
        }
        mETInput = (EditText) root.findViewById(R.id.et_input);

        mLayAbouts = (LinearLayout) root.findViewById(R.id.lay_blog_detail_about);
        mLayComments = (LinearLayout) root.findViewById(R.id.lay_blog_detail_comment);

        root.findViewById(R.id.iv_share).setOnClickListener(this);
        mIVFav.setOnClickListener(this);
        mBtnRelation.setOnClickListener(this);
        mETInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    handleSendComment();
                    return true;
                }
                return false;
            }
        });
        mETInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.e(TAG, "onKey:View:" + v.getClass().getSimpleName() + " keyCode:" + keyCode + " KeyEvent:" + event);
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    handleKeyDel();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 关注按钮
            case R.id.btn_relation: {
                handleRelation();
            }
            break;
            // 收藏
            case R.id.iv_fav: {
                handleFavorite();
            }
            break;
            // 分享
            case R.id.iv_share: {
                handleShare();
            }
            break;
        }
    }

    @Override
    protected void initData() {
        BlogDetail blog = (BlogDetail) mBundle.getSerializable("key");
        if (blog == null)
            return;

        mId = mCommentId = blog.getId();

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
            mBtnRelation.setEnabled(true);
            mBtnRelation.setText("关注");
            mBtnRelation.setOnClickListener(this);

        } else {
            mBtnRelation.setEnabled(false);
            mBtnRelation.setText("已关注");
        }

        if (blog.getAbouts() != null && blog.getAbouts().size() > 0) {
            final int size = blog.getAbouts().size();
            int i = 1;
            for (BlogDetail.About about : blog.getAbouts()) {
                View aboutLay = getLayoutInflater(null).inflate(R.layout.item_blog_detail_about_lay, mLayAbouts, true);
                ((TextView) aboutLay.findViewById(R.id.tv_title)).setText(about.title);
                ((TextView) aboutLay.findViewById(R.id.tv_info_view)).setText(about.viewCount);
                ((TextView) aboutLay.findViewById(R.id.tv_info_comment)).setText(about.commentCount);

                if (i == size) {
                    aboutLay.findViewById(R.id.line).setVisibility(View.INVISIBLE);
                }
                i++;
            }
        } else {
            setGone(R.id.tv_blog_detail_about);
            mLayAbouts.setVisibility(View.GONE);
        }

        if (blog.getAbouts() != null && blog.getAbouts().size() > 0) {
            final int size = blog.getComments().size();
            int i = 1;
            setText(R.id.tv_blog_detail_comment, String.format("评论(%s)", blog.getCommentCount()));
            for (final BlogDetail.Comment comment : blog.getComments()) {
                View aboutLay = getLayoutInflater(null).inflate(R.layout.item_blog_detail_comment_lay, mLayComments, true);
                mImgLoader.load(comment.authorPortrait).error(R.drawable.widget_dface)
                        .into(((ImageView) aboutLay.findViewById(R.id.iv_avatar)));

                ((TextView) aboutLay.findViewById(R.id.tv_name)).setText(comment.author);
                ((TextView) aboutLay.findViewById(R.id.tv_pub_date)).setText(comment.pubDate);
                ((TextView) aboutLay.findViewById(R.id.tv_content)).setText(comment.content);
                final long commentId = comment.id;
                aboutLay.findViewById(R.id.btn_comment).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCommentId = commentId;
                        mETInput.setHint(String.format("回复: %s", comment.author));
                    }
                });

                if (i == size) {
                    aboutLay.findViewById(R.id.line).setVisibility(View.INVISIBLE);
                }
                i++;
            }
        } else {
            setGone(R.id.tv_blog_detail_comment);
            mLayComments.setVisibility(View.GONE);
        }
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

    private String getWebViewBody(net.oschina.app.bean.blog.BlogDetail blog) {
        return String.format("<!DOCTYPE HTML><html><head>%s</head><body>%s</body></html>",
                linkCss + UIHelper.WEB_LOAD_IMAGES,
                UIHelper.setHtmlCotentSupportImagePreview(blog.getBody()));
    }

    private void handleKeyDel() {
        if (mCommentId != mId && TextUtils.isEmpty(mETInput.getText())) {
            mCommentId = mId;
            mETInput.setHint("评论作者~");
        }
    }

    private void handleRelation() {
        mOperator.toFollow();
    }

    private void handleFavorite() {
        mOperator.toFavorite();
    }

    private void handleShare() {
        mOperator.toShare();
    }

    private void handleSendComment() {
        mOperator.toSendComment(mCommentId, mETInput.getText().toString());
    }

    @Override
    public void toFavoriteOk() {
        mIVFav.setEnabled(false);
        mIVFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_faved_normal));
    }

    @Override
    public void toShareOk() {
        (Toast.makeText(getContext(), "分享成功", Toast.LENGTH_LONG)).show();
    }

    @Override
    public void toFollowOk() {
        mBtnRelation.setEnabled(false);
        mBtnRelation.setText("已关注");
    }

    @Override
    public void toSendCommentOk() {
        (Toast.makeText(getContext(), "评论成功", Toast.LENGTH_LONG)).show();
        mETInput.setText("");
    }
}
