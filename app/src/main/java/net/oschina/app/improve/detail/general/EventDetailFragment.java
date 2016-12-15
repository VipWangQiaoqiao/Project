package net.oschina.app.improve.detail.general;

import android.widget.ImageView;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.bean.Event;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.detail.v2.DetailFragment;
import net.qiujuer.genius.ui.compat.UiCompat;

import java.util.HashMap;

import butterknife.Bind;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class EventDetailFragment extends DetailFragment {

    @Bind(R.id.iv_event_img)
    ImageView mImageEvent;

    @Bind(R.id.iv_fav)
    ImageView mImageFav;

    @Bind(R.id.iv_sign)
    ImageView mImageSign;

    @Bind(R.id.tv_event_title)
    TextView mTextTitle;

    @Bind(R.id.tv_event_author)
    TextView mTextAuthor;

    @Bind(R.id.tv_event_type)
    TextView mTextType;

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

    @Bind(R.id.tv_fav)
    TextView mTextFav;

    @Bind(R.id.tv_apply_status)
    TextView mTextApplyStatus;

    @Bind(R.id.tv_comment)
    TextView mTextComment;

    public static EventDetailFragment newInstance() {
        EventDetailFragment fragment = new EventDetailFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_event_detail_v2;
    }

    @Override
    public void showGetDetailSuccess(SubBean bean) {
        super.showGetDetailSuccess(bean);
        mTextComment.setText(String.format("评论(%s)", bean.getStatistics().getComment()));
        mTextTitle.setText(bean.getTitle());
        mTextAuthor.setText(String.format("发起人：%s", bean.getAuthor().getName()));


        HashMap<String, Object> extra = bean.getExtra();
        if (extra != null) {
            mTextLocation.setText(extra.get("eventSpot").toString());
            mTextMember.setText(String.format("%s人参与", extra.get("eventApplyCount")));
            mTextStartDate.setText(extra.get("eventStartDate").toString());
            mTextCostDesc.setText(extra.get("eventCostDesc").toString());

            int typeStr = R.string.oscsite;
            switch (Double.valueOf(extra.get("eventType").toString()).intValue()) {
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
            mTextType.setText(String.format("类型：%s", getResources().getString(typeStr)));

            switch (Double.valueOf(extra.get("eventStatus").toString()).intValue()) {
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
        }
        getImgLoader().load(bean.getImage().getHref()).into(mImageEvent);


    }
}
