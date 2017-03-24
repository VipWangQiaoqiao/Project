package net.oschina.app.improve.search.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.NearbyResult;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.widget.IdentityView;
import net.oschina.app.improve.widget.PortraitView;
import net.oschina.app.util.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thanatos
 * on 16/10/24.
 * Updated by fei
 * on 17/01/12.
 */

public class NearbyUserAdapter extends BaseRecyclerAdapter<NearbyResult> {

    public NearbyUserAdapter(Context context) {
        super(context, ONLY_FOOTER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_nearby_user, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder h, NearbyResult item, int position) {

        if (h instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) h;
            holder.mIdentityView.setup(item.getUser());
            if (item.getUser() != null) {
                holder.mViewPortrait.setup(item.getUser());
                holder.mViewNick.setText(item.getUser().getName());
                holder.mViewGender.setVisibility(View.VISIBLE);
                switch (item.getUser().getGender()) {
                    case User.GENDER_FEMALE:
                        holder.mViewGender.setImageResource(R.mipmap.ic_female);
                        break;
                    case User.GENDER_MALE:
                        holder.mViewGender.setImageResource(R.mipmap.ic_male);
                        break;
                    default:
                        holder.mViewGender.setVisibility(View.GONE);
                }
                User.More more = item.getUser().getMore();
                if (more != null) {
                    holder.mViewPosition.setText(more.getCompany());
                } else {
                    holder.mViewPosition.setText("??? ???");
                }
            } else {
                holder.mViewPortrait.setup(0, "?", "");
                holder.mViewNick.setText("???");
                holder.mViewGender.setVisibility(View.GONE);
                holder.mViewPosition.setText("??? ???");
            }

            if (item.getNearby() != null) {
                holder.mViewDistance.setText(StringUtils.formatDistance(item.getNearby().getDistance()));
            } else {
                holder.mViewDistance.setText("未知距离");
            }
        }

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.identityView)
        IdentityView mIdentityView;
        @Bind(R.id.iv_portrait)
        PortraitView mViewPortrait;
        @Bind(R.id.tv_nick)
        TextView mViewNick;
        @Bind(R.id.tv_position)
        TextView mViewPosition;
        @Bind(R.id.tv_distance)
        TextView mViewDistance;
        @Bind(R.id.iv_gender)
        ImageView mViewGender;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
