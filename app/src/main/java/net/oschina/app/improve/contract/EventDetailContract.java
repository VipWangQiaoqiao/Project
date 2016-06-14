package net.oschina.app.improve.contract;

import net.oschina.app.bean.EventApplyData;
import net.oschina.app.improve.bean.EventDetail;

/**
 * Created by huanghaibin
 * on 16-6-13.
 */
public interface EventDetailContract {
    interface View {
        void toFavOk(EventDetail detail);

        void toSignUpOk(EventDetail detail);
    }

    interface Operator {
        void toFav();

        void toSignUp(EventApplyData data);
    }
}
