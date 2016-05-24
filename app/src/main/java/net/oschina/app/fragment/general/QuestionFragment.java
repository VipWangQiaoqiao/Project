package net.oschina.app.fragment.general;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.R;
import net.oschina.app.adapter.base.BaseListAdapter;
import net.oschina.app.adapter.general.QuesActionAdapter;
import net.oschina.app.adapter.general.QuestionAdapter;
import net.oschina.app.bean.base.PageBean;
import net.oschina.app.bean.base.ResultBean;
import net.oschina.app.bean.question.Question;
import net.oschina.app.fragment.base.BaseListFragment;

import java.lang.reflect.Type;

/**
 * 技术问答界面
 */
public class QuestionFragment extends BaseListFragment<Question> implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "QuestionFragment";
    private GridView quesGridView = null;
    private View headView;


    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        headView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main_question_header, null, false);
        quesGridView = (GridView) headView.findViewById(R.id.gv_ques);
        QuesActionAdapter quesActionAdapter = new QuesActionAdapter(getActivity());
        quesGridView.setAdapter(quesActionAdapter);
        quesGridView.setItemChecked(0, true);
        quesGridView.setOnItemSelectedListener(this);
        mListView.addHeaderView(headView);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_question;
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected BaseListAdapter<Question> getListAdapter() {
        return new QuestionAdapter(this);
    }

    @Override
    protected Type getType() {
        return new TypeToken<PageBean<Question>>() {
        }.getType();
    }

    @Override
    protected void setListData(ResultBean<PageBean<Question>> resultBean) {
        super.setListData(resultBean);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:

                break;
            case 1:

                break;
            case 2:

                break;
            case 3:

                break;
            default:
                break;

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
