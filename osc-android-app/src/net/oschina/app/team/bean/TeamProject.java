package net.oschina.app.team.bean;

import java.io.Serializable;

import net.oschina.app.bean.Entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** 
 * 团队项目实体类
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2015年1月14日 下午3:18:53 
 * 
 */

@SuppressWarnings("serial")
@XStreamAlias("project")
public class TeamProject extends Entity {
	@XStreamAlias("source")
	private String source;
	
	@XStreamAlias("team")
	private String team;
	
	@XStreamAlias("git")
	private Git git;
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public Git getGit() {
		return git;
	}

	public void setGit(Git git) {
		this.git = git;
	}

	@XStreamAlias("git")
	class Git implements Serializable {
		
		@XStreamAlias("id")
		private int id;
		
		@XStreamAlias("name")
		private String name;
		
		@XStreamAlias("path")
		private String path;
		
		@XStreamAlias("ownerName")
		private String ownerName;
		
		@XStreamAlias("ownerUserName")
		private String ownerUserName;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getOwnerName() {
			return ownerName;
		}

		public void setOwnerName(String ownerName) {
			this.ownerName = ownerName;
		}

		public String getOwnerUserName() {
			return ownerUserName;
		}

		public void setOwnerUserName(String ownerUserName) {
			this.ownerUserName = ownerUserName;
		}
	}
}
