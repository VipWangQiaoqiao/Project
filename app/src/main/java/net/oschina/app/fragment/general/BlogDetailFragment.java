package net.oschina.app.fragment.general;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.CommonDetailFragment;
import net.oschina.app.bean.Blog;
import net.oschina.app.bean.BlogDetail;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.FavoriteList;
import net.oschina.app.fragment.base.BaseFragment;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.ThemeSwitchUtils;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.URLsUtils;
import net.oschina.app.util.XmlUtils;

import java.io.InputStream;

/**
 * Created by qiujuer
 * on 16/5/26.
 */

public class BlogDetailFragment extends BaseFragment {
    private RequestManager mImgLoader;
    private long mId;
    private WebView mWebView;
    private TextView mTVAuthorName;
    private TextView mTVPubDate;
    private TextView mTVTitle;
    private TextView mTVCommentCount;
    private ImageView mIVLabelRecommend;
    private ImageView mIVLabelOriginate;
    private ImageView mIVAuthorPortrait;
    private Button mBtnRelation;
    private EditText mETInput;

    private ImageView mIVFlow;
    private ImageView mIVShare;

    private LinearLayout mLayAbouts;
    private LinearLayout mLayComments;

    private long mCommentId;
    private net.oschina.app.bean.blog.BlogDetail mBlog;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_general_blog_detail;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
    }
}
