package net.oschina.app.team.ui;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaTeamApi;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.team.bean.Team;
import net.oschina.app.team.bean.TeamGit;
import net.oschina.app.team.bean.TeamIssueCatalog;
import net.oschina.app.team.bean.TeamIssueCatalogList;
import net.oschina.app.team.bean.TeamMember;
import net.oschina.app.team.bean.TeamMemberList;
import net.oschina.app.team.bean.TeamProject;
import net.oschina.app.team.bean.TeamProjectList;
import net.oschina.app.ui.dialog.CommonDialog;
import net.oschina.app.ui.dialog.DialogHelper;
import net.oschina.app.util.TypefaceUtils;
import net.oschina.app.util.XmlUtils;

import org.apache.http.Header;

import java.util.Calendar;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 火蚁 on 15/5/28.
 */
public class TeamNewIssueActivity extends BaseActivity {

    @InjectView(R.id.et_issue_title)
    EditText mEtTitle;

    @InjectView(R.id.tv_issue_project)
    TextView mTvProject;

    @InjectView(R.id.tv_issue_catalog)
    TextView mTvCatalog;

    @InjectView(R.id.tv_issue_touser)
    TextView mTvToUser;

    @InjectView(R.id.tv_issue_time)
    TextView mTvTime;

    @InjectView(R.id.rl_issue_push)
    View mRlGitPush;
    @InjectView(R.id.push_line)
    View mPushLine;

    @InjectView(R.id.tv_issue_push_source)
    TextView mTvPushSource;

    @InjectView(R.id.cb_issue_push_check)
    CheckBox mCbPush;

    private Team mTeam;

    private TeamProject mTeamProject;

    private TeamIssueCatalog mTeamCatalog;

