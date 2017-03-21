package net.oschina.app.improve.git.feature;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.git.bean.Project;
import net.oschina.app.improve.widget.PortraitView;

import java.text.DecimalFormat;

/**
 * Created by haibin
 * on 2017/3/9.
 */

class ProjectAdapter extends BaseGeneralRecyclerAdapter<Project> {
    private DecimalFormat decimalFormat = new DecimalFormat(".0");

    ProjectAdapter(Callback callback) {
        super(callback, ONLY_FOOTER);
        setState(STATE_HIDE, false);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new ProjectViewHolder(mInflater.inflate(R.layout.item_list_project, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Project item, int position) {
        ProjectViewHolder h = (ProjectViewHolder) holder;
        h.mImageOwner.setup(0, item.getOwner().getName(), item.getOwner().getNewPortrait());
        h.mImageOwner.setOnClickListener(null);
        h.mTextName.setText(item.getOwner().getName() + "/" + item.getName());
        h.mTextDescription.setText(item.getDescription());
        h.mTextLanguage.setText(item.getLanguage());
        h.mTextLanguage.setVisibility(TextUtils.isEmpty(item.getLanguage()) ? View.GONE : View.VISIBLE);
        h.mTextViewCount.setText(getCount(item.getWatchesCount()));
        h.mTextFavCount.setText(getCount(item.getStarsCount()));
        h.mTextForkCount.setText(getCount(item.getForksCount()));
    }

    private static class ProjectViewHolder extends RecyclerView.ViewHolder {
        PortraitView mImageOwner;
        TextView mTextName, mTextDescription, mTextViewCount,
                mTextFavCount, mTextForkCount, mTextLanguage;

        ProjectViewHolder(View itemView) {
            super(itemView);
            mImageOwner = (PortraitView) itemView.findViewById(R.id.civ_owner);
            mTextName = (TextView) itemView.findViewById(R.id.tv_name);
            mTextDescription = (TextView) itemView.findViewById(R.id.tv_description);
            mTextViewCount = (TextView) itemView.findViewById(R.id.tv_view_count);
            mTextFavCount = (TextView) itemView.findViewById(R.id.tv_fav_count);
            mTextForkCount = (TextView) itemView.findViewById(R.id.tv_fork_count);
            mTextLanguage = (TextView) itemView.findViewById(R.id.tv_language);
        }
    }

    private String getCount(int count) {
        return count >= 1000 ? String.format("%sk", decimalFormat.format((float) count / 1000)) : String.valueOf(count);
    }
}
