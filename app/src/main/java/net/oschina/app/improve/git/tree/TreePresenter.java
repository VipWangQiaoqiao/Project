package net.oschina.app.improve.git.tree;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.improve.git.api.API;
import net.oschina.app.improve.git.bean.Project;
import net.oschina.app.improve.git.bean.Tree;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2017/3/13.
 */

class TreePresenter implements TreeContract.Presenter {
    private final TreeContract.View mView;
    private Project mProject;
    private String mBranch;
    private List<String> mPaths;
    private HashMap<String, List<Tree>> mTreeMap = new HashMap<>();
    private LinkedHashMap<Integer, List<Tree>> mCodeMap;
    private boolean isLoading;

    TreePresenter(TreeContract.View mView, Project project) {
        this.mView = mView;
        this.mProject = project;
        mBranch = project.getDefaultBranch();
        mPaths = new ArrayList<>();
        mCodeMap = new LinkedHashMap<>();
        this.mView.setPresenter(this);
    }

    @Override
    public void onRefreshing() {
        if (mTreeMap.containsKey(mBranch)) {
            mView.onRefreshSuccess(mTreeMap.get(mBranch));
            return;
        }
        if (isLoading) return;
        isLoading = true;
        API.getCodeTree(mProject.getId(), getPath(), mBranch, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.state_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<List<Tree>>() {
                    }.getType();
                    List<Tree> bean = new Gson().fromJson(responseString, type);
                    if (bean != null && bean.size() != 0) {
                        mTreeMap.put(mBranch, bean);
                        mCodeMap.put(0, bean);
                        mView.onRefreshSuccess(bean);
                        isLoading = false;
                    } else {
                        mView.showNetworkError(R.string.state_network_error);
                    }
                    mView.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showNetworkError(R.string.state_network_error);
                    mView.onComplete();
                }
            }
        });
    }

    @Override
    public void onLoadMore() {
        // TODO: 2017/3/13
    }

    private void remove(int position) {
        if (mCodeMap.size() < position && mCodeMap.containsKey(position))
            return;
        int size = mCodeMap.size();
        for (int i = position; i <= size; i++) {
            mCodeMap.remove(i + 1);
            if (mPaths.size() > position && position >= 0)
                mPaths.remove(position);//一直移除最后一项
        }
        isLoading = false;
    }

    @Override
    public void preLoad(int position) {
        if (mPaths.size() < position)
            return;
        isLoading = true;
        List<Tree> codes = mCodeMap.get(position);
        mView.onRefreshSuccess(codes);
        remove(position);
    }

    @Override
    public void preLoad() {
        preLoad(mPaths.size() - 1);
    }

    @Override
    public void nextLoad(final String path) {
        if (isLoading) return;
        isLoading = true;
        API.getCodeTree(mProject.getId(),
                getPath() + path + "/",
                mBranch,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        mView.showNetworkError(R.string.state_network_error);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            Type type = new TypeToken<List<Tree>>() {
                            }.getType();
                            List<Tree> bean = new Gson().fromJson(responseString, type);
                            if (bean != null && bean.size() != 0) {
                                mCodeMap.put(mPaths.size() + 1, bean);
                                mPaths.add(path);
                                mView.onRefreshSuccess(bean);

                            } else {
                                mView.showNetworkError(R.string.state_network_error);
                            }
                            mView.onComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mView.showNetworkError(R.string.state_network_error);
                            mView.onComplete();
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        isLoading = false;
                    }
                });
    }

    @Override
    public String getPath() {
        StringBuilder sb = new StringBuilder();
        for (String s : mPaths) {
            sb.append(s).append("/");
        }
        return sb.toString();
    }

    boolean isCanBack() {
        return mPaths.size() == 0;
    }

    @Override
    public Project getProject() {
        return mProject;
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getImageUrl(String fileName) {
        return "https://git.oschina.net/" + mProject.getPathWithNamespace() + "/" + "raw" + "/" + mBranch + "/" + URLEncoder.encode
                (getPath() + fileName);
    }

    /**
     * 切换分支清空缓存代码仓库
     */
    @Override
    public void setBranch(String branch) {
        this.mBranch = branch;
        mPaths.clear();
        mTreeMap.clear();
        mCodeMap.clear();
    }

    @Override
    public String getBranch() {
        return mBranch;
    }
}
