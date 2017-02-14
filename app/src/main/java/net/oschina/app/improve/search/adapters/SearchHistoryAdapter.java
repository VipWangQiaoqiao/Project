package net.oschina.app.improve.search.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;

/**
 * Created by haibin
 * on 17/2/14.
 */

public class SearchHistoryAdapter extends BaseRecyclerAdapter<SearchHistoryAdapter.SearchItem> {
    private static final int VIEW_TYPE_CLEAR = 3;

    public SearchHistoryAdapter(Context context) {
        super(context, NEITHER);
    }


    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position).getType() != 0)
            return VIEW_TYPE_CLEAR;
        return super.getItemViewType(position);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        if (type == VIEW_TYPE_CLEAR)
            return new ClearViewHolder(mInflater.inflate(R.layout.item_list_clear_search, null));
        return new SearchViewHolder(mInflater.inflate(R.layout.item_list_search_history, null));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, SearchItem item, int position) {
        if (holder.getItemViewType() != VIEW_TYPE_CLEAR) {
            SearchViewHolder h = (SearchViewHolder) holder;
            h.mTextTitle.setText(item.getSearchText());
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView mTextTitle;

        SearchViewHolder(View itemView) {
            super(itemView);
            mTextTitle = (TextView) itemView.findViewById(R.id.tv_search);
        }
    }

    private class ClearViewHolder extends RecyclerView.ViewHolder {
        TextView mTextTitle;

        ClearViewHolder(View itemView) {
            super(itemView);
            mTextTitle = (TextView) itemView.findViewById(R.id.tv_search);
        }
    }

    public static class SearchItem {
        private String searchText;
        private int type;

        public SearchItem(String item) {
            this.searchText = item;
        }


        public SearchItem(String searchText, int type) {
            this.searchText = searchText;
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getSearchText() {
            return searchText;
        }

        public void setSearchText(String searchText) {
            this.searchText = searchText;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SearchItem && obj != null) {
                return searchText.equals(((SearchItem) obj).searchText);
            }
            return super.equals(obj);
        }
    }
}
