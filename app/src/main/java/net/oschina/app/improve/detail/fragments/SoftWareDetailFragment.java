package net.oschina.app.improve.detail.fragments;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.bean.SoftwareDetail;
import net.oschina.app.improve.detail.contract.SoftDetailContract;
import net.oschina.app.improve.tweet.activities.SoftwareTweetActivity;
import net.oschina.app.improve.widget.DetailAboutView;
import net.oschina.app.util.UIHelper;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by fei on 2016/6/20.
 * desc:  software detail module
 */
public class SoftWareDetailFragment extends DetailFragment<SoftwareDetail, SoftDetailContract.View, SoftDetailContract.Operator>
        implements View.OnClickListener, SoftDetailContract.View {

    @Bind(R.id.iv_label_recommend)
    ImageView ivRecomment;

    @Bind(R.id.iv_software_icon)
    ImageView ivIcon;
    @Bind(R.id.tv_software_name)
    TextView tvName;

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

    @Bind(R.id.lay_option_fav_text)
    TextView mCommentText;
    @Bind(R.id.lay_option_fav_icon)
    ImageView mIVFav;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_general_soft_detail;
    }

    @OnClick({R.id.lay_option_share, R.id.lay_option_fav, R.id.bt_software_home, R.id.bt_software_document,
            R.id.lay_option_comment})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lay_option_share:
                // 分享
                handleShare();
                break;
            case R.id.lay_option_fav:
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
            case R.id.lay_option_comment:
                // 评论列表
                Intent intent = new Intent(getActivity(), SoftwareTweetActivity.class);
                intent.putExtra(SoftwareTweetActivity.BUNDLE_KEY_NAME, mOperator.getData().getName());
                startActivity(intent);
                //UIUtil.showSoftwareTweets(getActivity(), (int) mId, mOperator.getData().getName());
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initData() {
        final SoftwareDetail softwareDetail = mOperator.getData();

        if (softwareDetail == null) {
            return;
        }

        if (softwareDetail.isRecommend()) {
            ivRecomment.setVisibility(View.VISIBLE);
        } else {
            ivRecomment.setVisibility(View.INVISIBLE);
        }

        String name = softwareDetail.getName();
        String extName = softwareDetail.getExtName();
        tvName.setText(String.format("%s%s", TextUtils.isEmpty(name) ? "" : name, (TextUtils.isEmpty(extName)) ? "" : " " + extName.trim()));

        String author = softwareDetail.getAuthor();
        tvAuthor.setText(TextUtils.isEmpty(author) ? "匿名" : author.trim());
        String license = softwareDetail.getLicense();
        tvLicense.setText(TextUtils.isEmpty(license) ? "无" : license.trim());
        tvLanguage.setText(softwareDetail.getLanguage());
        tvSystem.setText(softwareDetail.getSupportOS());
        tvRecordTime.setText(softwareDetail.getCollectionDate());

        setCommentCount(softwareDetail.getCommentCount());
        setBodyContent(softwareDetail.getBody());
        getImgLoader().load(softwareDetail.getLogo())
                .error(R.mipmap.logo_software_default)
                .placeholder(R.mipmap.logo_software_default)
                .into(ivIcon);

        toFavoriteOk(softwareDetail);

        mAbouts.setAbout(softwareDetail.getAbouts(), 1);
    }

    @Override
    void setCommentCount(int count) {
        mCommentText.setText(String.format("评论 (%s)", count));
    }

    private void handleFavorite() {
        mOperator.toFavorite();
    }

    private void handleShare() {
        mOperator.toShare();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void toFavoriteOk(SoftwareDetail softwareDetail) {
        if (softwareDetail.isFavorite())
            mIVFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_faved));
        else
            mIVFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav));
    }
}
