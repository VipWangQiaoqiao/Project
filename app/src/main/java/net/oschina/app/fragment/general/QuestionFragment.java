package net.oschina.app.fragment.general;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.adapter.base.BaseListAdapter;
import net.oschina.app.bean.base.PageBean;
import net.oschina.app.bean.base.ResultBean;
import net.oschina.app.bean.question.Question;

import java.lang.reflect.Type;

/**
 * 技术问答界面
 */
public class QuestionFragment extends Fragment {

    private static final String TAG = "QuestionFragment";
    private GridView quesGridView = null;



    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Question>>>() {
        }.getType();
    }


    protected void initWidget(View root) {
      //  super.initWidget(root);

    }



    protected void initData() {
      //  super.initData();

    }



    protected BaseListAdapter<Question> getListAdapter() {
        return null;
    }


   // @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), position, Toast.LENGTH_SHORT).show();
    }

   // @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
