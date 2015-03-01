package net.oschina.app.bean;

import net.oschina.app.R;
import net.oschina.app.fragment.AboutOSCFragment;
import net.oschina.app.fragment.ActiveFragment;
import net.oschina.app.fragment.BrowserFragment;
import net.oschina.app.fragment.CommentFrament;
import net.oschina.app.fragment.EventAppliesFragment;
import net.oschina.app.fragment.EventFragment;
import net.oschina.app.fragment.FeedBackFragment;
import net.oschina.app.fragment.FindUserFragment;
import net.oschina.app.fragment.MessageDetailFragment;
import net.oschina.app.fragment.MyInformationFragment;
import net.oschina.app.fragment.MyInformationFragmentDetail;
import net.oschina.app.fragment.QuestionTagFragment;
import net.oschina.app.fragment.SettingsFragment;
import net.oschina.app.fragment.SettingsNotificationFragment;
import net.oschina.app.fragment.ShakeFragment;
import net.oschina.app.fragment.SoftWareTweetsFrament;
import net.oschina.app.fragment.TweetPubFragment;
import net.oschina.app.fragment.TweetRecordFragment;
import net.oschina.app.fragment.UserBlogFragment;
import net.oschina.app.fragment.UserCenterFragment;
import net.oschina.app.team.fragment.DynamicDetailFragment;
import net.oschina.app.team.fragment.DynamicFragment;
import net.oschina.app.team.fragment.NoteBookFragment;
import net.oschina.app.team.fragment.NoteEditFragment;
import net.oschina.app.team.fragment.SelectTeamFragment;
import net.oschina.app.team.fragment.TeamNewIssueFragment;
import net.oschina.app.team.fragment.TeamTweetDetailFragment;
import net.oschina.app.team.fragment.UserInfoFragment;
import net.oschina.app.team.viewpagefragment.MyIssuePagerfragment;
import net.oschina.app.team.viewpagefragment.TeamProjectViewPagerFragment;
import net.oschina.app.viewpagerfragment.BlogViewPagerFragment;
import net.oschina.app.viewpagerfragment.EventViewPagerFragment;
import net.oschina.app.viewpagerfragment.FriendsViewPagerFragment;
import net.oschina.app.viewpagerfragment.NoticeViewPagerFragment;
import net.oschina.app.viewpagerfragment.OpensourceSoftwareFragment;
import net.oschina.app.viewpagerfragment.QuestViewPagerFragment;
import net.oschina.app.viewpagerfragment.SearchViewPageFragment;
import net.oschina.app.viewpagerfragment.UserFavoriteViewPagerFragment;

public enum SimpleBackPage {

    COMMENT(1, R.string.actionbar_title_comment, CommentFrament.class),

    QUEST(2, R.string.actionbar_title_questions, QuestViewPagerFragment.class),

    TWEET_PUB(3, R.string.actionbar_title_tweetpub, TweetPubFragment.class),

    SOFTWARE_TWEETS(4, R.string.actionbar_title_softtweet,
            SoftWareTweetsFrament.class),

    USER_CENTER(5, R.string.actionbar_title_user_center,
            UserCenterFragment.class),

    USER_BLOG(6, R.string.actionbar_title_user_blog, UserBlogFragment.class),

    MY_INFORMATION(7, R.string.actionbar_title_my_information,
            MyInformationFragment.class),

    MY_ACTIVE(8, R.string.actionbar_title_active, ActiveFragment.class),

    MY_MES(9, R.string.actionbar_title_mes, NoticeViewPagerFragment.class),

    OPENSOURCE_SOFTWARE(10, R.string.actionbar_title_softwarelist,
            OpensourceSoftwareFragment.class),

    MY_FRIENDS(11, R.string.actionbar_title_my_friends,
            FriendsViewPagerFragment.class),

    QUESTION_TAG(12, R.string.actionbar_title_question,
            QuestionTagFragment.class),

    MESSAGE_DETAIL(13, R.string.actionbar_title_message_detail,
            MessageDetailFragment.class),

    USER_FAVORITE(14, R.string.actionbar_title_user_favorite,
            UserFavoriteViewPagerFragment.class),

    SETTING(15, R.string.actionbar_title_setting, SettingsFragment.class),

    SETTING_NOTIFICATION(16, R.string.actionbar_title_setting_notification,
            SettingsNotificationFragment.class),

    ABOUT_OSC(17, R.string.actionbar_title_about_osc, AboutOSCFragment.class),

    BLOG(18, R.string.actionbar_title_blog_area, BlogViewPagerFragment.class),

    RECORD(19, R.string.actionbar_title_tweetpub, TweetRecordFragment.class),

    SEARCH(20, R.string.actionbar_title_search, SearchViewPageFragment.class),

    FIND_USER(21, R.string.actionbar_title_find_user, FindUserFragment.class),

    EVENT_LIST(22, R.string.actionbar_title_event, EventViewPagerFragment.class),

    EVENT_APPLY(23, R.string.actionbar_title_event_apply,
            EventAppliesFragment.class),

    SAME_CITY(24, R.string.actionbar_title_same_city, EventFragment.class),

    NOTE(25, R.string.actionbar_title_note, NoteBookFragment.class),

    NOTE_EDIT(26, R.string.actionbar_title_noteedit, NoteEditFragment.class),

    SHAKE(27, R.string.actionbar_title_shake, ShakeFragment.class),

    BROWSER(28, R.string.app_name, BrowserFragment.class),

    DYNAMIC(29, R.string.team_dynamic, DynamicFragment.class),

    MY_INFORMATION_DETAIL(30, R.string.actionbar_title_my_information,
            MyInformationFragmentDetail.class),

    FEED_BACK(31, R.string.str_feedback_title, FeedBackFragment.class),

    TEAM_USER_INFO(32, R.string.str_team_userinfo, UserInfoFragment.class),

    SELECT_TEAM(33, R.string.str_team_select_team, SelectTeamFragment.class),

    MY_ISSUE_PAGER(34, R.string.str_team_my_issue, MyIssuePagerfragment.class),

    TEAM_NEW_ISSUE(35, R.string.team_issue_new, TeamNewIssueFragment.class),

    DYNAMIC_DETAIL(36, R.string.team_detail_dynamic,
            DynamicDetailFragment.class),
            
    TEAM_PROJECT_MAIN(37, 0, TeamProjectViewPagerFragment.class);

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
