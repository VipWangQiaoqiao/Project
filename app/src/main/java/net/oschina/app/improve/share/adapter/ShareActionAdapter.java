package net.oschina.app.improve.share.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.share.bean.ShareItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by fei on 2016/10/10.
 * desc:
 */

public class ShareActionAdapter extends RecyclerView.Adapter<ShareActionAdapter.ViewHolder>
        implements View.OnClickListener {

    private List<ShareItem> mShareActions;
    private OnItemClickListener OnItemClickListener;

    public ShareActionAdapter(List<ShareItem> shareActions) {
        this.mShareActions = shareActions;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View rootView = LayoutInflater.from(context).inflate(R.layout.dialog_share_item, parent,
                false);
        ViewHolder viewHolder = new ViewHolder(rootView);
        rootView.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ShareItem shareAction = mShareActions.get(position);

        holder.mIvIcon.setTag(holder);
        holder.mIvIcon.setImageResource(shareAction.getIconId());
        holder.mIvIcon.setOnClickListener(this);
        holder.mTvName.setText(shareAction.getNameId());

    }

    public void addOnItemClickListener(OnItemClickListener itemClickListener) {
        this.OnItemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return mShareActions == null ? 0 : mShareActions.size();
    }

    @Override
    public void onClick(View v) {

        if (OnItemClickListener != null) {
            ViewHolder viewHolder = (ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            long itemId = viewHolder.getItemId();
            OnItemClickListener.onItemClick(position, itemId);
        }

    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.share_icon)
        ImageView mIvIcon;
        @Bind(R.id.share_name)
        TextView mTvName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, long itemId);
    }
}
