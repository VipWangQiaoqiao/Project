package net.oschina.app.team.bean;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import net.oschina.app.bean.Entity;
import net.oschina.app.bean.ListEntity;

/**
 * TeamProjectMemberList.java
 * 
 * @author 火蚁(http://my.oschina.net/u/253900)
 * 
 * @data 2015-2-6 上午10:41:36
 */
@SuppressWarnings("serial")
@XStreamAlias("oschina")
public class TeamProjectMemberList extends Entity implements
	ListEntity<TeamProjectMember> {

    @XStreamAlias("totalCount")
    private int totalCount;

    @XStreamAlias("members")
    private List<TeamProjectMember> list = new ArrayList<TeamProjectMember>();

    @Override
    public List<TeamProjectMember> getList() {
	// TODO Auto-generated method stub
	return list;
    }

    public int getTotalCount() {
	return totalCount;
    }

    public void setTotalCount(int totalCount) {
	this.totalCount = totalCount;
    }

    public void setList(List<TeamProjectMember> list) {
	this.list = list;
    }
}
