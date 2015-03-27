package net.oschina.app.widget;

import java.util.List;

import net.oschina.app.R;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.bean.Tweet;
import net.oschina.app.bean.User;
import net.oschina.app.util.TLog;
import net.oschina.app.util.UIHelper;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * LikeContainer.java
 * 
 * @author 火蚁(http://my.oschina.net/u/253900)
 *
 * @data 2015-3-25 下午3:44:49
 */
public class LikeContainer extends LinearLayout {
    
    private Tweet tweet;
    private Context context;
    
    public void setLikeUser(Tweet tweet) {
	if (tweet.getLikeUser() == null || tweet.getLikeUser().isEmpty()) {
	    this.setVisibility(View.GONE);
	    return;
	}
	this.setVisibility(View.VISIBLE);
	this.tweet = tweet;
	for (User user : this.tweet.getLikeUser()) {
	    addLikeUser(user);
	}
	if (tweet.getLikeUser().size() != tweet.getLikeCount()) {
	    addLikeCount();
	}
	
	//TLog.log("Test", this.getWidth() + "");
    }
    
    public void addLikeUser(User user) {
	if (user == null) return;
	if (this.getVisibility() != View.VISIBLE) {
	    this.setVisibility(View.VISIBLE);
	}
	LayoutInflater lInflater = LayoutInflater.from(context);
	AvatarView likeUser = (AvatarView) lInflater.inflate(R.layout.list_cell_like_user, null, false);
	likeUser.setAvatarUrl(user.getPortrait());
	int w = (int)this.context.getResources().getDimension(R.dimen.space_25);
	LayoutParams params = new LayoutParams(
		w, w);
	params.setMargins(5, 0, 5, 0);
	
	likeUser.setUserInfo(user.getUid(), user.getName());
	this.addView(likeUser, 0, params);
    }
    
    public void addLikeCount() {
	LayoutInflater lInflater = LayoutInflater.from(context);
	TextView more = (TextView) lInflater.inflate(R.layout.list_cell_like_more, null, false);
	int w = (int)this.context.getResources().getDimension(R.dimen.space_26);
	LayoutParams params = new LayoutParams(
		w, w);
	params.setMargins(5, 0, 5, 0);
	more.setText(this.tweet.getLikeCount() + "");
	more.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		Bundle bundle = new Bundle();
		bundle.putInt(BaseListFragment.BUNDLE_KEY_CATALOG, tweet.getId());
		UIHelper.showSimpleBack(context, SimpleBackPage.TWEET_LIKE_USER_LIST, bundle);
	    }
	});
	this.addView(more, params);
    }
    
    public void displayLikeUser() {
	
	
    }
    
    public LikeContainer(Context context) {
	super(context);
	this.context = context;
	// TODO Auto-generated constructor stub
    }

    public LikeContainer(Context context, AttributeSet attrs) {
	super(context, attrs);
	this.context = context;
	// TODO Auto-generated constructor stub
    }

    public LikeContainer(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	this.context = context;
	// TODO Auto-generated constructor stub
    }
}

