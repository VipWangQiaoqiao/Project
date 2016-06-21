package net.oschina.app.improve.detail.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
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

import net.oschina.app.R;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.improve.bean.QuestionDetail;
import net.oschina.app.improve.detail.contract.QuestionDetailContract;
import net.oschina.app.improve.fragments.base.BaseFragment;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.MyLinkMovementMethod;
import net.oschina.app.widget.MyURLSpan;
import net.oschina.app.widget.TweetTextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by qiujuer
 * on 16/5/26.
 */

public class QuestionDetailFragment extends BaseFragment implements View.OnClickListener, QuestionDetailContract.View {
    private long mId;
    private WebView mWebView;
    private TextView mTVAuthorName;
    private TextView mTVPubDate;
    private TextView mTVTitle;
    private TextView mTVAbstract;
    private ImageView mIVLabelRecommend;
    private ImageView mIVLabelOriginate;
    private ImageView mIVAuthorPortrait;
    private ImageView mIVFav;
    private Button mBtnRelation;
    private EditText mETInput;

    private LinearLayout mLayAbouts;
    private LinearLayout mLayComments;
    private LinearLayout mLayAbstract;

    private long mCommentId;
    private long mCommentAuthorId;

    private QuestionDetailContract.Operator mOperator;


    public static QuestionDetailFragment instantiate(QuestionDetailContract.Operator operator, QuestionDetail detail) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("key", detail);
        QuestionDetailFragment fragment = new QuestionDetailFragment();
        fragment.setArguments(bundle);
        fragment.mOperator = operator;
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_post_answer_detail;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onDestroy() {
        WebView view = mWebView;
        if (view != null) {
            mWebView = null;
            view.getSettings().setJavaScriptEnabled(true);
            view.removeJavascriptInterface("mWebViewImageListener");
            view.removeAllViewsInLayout();
            view.setWebChromeClient(null);
            view.removeAllViews();
            view.destroy();
        }
        mOperator = null;

        super.onDestroy();
    }

    @Override
    protected void initWidget(View root) {
        WebView webView = new WebView(getActivity());
        webView.setHorizontalScrollBarEnabled(false);
        UIHelper.initWebView(webView);
        UIHelper.addWebImageShow(getActivity(), webView);
        // ((FrameLayout) root.findViewById(R.id.lay_webview)).addView(webView);
        //mWebView = webView;

        mTVAuthorName = (TextView) root.findViewById(R.id.tv_ques_detail_author);
        mTVPubDate = (TextView) root.findViewById(R.id.tv_ques_detail_pub_date);
        mTVTitle = (TextView) root.findViewById(R.id.tv_ques_detail_title);
        //mTVAbstract = (TextView) root.findViewById(R.id.tv_blog_detail_abstract);

        // mIVLabelRecommend = (ImageView) root.findViewById(R.id.iv_label_recommend);
        //mIVLabelOriginate = (ImageView) root.findViewById(R.id.iv_label_originate);
        // mIVAuthorPortrait = (ImageView) root.findViewById(R.id.iv_avatar);
        // mIVFav = (ImageView) root.findViewById(R.id.iv_fav);

        // mBtnRelation = (Button) root.findViewById(R.id.btn_relation);

        mETInput = (EditText) root.findViewById(R.id.et_input);

        root.findViewById(R.id.iv_share).setOnClickListener(this);
//        mIVFav.setOnClickListener(this);
//        mBtnRelation.setOnClickListener(this);
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
            // 评论列表
            case R.id.tv_see_comment: {
                UIHelper.showBlogComment(getActivity(), (int) mId,
                        (int) mOperator.getQuestionDetail().getAuthorId());
            }
            break;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initData() {
        QuestionDetail questionDetail = (QuestionDetail) mBundle.getSerializable("key");
        if (questionDetail == null)
            return;

        mId = mCommentId = questionDetail.getId();

        String body = getWebViewBody(questionDetail);

        mTVAuthorName.setText(questionDetail.getAuthor());

        String time = String.format("%s (%s)", StringUtils.friendly_time(getStrTime(questionDetail.getPubDate())), questionDetail.getPubDate());
        mTVPubDate.setText(time);

        mTVTitle.setText(questionDetail.getTitle());

        toFavoriteOk(questionDetail);

    }


    private static String getStrTime(String cc_time) {
        try {
            long lTime = Long.valueOf(cc_time);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(new Date(lTime));
        } catch (Exception e) {
            return cc_time;
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

    private String getWebViewBody(QuestionDetail questionDetail) {
        return String.format("<!DOCTYPE HTML><html><head>%s</head><body><div class=\"body-content\">%s</div></body></html>",
                linkCss + UIHelper.WEB_LOAD_IMAGES,
                UIHelper.setHtmlCotentSupportImagePreview(questionDetail.getBody()));
    }

    private boolean mInputDoubleEmpty = false;

    private void handleKeyDel() {
        if (mCommentId != mId) {
            if (TextUtils.isEmpty(mETInput.getText())) {
                if (mInputDoubleEmpty) {
                    mCommentId = mId;
                    mCommentAuthorId = 0;
                    mETInput.setHint("发表评论");
                } else {
                    mInputDoubleEmpty = true;
                }
            } else {
                mInputDoubleEmpty = false;
            }
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
        mOperator.toSendComment(mCommentId, mCommentAuthorId, mETInput.getText().toString());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void toFavoriteOk(QuestionDetail questionDetail) {

    }

    @Override
    public void toShareOk() {
        (Toast.makeText(getContext(), "分享成功", Toast.LENGTH_LONG)).show();
    }

    @Override
    public void toFollowOk(QuestionDetail questionDetail) {
        mBtnRelation.setEnabled(false);
        mBtnRelation.setText("已关注");
    }


    @Override
    public void toSendCommentOk() {
        (Toast.makeText(getContext(), "评论成功", Toast.LENGTH_LONG)).show();
        mETInput.setText("");
    }

    private static void formatHtml(Resources resources, TextView textView, String str) {
        textView.setMovementMethod(MyLinkMovementMethod.a());
        textView.setFocusable(false);
        textView.setLongClickable(false);

        if (textView instanceof TweetTextView) {
            ((TweetTextView) textView).setDispatchToParent(true);
        }

        str = TweetTextView.modifyPath(str);
        Spanned span = Html.fromHtml(str);
        span = InputHelper.displayEmoji(resources, span.toString());
        textView.setText(span);
        MyURLSpan.parseLinkText(textView, span);
    }
}
