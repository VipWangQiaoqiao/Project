package net.oschina.app.improve.git.feature;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.git.bean.Project;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by haibin
 * on 2017/3/9.
 */

class ProjectAdapter extends BaseGeneralRecyclerAdapter<Project> {
    ProjectAdapter(Callback callback) {
        super(callback, NEITHER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new ProjectViewHolder(mInflater.inflate(R.layout.item_list_project, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Project item, int position) {
        ProjectViewHolder h = (ProjectViewHolder) holder;
        mCallBack.getImgLoader()
                .load(item.getOwner().getNewPortrait())
                .asBitmap()
                .placeholder(R.mipmap.widget_default_face)
                .into(h.mImageOwner);
        h.mTextName.setText(item.getName());
        h.mTextDescription.setText(item.getDescription());
        h.mTextLanguage.setText(item.getLanguage());
        h.mTextViewCount.setText(String.valueOf(item.getForksCount()));
        h.mTextFavCount.setText(String.valueOf(item.getStarsCount()));
        h.mTextForkCount.setText(String.valueOf(item.getForksCount()));
    }

    private static class ProjectViewHolder extends RecyclerView.ViewHolder {
        CircleImageView mImageOwner;
        TextView mTextName, mTextDescription, mTextViewCount,
                mTextFavCount, mTextForkCount, mTextLanguage;

        ProjectViewHolder(View itemView) {
            super(itemView);
            mImageOwner = (CircleImageView) itemView.findViewById(R.id.civ_owner);
            mTextName = (TextView) itemView.findViewById(R.id.tv_name);
            mTextDescription = (TextView) itemView.findViewById(R.id.tv_description);
            mTextViewCount = (TextView) itemView.findViewById(R.id.tv_view_count);
            mTextFavCount = (TextView) itemView.findViewById(R.id.tv_fav_count);
            mTextForkCount = (TextView) itemView.findViewById(R.id.tv_fork_count);
            mTextLanguage = (TextView) itemView.findViewById(R.id.tv_language);
        }
    }
}
