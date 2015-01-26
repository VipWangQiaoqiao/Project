package net.oschina.app.ui;

import java.lang.ref.WeakReference;

import net.oschina.app.R;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.emoji.EmojiFragment;
import net.oschina.app.fragment.BlogDetailFragment;
import net.oschina.app.fragment.EventDetailFragment;
import net.oschina.app.fragment.NewsDetailFragment;
import net.oschina.app.fragment.PostDetailFragment;
import net.oschina.app.fragment.SoftwareDetailFragment;
import net.oschina.app.fragment.ToolbarFragment;
import net.oschina.app.fragment.TweetDetailFragment;
import net.oschina.app.interf.EmojiFragmentControl;
import net.oschina.app.interf.ToolbarEmojiVisiableControl;
import net.oschina.app.interf.ToolbarFragmentControl;
import net.oschina.app.util.TDevice;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import butterknife.InjectView;

/**
 * 详情activity（包括：资讯、博客、软件、问答、动弹）
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年10月11日 上午11:18:41
 *	
 */
public class DetailActivity extends BaseActivity implements
		ToolbarEmojiVisiableControl {

	public static final int DISPLAY_NEWS = 0;
	public static final int DISPLAY_BLOG = 1;
	public static final int DISPLAY_SOFTWARE = 2;
	public static final int DISPLAY_POST = 3;
	public static final int DISPLAY_TWEET = 4;
	public static final int DISPLAY_EVENT = 5;
	public static final String BUNDLE_KEY_DISPLAY_TYPE = "BUNDLE_KEY_DISPLAY_TYPE";
	
	@InjectView(R.id.emoji_container)
	View mViewEmojiContaienr;
	
	@InjectView(R.id.toolbar_container)
	View mViewToolBarContaienr;
	
	private WeakReference<BaseFragment> mFragment, mEmojiFragment;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_detail;
	}

	@Override
	protected boolean hasBackButton() {
		return true;
	}

	@Override
	protected int getActionBarTitle() {
		return R.string.actionbar_title_detail;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		super.init(savedInstanceState);
		int displayType = getIntent().getIntExtra(BUNDLE_KEY_DISPLAY_TYPE,
				DISPLAY_NEWS);
		BaseFragment fragment = null;
		int actionBarTitle = 0;
		switch (displayType) {
		case DISPLAY_NEWS:
			actionBarTitle = R.string.actionbar_title_news;
			fragment = new NewsDetailFragment();
			break;
		case DISPLAY_BLOG:
			actionBarTitle = R.string.actionbar_title_blog;
			fragment = new BlogDetailFragment();
			break;
		case DISPLAY_SOFTWARE:
			actionBarTitle = R.string.actionbar_title_software;
			fragment = new SoftwareDetailFragment();
			break;
		case DISPLAY_POST:
			actionBarTitle = R.string.actionbar_title_question;
			fragment = new PostDetailFragment();
			break;
		case DISPLAY_TWEET:
			actionBarTitle = R.string.actionbar_title_tweet;
			fragment = new TweetDetailFragment();
			break;
		case DISPLAY_EVENT:
			actionBarTitle = R.string.actionbar_title_event_detail;
			fragment = new EventDetailFragment();
		default:
			break;
		}
		setActionBarTitle(actionBarTitle);
		FragmentTransaction trans = getSupportFragmentManager()
				.beginTransaction();
		mFragment = new WeakReference<BaseFragment>(fragment);
		trans.replace(R.id.container, fragment);

		// 加表情操作界面
		if (fragment instanceof EmojiFragmentControl) {
			EmojiFragment f = new EmojiFragment();
			mEmojiFragment = new WeakReference<BaseFragment>(f);
			trans.replace(R.id.emoji_container, f);
			((EmojiFragmentControl) fragment).setEmojiFragment(f);
		}
		// 加入操作工具条
		if (fragment instanceof ToolbarFragmentControl) {
			ToolbarFragment f = new ToolbarFragment();
			mEmojiFragment = new WeakReference<BaseFragment>(f);
			trans.replace(R.id.toolbar_container, f);
			((ToolbarFragmentControl) fragment).setToolBarFragment(f);


			mViewEmojiContaienr.setVisibility(View.GONE);
			mViewToolBarContaienr.setVisibility(View.VISIBLE);
		}

		trans.commitAllowingStateLoss();
	}

	@Override
	public void toggleToolbarEmoji() {
		if (mViewEmojiContaienr.getVisibility() == View.VISIBLE) {
			if (mEmojiFragment != null) {
				// mEmojiFragment.get().
			}
			TDevice.hideSoftKeyboard(getCurrentFocus());

			final Animation in = AnimationUtils.loadAnimation(this,
					R.anim.footer_menu_slide_in);
			Animation out = AnimationUtils.loadAnimation(this,
					R.anim.footer_menu_slide_out);
			mViewEmojiContaienr.clearAnimation();
			mViewToolBarContaienr.clearAnimation();
			mViewEmojiContaienr.startAnimation(out);
			out.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					//
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					mViewEmojiContaienr.setVisibility(View.GONE);
					mViewToolBarContaienr.setVisibility(View.VISIBLE);
					mViewToolBarContaienr.clearAnimation();
					mViewToolBarContaienr.startAnimation(in);
				}
			});
		} else {
			final Animation in = AnimationUtils.loadAnimation(this,
					R.anim.footer_menu_slide_in);
			Animation out = AnimationUtils.loadAnimation(this,
					R.anim.footer_menu_slide_out);
			mViewToolBarContaienr.clearAnimation();
			mViewEmojiContaienr.clearAnimation();
			mViewToolBarContaienr.startAnimation(out);
			out.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					mViewToolBarContaienr.setVisibility(View.GONE);
					mViewEmojiContaienr.setVisibility(View.VISIBLE);
					mViewEmojiContaienr.clearAnimation();
					mViewEmojiContaienr.startAnimation(in);
				}
			});
		}
	}

	@Override
	public void onBackPressed() {
		if (mEmojiFragment != null && mEmojiFragment.get() != null
				&& mViewEmojiContaienr.getVisibility() == View.VISIBLE) {
			if (mEmojiFragment.get().onBackPressed()) {
				return;
			}
		}
		if (mFragment != null && mFragment.get() != null) {
			if (mFragment.get().onBackPressed()) {
				return;
			}
		}
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	public void initView() {
	}

	@Override
	public void initData() {
	}
}
