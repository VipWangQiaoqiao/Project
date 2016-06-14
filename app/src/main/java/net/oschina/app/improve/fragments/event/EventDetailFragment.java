package net.oschina.app.improve.fragments.event;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.EventApplyData;
import net.oschina.app.improve.bean.Event;
import net.oschina.app.improve.bean.EventDetail;
import net.oschina.app.improve.contract.EventDetailContract;
import net.oschina.app.improve.dialog.EventDetailApplyDialog;
import net.oschina.app.improve.fragments.base.BaseFragment;
import net.oschina.app.ui.EventApplyDialog;
import net.oschina.app.ui.dialog.DialogControl;
import net.oschina.app.util.UIHelper;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by huanghaibin
 * on 16-6-13.
 */
public class EventDetailFragment extends BaseFragment implements
        View.OnClickListener, EventDetailContract.View {

    @Bind(R.id.iv_event_img)
    ImageView iv_event_img;

    @Bind(R.id.iv_fav)
    ImageView iv_fav;

    @Bind(R.id.tv_event_title)
    TextView tv_event_title;

    @Bind(R.id.tv_event_author)
    TextView tv_event_author;

    @Bind(R.id.tv_event_type)
    TextView tv_event_type;

    @Bind(R.id.tv_event_cost_desc)
    TextView tv_event_cost_desc;

    @Bind(R.id.tv_event_member)
    TextView tv_event_member;

    @Bind(R.id.tv_event_status)
    TextView tv_event_status;

    @Bind(R.id.tv_event_pub_time)
    TextView tv_event_pub_time;

    @Bind(R.id.tv_event_location)
    TextView tv_event_location;

    @Bind(R.id.tv_fav)
    TextView tv_fav;

    @Bind(R.id.tv_apply_status)
    TextView tv_apply_status;

    @Bind(R.id.wv_event_detail)
    WebView wv_event_detail;

    private EventDetail mDetail;
    private EventDetailContract.Operator mOperator;
    private EventDetailApplyDialog mEventApplyDialog;

    public static EventDetailFragment instantiate(EventDetailContract.Operator operator, EventDetail detail) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("event_detail", detail);
        EventDetailFragment fragment = new EventDetailFragment();
        fragment.setArguments(bundle);
        fragment.mOperator = operator;
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_improve_event_detail;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
    }

    @Override
    protected void initData() {
        mDetail = (EventDetail) mBundle.getSerializable("event_detail");
        if (mDetail == null) return;
        tv_event_title.setText(mDetail.getTitle());
        tv_event_author.setText(String.format("发起人：%s", mDetail.getAuthor()));
        tv_event_member.setText(String.format("%s人参与", mDetail.getApplyCount()));
        tv_event_cost_desc.setText(String.format("费用：%s", mDetail.getCostDesc()));
        tv_event_location.setText(mDetail.getLocation());
        tv_event_pub_time.setText(mDetail.getPubDate());
        getImgLoader().load(mDetail.getImg()).into(iv_event_img);
        iv_fav.setImageResource(mDetail.isFavorite() ? R.drawable.ic_faved_normal : R.drawable.ic_fav_normal);
        tv_fav.setText(mDetail.isFavorite() ? getResources().getString(R.string.event_is_fav) : getResources().getString(R.string.event_un_fav));
        switch (mDetail.getStatus()) {
            case Event.STATUS_END:
                tv_event_status.setText(getResources().getString(R.string.event_status_end));
                break;
            case Event.STATUS_ING:
                tv_event_status.setText(getResources().getString(R.string.event_status_ing));
                break;
            case Event.STATUS_SING_UP:
                tv_event_status.setText(getResources().getString(R.string.event_status_sing_up));
                break;
        }
        int typeStr = R.string.oscsite;
        switch (mDetail.getType()) {
            case Event.EVENT_TYPE_OSC:
                typeStr = R.string.event_type_osc;
                break;
            case Event.EVENT_TYPE_TEC:
                typeStr = R.string.event_type_tec;
                break;
            case Event.EVENT_TYPE_OTHER:
                typeStr = R.string.event_type_other;
                break;
            case Event.EVENT_TYPE_OUTSIDE:
                typeStr = R.string.event_type_outside;
                break;
        }
        tv_event_type.setText(String.format("类型：%s", getResources().getString(typeStr)));
        tv_apply_status.setText(getResources().getString(getApplyStatusStrId(mDetail.getApplyStatus())));
        UIHelper.initWebView(wv_event_detail);
        UIHelper.addWebImageShow(getActivity(), wv_event_detail);
        wv_event_detail.loadDataWithBaseURL("", getWebViewBody(mDetail), "text/html", "UTF-8", "");
    }

    @OnClick({R.id.ll_fav, R.id.ll_sign})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_fav:
                mOperator.toFav();
                break;
            case R.id.ll_sign:
                if (mDetail.getApplyStatus() == EventDetail.APPLY_STATUS_UN_SIGN) {
                    if (mEventApplyDialog == null) {
                        mEventApplyDialog = new EventDetailApplyDialog(getActivity(), mDetail);
                        mEventApplyDialog.setCanceledOnTouchOutside(true);
                        mEventApplyDialog.setCancelable(true);
                        mEventApplyDialog.setTitle("活动报名");
                        mEventApplyDialog.setCanceledOnTouchOutside(true);
                        mEventApplyDialog.setNegativeButton(R.string.cancle, null);
                        mEventApplyDialog.setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface d, int which) {
                                        EventApplyData data;
                                        if ((data = mEventApplyDialog.getApplyData()) != null) {
                                            data.setEvent(Integer.parseInt(String.valueOf(mDetail.getId())));
                                            data.setUser(AppContext.getInstance()
                                                    .getLoginUid());
                                            mOperator.toSignUp(data);
                                        }

                                    }
                                });
                    }
                    mEventApplyDialog.show();
                }

                break;
        }
    }

    /**
     * 添加收藏成功
     *
     * @param detail detail
     */
    @Override
    public void toFavOk(EventDetail detail) {
        mDetail = detail;
        iv_fav.setImageResource(mDetail.isFavorite() ? R.drawable.ic_faved_normal : R.drawable.ic_fav_normal);
        tv_fav.setText(mDetail.isFavorite() ? getResources().getString(R.string.event_is_fav) : getResources().getString(R.string.event_un_fav));
    }

    /**
     * 报名成功
     *
     * @param detail detail
     */
    @Override
    public void toSignUpOk(EventDetail detail) {
        mDetail = detail;
        mEventApplyDialog.dismiss();
    }

    private final static String linkCss = "<script type=\"text/javascript\" " +
            "src=\"file:///android_asset/shCore.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/brush.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/client.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/detail_page" +
            ".js\"></script>"
            + "<script type=\"text/javascript\">SyntaxHighlighter.all();</script>"
            + "<script type=\"text/javascript\">function showImagePreview(var url){window" +
            ".location.url= url;}</script>"
            + "<link rel=\"stylesheet\" type=\"text/css\" " +
            "href=\"file:///android_asset/shThemeDefault.css\">"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shCore" +
            ".css\">"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/common_new" +
            ".css\">";

    private String getWebViewBody(EventDetail detail) {
        return String.format("<!DOCTYPE HTML><html><head>%s</head><body><div class=\"body-content\">%s</div></body></html>",
                linkCss + UIHelper.WEB_LOAD_IMAGES,
                UIHelper.setHtmlCotentSupportImagePreview(detail.getBody()));
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onDestroy() {
        WebView view = wv_event_detail;
        if (view != null) {
            wv_event_detail = null;
            view.getSettings().setJavaScriptEnabled(true);
            view.removeJavascriptInterface("mWebViewImageListener");
            view.removeAllViewsInLayout();
            view.setWebChromeClient(null);
            view.removeAllViews();
            view.destroy();
        }
        super.onDestroy();
    }

    public int getApplyStatusStrId(int status) {
        int strId = R.string.event_status_ing;
        switch (status) {
            case EventDetail.APPLY_STATUS_UN_SIGN:
                strId = R.string.event_apply_status_un_sign;
                break;
            case EventDetail.APPLY_STATUS_AUDIT:
                strId = R.string.event_apply_status_audit;
                break;
            case EventDetail.APPLY_STATUS_CONFIRMED:
                strId = R.string.event_apply_status_confirmed;
                break;
            case EventDetail.APPLY_STATUS_PRESENTED:
                strId = R.string.event_apply_status_presented;
                break;
            case EventDetail.APPLY_STATUS_CANCELED:
                strId = R.string.event_apply_status_canceled;
                break;
            case EventDetail.APPLY_STATUS_REFUSED:
                strId = R.string.event_apply_status_refused;
                break;
        }
        return strId;
    }
}
