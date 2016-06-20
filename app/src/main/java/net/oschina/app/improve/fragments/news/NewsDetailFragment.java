package net.oschina.app.improve.fragments.news;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.oschina.app.R;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.improve.detail.activities.BlogDetailActivity;
import net.oschina.app.improve.bean.NewsDetail;
import net.oschina.app.improve.bean.Software;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.bean.simple.Comment;
import net.oschina.app.improve.contract.NewsDetailContract;
import net.oschina.app.improve.fragments.base.BaseFragment;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.MyLinkMovementMethod;
import net.oschina.app.widget.MyURLSpan;
import net.oschina.app.widget.TweetTextView;
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.drawable.shape.BorderShape;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by qiujuer
 * on 16/5/26.
 */

public class NewsDetailFragment extends BaseFragment implements View.OnClickListener, NewsDetailContract.View {
    private static final String TAG = "NewsDetailFragment";
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
    private LinearLayout mLayAbstract;

    private long mCommentId;
    private long mCommentAuthorId;

    private NewsDetailContract.Operator mOperator;


    public static NewsDetailFragment instantiate(NewsDetailContract.Operator operator, NewsDetail detail) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("key", detail);
        NewsDetailFragment fragment = new NewsDetailFragment();
        fragment.setArguments(bundle);
        fragment.mOperator = operator;
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_general_news_detail;
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
        ((FrameLayout) root.findViewById(R.id.lay_webview)).addView(webView);
        mWebView = webView;

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
        mLayAbstract = (LinearLayout) root.findViewById(R.id.lay_blog_detail_abstract);


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
            //case R.id.tv_see_comment: {
            // UIHelper.showBlogComment(getActivity(), (int) mId,
            //  (int) mOperator.getNewsDetail().getId());
            //   }
            // break;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initData() {
        final NewsDetail newsDetail = (NewsDetail) mBundle.getSerializable("key");
        if (newsDetail == null)
            return;

        mId = mCommentId = newsDetail.getId();

        String body = getWebViewBody(newsDetail);
        mWebView.loadDataWithBaseURL("", body, "text/html", "UTF-8", "");

        mTVAuthorName.setText(newsDetail.getAuthor());
        getImgLoader().load(newsDetail.getAuthorPortrait()).error(R.drawable.widget_dface).into(mIVAuthorPortrait);

        if (newsDetail.getPubDate() != null) {
            String time = String.format("%s (%s)", StringUtils.friendly_time(getStrTime(newsDetail.getPubDate().trim())), newsDetail.getPubDate().trim());
            mTVPubDate.setText(time);
        }

        mTVTitle.setText(newsDetail.getTitle());

        mIVLabelRecommend.setVisibility(View.VISIBLE);
        mIVLabelOriginate.setImageDrawable(getResources().getDrawable(R.drawable.ic_label_reprint));

        //这个地方，貌似没有这个相互关注关系字段，所以先用favorite
        if (newsDetail.isFavorite()) {
            mBtnRelation.setEnabled(true);
            mBtnRelation.setText("关注");
            mBtnRelation.setOnClickListener(this);
        } else {
            mBtnRelation.setEnabled(false);
            mBtnRelation.setText("已关注");
        }

        toFavoriteOk(newsDetail);
        setText(R.id.tv_info_view, String.valueOf(newsDetail.getViewCount()));

