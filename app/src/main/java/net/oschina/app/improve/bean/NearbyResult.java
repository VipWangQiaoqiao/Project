package net.oschina.app.improve.bean;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by thanatosx
 * on 2016/12/23.
 * Updated by fei
 * on 2017/01/13.
 */

public class NearbyResult implements Serializable, Comparable<NearbyResult> {

    private User user;
    private Nearby nearby;

    public NearbyResult(User user, Nearby nearby) {
        this.user = user;
        this.nearby = nearby;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Nearby getNearby() {
        return nearby;
    }

    public void setNearby(Nearby nearby) {
        this.nearby = nearby;
    }

    @Override
    public int compareTo(@NonNull NearbyResult nearbyResult) {
        return nearby.distance - nearbyResult.getNearby().distance;
    }

    public static class Nearby implements Serializable {
        private int distance;
        private String mobileName;
        private String mobileOS;

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public String getMobileName() {
            return mobileName;
        }

        public void setMobileName(String mobileName) {
            this.mobileName = mobileName;
        }

        public String getMobileOS() {
            return mobileOS;
        }

        public void setMobileOS(String mobileOS) {
            this.mobileOS = mobileOS;
        }


    }
}
