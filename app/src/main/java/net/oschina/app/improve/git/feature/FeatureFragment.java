package net.oschina.app.improve.git.feature;

import net.oschina.app.improve.base.BaseRecyclerFragment;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.git.bean.Project;
import net.oschina.app.improve.git.detail.ProjectDetailActivity;

/**
 * Created by haibin
 * on 2017/3/9.
 */

public class FeatureFragment extends BaseRecyclerFragment<FeatureContract.Presenter, Project>
        implements FeatureContract.View {

    public static FeatureFragment newInstance() {
        return new FeatureFragment();
    }

    @Override
    protected void onItemClick(Project project, int position) {
        ProjectDetailActivity.show(mContext, project);
    }

    @Override
    protected BaseRecyclerAdapter<Project> getAdapter() {
        return new ProjectAdapter(this);
    }
}
