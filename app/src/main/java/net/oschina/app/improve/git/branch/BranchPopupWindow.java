package net.oschina.app.improve.git.branch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.git.bean.Branch;

import java.util.List;

/**
 * Created by haibin
 * on 2017/3/13.
 */
@SuppressWarnings("unused")
public class BranchPopupWindow extends PopupWindow implements
        View.OnAttachStateChangeListener,
        BaseRecyclerAdapter.OnItemClickListener,
        BranchContract.View {
    private BranchAdapter mAdapter;
    private Callback mCallback;
    private long mProjectId;
    private BranchContract.Presenter mPresenter;

    @SuppressLint("InflateParams")
    public BranchPopupWindow(Context context, long mProjectId, Callback callback) {
        super(LayoutInflater.from(context).inflate(R.layout.popup_window_branch, null),
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mCallback = callback;
        this.mProjectId = mProjectId;

        setAnimationStyle(R.style.popup_anim_style_alpha);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        setFocusable(true);

        View content = getContentView();
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        content.addOnAttachStateChangeListener(this);
        RecyclerView mRecyclerView = (RecyclerView) content.findViewById(R.id.rv_branch);
        mAdapter = new BranchAdapter(context);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mPresenter = new BranchPresenter(this);
        mPresenter.getBranches(mProjectId);
    }

    @Override
    public void setPresenter(BranchContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {

    }

    @Override
    public void showGetBranchSuccess(List<Branch> branches) {
        mAdapter.resetItem(branches);
    }

    @Override
    public void showGetBranchFailure(int strId) {

    }

    @Override
    public void onViewAttachedToWindow(View v) {
        if (mAdapter.getCount() == 0)
            mPresenter.getBranches(mProjectId);
        if (mCallback != null)
            mCallback.onShow();
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        if (mCallback != null)
            mCallback.onDismiss();
    }

    @Override
    public void onItemClick(int position, long itemId) {
        if (mCallback != null)
            mCallback.onSelect(mAdapter.getItem(position));
        dismiss();
    }

    public interface Callback {
        void onSelect(Branch branch);

        void onDismiss();

        void onShow();
    }
}
