package net.oschina.app.bean;

import net.oschina.app.R;
import net.oschina.app.fragment.CommentFrament;
import net.oschina.app.fragment.SoftWareTweetsFrament;
import net.oschina.app.fragment.TweetPubFragment;
import net.oschina.app.viewpagefragment.QuestViewPagerFragment;

public enum SimpleBackPage {

	COMMENT(1, R.string.actionbar_title_comment, CommentFrament.class),
	
	QUEST(2, R.string.actionbar_title_questions, QuestViewPagerFragment.class),
	
	TWEET_PUB(3, R.string.actionbar_title_tweetpub, TweetPubFragment.class),
	
	SOFTWARE_TWEETS(4, R.string.actionbar_title_softtweet, SoftWareTweetsFrament.class);

	private int title;
	private Class<?> clz;
	private int value;

	private SimpleBackPage(int value, int title, Class<?> clz) {
		this.value = value;
		this.title = title;
		this.clz = clz;
	}

	public int getTitle() {
		return title;
	}

	public void setTitle(int title) {
		this.title = title;
	}

	public Class<?> getClz() {
		return clz;
	}

	public void setClz(Class<?> clz) {
		this.clz = clz;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public static SimpleBackPage getPageByValue(int val) {
		for (SimpleBackPage p : values()) {
			if (p.getValue() == val)
				return p;
		}
		return null;
	}
}