    private MenuItem mSendMenu;

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_team_new_issue;
    }

    @Override
    public void initView() {
        mEtTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                updateMenuState();
            }
        });

        TypefaceUtils.setTypeface((TextView) findViewById(R.id.tv_fa_project));
        TypefaceUtils.setTypeface((TextView) findViewById(R.id.tv_fa_catalog));
        TypefaceUtils.setTypeface((TextView) findViewById(R.id.tv_fa_touser));
        TypefaceUtils.setTypeface((TextView) findViewById(R.id.tv_fa_time));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.team_new_issue_menu, menu);
        mSendMenu = menu.findItem(R.id.team_issue_new_pub);
        updateMenuState();
        return super.onCreateOptionsMenu(menu);
    }

    private void updateMenuState() {
        if (mEtTitle.getText().length() == 0) {
            mSendMenu.setEnabled(false);
            mSendMenu.setIcon(R.drawable.actionbar_unsend_icon);
        } else {
            mSendMenu.setEnabled(true);
            mSendMenu.setIcon(R.drawable.actionbar_send_icon);
        }
    }

    private int mYear, mMonth, mDay;

    @Override
    public void initData() {
        Bundle args = getIntent().getExtras();
        if (args != null) {
            mTeam = (Team) args.getSerializable("team");
            mTeamProject = (TeamProject) args.getSerializable("project");
            mTeamCatalog = (TeamIssueCatalog) args.getSerializable("catalog");
        }

        if (mTeamProject != null && mTeamProject.getGit().getId() != 0
                && mTeamProject.getGit().getId() != -1) {
            mTvProject.setText(mTeamProject.getGit().getName());
            mTvProject.setTag(mTeamProject);
        } else {
            TeamProject project = new TeamProject();
            TeamGit git = new TeamGit();
            project.setSource("");
            git.setId(-1);// -1表示
            git.setName("不指定项目");
            project.setGit(git);
            mTeamProject = project;
        }
        checkIsShowPush();
        if (mTeamCatalog != null) {
            mTvCatalog.setText(mTeamCatalog.getTitle());
            mTvCatalog.setTag(mTeamCatalog);
        }

        // 初始化为今天
        Calendar cal = Calendar.getInstance();
        this.mYear = cal.get(Calendar.YEAR);
        this.mMonth = cal.get(Calendar.MONTH);
        this.mDay = cal.get(Calendar.DATE);
    }

    private CommonDialog projectDialog;
    private CommonDialog catalogDialog;
    private CommonDialog toUserDialog;

    private List<TeamProject> projects;
    private int projectIndex = 0;

    private List<TeamIssueCatalog> catalogs;
    private int catalogIndex = 0;

    private List<TeamMember> toUsers;
    private int toUserIndex = 0;

    private String issueTime;

    private void showTeamProjectSelected(final List<TeamProject> projects) {
        if (this.projects == null) {
            TeamProject unProject = new TeamProject();
            TeamGit git = new TeamGit();
            git.setId(-1);
            git.setName("不指定项目");
            unProject.setGit(git);
            projects.add(0, unProject);
            this.projects = projects;
        }
        if (projectDialog == null) {
            projectDialog = DialogHelper
                    .getPinterestDialogCancelable(this);
            projectDialog.setTitle("指定项目");
        }

        final CharSequence[] arrays = new CharSequence[projects.size()];
        for (int i = 0; i < projects.size(); i++) {
            arrays[i] = projects.get(i).getGit().getName();
            if (mTeamProject != null) {
                if (mTeamProject.getGit().getName()
                        .equals(projects.get(i).getGit().getName())
                        && mTeamProject.getGit().getId() == projects.get(i)
                        .getGit().getId()) {
                    projectIndex = i;
                }
            }
        }
        projectDialog.setItems(arrays, projectIndex, new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                if (position == projectIndex) {
                    projectDialog.dismiss();
                    return;
                }
                projectIndex = position;
                mTvProject.setText(arrays[position]);
                mTeamProject = projects.get(position);
                checkIsShowPush();
                clearCatalogAndToUser();
                projectDialog.dismiss();
            }
        });

        projectDialog.show();
    }

    private void showTeamCatalogSelected(final List<TeamIssueCatalog> list) {
        this.catalogs = list;
        if (catalogDialog == null) {
            catalogDialog = DialogHelper
                    .getPinterestDialogCancelable(this);
            catalogDialog.setTitle("指定任务列表");

        }
        final CharSequence[] catalogs = new CharSequence[list.size()];
        for (int i = 0; i < list.size(); i++) {
            catalogs[i] = list.get(i).getTitle();
            if (mTeamCatalog != null) {
                if (mTeamCatalog.getTitle().equals(list.get(i).getTitle())) {
                    catalogIndex = i;
                }
            }
        }
        catalogDialog.setItems(catalogs, catalogIndex,
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // TODO Auto-generated method stub
                        catalogIndex = position;
                        mTeamCatalog = list.get(position);
                        mTvCatalog.setText(catalogs[position]);
                        catalogDialog.dismiss();
                    }
                });
        catalogDialog.show();
    }

    private void showIssueToUser(List<TeamMember> list) {
        TeamMember member = new TeamMember();
        member.setId(-1);
        member.setName("未指派");
        list.add(0, member);
        this.toUsers = list;
        if (toUserDialog == null) {
            toUserDialog = DialogHelper
                    .getPinterestDialogCancelable(this);
            toUserDialog.setTitle("指派成员");

        }
        final CharSequence[] toUsers = new CharSequence[list.size()];
        for (int i = 0; i < list.size(); i++) {
            toUsers[i] = list.get(i).getName();
        }
        toUserDialog.setItems(toUsers, toUserIndex, new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                toUserIndex = position;
                mTvToUser.setText(toUsers[position]);
                toUserDialog.dismiss();
            }
        });
        toUserDialog.show();
    }

    // 重新选定项目之后清空任务列表和指派成员
    private void clearCatalogAndToUser() {
        // 清除任务列表
        catalogIndex = 0;
        catalogs = null;
        mTvCatalog.setText("未指定列表");

        // 清除指派列表
        toUserIndex = 0;
        toUsers = null;
        mTvToUser.setText("未指派");
    }

    private void checkIsShowPush() {
        if (mTeamProject == null) return;
        if (mTeamProject.getGit().getId() == -1 || !mTeamProject.isGitpush()) {
            mRlGitPush.setVisibility(View.GONE);
            mPushLine.setVisibility(View.GONE);
        } else {
            mRlGitPush.setVisibility(View.VISIBLE);
            mPushLine.setVisibility(View.VISIBLE);
            if (mTeamProject.getSource().equals(TeamProject.GITHUB)) {
                mTvPushSource.setText("同步到GitHub");
            } else {
                mTvPushSource.setText("同步到Git@OSC");
            }
        }
    }


    @Override
    @OnClick({R.id.rl_issue_project, R.id.rl_issue_catalog,
            R.id.rl_issue_touser, R.id.rl_issue_time})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_issue_project:
            case R.id.rl_issue_catalog:
            case R.id.rl_issue_touser:
                showSelectSomeInfo(v.getId());
                break;
            case R.id.rl_issue_time:
                showIssueDeadlineTime();
                break;
            default:
                break;
        }
    }

    private void showIssueDeadlineTime() {

        final DatePickerDialog dateDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                    }

                }, mYear, mMonth, mDay);
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        issueTime = "";
                        mTvTime.setText(issueTime);
                        break;
                    case DialogInterface.BUTTON_POSITIVE:
                        mYear = dateDialog.getDatePicker().getYear();
                        mMonth = dateDialog.getDatePicker().getMonth();
                        mDay = dateDialog.getDatePicker().getDayOfMonth();
                        issueTime = mYear + "-" + (mMonth + 1) + "-" + mDay;
                        mTvTime.setText(issueTime);
                        break;

                    default:
                        break;
                }
            }
        };
        dateDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                clickListener);
        dateDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "清除",
                clickListener);
        dateDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确认",
                clickListener);
        dateDialog.show();
    }

    private final int show_project = R.id.rl_issue_project;
    private final int show_issue_catalog = R.id.rl_issue_catalog;
    private final int show_issue_touser = R.id.rl_issue_touser;

    private void showSelectSomeInfo(int showType) {
        switch (showType) {
            case show_project:
                tryToShowProjectDialog();
                break;
            case show_issue_catalog:
                tryToShowCatalogDialog();
                break;
            case show_issue_touser:
                tryToShowToUserDilaog();
                break;
            default:
                break;
        }
    }

    private void tryToShowProjectDialog() {
        if (projectDialog != null && projects != null) {
            showTeamProjectSelected(projects);
        } else {
            OSChinaTeamApi.getTeamProjectList(mTeam.getId(),
                    new MySomeInfoHandler(show_project));

        }
    }

    private void tryToShowCatalogDialog() {
        OSChinaTeamApi.getTeamCatalogIssueList(AppContext.getInstance()
                        .getLoginUid(), mTeam.getId(), mTeamProject.getGit().getId(),
                mTeamProject.getSource(), new MySomeInfoHandler(
                        show_issue_catalog));

    }

    private void tryToShowToUserDilaog() {
        OSChinaTeamApi.getTeamProjectMemberList(mTeam.getId(), mTeamProject,
                new MySomeInfoHandler(show_issue_touser));
    }

    public class MySomeInfoHandler extends AsyncHttpResponseHandler {

        private int showType = show_project;

        public MySomeInfoHandler(int showType) {
            this.showType = showType;
        }

        @Override
        public void onFinish() {
            // TODO Auto-generated method stub
            super.onFinish();
            hideWaitDialog();
        }

        @Override
        public void onStart() {
            // TODO Auto-generated method stub
            super.onStart();
            showWaitDialog("获取中...");
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            // TODO Auto-generated method stub
            showFaile();
        }

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            // TODO Auto-generated method stub
            switch (showType) {
                // 显示项目选择对话框
                case show_project:
                    TeamProjectList plist = XmlUtils.toBean(TeamProjectList.class,
                            arg2);
                    if (plist == null) {
                        showFaile();
                        return;
                    }
                    showTeamProjectSelected(plist.getList());
                    break;
                // 显示任务列表选择对话框
                case show_issue_catalog:
                    TeamIssueCatalogList clist = XmlUtils.toBean(
                            TeamIssueCatalogList.class, arg2);
                    if (clist == null) {
                        showFaile();
                        return;
                    }
                    showTeamCatalogSelected(clist.getList());
                    break;
                // 显示指派用户对话框
                case show_issue_touser:
                    TeamMemberList tpmList = XmlUtils.toBean(TeamMemberList.class,
                            arg2);
                    if (tpmList == null) {
                        showFaile();
                        return;
                    }
                    showIssueToUser(tpmList.getList());
                    break;
                default:
                    break;
            }
        }

        private void showFaile() {
            AppContext.showToast("获取失败");
        }
    }
}
