package net.oschina.app.improve.detail.fragments;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.QuestionDetail;
import net.oschina.app.improve.bean.simple.Comment;
import net.oschina.app.improve.behavior.FloatingAutoHideDownBehavior;
import net.oschina.app.improve.comment.CommentsView;
import net.oschina.app.improve.comment.OnCommentClickListener;
import net.oschina.app.improve.detail.contract.QuestionDetailContract;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.UIHelper;

import java.util.List;

/**
 * Created by fei
 * on 16/5/26.
 * desc:
 */

public class QuestionDetailFragment extends DetailFragment<QuestionDetail, QuestionDetailContract.View, QuestionDetailContract.Operator>
        implements View.OnClickListener, QuestionDetailContract.View, OnCommentClickListener {
    private static final String TAG = "QuestionDetailFragment";
    private long mId;
    private TextView mTVAuthorName;
    private TextView mTVPubDate;
    private TextView mTVTitle;
    private ImageView mIVAuthorPortrait;
    private EditText mETInput;

    private long mCommentId;
    private long mCommentAuthorId;
    private CommentsView mComments;
    private CoordinatorLayout mLayCoordinator;
    private View mLayContent;
    private View mLayBottom;
    private TextView mTvTagOne;
    private TextView mTvTagTwo;
    private TextView mTvContent;
    private TextView mTvViewCount;
    private TextView mTvCommentCount;
    private ImageView mIVFav;


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
        mTvTagOne = (TextView) root.findViewById(R.id.tv_ques_detail_tag1);
        mTvTagTwo = (TextView) root.findViewById(R.id.tv_ques_detail_tag2);
        // mTvContent = (TextView) root.findViewById(R.id.tv_ques_detail_content);
        mTvViewCount = (TextView) root.findViewById(R.id.tv_info_view);
        mTvCommentCount = (TextView) root.findViewById(R.id.tv_info_comment);

        mIVFav = (ImageView) root.findViewById(R.id.iv_fav);

        mComments = (CommentsView) root.findViewById(R.id.lay_detail_comment);

        mLayCoordinator = (CoordinatorLayout) root.findViewById(R.id.activity_blog_detail);

        mLayContent = root.findViewById(R.id.lay_nsv);

        mLayBottom = root.findViewById(R.id.lay_option);

        mETInput = (EditText) root.findViewById(R.id.et_input);

        root.findViewById(R.id.iv_share).setOnClickListener(this);
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
            // case R.id.btn_relation: {
            // handleRelation();
            //   }
            // break;
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

        setBodyContent(body);

        mTVAuthorName.setText(questionDetail.getAuthor().trim());

        List<String> tags = questionDetail.getTags();
        if (tags == null || tags.isEmpty()) {
            mTvTagOne.setVisibility(View.GONE);
            mTvTagTwo.setVisibility(View.GONE);
        } else {
            int size = tags.size();
            if (size == 1) {
                mTvTagOne.setText(tags.get(0));
                mTvTagOne.setVisibility(View.VISIBLE);
                mTvTagTwo.setVisibility(View.INVISIBLE);
            } else {
                mTvTagOne.setText(tags.get(0));
                mTvTagOne.setVisibility(View.VISIBLE);
                mTvTagTwo.setText(tags.get(1));
                mTvTagTwo.setVisibility(View.VISIBLE);
            }
        }

        String time = String.format("%s (%s)", StringUtils.friendly_time(questionDetail.getPubDate()), questionDetail.getPubDate());
        mTVPubDate.setText(time);

        mTVTitle.setText(questionDetail.getTitle());

        toFavoriteOk(questionDetail);

        setText(R.id.tv_info_view, String.valueOf(questionDetail.getViewCount()));
        setText(R.id.tv_info_comment, String.valueOf(questionDetail.getCommentCount()));

        TextView lable = (TextView) mComments.getChildAt(0);
        lable.setText(String.format("%s (%d)", "回答", questionDetail.getCommentCount()));

        mComments.init(questionDetail.getId(), OSChinaApi.COMMENT_QUESTION, questionDetail.getCommentCount(), getImgLoader(), this);

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


    private void handleFavorite() {
        mOperator.toFavorite();
    }

    private void handleShare() {
        mOperator.toShare();
    }

    private void handleSendComment() {
        mOperator.toSendComment(mCommentId, mCommentAuthorId, mETInput.getText().toString());
    }


    @Override
    public void toFavoriteOk(QuestionDetail questionDetail) {
        if (questionDetail.isFavorite())
            mIVFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_faved_normal));
        else
            mIVFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav_normal));

    }


    @Override
    public void toSendCommentOk() {
        (Toast.makeText(getContext(), "评论成功", Toast.LENGTH_LONG)).show();
        mETInput.setText("");
    }


    @Override
    public void scrollToComment() {
        mLayContent.scrollTo(0, mComments.getTop());
    }

    @Override
    public void onClick(View view, Comment comment) {

        FloatingAutoHideDownBehavior.showBottomLayout(mLayCoordinator, mLayContent, mLayBottom);
        mCommentId = comment.getId();
        mCommentAuthorId = comment.getAuthorId();
        mETInput.setHint(String.format("回复: %s", comment.getAuthor()));
    }

}