        LayoutInflater inflater = getLayoutInflater(null);
        Log.d(TAG, "initData: ---->" + newsDetail.getAbouts().size());
        if (newsDetail.getAbouts() != null && newsDetail.getAbouts().size() > 0) {

            int i = 1;
            for (final About about : newsDetail.getAbouts()) {
                if (about == null)
                    continue;
                @SuppressLint("InflateParams") View lay = inflater.inflate(R.layout.lay_blog_detail_about, null, false);
                ((TextView) lay.findViewById(R.id.tv_title)).setText(about.getTitle());

                View layInfo = lay.findViewById(R.id.lay_info_view_comment);
                ((TextView) layInfo.findViewById(R.id.tv_info_view)).setText(String.valueOf(about.getViewCount()));
                ((TextView) layInfo.findViewById(R.id.tv_info_comment)).setText(String.valueOf(about.getCommentCount()));

                if (i == 1) {
                    lay.findViewById(R.id.line).setVisibility(View.INVISIBLE);
                }
                lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BlogDetailActivity.show(getActivity(), about.getId());
                    }
                });
                i++;

                mLayAbouts.addView(lay, 0);
            }
        } else {
            setGone(R.id.tv_blog_detail_about);
            mLayAbouts.setVisibility(View.GONE);
        }

        Software software = newsDetail.getSoftware();
        if (software != null) {
            setVisibility(R.id.tv_blog_detail_about);
            mLayAbouts.setVisibility(View.VISIBLE);
            setText(R.id.tv_blog_detail_about, "软件资讯");

            View lay = inflater.inflate(R.layout.lay_blog_detail_about, null, false);
            ((TextView) lay.findViewById(R.id.tv_title)).setText(software.getName());
            ImageView ivViewCount = (ImageView) lay.findViewById(R.id.iv_info_view);
            ivViewCount.setVisibility(View.GONE);
            ImageView ivCommentCount = (ImageView) lay.findViewById(R.id.iv_info_comment);
            ivCommentCount.setVisibility(View.GONE);
            View layInfo = lay.findViewById(R.id.lay_info_view_comment);
            (layInfo.findViewById(R.id.tv_info_view)).setVisibility(View.GONE);
            (layInfo.findViewById(R.id.tv_info_comment)).setVisibility(View.GONE);

            lay.findViewById(R.id.line).setVisibility(View.INVISIBLE);
            lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //software.show(getActivity(), newsDetail.getId());
                }
            });

            mLayAbouts.addView(lay, 0);
        } else {
            setGone(R.id.tv_blog_detail_about);
            mLayAbouts.setVisibility(View.GONE);
        }


        setText(R.id.tv_info_comment, String.valueOf(newsDetail.getCommentCount()));
        if (newsDetail.getComments() != null && newsDetail.getComments().size() > 0) {

            if (newsDetail.getComments().size() < newsDetail.getCommentCount()) {
                setVisibility(R.id.tv_see_comment);
                mLayComments.findViewById(R.id.tv_see_comment).setOnClickListener(this);
            } else {
                setGone(R.id.tv_see_comment);
            }

            final Resources resources = getResources();
            for (final Comment comment : newsDetail.getComments()) {
                if (comment == null)
                    continue;

                @SuppressLint("InflateParams") ViewGroup lay = (ViewGroup) inflater.inflate(R.layout.lay_blog_detail_comment, null, false);
                getImgLoader().load(comment.getAuthorPortrait()).error(R.drawable.widget_dface)
                        .into(((ImageView) lay.findViewById(R.id.iv_avatar)));

                ((TextView) lay.findViewById(R.id.tv_name)).setText(comment.getAuthor());

                TweetTextView content = ((TweetTextView) lay.findViewById(R.id.tv_content));
                formatHtml(resources, content, comment.getContent());

                if (comment.getRefer() != null) {
                    // 最多5层
                    View view = getReferLayout(comment.getRefer(), inflater, 5);
                    lay.addView(view, lay.indexOfChild(content));
                }

                ((TextView) lay.findViewById(R.id.tv_pub_date)).setText(
                        StringUtils.friendly_time(getStrTime(comment.getPubDate())));

                final long commentId = comment.getId();
                lay.findViewById(R.id.btn_comment).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCommentId = commentId;
                        mCommentAuthorId = comment.getAuthorId();
                        mETInput.setHint(String.format("回复: %s", comment.getAuthor()));
                    }
                });

                mLayComments.addView(lay, 0);
            }


        } else {
            setGone(R.id.tv_blog_detail_comment);
            mLayComments.setVisibility(View.GONE);
        }

    }

    @SuppressWarnings("deprecation")
    private View getReferLayout(Comment.Refer refer, LayoutInflater inflater, int count) {
        final Context context = getContext();

        @SuppressLint("InflateParams") ViewGroup lay = (ViewGroup) inflater.inflate(R.layout.lay_blog_detail_comment_refer, null, false);
        ShapeDrawable drawable = new ShapeDrawable(new BorderShape(new RectF(Ui.dipToPx(getContext(), 1), 0, 0, 0)));
        drawable.getPaint().setColor(0xffd7d6da);
        lay.findViewById(R.id.lay_blog_detail_comment_refer).setBackgroundDrawable(drawable);

        TextView textView = ((TextView) lay.findViewById(R.id.tv_blog_detail_comment_refer));
        drawable = new ShapeDrawable(new BorderShape(new RectF(0, 0, 0, 1)));
        drawable.getPaint().setColor(0xffd7d6da);
        textView.setBackgroundDrawable(drawable);

        formatHtml(context.getResources(), textView, refer.author + ":<br>" + refer.content);


        if (refer.refer != null && (--count) > 0) {
            View view = getReferLayout(refer.refer, inflater, count);
            lay.addView(view, lay.indexOfChild(textView));
        }

        return lay;
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

    private String getWebViewBody(NewsDetail newsDetail) {
        if (newsDetail.getBody() != null) {
            return String.format("<!DOCTYPE HTML><html><head>%s</head><body><div class=\"body-content\">%s</div></body></html>",
                    linkCss + UIHelper.WEB_LOAD_IMAGES,
                    UIHelper.setHtmlCotentSupportImagePreview(newsDetail.getBody()));
        } else {
            return null;
        }
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
    public void toFavoriteOk(NewsDetail newsDetail) {
        if (newsDetail.isFavorite())
            mIVFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_faved_normal));
        else
            mIVFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav_normal));
    }

    @Override
    public void toShareOk() {
        (Toast.makeText(getContext(), "分享成功", Toast.LENGTH_LONG)).show();
    }

    @Override
    public void toFollowOk(NewsDetail newsDetail) {
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
