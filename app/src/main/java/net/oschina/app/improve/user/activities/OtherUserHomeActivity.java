package net.oschina.app.improve.user.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.ApiHttpClient;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Active;
import net.oschina.app.bean.ActiveList;
import net.oschina.app.bean.User;
import net.oschina.app.bean.UserInformation;
import net.oschina.app.improve.base.activities.BaseRecyclerViewActivity;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.user.adapter.UserActiveAdapter;
import net.oschina.app.improve.widget.SolarSystemView;
import net.oschina.app.util.XmlUtils;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 别的用户的主页
 * Created by thanatos on 16/7/13.
 */
public class OtherUserHomeActivity extends BaseRecyclerViewActivity<Active> {

    public static final String KEY_BUNDLE = "KEY_BUNDLE_IN_OTHER_USER_HOME";

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.iv_portrait) CircleImageView mPortrait;
    @Bind(R.id.tv_nick) TextView mNick;
    @Bind(R.id.tv_summary) TextView mSummary;
    @Bind(R.id.tv_score) TextView mScore;
    @Bind(R.id.tv_count_follow) TextView mCountFollow;
    @Bind(R.id.tv_count_fans) TextView mCountFans;
    @Bind(R.id.view_solar_system) SolarSystemView mSolarSystem;

    private User user;
    private int pageNum = 0;
    private AsyncHttpResponseHandler mActivesHandler;

    public static void show(Context context, User user) {
//        if (user == null) return;
        Intent intent = new Intent(context, OtherUserHomeActivity.class);
        intent.putExtra(KEY_BUNDLE, user);
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        user = (User) bundle.getSerializable(KEY_BUNDLE);
        if (user == null || user.getId() <= 0) return false;
        return super.initBundle(bundle);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_other_user_home;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mToolbar.setTitle("");
        mToolbar.setSubtitle("");
        mToolbar.setNavigationIcon(R.drawable.btn_back_normal);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        mPortrait.post(new Runnable() {
            @Override
            public void run() {
                int mStatusBarHeight = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Rect rectangle = new Rect();
                    Window window = getWindow();
                    window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
                    mStatusBarHeight = rectangle.top;
                }
                float x = mPortrait.getX();
                float y = mPortrait.getY();
                float px = x + mPortrait.getWidth() / 2;
                float py = y + mPortrait.getHeight() / 2 + mStatusBarHeight;

                SolarSystemView.Planet planet1 = new SolarSystemView.Planet();
                planet1.setClockwise(true);
                planet1.setTrackColor(0XFF24e28e);
                planet1.setColor(0XFF24e28e);
                planet1.setTrackWidth(2);
                planet1.setSelfRadius(6);
                planet1.setAngleRate(0.01F);
                planet1.setRadius(140);
                planet1.setOriginAngle(0);

                SolarSystemView.Planet planet2 = new SolarSystemView.Planet();
                planet2.setClockwise(false);
                planet2.setTrackColor(0XFF24e28e);
                planet2.setColor(0XFF24e28e);
                planet2.setTrackWidth(2);
                planet2.setSelfRadius(6);
                planet2.setAngleRate(0.02F);
                planet2.setRadius(240);
                planet2.setOriginAngle(0);

                SolarSystemView.Planet planet3 = new SolarSystemView.Planet();
                planet3.setClockwise(true);
                planet3.setTrackColor(0XFF24e28e);
                planet3.setColor(0XFF24e28e);
                planet3.setTrackWidth(2);
                planet3.setSelfRadius(6);
                planet3.setAngleRate(0.03F);
                planet3.setRadius(380);
                planet3.setOriginAngle(0);

                SolarSystemView.Planet planet4 = new SolarSystemView.Planet();
                planet4.setClockwise(false);
                planet4.setTrackColor(0XFF24e28e);
                planet4.setColor(0XFF24e28e);
                planet4.setTrackWidth(2);
                planet4.setSelfRadius(6);
                planet4.setAngleRate(0.02F);
                planet4.setRadius(500);
                planet4.setOriginAngle(0);


                mSolarSystem.addPlanets(planet1);
                mSolarSystem.addPlanets(planet2);
                mSolarSystem.addPlanets(planet3);
                mSolarSystem.addPlanets(planet4);
                mSolarSystem.setPivotPoint(px, py);
            }
        });
        injectDataToView();
    }

    private void injectDataToView() {
        getImageLoader()
                .load(user.getPortrait())
                .asBitmap()
                .placeholder(R.drawable.widget_dface)
                .error(R.drawable.widget_dface)
                .into(mPortrait);
        mNick.setText(user.getName());
        // TODO summary
        mScore.setText(String.format("积分 %s", user.getScore()));
        mCountFans.setText(String.format("粉丝 %s", user.getFans()));
        mCountFollow.setText(String.format("关注 %s", user.getFollowers()));

    }

    @Override
    protected void initData() {
        super.initData();
        // temporary usage, changing it util new api come up
        mActivesHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    UserInformation info = XmlUtils.toBean(UserInformation.class, responseBody);
                    if (pageNum == 0){
                        user = info.getUser();
                        injectDataToView();
                    }
                    setListData(info.getActiveList());
                    onLoadingSuccess();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(OtherUserHomeActivity.this, "获取列表失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                onLoadingFinish();
            }
        };

        onRefreshing();
    }

    @Override
    public void onLoadMore() {
        requestData(pageNum);
    }

    @Override
    protected void requestData() {
        requestData(0);
    }

    @Override
    protected void onLoadingSuccess() {
        super.onLoadingSuccess();
        if (mIsRefresh) pageNum = 0;
        ++pageNum;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_other_user, menu);
        return true;
    }

    private void requestData(int pageNum) {
        OSChinaApi.getUserInformation(
                AppContext.getInstance().getLoginUid(),
                user.getId(),
                user.getName(),
                pageNum,
                mActivesHandler
        );
    }

    protected void setListData(List<Active> actives) {
        if (mIsRefresh) {
            mAdapter.clear();
            mAdapter.addAll(actives);
            mRefreshLayout.setCanLoadMore(true);
        } else {
            mAdapter.addAll(actives);
        }
        if (actives.size() < 20) {
            mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, false);
        }
    }

    @Override
    protected Type getType() {
        return new TypeToken<Active>() {}.getType();
    }

    @Override
    protected BaseRecyclerAdapter<Active> getRecyclerAdapter() {
        return new UserActiveAdapter(this, BaseRecyclerAdapter.ONLY_FOOTER);
    }
}
