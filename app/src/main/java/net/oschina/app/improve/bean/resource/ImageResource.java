package net.oschina.app.improve.bean.resource;

/**
 * Created by JuQiu
 * on 16/7/19.
 */
public class ImageResource {
    private Image[] resources;
    private String token;

    public Image[] getResources() {
        return resources;
    }

    public void setResources(Image[] resources) {
        this.resources = resources;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static class Image {
        public String name;
        public String thumb;
        public String href;
        public String type;
    }
}
