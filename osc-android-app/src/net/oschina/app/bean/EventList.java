package net.oschina.app.bean;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 活动实体类列表
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年12月10日 下午2:28:54
 *
 */
@SuppressWarnings("serial")
@XStreamAlias("oschina")
public class EventList extends Entity implements ListEntity {

	@XStreamAlias("events")
	private List<Event> friendlist = new ArrayList<Event>();

	public List<Event> getFriendlist() {
		return friendlist;
	}

	public void setFriendlist(List<Event> resultlist) {
		this.friendlist = resultlist;
	}

	@Override
	public List<?> getList() {
		return friendlist;
	}
}
