package net.oschina.app.widget;

import net.oschina.app.R;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.UIHelper;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class AvatarView extends CircleImageView  {
	public static final String AVATAR_SIZE_REG = "_[0-9]{1,3}";
	public static final String MIDDLE_SIZE = "_100";
	public static final String LARGE_SIZE = "_200";
	
	private static final String PGIF = "portrait.gif";
	private int id;
	private String name;

	public AvatarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public AvatarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AvatarView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(name)) {
					UIHelper.showUserCenter(getContext(), id, name);
				}
			}
		});
	}

	public void setUserInfo(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public void setAvatarUrl(String url) {
		
		if (null == url || url.endsWith(PGIF) || StringUtils.isEmpty(url))
			setImageResource(R.drawable.widget_dface);
		else
			ImageLoader.getInstance().displayImage(
					url, this);
	}
	
	// http://static.oschina.net/uploads/user/63/127726_50.png?t=1390533116000
	public static String getSmallAvatar(String source) {
		return source;
	}

	// http://static.oschina.net/uploads/user/63/127726_100.png?t=1390533116000
	public static String getMiddleAvatar(String source) {
		if (source == null)
			return "";
		return source.replaceAll(AVATAR_SIZE_REG, MIDDLE_SIZE);
	}

	public static String getLargeAvatar(String source) {
		if (source == null)
			return "";
		return source.replaceAll(AVATAR_SIZE_REG, LARGE_SIZE);
	}
}
