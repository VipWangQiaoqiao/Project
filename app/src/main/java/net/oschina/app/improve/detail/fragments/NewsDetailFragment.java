package net.oschina.app.improve.detail.fragments;

import android.content.Context;
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
import net.oschina.app.improve.bean.NewsDetail;
import net.oschina.app.improve.bean.Software;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.detail.activities.NewsDetailActivity;
import net.oschina.app.improve.detail.contract.NewsDetailContract;
import net.oschina.app.improve.widget.DetailAboutView;
import net.oschina.app.improve.widget.DetailCommentView;
import net.oschina.app.util.StringUtils;

/**
 * Created by qiujuer
 * on 16/5/26.
 */

public class NewsDetailFragment extends DetailFragment<NewsDetail, NewsDetailContract.View, NewsDetailContract.Operator> implements View.OnClickListener, NewsDetailContract.View {

    private static final String TAG = "NewsDetailFragment";
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
    private DetailCommentView mLayComments;
    private DetailAboutView mSoft;


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

        mETInput = (EditText) root.findViewById(R.id.et_input);

        mAbouts = (DetailAboutView) root.findViewById(R.id.lay_detail_about);
        mLayComments = (DetailCommentView) root.findViewById(R.id.lay_detail_comment);

        mSoft = (DetailAboutView) root.findViewById(R.id.lay_detail_software);

        root.findViewById(R.id.iv_share).setOnClickListener(this);
        mIVFav.setOnClickListener(this);
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
            // 相关软件
            case R.id.lay_detail_software:


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

    @SuppressWarnings("deprecation")
    @Override
    protected void initData() {
        NewsDetail newsDetail = mOperator.getData();
        if (newsDetail == null)
            return;

        mId = mCommentId = newsDetail.getId();

        setBodyContent(newsDetail.getBody());

        mTVAuthorName.setText(newsDetail.getAuthor());
        getImgLoader().load(newsDetail.getAuthorPortrait()).error(R.drawable.widget_dface).into(mIVAuthorPortrait);

        String time = String.format("%s (%s)", StringUtils.friendly_time(newsDetail.getPubDate()), newsDetail.getPubDate());
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
            mSoft.addView(child, 1);
            mSoft.setVisibility(View.VISIBLE);
            mSoft.setOnClickListener(this);
        } else {
            mSoft.setVisibility(View.GONE);
        }


        mAbouts.setAbout(newsDetail.getAbouts(), new DetailAboutView.OnAboutClickListener() {
            @Override
            public void onClick(View view, About about) {
                NewsDetailActivity.show(getActivity(), about.getId());
            }
        });
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
    public void toSendCommentOk() {
        (Toast.makeText(getContext(), "评论成功", Toast.LENGTH_LONG)).show();
        mETInput.setText("");
    }

    @Override
    public void scrollToComment() {

    }
}
