package net.oschina.app.improve.user.activities;

import net.oschina.app.improve.user.bean.UserFriend;

/**
 * Created by fei
 * on 2016/12/29.
 * desc:
 */

public interface OnSelectFriendListener {

    void select(UserFriend userFriend);

    void unSelect(UserFriend userFriend);

    void selectFull(int selectCount);
}
