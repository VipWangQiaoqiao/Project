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
import net.oschina.app.improve.bean.NewsDetail;
import net.oschina.app.improve.bean.Software;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.bean.simple.Comment;
import net.oschina.app.improve.behavior.FloatingAutoHideDownBehavior;
import net.oschina.app.improve.comment.CommentsView;
import net.oschina.app.improve.comment.OnCommentClickListener;
import net.oschina.app.improve.detail.activities.NewsDetailActivity;
import net.oschina.app.improve.detail.activities.SoftwareDetailActivity;
import net.oschina.app.improve.detail.activities.TranslateDetailActivity;
import net.oschina.app.improve.detail.contract.NewsDetailContract;
import net.oschina.app.improve.widget.DetailAboutView;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;

/**
 * Created by qiujuer
 * on 16/5/26.
 */

public class NewsDetailFragment extends DetailFragment<NewsDetail, NewsDetailContract.View, NewsDetailContract.Operator>
        implements View.OnClickListener, NewsDetailContract.View, OnCommentClickListener {

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
    private DetailAboutView mSoft;
    private DetailAboutView mAbouts;
    private CommentsView mComments;
    private CoordinatorLayout mLayCoordinator;
    private NestedScrollView mLayContent;
    private View mLayBottom;


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

        mIVAuthorPortrait = (ImageView) root.findViewById(R.id.iv_avatar);
        mIVFav = (ImageView) root.findViewById(R.id.iv_fav);
        mIVFav.setOnClickListener(this);

        mETInput = (EditText) root.findViewById(R.id.et_input);

        mAbouts = (DetailAboutView) root.findViewById(R.id.lay_detail_about);
        mSoft = (DetailAboutView) root.findViewById(R.id.lay_detail_software);
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
            case R.id.lay_detail_software:
                SoftwareDetailActivity.show(getActivity(),mOperator.getData().getSoftware().getId());
                break;
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
            // UIHelper.showBlogComment(getActivity(), (int) mId,
            //  (int) mOperator.getNewsDetail().getId());
            //   }
            // break;
        }
    }

    @Override
    protected void initData() {
        final NewsDetail newsDetail = mOperator.getData();
        if (newsDetail == null)
            return;

        mId = mCommentId = newsDetail.getId();

        setCommentCount(newsDetail.getCommentCount());
        setBodyContent(newsDetail.getBody());

        mTVAuthorName.setText(newsDetail.getAuthor());
        getImgLoader().load(newsDetail.getAuthorPortrait()).error(R.drawable.widget_dface).into(mIVAuthorPortrait);

        String time = String.format("%s", StringUtils.friendly_time(newsDetail.getPubDate()));
        mTVPubDate.setText(time);

        mTVTitle.setText(newsDetail.getTitle());

        toFavoriteOk(newsDetail);

        setText(R.id.tv_info_view, String.valueOf(newsDetail.getViewCount()));
        setText(R.id.tv_info_comment, String.valueOf(newsDetail.getCommentCount()));

        Software software = newsDetail.getSoftware();
        if (software != null) {

            TextView lable = (TextView) mSoft.getChildAt(0);
            lable.setText(getResources().getString(R.string.lable_software));
            View child = getActivity().getLayoutInflater().inflate(R.layout.lay_blog_detail_about, mSoft, false);
            ((TextView) child.findViewById(R.id.tv_title)).setText(software.getName());
            View layInfo = child.findViewById(R.id.lay_info_view_comment);
            layInfo.setVisibility(View.GONE);
            View line = child.findViewById(R.id.line);
            line.setVisibility(View.GONE);
            mSoft.addView(child, 1);
            mSoft.setVisibility(View.VISIBLE);
            mSoft.setOnClickListener(this);
        } else {
            mSoft.setVisibility(View.GONE);
        }


        mAbouts.setAbout(newsDetail.getAbouts(), new DetailAboutView.OnAboutClickListener() {
            @Override
            public void onClick(View view, About about) {
                int type = newsDetail.getType();
                if (type == 6) {
                    NewsDetailActivity.show(getActivity(), about.getId());
                } else if (type == 4) {
                    TranslateDetailActivity.show(getActivity(), about.getId());
                }
            }
        });


        mComments.init(newsDetail.getId(), OSChinaApi.COMMENT_NEWS, newsDetail.getCommentCount(), getImgLoader(), this);
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
        TDevice.hideSoftKeyboard(mETInput);
        mOperator.toSendComment(mId, mCommentId, mCommentAuthorId, mETInput.getText().toString());
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
    public void toSendCommentOk(Comment comment) {
        (Toast.makeText(getContext(), "评论成功", Toast.LENGTH_LONG)).show();
        mETInput.setText("");
        mComments.addComment(comment, getImgLoader(), this);
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
