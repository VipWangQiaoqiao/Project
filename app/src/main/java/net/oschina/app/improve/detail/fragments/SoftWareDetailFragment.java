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
import net.oschina.app.improve.bean.SoftwareDetail;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.bean.simple.Comment;
import net.oschina.app.improve.behavior.FloatingAutoHideDownBehavior;
import net.oschina.app.improve.comment.CommentsView;
import net.oschina.app.improve.comment.OnCommentClickListener;
import net.oschina.app.improve.detail.activities.NewsDetailActivity;
import net.oschina.app.improve.detail.contract.SoftDetailContract;
import net.oschina.app.improve.widget.DetailAboutView;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by fei on 2016/6/20.
 * desc:  software detail module
 */
public class SoftWareDetailFragment extends DetailFragment<SoftwareDetail, SoftDetailContract.View, SoftDetailContract.Operator>
        implements View.OnClickListener, SoftDetailContract.View, OnCommentClickListener {

    private long mId;

    @Bind(R.id.iv_label_recommend)
    ImageView ivRecomment;

    @Bind(R.id.iv_software_icon)
    ImageView ivIcon;
    @Bind(R.id.tv_software_name)
    TextView tvName;

    @Bind(R.id.bt_software_home)
    TextView btWebsite;
    @Bind(R.id.bt_software_document)
    TextView btDocument;

    @Bind(R.id.tv_software_body)
    TextView tvBody;

    @Bind(R.id.tv_software_authorName)
    TextView tvAuthor;
    @Bind(R.id.tv_software_law)
    TextView tvLicense;
    @Bind(R.id.tv_software_language)
    TextView tvLanguage;
    @Bind(R.id.tv_software_system)
    TextView tvSystem;
    @Bind(R.id.tv_software_record_time)
    TextView tvRecordTime;


    @Bind(R.id.lay_detail_about)
    DetailAboutView mAbouts;
    @Bind(R.id.lay_detail_comment)
    CommentsView mComments;

    @Bind(R.id.fragment_blog_detail)
    CoordinatorLayout mLayCoordinator;
    @Bind(R.id.lay_nsv)
    NestedScrollView mLayContent;

    @Bind(R.id.lay_option)
    LinearLayout mLayBottom;
    @Bind(R.id.et_input)
    EditText mETInput;
    @Bind(R.id.iv_fav)
    ImageView mIVFav;
    @Bind(R.id.iv_share)
    ImageView ivShare;


    private long mCommentId;
    private long mCommentAuthorId;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_general_soft_detail;
    }


    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        registerScroller(mLayContent, mComments);
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

    @OnClick({R.id.iv_share, R.id.iv_fav, R.id.bt_software_home, R.id.bt_software_document, R.id.tv_see_more_comment})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_share:
                // 分享
                handleShare();
                break;
            case R.id.iv_fav:
                // 收藏
                handleFavorite();
                break;
            case R.id.bt_software_home:
                //进入官网
                UIHelper.showUrlRedirect(getActivity(), mOperator.getData().getHomePage());
                break;
            case R.id.bt_software_document:
                //软件文档
                UIHelper.showUrlRedirect(getActivity(), mOperator.getData().getDocument());
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initData() {

        final SoftwareDetail softwareDetail = mOperator.getData();
        if (softwareDetail == null)
            return;
        mId = mCommentId = softwareDetail.getId();

        if (softwareDetail.isRecommend()) {
            ivRecomment.setVisibility(View.VISIBLE);
        } else {
            ivRecomment.setVisibility(View.INVISIBLE);
        }
        tvName.setText(softwareDetail.getName().trim());
        tvBody.setText(softwareDetail.getBody().trim());

        tvAuthor.setText(softwareDetail.getAuthor().trim());
        tvLicense.setText(softwareDetail.getLicense().trim());
        tvLanguage.setText(softwareDetail.getLanguage().trim());
        tvSystem.setText(softwareDetail.getSupportOS());
        tvRecordTime.setText(softwareDetail.getCollectionDate().trim());

        setCommentCount(softwareDetail.getCommentCount());
        setBodyContent(softwareDetail.getBody());
        getImgLoader().load(softwareDetail.getLogo()).error(R.drawable.widget_dface).into(ivIcon);

        toFavoriteOk(softwareDetail);

        mAbouts.setAbout(softwareDetail.getAbouts(), new DetailAboutView.OnAboutClickListener() {
            @Override
            public void onClick(View view, About about) {
                NewsDetailActivity.show(getActivity(), about.getId());
            }
        });

        mComments.init(softwareDetail.getId(), OSChinaApi.COMMENT_SOFT, softwareDetail.getCommentCount(), getImgLoader(), this);
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
        TDevice.hideSoftKeyboard(mETInput);
        mOperator.toSendComment(mId, mCommentId, mCommentAuthorId, mETInput.getText().toString());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void toFavoriteOk(SoftwareDetail softwareDetail) {
        if (softwareDetail.isFavorite())
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
