package net.oschina.app.bean;

import java.io.Serializable;
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

	@XStreamAlias("event")
	public static class Event implements Serializable {
		@XStreamAlias("id")
		private int id;
		@XStreamAlias("img")
		private String img;
		@XStreamAlias("title")
		private String title;
		@XStreamAlias("url")
		private String url;
		@XStreamAlias("createTime")
		private String createTime;
		@XStreamAlias("startTime")
		private String startTime;
		@XStreamAlias("endTime")
		private String endTime;
		@XStreamAlias("spot")
		private String spot;
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getImg() {
			return img;
		}
		public void setImg(String img) {
			this.img = img;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getCreateTime() {
			return createTime;
		}
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		public String getStartTime() {
			return startTime;
		}
		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}
		public String getEndTime() {
			return endTime;
		}
		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}
		public String getSpot() {
			return spot;
		}
		public void setSpot(String spot) {
			this.spot = spot;
		}
	}

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
