package net.oschina.app.improve.user.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.oschina.app.R;

/**
 * Created by fei on 2016/8/15.
 * desc: user info module
 */

public class UserInfoFragment extends Fragment {

    private static final String TAG = "UserInfoFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ------>" + inflater.toString());
        View rootView = inflater.inflate(R.layout.fragment_main_user_home, container, false);

        return rootView;
    }
}
