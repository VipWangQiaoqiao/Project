package net.oschina.app.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.oschina.app.R;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.bean.NotebookData;
import net.oschina.app.db.NoteDatabase;
import net.oschina.app.ui.SimpleBackActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 便签编辑界面
 * 
 * @author kymjs(kymjs123@gmail.com)
 * 
 */
public class NoteEditFragment extends BaseFragment {
    @InjectView(R.id.note_detail_edit)
    EditText mEtContent;

    private NotebookData editData;
    private NoteDatabase noteDb;
    public static final String NOTE_KEY = "notebook_key";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_note_detail,
                container, false);
        ButterKnife.inject(this, rootView);
        initData();
        initView(rootView);
        return rootView;
    }

    @Override
    public void onClick(View v) {}

    @Override
    public void initData() {
        noteDb = new NoteDatabase(getActivity());
        if (editData == null) {
            editData = new NotebookData();
        }
    }

    @Override
    public void initView(View view) {
        mEtContent.setText(editData.getContent());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = getActivity().getIntent().getBundleExtra(
                SimpleBackActivity.BUNDLE_KEY_ARGS);
        if (bundle != null) {
            editData = (NotebookData) bundle.getSerializable(NOTE_KEY);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.pub_tweet_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.public_menu_send:
            save();
            getActivity().finish();
            break;
        }
        return true;
    }

    private void save() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM年dd月");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        editData.setStar(false);
        editData.setContent(mEtContent.getText().toString());
        editData.setDate(dateFormat.format(date));
        editData.setTime(timeFormat.format(date));
        noteDb.save(editData);
    }
}
