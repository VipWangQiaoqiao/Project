package net.oschina.app.improve.detail.fragments;

import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.BlogDetail;
import net.oschina.app.improve.bean.simple.Comment;
import net.oschina.app.improve.behavior.FloatingAutoHideDownBehavior;
import net.oschina.app.improve.comment.CommentsView;
import net.oschina.app.improve.comment.OnCommentClickListener;
import net.oschina.app.improve.detail.contract.BlogDetailContract;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.widget.DetailAboutView;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by qiujuer
 * on 16/5/26.
 */

@SuppressWarnings("WeakerAccess")
public class BlogDetailFragment
        extends DetailFragment<BlogDetail, BlogDetailContract.View, BlogDetailContract.Operator>
        implements BlogDetailContract.View, View.OnClickListener, OnCommentClickListener {

    private long mId;
    private long mCommentId;
    private long mCommentAuthorId;

    @Bind(R.id.tv_name)
    TextView mTVAuthorName;
    @Bind(R.id.tv_pub_date)
    TextView mTVPubDate;
    @Bind(R.id.tv_title)
    TextView mTVTitle;

    @Bind(R.id.tv_blog_detail_abstract)
    TextView mTVAbstract;
    @Bind(R.id.iv_label_recommend)
    ImageView mIVLabelRecommend;
    @Bind(R.id.iv_label_originate)
    ImageView mIVLabelOriginate;
    @Bind(R.id.iv_avatar)
    ImageView mIVAuthorPortrait;
    @Bind(R.id.iv_fav)
    ImageView mIVFav;
    @Bind(R.id.btn_relation)
    Button mBtnRelation;

    @Bind(R.id.et_input)
    EditText mETInput;

    @Bind(R.id.lay_detail_about)
    DetailAboutView mAbouts;
    @Bind(R.id.lay_detail_comment)
    CommentsView mComments;
    @Bind(R.id.lay_blog_detail_abstract)
    LinearLayout mLayAbstract;

    @Bind(R.id.fragment_blog_detail)
    CoordinatorLayout mLayCoordinator;
    @Bind(R.id.lay_nsv)
    NestedScrollView mLayContent;
    @Bind(R.id.lay_option)
    View mLayBottom;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_general_blog_detail;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mAbouts.setTitle(getString(R.string.lable_about_title));
        registerScroller(mLayContent, mComments);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBtnRelation.setElevation(0);
        }

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

    @OnClick({R.id.iv_share, R.id.iv_fav, R.id.btn_relation, R.id.iv_avatar})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 关注按钮
            case R.id.btn_relation:
                handleRelation();
                break;
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
            case R.id.iv_avatar:
                OtherUserHomeActivity.show(getActivity(), mOperator.getData().getAuthorId());
                break;
            default:
                break;

        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initData() {
        BlogDetail blog = mOperator.getData();
        if (blog == null)
            return;

        mId = mCommentId = blog.getId();

        setCommentCount(blog.getCommentCount());
        setBodyContent(blog.getBody());

        mTVAuthorName.setText(blog.getAuthor());
        getImgLoader().load(blog.getAuthorPortrait()).error(R.mipmap.widget_dface).into(mIVAuthorPortrait);
        mIVAuthorPortrait.setOnClickListener(this);


        mTVPubDate.setText(StringUtils.formatSomeAgo(blog.getPubDate()));

        mTVTitle.setText(blog.getTitle());

        if (TextUtils.isEmpty(blog.getAbstract())) {
            mLayAbstract.setVisibility(View.GONE);
        } else {
            mTVAbstract.setText(blog.getAbstract());
            mLayAbstract.setVisibility(View.VISIBLE);
        }

        mIVLabelRecommend.setVisibility(blog.isRecommend() ? View.VISIBLE : View.GONE);
        mIVLabelOriginate.setImageDrawable(blog.isOriginal() ?
                getResources().getDrawable(R.mipmap.ic_label_originate) :
                getResources().getDrawable(R.mipmap.ic_label_reprint));

        toFollowOk(blog);
        toFavoriteOk(blog);

        setText(R.id.tv_info_view, String.valueOf(blog.getViewCount()));
        setText(R.id.tv_info_comment, String.valueOf(blog.getCommentCount()));

        mAbouts.setAbout(blog.getAbouts(), 3);

        mComments.setTitle(String.format("评论 (%s)", blog.getCommentCount()));
        mComments.init(blog.getId(), OSChinaApi.COMMENT_BLOG, blog.getCommentCount(), getImgLoader(), this);
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
        mOperator.toSendComment(mId, mCommentId, mCommentAuthorId, mETInput.getText().toString().trim());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void toFavoriteOk(BlogDetail blogDetail) {
        if (blogDetail.isFavorite())
            mIVFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_faved));
        else
            mIVFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav));
    }

    @Override
    public void toFollowOk(BlogDetail blogDetail) {
        if (blogDetail.getAuthorRelation() <= 2) {
            mBtnRelation.setText("已关注");
        } else {
            mBtnRelation.setText("关注");
        }
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
