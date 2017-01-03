package net.oschina.app.improve.detail.general;

import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.bean.simple.UserRelation;
import net.oschina.app.improve.detail.v2.DetailFragment;
import net.oschina.app.improve.utils.ReadedIndexCacheManager;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.util.StringUtils;

import butterknife.Bind;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by haibin
 * on 2016/11/30.
 */

public class BlogDetailFragment extends DetailFragment {

    @Bind(R.id.iv_avatar)
    CircleImageView mImageAvatar;

    @Bind(R.id.tv_name)
    TextView mTextName;

    @Bind(R.id.tv_pub_date)
    TextView mTextPubDate;

    @Bind(R.id.tv_title)
    TextView mTextTitle;

    @Bind(R.id.tv_detail_abstract)
    TextView mTextAbstract;

    @Bind(R.id.tv_info_comment)
    TextView mTextComCount;

    @Bind(R.id.tv_info_view)
    TextView mTextViewCount;

    @Bind(R.id.btn_relation)
    Button mBtnRelation;

    @Bind(R.id.lay_nsv)
    NestedScrollView mViewScroller;

    public static BlogDetailFragment newInstance() {
        return new BlogDetailFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_blog_detail_v2;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mBtnRelation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBean.getAuthor() != null) {
                    mPresenter.addUserRelation(mBean.getAuthor().getId());
                }
            }
        });
    }

    @Override
    public void showGetDetailSuccess(SubBean bean) {
        super.showGetDetailSuccess(bean);
        Author author = bean.getAuthor();
        if (author != null) {
            mTextName.setText(author.getName());
            getImgLoader().load(author.getPortrait()).asBitmap().into(mImageAvatar);
        }
        mTextPubDate.setText(StringUtils.formatSomeAgo(bean.getPubDate()));
        mTextTitle.setText(bean.getTitle());
        mTextComCount.setText(String.valueOf(bean.getStatistics().getComment()));
        mTextViewCount.setText(String.valueOf(bean.getStatistics().getView()));
        mTextAbstract.setText(bean.getSummary());
        if (TextUtils.isEmpty(bean.getSummary())) {
            mRoot.findViewById(R.id.line).setVisibility(View.GONE);
            mRoot.findViewById(R.id.line1).setVisibility(View.GONE);
            mTextAbstract.setVisibility(View.GONE);
        }
        mBtnRelation.setText(bean.getAuthor().getRelation() < UserRelation.RELATION_ONLY_HER
                ? "已关注" : "关注");

    }

    @Override
    public void onPageFinished() {
        super.onPageFinished();
        if (mBean == null || mBean.getId() <= 0) return;
        final int index = ReadedIndexCacheManager.getIndex(getContext(), mBean.getId(),
                OSChinaApi.CATALOG_BLOG);
        if (index != 0) {
            mViewScroller.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mViewScroller.smoothScrollTo(0, index);
                }
            }, 250);
        }
    }

    @Override
    public void showAddRelationSuccess(boolean isRelation, int strId) {
        mBtnRelation.setText(isRelation ? "已关注" : "关注");
        SimplexToast.show(mContext, strId);
    }

    @Override
    protected int getCommentOrder() {
        return OSChinaApi.COMMENT_NEW_ORDER;
    }

    @Override
    public void onDestroy() {
        if (mBean != null && mBean.getId() > 0) {
            ReadedIndexCacheManager.saveIndex(getContext(), mBean.getId(), OSChinaApi.CATALOG_BLOG,
                    mViewScroller.getScrollY());
        }
        super.onDestroy();
    }
}
