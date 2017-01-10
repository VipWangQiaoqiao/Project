package net.oschina.app.improve.tweet.activities;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.utils.CacheManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class TweetTopicActivity extends BaseBackActivity {
    private static final String CACHE_FILE = "TweetTopicLocalCache";
    @Bind(R.id.edit_enter_tag)
    EditText mTopicContent;

    @Bind(R.id.recycler)
    RecyclerView mRecycler;

    private List<Object> mList = new ArrayList<>();
    private List<String> mCache;

    @Override
    protected int getContentView() {
        return R.layout.activity_tweet_topic;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(adapter);
        mTopicContent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.ACTION_DOWN) {
                    onSubmit();
                    return true;
                }
                return false;
            }
        });
        mTopicContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_DONE) {
                    onSubmit();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();

        loadCache();

        mList.add("热门");
        mList.add(new TopicBean("开源中国"));
        mList.add(new TopicBean("开源中国客户端", false));
        mList.add("本地");

        int size = mCache.size();
        for (int i = 0; i < size; i++) {
            mList.add(new TopicBean(mCache.get(i), i != size - 1));
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tweet_topic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_submit) {
            onSubmit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadCache() {
        mCache = CacheManager.readListJson(this, CACHE_FILE, String.class);
        if (mCache == null)
            mCache = new ArrayList<>();
    }

    private void saveCache(String str) {
        mCache.add(0, str);
        List<String> cache;

        if (mCache.size() >= 15) {
            cache = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                cache.add(mCache.get(i));
            }
        } else {
            cache = mCache;
        }
        CacheManager.saveToJson(this, "TweetTopicLocalCache", cache);
    }

    private void onSubmit() {
        String str = mTopicContent.getText().toString().trim();
        if (TextUtils.isEmpty(str))
            finish();
        else {
            saveCache(str);
            doResult(str);
        }
    }

    private void onSelect(int position) {
        Object obj = mList.get(position);
        if (obj instanceof TopicBean) {
            doResult(((TopicBean) obj).text);
        }
    }

    private void doResult(String topic) {
        Intent result = new Intent();
        result.putExtra("topic", topic);
        setResult(RESULT_OK, result);
        finish();
    }

    private RecyclerView.Adapter adapter = new RecyclerView.Adapter() {
        @Override
        public int getItemViewType(int position) {
            if (mList.get(position) instanceof String) {
                return R.layout.list_item_sample_label;
            } else {
                return R.layout.list_item_topic;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == R.layout.list_item_sample_label) {
                return new LabelHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_sample_label, parent, false));
            } else {
                View root = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_topic, parent, false);
                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Object obj = v.getTag();
                        if (obj != null && obj instanceof Integer) {
                            onSelect((Integer) obj);
                        }
                    }
                });
                return new DataHolder(root);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof LabelHolder) {
                ((LabelHolder) holder).set((String) mList.get(position));
            } else {
                ((DataHolder) holder).set((TopicBean) mList.get(position));
                holder.itemView.setTag(position);
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    };

    private class LabelHolder extends RecyclerView.ViewHolder {
        private TextView mText;

        LabelHolder(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(R.id.txt_string);
        }


        void set(String data) {
            mText.setText(data);
        }
    }

    private class DataHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private View mLine;

        DataHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.txt_title);
            mLine = itemView.findViewById(R.id.line);
        }

        void set(TopicBean data) {
            mTitle.setText(data.text);
            mLine.setVisibility(data.needLine ? View.VISIBLE : View.GONE);
        }
    }

    private class TopicBean {
        String text;
        boolean needLine = true;

        TopicBean(String text) {
            this.text = text;
        }

        TopicBean(String text, boolean needLine) {
            this.text = text;
            this.needLine = needLine;
        }
    }

}
