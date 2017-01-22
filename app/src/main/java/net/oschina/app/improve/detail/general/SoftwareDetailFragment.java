package net.oschina.app.improve.detail.general;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.comment.CommentsActivity;
import net.oschina.app.improve.detail.v2.DetailFragment;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.util.UIHelper;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class SoftwareDetailFragment extends DetailFragment {
    @Bind(R.id.iv_label_recommend)
    ImageView mImageRecommend;
    @Bind(R.id.iv_software_icon)
    ImageView mImageSoftware;
    @Bind(R.id.tv_software_name)
    TextView mTextName;
    @Bind(R.id.tv_software_author_name)
    TextView mTextAuthor;
    @Bind(R.id.tv_software_protocol)
    TextView mTextProtocol;
    @Bind(R.id.tv_software_language)
    TextView mTextLanguage;
    @Bind(R.id.tv_software_system)
    TextView mTextSystem;
    @Bind(R.id.tv_software_record_time)
    TextView mTextRecordTime;
    @Bind(R.id.iv_fav)
    ImageView mImageFav;
    @Bind(R.id.tv_comment_count)
    TextView mTextCommentCount;

    public static SoftwareDetailFragment newInstance() {
        return new SoftwareDetailFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_software_detail_v2;
    }

    @Override
    protected void initData() {
        super.initData();
        CACHE_CATALOG = OSChinaApi.CATALOG_SOFTWARE;
    }

    @OnClick({R.id.ll_comment, R.id.ll_fav, R.id.tv_software_author_name,
            R.id.ll_share, R.id.tv_home, R.id.tv_document})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_comment:
                CommentsActivity.show(mContext, mBean.getId(), mBean.getType(), OSChinaApi.COMMENT_NEW_ORDER);
                break;
            case R.id.ll_fav:
                if (!AccountHelper.isLogin()) {
                    LoginActivity.show(mContext);
                    return;
                }
                mPresenter.favReverse();
                break;
            case R.id.ll_share:
                toShare(mBean.getTitle(), mBean.getBody(), mBean.getHref());
                break;
            case R.id.tv_home:
            case R.id.tv_document:
                Map<String, Object> extras1 = mBean.getExtra();
                if (extras1 != null)
                    UIHelper.showUrlRedirect(mContext, getExtraString(extras1.get("softwareHomePage")));
                break;
            case R.id.tv_software_author_name:
                Author author = mBean.getAuthor();
                if (author == null) return;
                OtherUserHomeActivity.show(getActivity(), author);
                break;
        }
    }

    @Override
    public void showGetDetailSuccess(SubBean bean) {
        super.showGetDetailSuccess(bean);
        if (mContext == null)
            return;
        mImageFav.setImageResource(bean.isFavorite() ? R.drawable.ic_faved : R.drawable.ic_fav);
        mTextName.setText(bean.getTitle());
        mImageRecommend.setVisibility(bean.isRecommend() ? View.VISIBLE : View.INVISIBLE);
        List<SubBean.Image> images = bean.getImages();
        if (images != null && images.size() != 0)
            getImgLoader().load(images.get(0).getHref()).asBitmap().into(mImageSoftware);
        SubBean.Statistics statistics = bean.getStatistics();
        mTextCommentCount.setText(String.format("评论(%d)", statistics != null ? statistics.getComment() : 0));
        Author author = bean.getAuthor();
        if (author != null) {
            mTextAuthor.setText(author.getName());
        } else {
            mTextAuthor.setText("匿名");
        }
        Map<String, Object> extras = bean.getExtra();
        if (extras != null) {
            String protocol = getExtraString(extras.get("softwareLicense"));
            if(TextUtils.isEmpty(protocol))
                protocol = "未知";
            mTextProtocol.setText(protocol);
            mTextRecordTime.setText(getExtraString(extras.get("softwareCollectionDate")));
            mTextSystem.setText(getExtraString(extras.get("softwareSupportOS")));
            mTextLanguage.setText(getExtraString(extras.get("softwareLanguage")));
        }
    }

    @Override
    protected int getCommentOrder() {
        return OSChinaApi.COMMENT_HOT_ORDER;
    }

    @Override
    public void showFavReverseSuccess(boolean isFav, int strId) {
        super.showFavReverseSuccess(isFav, strId);
        mImageFav.setImageResource(isFav ? R.drawable.ic_faved : R.drawable.ic_fav);
    }
}
