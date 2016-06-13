package net.oschina.app.ui.tweet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.bean.Tweet;
import net.oschina.app.util.PlatfromUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.viewpagerfragment.TweetDetailViewPagerFragment;
import net.oschina.app.widget.CircleImageView;

import java.io.Serializable;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 动弹详情
 * Created by thanatos on 16/6/13.
 */
public class TweetDetailActivity extends AppCompatActivity{

    public static final String BUNDLE_KEY_TWEET = "BUNDLE_KEY_TWEET";

    @Bind(R.id.iv_portrait) CircleImageView ivPortrait;
    @Bind(R.id.tv_nick) TextView tvNick;
    @Bind(R.id.iv_small_img) ImageView ivSmallImg;
    @Bind(R.id.tv_content) TextView tvContent;
    @Bind(R.id.tv_time) TextView tvTime;
    @Bind(R.id.tv_client) TextView tvClient;
    @Bind(R.id.iv_thumbup) ImageView ivThumbup;
    @Bind(R.id.iv_comment) ImageView ivComment;
    @Bind(R.id.tv_like_users) TextView tvLikeUsers;
    @Bind(R.id.tv_comment_count) TextView tvCmnCount;

    private Tweet tweet;

    public static void show(Context context, Tweet tweet){
        Intent intent = new Intent(context, TweetDetailActivity.class);
        intent.putExtra(BUNDLE_KEY_TWEET, (Serializable) tweet);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tweet_detail);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("动弹详情");
        }
        initData();
        initView();
    }

    private void initData(){
        tweet = (Tweet) getIntent().getSerializableExtra(BUNDLE_KEY_TWEET);
        if (tweet == null){
            Toast.makeText(this, "对象没找到", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initView(){
        RequestManager reqManager = Glide.with(this);

        reqManager.load(tweet.getPortrait()).into(ivPortrait);
        tvNick.setText(tweet.getAuthor());
        tvContent.setText(tweet.getBody());
        tvTime.setText(StringUtils.friendly_time(tweet.getPubDate()));
        tvCmnCount.setText(tweet.getCommentCount());
        PlatfromUtil.setPlatFromString(tvClient, tweet.getAppclient());

        if (!TextUtils.isEmpty(tweet.getImgSmall())){
            ivSmallImg.setVisibility(View.VISIBLE);
            reqManager.load(tweet.getImgSmall()).into(ivSmallImg);
        }

        if (tweet.getIsLike() == 1) {
            ivThumbup.setImageResource(R.drawable.ic_thumbup_actived);
        } else {
            ivThumbup.setImageResource(R.drawable.ic_thumbup_normal);
        }

        if (tweet.getLikeUser() != null && !tweet.getLikeUser().isEmpty()){
            tvLikeUsers.setVisibility(View.VISIBLE);
            tweet.setLikeUsers(this, tvLikeUsers, false);
        }


        Bundle bundle = new Bundle();
        bundle.putInt(TweetDetailViewPagerFragment.BUNDLE_KEY_TWEET_ID, tweet.getId());
        bundle.putInt(TweetDetailViewPagerFragment.BUNDLE_KEY_TWEET_UP_COUNT, tweet.getLikeCount());
        bundle.putInt(TweetDetailViewPagerFragment.BUNDLE_KEY_TWEET_COMMENT_COUNT, Integer.valueOf(tweet.getCommentCount()));

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, Fragment.instantiate(this, TweetDetailViewPagerFragment.class.getName(), bundle))
                .commitAllowingStateLoss();
    }
}
