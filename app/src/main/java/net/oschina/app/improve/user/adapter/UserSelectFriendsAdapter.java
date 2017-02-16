package net.oschina.app.improve.user.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.oschina.app.R;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.user.helper.ContactsCacheManager;
import net.oschina.app.util.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class UserSelectFriendsAdapter extends RecyclerView.Adapter implements ContactsCacheManager.SelectedTrigger<ContactsCacheManager.Friend> {
    // 第一个头部
    private static final int TYPE_FIRST = 0x1111;
    private static final int TYPE_NONE = 0x0000;
    private static final int TYPE_TOP = 0x0001;
    private static final int TYPE_BOTTOM = 0x0010;

    private final ArrayList<ContactsCacheManager.Friend> mItems = new ArrayList<>();
    private final ContactsCacheManager.OnSelectedChangeListener listener;
    private final View mFirstView;

    public UserSelectFriendsAdapter(View firstView, ContactsCacheManager.OnSelectedChangeListener listener) {
        mFirstView = firstView;
        this.listener = listener;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_FIRST;

        ContactsCacheManager.Friend item = mItems.get(position);

        int type = TYPE_NONE;
        // 判断是否显示标题
        if (position == 1 ||
                ((position > 1) && (!mItems.get(position - 1).firstChar.equals(item.firstChar)))) {
            type = type | TYPE_TOP;
        }

        // 判断是否是类型结束，类型结束不显示底线
        int maxPos = getItemCount() - 1;
        if ((position == maxPos)
                || !(mItems.get(position + 1).firstChar.equals(item.firstChar))) {
            // 如果是最后一个或者后面一个不是当前首字母的类型则当前item是当前类型最后一个
            type = type | TYPE_BOTTOM;
        }
        return type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FIRST) {
            return new RecyclerView.ViewHolder(mFirstView) {
            };
        }

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewHolder userInfoViewHolder = new ViewHolder(inflater.inflate(R.layout.activity_item_select_friend, parent, false));
        userInfoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() != null && v.getTag() instanceof ContactsCacheManager.Friend) {
                    onViewHolderClick((ContactsCacheManager.Friend) v.getTag());
                }
            }
        });

        boolean showLabel = ((viewType & TYPE_TOP) == TYPE_TOP);
        boolean showLine = !((viewType & TYPE_BOTTOM) == TYPE_BOTTOM);
        userInfoViewHolder.initView(showLabel, showLine);

        return userInfoViewHolder;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // 第一位不做处理，预留为顶部最近联系人
        if (position == 0)
            return;

        ContactsCacheManager.Friend item = mItems.get(position);
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).onBindView(item);
        } else {
            holder.itemView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return this.mItems.size();
    }

    public void initItems(List<ContactsCacheManager.Friend> items) {
        mItems.clear();

        // 添加一个空的UserFriend 用于最近联系人占位
        mItems.add(new ContactsCacheManager.Friend(null));
        if (items != null && items.size() > 0)
            mItems.addAll(items);

        mItems.trimToSize();
        notifyDataSetChanged();
    }

    public List<ContactsCacheManager.Friend> getItems() {
        return mItems;
    }

    private void onViewHolderClick(ContactsCacheManager.Friend friend) {
        if (listener != null)
            listener.tryTriggerSelected(friend, this);
    }

    @Override
    public void trigger(ContactsCacheManager.Friend friend, boolean selected) {
        friend.isSelected = selected;
        int pos = mItems.indexOf(friend);
        if (pos >= 0) {
            notifyItemChanged(pos);
        }
    }

    @Override
    public void trigger(Author author, boolean selected) {
        if (mItems.size() == 0 || author == null)
            return;
        for (ContactsCacheManager.Friend friend : mItems) {
            if (friend.author != null &&
                    friend.author.getId() == author.getId()) {
                trigger(friend, selected);
                return;
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_index_label)
        TextView mLabel;
        @Bind(R.id.iv_portrait)
        CircleImageView mCirclePortrait;
        @Bind(R.id.tv_name)
        TextView mtvName;
        @Bind(R.id.iv_select)
        ImageView mViewSelect;
        @Bind(R.id.line)
        View mLine;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindView(final ContactsCacheManager.Friend item) {
            // 存储Tag
            itemView.setTag(item);

            final Author author = item.author;
            if (author == null || author.getId() <= 0 || TextUtils.isEmpty(author.getName())) {
                itemView.setVisibility(View.GONE);
                return;
            }

            ImageLoader.loadImage(Glide.with(itemView.getContext()),
                    mCirclePortrait, author.getPortrait(), R.mipmap.widget_default_face);
            mCirclePortrait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OtherUserHomeActivity.show(v.getContext(), author.getId());
                }
            });
            mtvName.setText(author.getName());
            mLabel.setText(item.firstChar);

            mViewSelect.setVisibility(item.isSelected ? View.VISIBLE : View.GONE);
        }

        void initView(boolean showLabel, boolean showLine) {
            mLabel.setVisibility(showLabel ? View.VISIBLE : View.GONE);
            mLine.setVisibility(showLine ? View.VISIBLE : View.GONE);
        }
    }
}
