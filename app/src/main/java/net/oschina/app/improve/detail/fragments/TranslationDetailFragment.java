package net.oschina.app.improve.detail.fragments;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.TranslationDetail;
import net.oschina.app.improve.bean.simple.Comment;
import net.oschina.app.improve.behavior.FloatingAutoHideDownBehavior;
import net.oschina.app.improve.comment.CommentsView;
import net.oschina.app.improve.comment.OnCommentClickListener;
import net.oschina.app.improve.detail.contract.TranslateDetailContract;
import net.oschina.app.improve.widget.DetailAboutView;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;

/**
 * Created by fei
 * on 16/06/28.
 */

public class TranslationDetailFragment extends DetailFragment<TranslationDetail,
        TranslateDetailContract.View, TranslateDetailContract.Operator>
        implements View.OnClickListener, TranslateDetailContract.View, OnCommentClickListener {

    private long mId;
    private TextView mTVAuthorName;
    private TextView mTVPubDate;
    private TextView mTVTitle;
    private ImageView mIVAuthorPortrait;
    private ImageView mIVFav;
    private EditText mETInput;
    private long mCommentId;
    private long mCommentAuthorId;
    private boolean mInputDoubleEmpty = false;
    private DetailAboutView mAbouts;
    private CommentsView mComments;
    private CoordinatorLayout mLayCoordinator;
    private NestedScrollView mLayContent;
    private View mLayBottom;
    private LinearLayout mAboutSoftware;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_general_news_detail;
    }


    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mTVAuthorName = (TextView) root.findViewById(R.id.tv_name);
        mTVPubDate = (TextView) root.findViewById(R.id.tv_pub_date);
        mTVTitle = (TextView) root.findViewById(R.id.tv_title);

        setGone(R.id.iv_info_view);
        setGone(R.id.tv_info_view);
        setGone(R.id.iv_info_comment);

        mIVAuthorPortrait = (ImageView) root.findViewById(R.id.iv_avatar);
        mIVFav = (ImageView) root.findViewById(R.id.iv_fav);
        mIVFav.setOnClickListener(this);

        mETInput = (EditText) root.findViewById(R.id.et_input);

        mAbouts = (DetailAboutView) root.findViewById(R.id.lay_detail_about);
        mAboutSoftware = (LinearLayout) root.findViewById(R.id.lay_about_software);
        mComments = (CommentsView) root.findViewById(R.id.lay_detail_comment);

        mLayCoordinator = (CoordinatorLayout) root.findViewById(R.id.fragment_blog_detail);
        mLayContent = (NestedScrollView) root.findViewById(R.id.lay_nsv);

        registerScroller(mLayContent, mComments);

        mLayBottom = root.findViewById(R.id.lay_option);

        root.findViewById(R.id.iv_share).setOnClickListener(this);
        mIVFav.setOnClickListener(this);
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
            // 相关软件
            // case R.id.lay_about_software:
            // SoftwareDetailActivity.show(getActivity(), mOperator.getData().getSoftware().getId());
            // break;
            // 收藏
            case R.id.iv_fav:
                handleFavorite();
                break;
            // 分享
            case R.id.iv_share:
                handleShare();
                break;
            default:
                break;
            // 评论列表
            //case R.id.tv_see_comment: {
            // UIUtil.showBlogComment(getActivity(), (int) mId,
            //  (int) mOperator.getNewsDetail().getId());
            //   }
            // break;
        }
    }

    @Override
    protected void initData() {
        final TranslationDetail translationDetail = mOperator.getData();
        if (translationDetail == null)
            return;

        mId = mCommentId = translationDetail.getId();

        setCommentCount(translationDetail.getCommentCount());
        setBodyContent(translationDetail.getBody());

        mTVAuthorName.setText(translationDetail.getAuthor());
        getImgLoader().load(translationDetail.getAuthorPortrait()).error(R.mipmap.widget_dface).into(mIVAuthorPortrait);

        mTVPubDate.setText(StringUtils.formatSomeAgo(translationDetail.getPubDate()));

        mTVTitle.setText(translationDetail.getTitle());

        toFavoriteOk(translationDetail);

        // setText(R.id.tv_info_view, String.valueOf(translationDetail.getViewCount()));
        setText(R.id.tv_info_comment, translationDetail.getPubDate());

        mAboutSoftware.setVisibility(View.GONE);
        mAbouts.setVisibility(View.GONE);

        mComments.setTitle(String.format("评论 (%s)", translationDetail.getCommentCount()));
        mComments.init(translationDetail.getId(), OSChinaApi.COMMENT_TRANSLATION, translationDetail.getCommentCount(), getImgLoader(), this);
    }

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
        mOperator.toSendComment(mId, mCommentId, mCommentAuthorId, mETInput.getText().toString());
    }


    @SuppressWarnings("deprecation")
    @Override
    public void toFavoriteOk(TranslationDetail translationDetail) {
        if (translationDetail.isFavorite())
            mIVFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_faved));
        else
            mIVFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav));
    }

    @Override
    public void toSendCommentOk(Comment comment) {
        (Toast.makeText(getContext(), "评论成功", Toast.LENGTH_LONG)).show();
        mETInput.setText("");
        mComments.addComment(comment, getImgLoader(), this);
        TDevice.hideSoftKeyboard(mETInput);
    }

    @Override
    public void onClick(View view, Comment comment) {
        FloatingAutoHideDownBehavior.showBottomLayout(mLayCoordinator, mLayContent, mLayBottom);
        mCommentId = comment.getId();
        mCommentAuthorId = comment.getAuthorId();
        mETInput.setHint(String.format("回复: %s", comment.getAuthor()));
        TDevice.showSoftKeyboard(mETInput);
    }
}
