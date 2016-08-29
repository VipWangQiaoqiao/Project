package net.oschina.app.improve.detail.fragments;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.QuestionDetail;
import net.oschina.app.improve.bean.simple.Comment;
import net.oschina.app.improve.bean.simple.CommentEX;
import net.oschina.app.improve.comment.CommentExsView;
import net.oschina.app.improve.comment.OnCommentClickListener;
import net.oschina.app.improve.detail.contract.QuestionDetailContract;
import net.oschina.app.improve.widget.FlowLayout;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import java.util.List;

/**
 * Created by fei
 * on 16/5/26.
 * desc:
 */

public class QuestionDetailFragment extends DetailFragment<QuestionDetail, QuestionDetailContract.View, QuestionDetailContract.Operator>
        implements View.OnClickListener, QuestionDetailContract.View, OnCommentClickListener {
    private long mId;
    private TextView mTVAuthorName;
    private TextView mTVPubDate;
    private TextView mTVTitle;
    private EditText mETInput;

    private long mCommentId;
    private long mCommentAuthorId;
    private CommentExsView mComments;
    private CoordinatorLayout mLayCoordinator;
    private NestedScrollView mLayContent;
    private View mLayBottom;
    private TextView mTvTagOne;

    private ImageView mIVFav;
    private FlowLayout mFlowLayout;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_general_question_detail;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mTVAuthorName = (TextView) root.findViewById(R.id.tv_ques_detail_author);
        mTVPubDate = (TextView) root.findViewById(R.id.tv_ques_detail_pub_date);
        mTVTitle = (TextView) root.findViewById(R.id.tv_ques_detail_title);

        mFlowLayout = (FlowLayout) root.findViewById(R.id.ques_detail_flow);
        mTvTagOne = (TextView) root.findViewById(R.id.tv_ques_detail_tag);


        mIVFav = (ImageView) root.findViewById(R.id.iv_fav);
        mIVFav.setOnClickListener(this);

        mComments = (CommentExsView) root.findViewById(R.id.lay_detail_comment);
        mLayCoordinator = (CoordinatorLayout) root.findViewById(R.id.activity_blog_detail);
        mLayContent = (NestedScrollView) root.findViewById(R.id.lay_nsv);

        registerScroller(mLayContent, mComments);

        mLayBottom = root.findViewById(R.id.lay_option);

        mETInput = (EditText) root.findViewById(R.id.et_input);

        root.findViewById(R.id.iv_share).setOnClickListener(this);
        mETInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
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
            // 收藏
            case R.id.iv_fav:
                handleFavorite();
                break;
            // 分享
            case R.id.iv_share:
                handleShare();
                break;
            // 评论列表
            case R.id.tv_see_more_comment:
                UIHelper.showBlogComment(getActivity(), (int) mId,
                        (int) mOperator.getData().getAuthorId());
                System.out.println("-------------------------->");
                break;
        }
    }

    @Override
    protected void initData() {

        QuestionDetail questionDetail = mOperator.getData();
        if (questionDetail == null)
            return;

        mId = mCommentId = questionDetail.getId();

        String body = questionDetail.getBody();

        setCommentCount(questionDetail.getCommentCount());
        setBodyContent(body);

        String author = questionDetail.getAuthor();
        if (!TextUtils.isEmpty(author))
            mTVAuthorName.setText(author);

        List<String> tags = questionDetail.getTags();

        // mFlowLayout.removeAllViews();
        if (tags != null && !tags.isEmpty()) {
            for (String tag : tags) {
                TextView tvTag = (TextView) getActivity().getLayoutInflater().inflate(R.layout.flowlayout_item, mFlowLayout, false);
                if (!TextUtils.isEmpty(tag))
                    tvTag.setText(tag);
                mFlowLayout.addView(tvTag);
            }
        }

        mTVPubDate.setText(StringUtils.formatSomeAgo(questionDetail.getPubDate()));

        String title = questionDetail.getTitle();
        if (!TextUtils.isEmpty(title))
            mTVTitle.setText(title);

        toFavoriteOk(questionDetail);

        setText(R.id.tv_info_view, String.valueOf(questionDetail.getViewCount()));
        setText(R.id.tv_info_comment, String.valueOf(questionDetail.getCommentCount()));

        mComments.setTitle(String.format("回答 (%s)", questionDetail.getCommentCount()));
        mComments.init(questionDetail.getId(), OSChinaApi.COMMENT_QUESTION,
                questionDetail.getCommentCount(), getImgLoader(), this);

    }

    private boolean mInputDoubleEmpty = false;

    private void handleKeyDel() {
        if (mCommentId != mId) {
            if (TextUtils.isEmpty(mETInput.getText().toString().trim())) {
                if (mInputDoubleEmpty) {
                    mCommentId = mId;
                    mCommentAuthorId = 0;
                    mETInput.setHint("我要回答");
                } else {
                    mInputDoubleEmpty = true;
                }
            } else {
                mInputDoubleEmpty = false;
            }
        }
    }


    private void handleFavorite() {
        mOperator.toFavorite();
    }

    private void handleShare() {
        mOperator.toShare();
    }

    private void handleSendComment() {
        mOperator.toSendComment(mId, mCommentId, mCommentAuthorId, mETInput.getText().toString().trim());
    }


    @SuppressWarnings("deprecation")
    @Override
    public void toFavoriteOk(QuestionDetail questionDetail) {
        if (questionDetail.isFavorite())
            mIVFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_faved));
        else
            mIVFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav));
    }


    @Override
    public void toSendCommentOk(CommentEX commentEX) {
        (Toast.makeText(getContext(), "评论成功", Toast.LENGTH_LONG)).show();
        mETInput.setText("");
        mComments.addComment(commentEX, getImgLoader(), null);
        TDevice.hideSoftKeyboard(mETInput);
    }

    @Override
    public void onClick(View view, Comment comment) {

    }
}
