package net.oschina.app.fragment;

import android.text.Editable;
import android.text.TextUtils;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.CommonDetailFragment;
import net.oschina.app.bean.FavoriteList;
import net.oschina.app.bean.Software;
import net.oschina.app.bean.SoftwareDetail;
import net.oschina.app.bean.Tweet;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.ThemeSwitchUtils;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;

import java.io.InputStream;

/**
 * Created by 火蚁 on 15/5/26.
 */
public class SoftwareDetailFragment extends CommonDetailFragment<Software> {

    private String mIden;

    @Override
    protected String getCacheKey() {
        return "software_" + mIden;
    }

    @Override
    protected void sendRequestDataForNet() {
        if (TextUtils.isEmpty(mIden)) {
            executeOnLoadDataError();
            return;
        }
        OSChinaApi.getSoftwareDetail(mIden, mDetailHeandler);
    }

    @Override
    public void initData() {
        super.initData();
        mIden = getActivity().getIntent().getStringExtra("ident");
    }

    @Override
    protected Software parseData(InputStream is) {
        return XmlUtils.toBean(SoftwareDetail.class, is).getSoftware();
    }

    @Override
    protected void executeOnLoadDataSuccess(Software detail) {
        mCommentCount = detail.getTweetCount();
        setCommentCount();
        super.executeOnLoadDataSuccess(detail);
    }

    @Override
    protected String getWebViewBody(Software detail) {
        StringBuffer body = new StringBuffer();
        body.append(ThemeSwitchUtils.getWebViewBodyString());
        body.append(UIHelper.WEB_STYLE).append(UIHelper.WEB_LOAD_IMAGES);
        // 添加title
        body.append(String.format("<div class='title'>%s</div>", mDetail.getTitle()));
        // 添加图片点击放大支持
        body.append(UIHelper.setHtmlCotentSupportImagePreview(mDetail.getBody()));

        // 软件信息
        body.append("<div class='software_attr'>");
        if (!TextUtils.isEmpty(mDetail.getAuthor())) {
            String author = String.format("<a class='author' href='http://my.oschina.net/u/%s'>%s</a>", mDetail.getAuthorId(), mDetail.getAuthor());
            body.append(String.format("<li class='software'>软件作者:&nbsp;%s</li>", author));
        }
        body.append(String.format("<li class='software'>开源协议:&nbsp;%s</li>", mDetail.getLicense()));
        body.append(String.format("<li class='software'>开发语言:&nbsp;%s</li>", mDetail.getLanguage()));
        body.append(String.format("<li class='software'>操作系统:&nbsp;%s</li>", mDetail.getOs()));
        body.append(String.format("<li class='software'>收录时间:&nbsp;%s</li>", mDetail.getRecordtime()));
        body.append("</div>");
        // 软件的首页、文档、下载
        body.append("<div class='software_urls'>");
        body.append(String.format("<li class='software'><a href='%s'>软件首页</a></li>", mDetail.getHomepage()));
        body.append(String.format("<li class='software'><a href='%s'>软件文档</a></li>", mDetail.getDocument()));
        body.append(String.format("<li class='software'><a href='%s'>软件下载</a></li>", mDetail.getDownload()));
        body.append("</div>");
        // 封尾
        body.append("</div></body>");
        return body.toString();
    }

    @Override
    protected void showCommentView() {
        if (mDetail != null)
            UIHelper.showSoftWareTweets(getActivity(), mDetail.getId());
    }

    @Override
    public void onClickSendButton(Editable str) {
        if (mDetail.getId() == 0) {
            AppContext.showToast("无法获取该软件~");
            return;
        }
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_network_error);
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }
        if (TextUtils.isEmpty(str)) {
            AppContext.showToastShort(R.string.tip_comment_content_empty);
            return;
        }
        Tweet tweet = new Tweet();
        tweet.setAuthorid(AppContext.getInstance().getLoginUid());
        tweet.setBody(str.toString());
        showWaitDialog(R.string.progress_submit);
        OSChinaApi.pubSoftWareTweet(tweet, mDetail.getId(), mCommentHandler);
    }

    @Override
    protected int getCommentType() {
        return 0;
    }

    @Override
    protected int getFavoriteTargetType() {
        return FavoriteList.TYPE_SOFTWARE;
    }

    @Override
    protected int getFavoriteState() {
        return mDetail.getFavorite();
    }

    @Override
    protected void updateFavoriteChanged(int newFavoritedState) {
        mDetail.setFavorite(newFavoritedState);
        saveCache(mDetail);
    }
}
