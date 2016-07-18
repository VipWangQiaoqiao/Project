package net.oschina.app.improve.tweet.fragments;


import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.tweet.adapter.TweetSelectImageAdapter;
import net.qiujuer.genius.ui.widget.ImageView;

import butterknife.Bind;

/**
 * A simple {@link Fragment} subclass.
 */
public class TweetPublishFragment extends BaseFragment implements TweetSelectImageAdapter.Callback {

    @Bind(R.id.edit_content)
    EditText mEditContent;

    @Bind(R.id.recycler_images)
    RecyclerView mLayImages;

    @Bind(R.id.iv_picture)
    ImageView mOptImages;

    @Bind(R.id.iv_mention)
    ImageView mOptMention;

    @Bind(R.id.iv_tag)
    ImageView mOptTag;

    @Bind(R.id.iv_emoji)
    ImageView mOptEmoji;

    private TweetSelectImageAdapter mImageAdapter;

    public TweetPublishFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tweet_publish;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mImageAdapter = new TweetSelectImageAdapter(this);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        mLayImages.setLayoutManager(layoutManager);
        mLayImages.setAdapter(mImageAdapter);
        mLayImages.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    @Override
    protected void initData() {
        super.initData();

        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            String extPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mImageAdapter.add(extPath + "/DCIM/Selfie/P60711-205912.jpg");
        }
    }

    @Override
    public void onLoadMoreClick() {
        String extPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mImageAdapter.add(extPath + "/DCIM/Selfie/P60711-202115.jpg");
        mImageAdapter.notifyDataSetChanged();
    }
}
