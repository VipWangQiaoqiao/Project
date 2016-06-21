package net.oschina.app.improve.detail.fragments;

import android.content.Context;

import net.oschina.app.improve.detail.contract.DetailContract;
import net.oschina.app.improve.fragments.base.BaseFragment;

/**
 * Created by JuQiu
 * on 16/6/20.
 */

public abstract class DetailFragment<Data, DataView extends DetailContract.View, Operator extends DetailContract.Operator<Data, DataView>> extends BaseFragment implements DetailContract.View {
    protected Operator mOperator;

    public Operator getOperator() {
        return mOperator;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(Context context) {
        this.mOperator = (Operator) context;
        this.mOperator.setDataView((DataView) this);
        super.onAttach(context);
    }
}
