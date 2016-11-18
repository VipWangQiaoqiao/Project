package net.oschina.app.improve.bean.comment;

import java.io.Serializable;

/**
 * Created by fei
 * on 2016/11/18.
 * desc:vote
 */

public class Vote implements Serializable {

    private long vote;
    private int voteState;

    public long getVote() {
        return vote;
    }

    public void setVote(long vote) {
        this.vote = vote;
    }

    public int getVoteState() {
        return voteState;
    }

    public void setVoteState(int voteState) {
        this.voteState = voteState;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "vote=" + vote +
                ", voteState=" + voteState +
                '}';
    }
}
