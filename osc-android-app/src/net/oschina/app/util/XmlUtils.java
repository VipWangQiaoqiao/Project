package net.oschina.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;

import net.oschina.app.AppException;

/**
 * xml解析工具类
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年9月27日 下午2:04:19
 * 
 */

public class XmlUtils {

	private final static String TAG = XmlUtils.class.getSimpleName();

	/**
	 * 将一个xml流转换为bean实体类
	 * 
	 * @param type
	 * @param instance
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T toBean(Class<T> type, InputStream is) {
		TLog.log(TAG, "开始解析xml");
		XStream xmStream = new XStream(new DomDriver("utf-8"));
		xmStream.ignoreUnknownElements();
		xmStream.processAnnotations(type);
		T obj = null;
		try {
			obj = (T) xmStream.fromXML(is);
		} catch (Exception e) {
			TLog.log(TAG, "解析xml发生异常：" + e.getMessage());
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					TLog.log(TAG, "关闭流出现异常：" + e.getMessage());
				}
			}
		}
		return obj;
	}
}
