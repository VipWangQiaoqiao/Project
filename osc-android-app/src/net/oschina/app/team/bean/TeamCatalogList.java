package net.oschina.app.team.bean;

import java.util.ArrayList;
import java.util.List;

import net.oschina.app.bean.Entity;
import net.oschina.app.bean.ListEntity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** 
 * 指定项目任务列表
 * 		包括所有任务列表以及为指定任务列表
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2015年1月15日 上午10:58:47 
 * 
 */
@SuppressWarnings("serial")
@XStreamAlias("oschina")
public class TeamCatalogList extends Entity implements ListEntity<TeamCatalog> {

	@XStreamAlias("totalCount")
	private int totalCount;
	
	@XStreamAlias("catalogs")
	private ArrayList<TeamCatalog> list = new ArrayList<TeamCatalog>();
	
	@Override
	public List<TeamCatalog> getList() {
		return list;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public void setList(ArrayList<TeamCatalog> list) {
		this.list = list;
	}

}
