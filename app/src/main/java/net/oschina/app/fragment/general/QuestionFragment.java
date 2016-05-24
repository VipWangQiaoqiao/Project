package net.oschina.app.fragment.general;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import net.oschina.app.R;
import net.oschina.app.adapter.base.BaseListAdapter;
import net.oschina.app.adapter.general.PostAdapter;
import net.oschina.app.adapter.general.QuesActionAdapter;
import net.oschina.app.bean.Post;
import net.oschina.app.bean.base.ResultBean;
import net.oschina.app.fragment.base.BaseListFragment;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 技术问答界面
 */
public class QuestionFragment extends BaseListFragment<Post> implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "QuestionFragment";
    private GridView quesGridView = null;

    @Override
    protected BaseListAdapter<Post> getListAdapter() {
        return new PostAdapter(this);
    }

    @Override
    protected Type getType() {
        return null;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mListView = (ListView) root.findViewById(R.id.lv_ques);
        quesGridView = (GridView) root.findViewById(R.id.gv_ques);
        QuesActionAdapter quesActionAdapter = new QuesActionAdapter(getActivity());
        quesGridView.setAdapter(quesActionAdapter);
        quesGridView.setOnItemSelectedListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_question;
    }

    @Override
    protected void requestData() {
        super.requestData();
        Log.d(TAG, "requestData: ---->");
    }

    @Override
    protected void initData() {
        super.initData();
        Log.d(TAG, "initData: ----->");

      //  AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
       // String url = "";
      //  RequestParams params = new RequestParams();
      //  params.put("", "");
      //  asyncHttpClient.get(getActivity(), url, params, mHandler);
    }

    @Override
    protected void setListData(ResultBean<List<Post>> resultBean) {
        super.setListData(resultBean);
        Log.d(TAG, "setListData: ------->");
    }

    @Override
    protected void onRequestStart() {
        super.onRequestStart();
        Log.d(TAG, "onRequestStart: ---------->");
    }

    @Override
    protected void onRequestFinish() {
        super.onRequestFinish();
        Log.d(TAG, "onRequestFinish: ------------->");
    }

    @Override
    protected void onRequestSuccess() {
        super.onRequestSuccess();
        Log.d(TAG, "onRequestSuccess: ---------------->");
    }

    @Override
    protected void onRequestError() {
        super.onRequestError();
        Log.d(TAG, "onRequestError: --------------->");
    }

    @Override
    public void onRefreshing() {
        super.onRefreshing();
        Log.d(TAG, "onRefreshing: ------------------>");
    }

    @Override
    protected void onComplete() {
        super.onComplete();
        Log.d(TAG, "onComplete: ----------------->");
    }

    @Override
    protected void onItemClick(Post item, int position) {
        super.onItemClick(item, position);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
