package net.oschina.app.improve.tweet.contract;

import java.util.List;

/**
 * Created by JuQiu
 * on 16/7/14.
 */

public interface TweetPublishContract {
    interface Operator {
        void setDataView(View view);

        void publish();

        void onBack();

        void loadXmlData();
    }

    interface View {
        String getContent();

        void setContent(String content);

        List<String> getImages();

        void setImages(List<String> paths);
    }
}
