package net.oschina.app.improve.fragments.event;

import android.annotation.SuppressLint;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.bean.Event;
import net.oschina.app.improve.bean.EventDetail;
import net.oschina.app.improve.fragments.base.BaseFragment;

import butterknife.Bind;

/**
 * Created by huanghaibin
 * on 16-6-13.
 */
public class EventDetailFragment extends BaseFragment implements View.OnClickListener {

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

    @Bind(R.id.wv_event_detail)
    WebView wv_event_detail;

    private EventDetail mDetail;

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
        tv_event_author.setText(String.format("发起人：%s", mDetail.getAuthor()));
        tv_event_member.setText(String.format("%s人参与", mDetail.getApplyCount()));
        tv_event_cost_desc.setText(String.format("费用：s%", mDetail.getCostDesc()));
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
    }

    @Override
    public void onClick(View v) {

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
}
