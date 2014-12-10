package net.oschina.app.fragment;

import java.util.ArrayList;

import net.oschina.app.R;
import net.oschina.app.adapter.NotebookAdapter;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.bean.NotebookData;
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.db.NoteDatabase;
import net.oschina.app.util.UIHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 便签列表界面
 * 
 * @author kymjs(kymjs123@gmail.com)
 * 
 */
public class NoteBookFragment extends BaseFragment implements
        OnItemClickListener, OnItemLongClickListener {

    @InjectView(R.id.frag_note_list)
    ListView mList;

    private NoteDatabase noteDb;
    private ArrayList<NotebookData> datas;
    private NotebookAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_note, container,
                false);
        ButterKnife.inject(this, rootView);
        initData();
        initView(rootView);
        return rootView;
    }

    @Override
    public void initData() {
        noteDb = new NoteDatabase(getActivity());
        datas = noteDb.query();
        if (datas != null) {
            adapter = new NotebookAdapter(getActivity(), datas);
        }
    }

    @Override
    public void initView(View view) {
        mList.setAdapter(adapter);
        mList.setOnItemLongClickListener(this);
        mList.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        refurbish();
    }

    private void refurbish() {
        datas = noteDb.query();
        if (datas != null && adapter != null) {
            adapter.refurbishData(datas);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
            int position, long id) {
        noteDb.delete(datas.get(position).getId());
        refurbish();
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(NoteEditFragment.NOTE_KEY, datas.get(position));
        UIHelper.showSimpleBack(getActivity(), SimpleBackPage.NOTE_EDIT, bundle);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.pub_tweet_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.public_menu_send:
            UIHelper.showSimpleBack(getActivity(), SimpleBackPage.NOTE_EDIT);
            break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {}
}
