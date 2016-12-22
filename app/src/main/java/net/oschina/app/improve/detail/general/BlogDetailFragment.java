package net.oschina.app.improve.detail.general;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.detail.v2.DetailFragment;
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

    public static BlogDetailFragment newInstance() {
        return new BlogDetailFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_blog_detail_v2;
    }


    @Override
    public void showGetDetailSuccess(SubBean bean) {
        super.showGetDetailSuccess(bean);
        mTextName.setText(bean.getAuthor().getName());
        getImgLoader().load(bean.getAuthor().getPortrait()).asBitmap().into(mImageAvatar);
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
    }
}
