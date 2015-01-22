package net.oschina.app.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;

/**
 * 基本适配器的基类
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年9月25日 下午6:10:47
 *
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {
	//标识LinkView上的链接，默认为false
	protected boolean isLinkViewClick = false;
	protected Context context;//运行上下文
	protected List<T> listData;//数据集合
	protected LayoutInflater listContainer;//视图容器

	/**
	 * 实例化MyBaseAdapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public MyBaseAdapter(Context context, List<T> data) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.listData = data;
	}
	public boolean isLinkViewClick() {
		return isLinkViewClick;
	}

	public void setLinkViewClick(boolean isLinkViewClick) {
		this.isLinkViewClick = isLinkViewClick;
	}
	
	@Override
	public int getCount() {
		return listData.size();
	}
	
	@Override
	public T getItem(int arg0) {
		return listData.get(arg0);
	}
	
	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	/**
	 * 获取适配器的布局文件
	 * @return
	 */
	protected abstract int getResourceId();
	
	protected View getAdapterView(int resourceId) {
		return listContainer.inflate(resourceId, null);
	}
}
