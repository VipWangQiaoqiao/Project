package net.oschina.app.improve.comment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.bean.comment.Refer;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.TweetTextView;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by JuQiu
 * on 16/6/12.
 */

public class CommentsView extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "CommentsView";
    private long mId;
    private int mType;
    private TextView mTitle;
    private TextView mSeeMore;
    private LinearLayout mLayComments;

    public CommentsView(Context context) {
        super(context);
        init();
    }

    public CommentsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CommentsView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.lay_detail_comment_layout, this, true);

        mTitle = (TextView) findViewById(R.id.tv_blog_detail_comment);
        mLayComments = (LinearLayout) findViewById(R.id.lay_blog_detail_comment);
        mSeeMore = (TextView) findViewById(R.id.tv_see_more_comment);
    }

    public void setTitle(String title) {
        if (!android.text.TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }
    }

    /**
     * @return TypeToken
     */
    Type getDataType() {
        return new TypeToken<ResultBean<PageBean<Comment>>>() {
        }.getType();
    }

    public void init(long id, int type, final int commentTotal, final RequestManager imageLoader, final OnCommentClickListener onCommentClickListener) {
        this.mId = id;
        this.mType = type;

        mSeeMore.setVisibility(View.GONE);
        setVisibility(GONE);

        OSChinaApi.getComments(id, type, "refer,reply", null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (throwable != null)
                    throwable.printStackTrace();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = getDataType();

                    String json = "{\n" +
                            "  \"code\": 1,\n" +
                            "  \"message\": \"SUCCESS\",\n" +
                            "  \"time\": \"2016-11-16 11:12:54\",\n" +
                            "  \"result\": {\n" +
                            "    \"items\": [\n" +
                            "      {\n" +
                            "        \"id\": 295852044,\n" +
                            "        \"author\": {\n" +
                            "          \"name\": \"_BBQ_\",\n" +
                            "          \"id\": 866432,\n" +
                            "          \"portrait\": \"https://static.oschina.net/uploads/user/433/866432_50.jpg?t=1462683883000\"\n" +
                            "        },\n" +
                            "        \"best\": true,\n" +
                            "        \"content\": \"回复 \\n<a href=\\\"https://my.oschina.net/eechen\\\">@eechen</a> : 真想不通你这屁都不懂的的写过几行php的小毛头就哪来的脸说别人无知？你这种见解估计也就做做高中计算机实验的水平吧？\",\n" +
                            "        \"pubDate\": \"2016-11-16 09:37:04\",\n" +
                            "        \"appClient\": 0,\n" +
                            "        \"vote\": 0,\n" +
                            "        \"voteState\": 0,\n" +
                            "        \"refer\": [\n" +
                            "          {\n" +
                            "            \"author\": \"eechen\",\n" +
                            "            \"content\": \"树莓派有什么用呢?一般不就是用来搭建低功耗Web服务么? 它的设备制造商居然卖了8.7亿美元. 话说iOS上的DraftCode卖11美元一份,Google Play上的KSWeb卖4美元. 没用的东西也能卖钱,我也是挺好奇的,你能告诉我为什么? 偏见源于无知,无知还不算可怕,可怕的是拿无知出来炫耀.\",\n" +
                            "            \"pubDate\": \"2016-11-16 09:37:04\"\n" +
                            "          },\n" +
                            "          {\n" +
                            "            \"author\": \"eechen\",\n" +
                            "            \"content\": \"买了Mac,就能免费安装Xcode用原生OC和Swift开发iOS和Mac应用,也能免费用Google的Android Studio用Java开发原生Android应用,为什么还要去用Visual Studio这个Xamarin Studio的马甲呢?就为了用CSharp?呵呵.\",\n" +
                            "            \"pubDate\": \"2016-11-16 09:37:04\"\n" +
                            "          },\n" +
                            "          {\n" +
                            "            \"author\": \"struct\",\n" +
                            "            \"content\": \"那么问题就来了。 买了 Mac，就能免费安装 Xcode 用原生 OC 和 Swift 开发 iOS 和 Mac 应用，也能免费用 Google 的 Android Studio 用 Java 开发原生 Android 应用，为什么还要去用 PHP7 + Webview 开发 Android 马甲应用呢？就为了用 PHP？呵呵。\",\n" +
                            "            \"pubDate\": \"2016-11-16 09:37:04\"\n" +
                            "          }\n" +
                            "        ],\n" +
                            "        \"reply\": [\n" +
                            "          {\n" +
                            "            \"id\": 32132,\n" +
                            "            \"author\": {\n" +
                            "              \"id\": 312312,\n" +
                            "              \"name\": \"发布者昵称\",\n" +
                            "              \"portrait\": \"https://static.oschina.net/uploads/user/1263/2527376_50.jpg?t=1448022217000\"\n" +
                            "            },\n" +
                            "            \"content\": \"我想说这是引用的内容\",\n" +
                            "            \"pubDate\": \"2013-09-17 16:49:34\"\n" +
                            "          }\n" +
                            "        ]\n" +
                            "      },\n" +
                            "      {\n" +
                            "        \"appClient\": 0,\n" +
                            "        \"author\": {\n" +
                            "          \"name\": \"_BBQ_\",\n" +
                            "          \"id\": 866432,\n" +
                            "          \"portrait\": \"https://static.oschina.net/uploads/user/433/866432_50.jpg?t=1462683883000\"\n" +
                            "        },\n" +
                            "        \"best\": true,\n" +
                            "        \"content\": \"回复 \\n<a href=\\\"https://my.oschina.net/eechen\\\">@eechen</a> : 树莓派有GPIO手机有吗？树莓派可以拓展RJ45接口手机能吗？树莓派能输出HDMI大部分手机能吗？树莓派能装ubuntu大部分手机能吗？不用扯后面乱七八糟的，就GPIO一项手机有什么可比性？拿开发板做web的都是智障。\",\n" +
                            "        \"id\": 295852018,\n" +
                            "        \"pubDate\": \"2016-11-16 09:34:54\",\n" +
                            "        \"refer\": [\n" +
                            "          {\n" +
                            "            \"author\": \"eechen\",\n" +
                            "            \"content\": \"树莓派有什么用呢?一般不就是用来搭建低功耗Web服务么? 它的设备制造商居然卖了8.7亿美元. 话说iOS上的DraftCode卖11美元一份,Google Play上的KSWeb卖4美元. 没用的东西也能卖钱,我也是挺好奇的,你能告诉我为什么? 偏见源于无知,无知还不算可怕,可怕的是拿无知出来炫耀.\"\n" +
                            "          },\n" +
                            "          {\n" +
                            "            \"author\": \"eechen\",\n" +
                            "            \"content\": \"买了Mac,就能免费安装Xcode用原生OC和Swift开发iOS和Mac应用,也能免费用Google的Android Studio用Java开发原生Android应用,为什么还要去用Visual Studio这个Xamarin Studio的马甲呢?就为了用CSharp?呵呵.\",\n" +
                            "            \"pubDate\": \"2016-11-16 09:37:04\"\n" +
                            "          },\n" +
                            "          {\n" +
                            "            \"author\": \"struct\",\n" +
                            "            \"content\": \"那么问题就来了。 买了 Mac，就能免费安装 Xcode 用原生 OC 和 Swift 开发 iOS 和 Mac 应用，也能免费用 Google 的 Android Studio 用 Java 开发原生 Android 应用，为什么还要去用 PHP7 + Webview 开发 Android 马甲应用呢？就为了用 PHP？呵呵。\",\n" +
                            "            \"pubDate\": \"2016-11-16 09:37:04\"\n" +
                            "          },\n" +
                            "          {\n" +
                            "            \"author\": \"eechen\",\n" +
                            "            \"content\": \"<a href=\\\"https://www.oschina.net/p/PHPDroid\\\">#PHPDroid#</a> 你说对了一半.PHPDroid就是给PHP开发者在Android上玩的.还有一点就是PHPDroid可以把Android手机打造成一台支持PHP编程和SQLite存储的Web服务器.iOS上的DraftCode也是如此.国人的Anmpp和俄罗斯人的KSWeb更除了PHP更是集成Nginx/MySQL/PostgreSQL,为Android提供完备的LAMP服务搭建支持.  可能有人会说,有什么用呢?我只能说那些人孤陋寡闻,用树莓派搭建个人家用的Web服务,有了上面我说的那些东西,你在Android手机上也可以搭建,毕竟本质都是Linux,而且一台廉价的Android手机(红米4A 500元)的性价比我觉得比树莓派(加上SD卡 250元)还高.说个具体实例吧,PocketMine这个MineCraft手机服务端就是PHP写的,可以安装在Android和iOS上. https://github.com/PocketMine/php-build-scripts\",\n" +
                            "            \"pubDate\": \"2016-11-16 09:37:04\"\n" +
                            "          },\n" +
                            "          {\n" +
                            "            \"author\": \"_BBQ_\",\n" +
                            "            \"content\": \"又有什么用呢？\",\n" +
                            "            \"pubDate\": \"2016-11-16 09:37:04\"\n" +
                            "          }\n" +
                            "        ],\n" +
                            "        \"vote\": 0,\n" +
                            "        \"voteState\": 0\n" +
                            "      },\n" +
                            "      {\n" +
                            "        \"appClient\": 3,\n" +
                            "        \"author\": {\n" +
                            "          \"name\": \"ccimage\",\n" +
                            "          \"id\": 2267007,\n" +
                            "          \"portrait\": \"https://static.oschina.net/uploads/user/1133/2267007_50.jpg?t=1415270116000\"\n" +
                            "        },\n" +
                            "        \"best\": false,\n" +
                            "        \"content\": \"用unity的人会用的\",\n" +
                            "        \"id\": 295850338,\n" +
                            "        \"pubDate\": \"2016-11-16 07:14:53\",\n" +
                            "        \"refer\": [\n" +
                            "          {\n" +
                            "            \"author\": \"LinkerLin\",\n" +
                            "            \"content\": \"似乎没人会要在Mac上用微软系的东西啊。\",\n" +
                            "            \"pubDate\": \"2016-11-16 07:14:53\"\n" +
                            "          }\n" +
                            "        ],\n" +
                            "        \"vote\": 0,\n" +
                            "        \"voteState\": 0\n" +
                            "      },\n" +
                            "      {\n" +
                            "        \"appClient\": 0,\n" +
                            "        \"author\": {\n" +
                            "          \"name\": \"oreak\",\n" +
                            "          \"id\": 1434631,\n" +
                            "          \"portrait\": \"https://static.oschina.net/uploads/user/717/1434631_50.jpg?t=1432619358000\"\n" +
                            "        },\n" +
                            "        \"best\": true,\n" +
                            "        \"content\": \"说jetbrains占用资源的，先换掉自己电脑\",\n" +
                            "        \"id\": 295835957,\n" +
                            "        \"pubDate\": \"2016-11-15 11:16:26\",\n" +
                            "        \"vote\": 0,\n" +
                            "        \"voteState\": 0\n" +
                            "      }\n" +
                            "    ],\n" +
                            "    \"nextPageToken\": \"DBA816934CD0AA59\",\n" +
                            "    \"prevPageToken\": \"0997C855C600E421\",\n" +
                            "    \"requestCount\": 20,\n" +
                            "    \"responseCount\": 20,\n" +
                            "    \"totalResults\": 63\n" +
                            "  }\n" +
                            "}";

                    ResultBean<PageBean<Comment>> resultBean = AppOperator.createGson().fromJson(json, type);

                    if (resultBean != null && resultBean.isSuccess()) {
                        addComment(resultBean.getResult().getItems(), commentTotal, imageLoader, onCommentClickListener);
                        return;
                    }
                    onFailure(statusCode, headers, responseString, null);
                } catch (Exception e) {
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    private void addComment(List<Comment> comments, int commentTotal, RequestManager imageLoader, final OnCommentClickListener onCommentClickListener) {
        if (comments != null && comments.size() > 0) {
            if (comments.size() < commentTotal) {
                mSeeMore.setVisibility(VISIBLE);
                mSeeMore.setOnClickListener(this);
            }

            if (getVisibility() != VISIBLE) {
                setVisibility(VISIBLE);
            }

            for (final Comment comment : comments) {
                if (comment != null) {
                    ViewGroup lay = addComment(false, comment, imageLoader, onCommentClickListener);
                    lay.findViewById(R.id.line).setVisibility(View.INVISIBLE);
                }
            }
        } else {
            setVisibility(View.GONE);
        }
    }

    public ViewGroup addComment(final Comment comment, RequestManager imageLoader, final OnCommentClickListener onCommentClickListener) {
        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }

        return addComment(true, comment, imageLoader, onCommentClickListener);
    }

    private ViewGroup addComment(boolean first, final Comment comment, RequestManager imageLoader, final OnCommentClickListener onCommentClickListener) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") ViewGroup lay = (ViewGroup) inflater.inflate(R.layout.lay_comment_item, null, false);
        ImageView ivAvatar = (ImageView) lay.findViewById(R.id.iv_avatar);
        imageLoader.load(comment.getAuthor().getPortrait()).error(R.mipmap.widget_dface)
                .into(ivAvatar);
        ivAvatar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                OtherUserHomeActivity.show(getContext(), comment.getAuthor().getId());
            }
        });

        ((TextView) lay.findViewById(R.id.tv_name)).setText(comment.getAuthor().getName());

        ((TextView) lay.findViewById(R.id.tv_pub_date)).setText(
                StringUtils.formatSomeAgo(comment.getPubDate()));

        TweetTextView content = ((TweetTextView) lay.findViewById(R.id.tv_content));
        CommentsUtil.formatHtml(getResources(), content, comment.getContent());
        Refer[] refers = comment.getRefer();
        if (refers != null && refers.length > 0) {
            // 最多5层
            for (int i = 0; i < 5; i++) {
                Refer refer = refers[i];
                View view = CommentsUtil.getReferLayout(inflater, refer, 5);
                lay.addView(view, lay.indexOfChild(content));
            }
        }

        lay.findViewById(R.id.btn_comment).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onCommentClickListener.onClick(v, comment);
            }
        });

        if (first)
            mLayComments.addView(lay, 0);
        else
            mLayComments.addView(lay);

        return lay;
    }

    @Override
    public void onClick(View v) {
        if (mId != 0 && mType != 0)
            CommentsActivity.show(getContext(), mId, mType);
    }
}
