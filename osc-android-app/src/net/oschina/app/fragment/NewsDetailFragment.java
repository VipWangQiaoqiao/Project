package net.oschina.app.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseDetailFragment;
import net.oschina.app.bean.Entity;
import net.oschina.app.bean.News;
import net.oschina.app.bean.News.Relative;
import net.oschina.app.bean.NewsDetail;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TLog;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

public class NewsDetailFragment extends BaseDetailFragment {

	protected static final String TAG = NewsDetailFragment.class
			.getSimpleName();
	private static final String NEWS_CACHE_KEY = "news_";
	private static final String NEWS_DETAIL_SCREEN = "news_detail_screen";
	private TextView mTvTitle, mTvSource, mTvTime;
	private int mNewsId;
	private News mNews;
	private TextView mTvCommentCount;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
//		ActionBarActivity act = (ActionBarActivity) activity;
//		mTvCommentCount = (TextView) act.getSupportActionBar().getCustomView()
//				.findViewById(R.id.tv_comment_count);
//		mTvCommentCount.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
////				UIHelper.showComment(getActivity(), mNews.getId(),
////						CommentList.CATALOG_NEWS);
//			}
//		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_news_detail,
				container, false);

		mNewsId = getActivity().getIntent().getIntExtra("news_id", 0);

		initViews(view);
		return view;
	}

	private void initViews(View view) {
		mEmptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
		mTvTitle = (TextView) view.findViewById(R.id.tv_title);
		mTvSource = (TextView) view.findViewById(R.id.tv_source);
		mTvTime = (TextView) view.findViewById(R.id.tv_time);

		mWebView = (WebView) view.findViewById(R.id.webview);
		initWebView(mWebView);
	}

	@Override
	protected String getCacheKey() {
		return new StringBuilder(NEWS_CACHE_KEY).append(mNewsId).toString();
	}

	@Override
	protected void sendRequestData() {
		mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
		TLog.log("Test", mNewsId + "");
		OSChinaApi.getNewsDetail(mNewsId, mHandler);
	}

	@Override
	protected Entity parseData(InputStream is) throws Exception {
		return XmlUtils.toBean(NewsDetail.class, is);
	}

	@Override
	protected Entity readData(Serializable seri) {
		return (NewsDetail) seri;
	}

	@Override
	protected void executeOnLoadDataSuccess(Entity entity) {
		mNews = ((NewsDetail) entity).getNews();
		fillUI();
		fillWebViewBody();
	}

	private void fillUI() {
		mTvTitle.setText(mNews.getTitle());
		mTvSource.setText(mNews.getAuthor());
		mTvTime.setText(StringUtils.friendly_time(mNews.getPubDate()));
	}

	private void fillWebViewBody() {
		String body = UIHelper.WEB_STYLE + mNews.getBody();

		body = UIHelper.setHtmlCotentSupportImagePreview(body);

		// 更多关于***软件的信息
		String softwareName = mNews.getSoftwareName();
		String softwareLink = mNews.getSoftwareLink();
		if (!StringUtils.isEmpty(softwareName)
				&& !StringUtils.isEmpty(softwareLink))
			body += String
					.format("<div id='oschina_software' style='margin-top:8px;color:#FF0000;font-weight:bold'>更多关于:&nbsp;<a href='%s'>%s</a>&nbsp;的详细信息</div>",
							softwareLink, softwareName);

		// 相关新闻
		if (mNews.getRelatives().size() > 0) {
			String strRelative = "";
			for (Relative relative : mNews.getRelatives()) {
				strRelative += String.format(
						"<a href='%s' style='text-decoration:none'>%s</a><p/>",
						relative.url, relative.title);
			}
			body += "<p/><div style=\"height:1px;width:100%;background:#DADADA;margin-bottom:10px;\"/>"
					+ String.format("<br/> <b>相关资讯</b> <div><p/>%s</div>",
							strRelative);
		}

		body += "<br/>";

		body += UIHelper.WEB_LOAD_IMAGES;

		mWebView.setWebViewClient(mWebClient);
		UIHelper.addWebImageShow(getActivity(), mWebView);
		mWebView.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
	}

	@Override
	protected int getFavoriteTargetId() {
		return mNews != null ? mNews.getId() : -1;
	}


	@Override
	protected String getShareContent() {
		return mNews != null ? mNews.getTitle() : null;
	}

	@Override
	protected String getShareUrl() {
		return mNews != null ? mNews.getUrl() : null;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
}
