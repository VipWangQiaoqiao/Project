package net.oschina.app.improve.detail.fragments;

import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.bean.EventApplyData;
import net.oschina.app.improve.bean.Event;
import net.oschina.app.improve.bean.EventDetail;
import net.oschina.app.improve.detail.contract.EventDetailContract;
import net.oschina.app.improve.dialog.EventDetailApplyDialog;
import net.oschina.app.util.UIHelper;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by huanghaibin
 * on 16-6-13.
 */
@SuppressWarnings("WeakerAccess")
public class EventDetailFragment extends DetailFragment<EventDetail, EventDetailContract.View, EventDetailContract.Operator> implements
        View.OnClickListener, EventDetailContract.View {

    @Bind(R.id.ll_sign)
    LinearLayout ll_sign;

    @Bind(R.id.iv_event_img)
    ImageView iv_event_img;

    @Bind(R.id.iv_fav)
    ImageView iv_fav;

    @Bind(R.id.iv_sign)
    ImageView iv_sign;

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

    @Bind(R.id.tv_event_start_date)
    TextView tv_event_start_date;

    @Bind(R.id.tv_event_location)
    TextView tv_event_location;

    @Bind(R.id.tv_fav)
    TextView tv_fav;

    @Bind(R.id.tv_apply_status)
    TextView tv_apply_status;


    private EventDetailApplyDialog mEventApplyDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_improve_event_detail;
    }


    @Override
    protected void initData() {
        final EventDetail mDetail = mOperator.getData();
        if (mDetail == null) return;
        tv_event_title.setText(mDetail.getTitle());
        tv_event_author.setText(String.format("发起人：%s", mDetail.getAuthor()));
        tv_event_member.setText(String.format("%s人参与", mDetail.getApplyCount()));
        tv_event_cost_desc.setText(mDetail.getCostDesc());
        tv_event_location.setText(mDetail.getSpot());
        tv_event_start_date.setText(mDetail.getStartDate());
        getImgLoader().load(mDetail.getImg()).into(iv_event_img);
        iv_fav.setImageResource(mDetail.isFavorite() ? R.drawable.ic_faved : R.drawable.ic_fav);
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

        if (mDetail.getApplyStatus() != EventDetail.APPLY_STATUS_UN_SIGN) {
            setSignUnEnable();
        }
        setBodyContent(mDetail.getBody());
    }

    @OnClick({R.id.ll_fav, R.id.ll_sign})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_fav:
                mOperator.toFav();
                break;
            case R.id.ll_sign:
                final EventDetail mDetail = mOperator.getData();
                if (mDetail.getApplyStatus() == EventDetail.APPLY_STATUS_UN_SIGN && mDetail.getStatus() == Event.STATUS_ING) {
                    if (AppContext.getInstance().isLogin()) {
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
                    } else {
                        UIHelper.showLoginActivity(getActivity());
                    }
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
        mOperator.getData().setFavorite(detail.isFavorite());
        final EventDetail mDetail = mOperator.getData();
        iv_fav.setImageResource(mDetail.isFavorite() ? R.drawable.ic_faved : R.drawable.ic_fav);
        tv_fav.setText(mDetail.isFavorite() ? getResources().getString(R.string.event_is_fav) : getResources().getString(R.string.event_un_fav));
    }

    /**
     * 报名成功
     *
     * @param detail detail
     */
    @Override
    public void toSignUpOk(EventDetail detail) {
        mOperator.getData().setApplyStatus(detail.getApplyStatus());
        final EventDetail mDetail = mOperator.getData();
        tv_apply_status.setText(getResources().getString(getApplyStatusStrId(mDetail.getApplyStatus())));
        setSignUnEnable();
        mEventApplyDialog.dismiss();
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

    private void setSignUnEnable() {
        tv_apply_status.setEnabled(false);
        ll_sign.setEnabled(false);
        iv_sign.setEnabled(false);
    }
}
