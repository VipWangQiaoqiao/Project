package net.oschina.app.api.remote;

import net.oschina.app.api.ApiHttpClient;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/** 
 * osc team api集合类
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2015年1月14日 下午3:32:18 
 * 
 */
public class OSChinaTeamApi {
	
	/**
	 * 获取团队项目列表
	 * 
	 * @param teamId 
	 * @param handler
	 */
	public static void getTeamProjectList(int teamId, AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.put("teamid", teamId);
		ApiHttpClient.get("action/api/team_project_list", params, handler);
	}
	
	/**
	 * 获取任务列表
	 * 
	 * @param teamId
	 * @param projectId
	 * 		项目id(-1获取非项目任务列表, 0获取所有任务列表)
	 * @param source
	 * 		"Team@OSC"(default),"Git@OSC","GitHub",如果指定了projectid的值，这个值就是必须的
	 * @param uid
	 * 		如果指定该值，则获取该id用户相关的任务
	 * @param state
	 * 		"all"(default),"opened","closed","outdate"
	 * @param scope
	 * 		"tome"(default,指派给我的任务),"meto"(我指派的任务)
	 * @param pageIndex
	 * @param pageSize
	 * @param handler
	 */
	public static void getTeamIssueList(int teamId, int projectId, String source, 
			int uid, String state, String scope, int pageIndex, int pageSize,
			AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.put("teamid", teamId);
		params.put("projectId", projectId);
//		params.put("uid", uid);
//		params.put("state", state);
//		params.put("scope", scope);
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);
		ApiHttpClient.get("action/api/team_issue_list", params, handler);
	}
}
