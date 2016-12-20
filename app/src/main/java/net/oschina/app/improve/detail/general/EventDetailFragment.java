package net.oschina.app.improve.detail.general;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.bean.Event;
import net.oschina.app.improve.bean.EventDetail;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.comment.CommentsActivity;
import net.oschina.app.improve.detail.sign.SignUpActivity;
import net.oschina.app.improve.detail.v2.DetailFragment;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class EventDetailFragment extends DetailFragment {

    @Bind(R.id.ll_sign)
    LinearLayout mLinearSign;

    @Bind(R.id.iv_event_img)
    ImageView mImageEvent;

    @Bind(R.id.iv_fav)
    ImageView mImageFav;

    @Bind(R.id.tv_fav)
    TextView mTextFav;

    @Bind(R.id.iv_sign)
    ImageView mImageSign;

    @Bind(R.id.tv_event_title)
    TextView mTextTitle;

    @Bind(R.id.tv_event_author)
    TextView mTextAuthor;

    @Bind(R.id.tv_event_cost_desc)
    TextView mTextCostDesc;

    @Bind(R.id.tv_event_member)
    TextView mTextMember;

    @Bind(R.id.tv_event_status)
    TextView mTextStatus;

    @Bind(R.id.tv_event_start_date)
    TextView mTextStartDate;

    @Bind(R.id.tv_event_location)
    TextView mTextLocation;

    @Bind(R.id.tv_apply_status)
    TextView mTextApplyStatus;

    @Bind(R.id.tv_comment)
    TextView mTextComment;

    public static EventDetailFragment newInstance() {
        return new EventDetailFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_event_detail_v2;
    }

    @OnClick({R.id.ll_fav, R.id.ll_comment, R.id.ll_sign})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_fav:
                mPresenter.favReverse();
                break;
            case R.id.ll_comment:
                CommentsActivity.show(mContext, mBean.getId(), mBean.getType(), 2);
                break;
            case R.id.ll_sign:
                if (!AccountHelper.isLogin()) {
                    LoginActivity.show(getActivity(), 0x02);
                    return;
                }
                SignUpActivity.show(this, mBean.getId());
                break;
        }
    }

    @Override
    public void showGetDetailSuccess(SubBean bean) {
        super.showGetDetailSuccess(bean);
        mTextComment.setText(String.format("评论(%s)", bean.getStatistics().getComment()));
        mTextTitle.setText(bean.getTitle());
        mTextAuthor.setText(bean.getAuthor().getName());
        mImageFav.setImageResource(bean.isFavorite() ? R.drawable.ic_faved : R.drawable.ic_fav);
        mTextFav.setText(bean.isFavorite() ? getString(R.string.event_is_fav) : getString(R.string.event_un_fav));
        HashMap<String, Object> extra = bean.getExtra();
        if (extra != null) {
            mTextLocation.setText(getExtraString(extra.get("eventSpot")));
            mTextMember.setText(String.format("%s人参与", getExtraInt(extra.get("eventApplyCount"))));
            mTextStartDate.setText(getExtraString(extra.get("eventStartDate")));
            mTextCostDesc.setText(getExtraString(extra.get("eventCostDesc")));
//
//            /**
//             * 活动类型判断
//             */
//            int typeStr = R.string.oscsite;
//            switch (getExtraInt(extra.get("eventType"))) {
//                case Event.EVENT_TYPE_OSC:
//                    typeStr = R.string.event_type_osc;
//                    break;
//                case Event.EVENT_TYPE_TEC:
//                    typeStr = R.string.event_type_tec;
//                    break;
//                case Event.EVENT_TYPE_OTHER:
//                    typeStr = R.string.event_type_other;
//                    break;
//                case Event.EVENT_TYPE_OUTSIDE:
//                    typeStr = R.string.event_type_outside;
//                    break;
//            }
//            mTextType.setText(String.format("类型：%s", getResources().getString(typeStr)));

            /**
             * 活动状态判断
             */
            int eventStatus = getExtraInt(extra.get("eventStatus"));
            switch (eventStatus) {
                case Event.STATUS_END:
                    mTextStatus.setText(getResources().getString(R.string.event_status_end));
                    break;
                case Event.STATUS_ING:
                    mTextStatus.setText(getResources().getString(R.string.event_status_ing));
                    break;
                case Event.STATUS_SING_UP:
                    mTextStatus.setText(getResources().getString(R.string.event_status_sing_up));
                    break;
            }

            /**
             * 活动状态和出席状态判断
             */
            int eventApplyStatus = getExtraInt(extra.get("eventApplyStatus"));
            mTextApplyStatus.setText(getString(getApplyStatusStrId(eventApplyStatus)));
            if (eventStatus != EventDetail.STATUS_ING ||
                    eventApplyStatus != EventDetail.APPLY_STATUS_UN_SIGN) {
                setSignUnEnable();
            }
        }
        getImgLoader().load(bean.getImage().getHref()[0]).into(mImageEvent);
    }

    @Override
    public void showFavReverseSuccess(boolean isFav, int strId) {
        super.showFavReverseSuccess(isFav, strId);
        mImageFav.setImageResource(isFav ? R.drawable.ic_faved : R.drawable.ic_fav);
        mTextFav.setText(isFav ? getString(R.string.event_is_fav) : getString(R.string.event_un_fav));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case 0x01:
                    mTextApplyStatus.setText(getResources().getString(getApplyStatusStrId(EventDetail.APPLY_STATUS_AUDIT)));
                    setSignUnEnable();
                    break;
            }
        }
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
        mTextApplyStatus.setEnabled(false);
        mLinearSign.setEnabled(false);
        mImageSign.setEnabled(false);
    }
}
