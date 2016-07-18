package net.oschina.app.improve.tweet.fragments;


import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;

import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.tweet.widget.TweetPicturesPreviewer;
import net.qiujuer.genius.ui.widget.ImageView;

import butterknife.Bind;

/**
 * A simple {@link Fragment} subclass.
 */
public class TweetPublishFragment extends BaseFragment {

    @Bind(R.id.edit_content)
    EditText mEditContent;

    @Bind(R.id.recycler_images)
    TweetPicturesPreviewer mLayImages;

    @Bind(R.id.iv_picture)
    ImageView mOptImages;

    @Bind(R.id.iv_mention)
    ImageView mOptMention;

    @Bind(R.id.iv_tag)
    ImageView mOptTag;

    @Bind(R.id.iv_emoji)
    ImageView mOptEmoji;


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

        String extPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mLayImages.add(extPath + "/DCIM/Selfie/P60711-202115.jpg");

    }

    @Override
    protected void initData() {
        super.initData();


    }
}
