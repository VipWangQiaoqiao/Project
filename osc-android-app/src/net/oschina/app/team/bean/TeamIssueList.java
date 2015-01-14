package net.oschina.app.team.bean;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import net.oschina.app.bean.Entity;
import net.oschina.app.bean.ListEntity;

/** 
 * 团队任务列表实体类
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2015年1月14日 下午5:09:11 
 * 
 */
@SuppressWarnings("serial")
@XStreamAlias("oschina")
public class TeamIssueList extends Entity implements ListEntity {

	@XStreamAlias("pagesize")
	private int pageSize;
	
	@XStreamAlias("totalCount")
	private int totalCount;
	
	@XStreamAlias("issues")
	private ArrayList<TeamIssue> list = new ArrayList<TeamIssue>();
	
	@Override
	public List<TeamIssue> getList() {
		return list;
	}
}
