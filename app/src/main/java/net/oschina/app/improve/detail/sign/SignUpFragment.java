package net.oschina.app.improve.detail.sign;

import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.bean.SignUpEventOptions;

import java.util.List;

/**
 * Created by haibin
 * on 2016/12/5.
 */

public class SignUpFragment extends BaseFragment implements SignUpContract.View {
    private SignUpContract.Presenter mPresenter;

    public static SignUpFragment newInstance() {
        SignUpFragment fragment = new SignUpFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public void showGetSignUpOptionsSuccess(List<SignUpEventOptions> options) {

    }

    @Override
    public void showGetSignUpOptionsError(String message) {

    }

    @Override
    public void showSignUpSuccess() {

    }

    @Override
    public void showSignUpError(String message) {

    }

    @Override
    public void setPresenter(SignUpContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showNetworkError(int strId) {

    }
}
