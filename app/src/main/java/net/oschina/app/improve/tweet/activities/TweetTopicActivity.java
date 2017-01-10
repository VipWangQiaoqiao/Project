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
import net.oschina.app.improve.utils.AssimilateUtils;
import net.oschina.app.improve.utils.CacheManager;
import net.oschina.common.adapter.TextWatcherAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;

public class TweetTopicActivity extends BaseBackActivity {
    private static final String CACHE_FILE = "TweetTopicLocalCache";
    @Bind(R.id.edit_enter_tag)
    EditText mTopicContent;

    @Bind(R.id.recycler)
    RecyclerView mRecycler;

    private List<Object> mList = new ArrayList<>();
    private List<TopicBean> mLocalList = new ArrayList<>();
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
        mTopicContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sortLocalList(getContent());
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
            mLocalList.add(new TopicBean(mCache.get(i), i != size - 1));
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

    private String getContent() {
        return mTopicContent.getText()
                .toString().trim().replace("#", "");
    }

    private void loadCache() {
        mCache = CacheManager.readListJson(this, CACHE_FILE, String.class);
        if (mCache == null)
            mCache = new ArrayList<>();
    }

    private void saveCache(String str) {
        // 避免重复添加
        boolean isHave = false;
        for (String s : mCache) {
            if (isHave = s.equals(str))
                break;
        }
        if (!isHave)
            mCache.add(0, str);

        // 至多存储15条，默认清理5条
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
        String str = getContent();
        if (TextUtils.isEmpty(str))
            finish();
        else {
            saveCache(str);
            doResult(str);
        }
    }

    private void doResult(String topic) {
        Intent result = new Intent();
        result.putExtra("topic", topic);
        setResult(RESULT_OK, result);
        finish();
    }

    private void sortLocalList(String text) {
        final String py = TextUtils.isEmpty(text) ? "!#" : AssimilateUtils.returnPinyin(text, false);
        Pattern pattern = Pattern.compile(py);
        for (TopicBean bean : mLocalList) {
            Matcher matcher = pattern.matcher(bean.py);
            if (matcher.find()) {
                bean.sort = matcher.start();
            } else {
                bean.sort = ORDER_MAX;
            }
        }

        Collections.sort(mLocalList, new Comparator<TopicBean>() {
            @Override
            public int compare(TopicBean o1, TopicBean o2) {
                if (o1.sort == ORDER_MAX && o2.sort == ORDER_MAX) {
                    return o1.order - o2.order;
                }
                return o1.sort - o2.sort;
            }
        });

        adapter.notifyDataSetChanged();
    }

    private RecyclerView.Adapter adapter = new RecyclerView.Adapter() {
        @Override
        public int getItemViewType(int position) {
            if (get(position) instanceof String) {
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
                        if (obj != null && obj instanceof TopicBean) {
                            doResult(((TopicBean) obj).text);
                        }
                    }
                });
                return new DataHolder(root);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof LabelHolder) {
                ((LabelHolder) holder).set((String) get(position));
            } else {
                TopicBean bean = (TopicBean) get(position);
                ((DataHolder) holder).set(bean);
                holder.itemView.setTag(bean);
            }
        }

        @Override
        public int getItemCount() {
            return mList.size() + mLocalList.size();
        }

        private Object get(int position) {
            int fixedCount = mList.size();
            if (position >= fixedCount) {
                return mLocalList.get(position - fixedCount);
            } else {
                return mList.get(position);
            }
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

    private static int ORDER_COUNT = 0;
    private static int ORDER_MAX = 100;

    private class TopicBean {
        String text;
        String py;
        boolean needLine = true;
        private int sort = ORDER_MAX;
        private int order = ORDER_COUNT++;

        TopicBean(String text) {
            this.text = text;
            this.py = AssimilateUtils.returnPinyin(text, false);
        }

        TopicBean(String text, boolean needLine) {
            this(text);
            this.needLine = needLine;
        }
    }

}
