package net.oschina.app.improve.detail.general;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.detail.v2.DetailFragment;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.utils.QuickOptionDialogHelper;
import net.oschina.app.improve.widget.PortraitView;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.StringUtils;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnLongClick;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class EventDetailFragment extends DetailFragment {

    @Bind(R.id.iv_event_img)
    ImageView mImageEvent;

    @Bind(R.id.tv_event_title)
    TextView mTextTitle;

    @Bind(R.id.tv_event_author)
    TextView mTextAuthor;

    @Bind(R.id.tv_event_cost_desc)
    TextView mTextCostDesc;

    @Bind(R.id.tv_event_status)
    TextView mTextStatus;

    @Bind(R.id.tv_event_start_date)
    TextView mTextStartDate;

    @Bind(R.id.tv_event_location)
    TextView mTextLocation;

    @Bind(R.id.civ_author)
    PortraitView mImageAuthor;

    public static EventDetailFragment newInstance() {
        return new EventDetailFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_event_detail_v2;
    }

    @Override
    protected void initData() {
        super.initData();
        CACHE_CATALOG = OSChinaApi.CATALOG_EVENT;
    }

    @Override
    public void showGetDetailSuccess(SubBean bean) {
        super.showGetDetailSuccess(bean);
        mTextTitle.setText(bean.getTitle());
        final Author author = bean.getAuthor();
        if (author != null) {
            mTextAuthor.setText(author.getName());
            mImageAuthor.setup(author);
            mImageAuthor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OtherUserHomeActivity.show(mContext, author);
                }
            });
        }
        HashMap<String, Object> extra = bean.getExtra();
        if (extra != null) {
            mTextLocation.setText(getExtraString(extra.get("eventProvince")) + " " +
                    getExtraString(extra.get("eventCity")) + " " +
                    getExtraString(extra.get("eventSpot")));
            mTextStartDate.setText(StringUtils.getDateString(getExtraString(extra.get("eventStartDate"))));
            mTextCostDesc.setText(getExtraString(extra.get("eventCostDesc")));
        }
    }

    @Override
    public void onPageFinished() {
    }

    @Override
    protected int getCommentOrder() {
        return OSChinaApi.COMMENT_HOT_ORDER;
    }

    @OnLongClick(R.id.lay_event_location)
    boolean onLongClickLocation() {
        final String text = mTextLocation.getText().toString();
        if (TextUtils.isEmpty(text))
            return false;
        QuickOptionDialogHelper.with(getContext())
                .addCopy(HTMLUtil.delHTMLTag(text))
                .show();
        return true;
    }
}
