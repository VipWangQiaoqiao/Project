package net.oschina.app.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 活动参与者列表实体类
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年12月12日 下午8:06:30
 *
 */
@SuppressWarnings("serial")
@XStreamAlias("oschina")
public class EventAppliesList extends Entity implements ListEntity {

	public final static int TYPE_FANS = 0x00;
	public final static int TYPE_FOLLOWER = 0x01;
	
	@XStreamAlias("applies")
	private List<Apply> friendlist = new ArrayList<Apply>();

	@XStreamAlias("apply")
	public static class Apply implements Serializable {
		
		@XStreamAlias("uid")
		private int userid;
		
		@XStreamAlias("name")
		private String name;
		
		@XStreamAlias("portrait")
		private String portrait;
		
		@XStreamAlias("company")
		private String company;
		
		@XStreamAlias("job")
		private String job;
		
		public int getUserid() {
			return userid;
		}
		public void setUserid(int userid) {
			this.userid = userid;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPortrait() {
			return portrait;
		}
		public void setPortrait(String portrait) {
			this.portrait = portrait;
		}
		public String getCompany() {
			return company;
		}
		public void setCompany(String company) {
			this.company = company;
		}
		public String getJob() {
			return job;
		}
		public void setJob(String job) {
			this.job = job;
		}
	}

	public List<Apply> getFriendlist() {
		return friendlist;
	}

	public void setFriendlist(List<Apply> resultlist) {
		this.friendlist = resultlist;
	}

	@Override
	public List<?> getList() {
		return friendlist;
	}
}
