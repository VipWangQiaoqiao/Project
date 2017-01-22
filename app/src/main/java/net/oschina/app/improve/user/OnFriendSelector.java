package net.oschina.app.improve.user;

import android.view.View;

import net.oschina.app.improve.user.bean.UserFriend;

/**
 * Created by fei
 * on 2016/12/29.
 * desc:
 */

public interface OnFriendSelector {

    void select(View view, UserFriend userFriend, int position);

    void unSelect(View view, UserFriend userFriend, int position);

    void selectFull(int selectCount);
}
