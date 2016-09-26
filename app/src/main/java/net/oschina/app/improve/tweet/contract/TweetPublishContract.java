package net.oschina.app.improve.tweet.contract;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by JuQiu
 * on 16/7/14.
 */

public interface TweetPublishContract {
    interface Operator {
        void setDataView(View view, String defaultContent);

        void publish();

        void onBack();

        void loadXmlData();

        void onSaveInstanceState(Bundle outState);

        void onRestoreInstanceState(Bundle savedInstanceState);
    }

    interface View {
        Context getContext();

        String getContent();

        void setContent(String content);

        String[] getImages();

        void setImages(String[] paths);

        void finish();

        Operator getOperator();
    }
}
