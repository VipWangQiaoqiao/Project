package net.oschina.app.improve.detail.apply;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.bean.ApplyUser;
import net.oschina.app.improve.bean.simple.UserRelation;
import net.oschina.app.widget.CircleImageView;

/**
 * Created by haibin
 * on 2016/12/27.
 */

class ApplyAdapter extends BaseGeneralRecyclerAdapter<ApplyUser> {
    private View.OnClickListener mRelationListener;

    ApplyAdapter(Callback callback, View.OnClickListener mRelationListener) {
        super(callback, ONLY_FOOTER);
        this.mRelationListener = mRelationListener;
    }


    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new ApplyViewHolder(mInflater.inflate(R.layout.item_list_apply, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, final ApplyUser item, int position) {
        ApplyViewHolder h = (ApplyViewHolder) holder;
        h.mTextAuthor.setText(item.getId() <= 0 ? "匿名" : item.getName());
        mCallBack.getImgLoader().load(item.getPortrait())
                .asBitmap()
                .into(h.mImageAuthor);
        ApplyUser.EventInfo info = item.getEventInfo();
        if (info != null) {
            h.mTextName.setText(info.getName());
            h.mTextCompany.setText(info.getCompany());
            h.mTextJob.setText(info.getJob());
        } else {
            h.mTextName.setText("");
            h.mTextCompany.setText("");
            h.mTextJob.setText("");
        }
        h.mBtnRelate.setEnabled(item.getId() == 0 || AccountHelper.getUserId() != item.getId());
        h.mBtnRelate.setText(item.getRelation() >= UserRelation.RELATION_ONLY_HER ? "关注" : "已关注");
        h.mBtnRelate.setTag(position);
        h.mBtnRelate.setOnClickListener(mRelationListener);

        h.mImageGender.setImageResource(item.getGender() == 1 ? R.mipmap.ic_male : R.mipmap.ic_female);
        h.mImageGender.setVisibility(item.getGender() == 0 ? View.GONE : View.VISIBLE);
    }

    private static class ApplyViewHolder extends RecyclerView.ViewHolder {
        TextView mTextAuthor, mTextName, mTextCompany, mTextJob;
        CircleImageView mImageAuthor;
        ImageView mImageGender;
        Button mBtnRelate;

        ApplyViewHolder(View itemView) {
            super(itemView);
            mTextAuthor = (TextView) itemView.findViewById(R.id.tv_author);
            mTextName = (TextView) itemView.findViewById(R.id.tv_name);
            mTextCompany = (TextView) itemView.findViewById(R.id.tv_company);
            mTextJob = (TextView) itemView.findViewById(R.id.tv_job);
            mImageAuthor = (CircleImageView) itemView.findViewById(R.id.civ_author);
            mBtnRelate = (Button) itemView.findViewById(R.id.btn_relation);
            mImageGender = (ImageView) itemView.findViewById(R.id.iv_gender);
        }
    }
}
