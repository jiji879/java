package com.tvm.thinkdb.util;

import com.tvm.util.PropertyUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

public final class ConfigAccessor
{

	private static Logger logger = Logger.getLogger(ConfigAccessor.class);

	private static PropertyUtil propUtil;

	private static Map<String, Object> properties = new HashMap<String, Object>();

	protected static boolean validConfig()
	{
		boolean validResult = true;

		Set<Object> keys = propUtil.keys();
		Iterator<Object> i = keys.iterator();
		while (i.hasNext())
		{
			Object key = i.next();
			try
			{
				Object obj = propUtil.getProperty(key.toString());
				properties.put(key.toString(), obj);
			}
			catch (Exception ex)
			{
				logger.error("加载配置文件错误,配置项:" + key + "为空");
				validResult = false;
				break;
			}

		}

		return validResult;
	}

	/**
	 * 以指定路径和编码加载配置文件
	 * 
	 * @param configPath
	 *            配置文件路径
	 * @param encoding
	 *            读取文件的编码
	 * @return 如果加载成功则返回true,否则返回false
	 * @author 余洪禹
	 */
	public static boolean init(String configPath, String encoding)
	{
		try
		{
			propUtil = new PropertyUtil(configPath, encoding);
		}
		catch (Exception ex)
		{
			logger.error("加载配置文件异常:" + ex.getMessage());
			return false;
		}

		return validConfig();
	}

	/**
	 * 校验全局配置文件是否已经加载
	 * 
	 * @return 如果已经被加载则返回true,否则返回false
	 * @author 余洪禹
	 */
	public static boolean isLoaded()
	{
		return propUtil != null;
	}

	private static <T> T getInternal(String propName, Class<T> type)
	{
		if (!isLoaded())
		{
			throw new IllegalStateException("还未进行配置的初始化,使用前请调用init()方法");
		}

		Object valueObj = null;
		if (properties.containsKey(propName))
		{
			valueObj = properties.get(propName);
		}

		return type.cast(valueObj);
	}

	/**
	 * 获取配置值的字符串形式
	 * 
	 * @param propName
	 *            配置项
	 * @return 对应的值
	 * @author 余洪禹
	 */
	public static String getString(String propName)
	{
		return (String) getInternal(propName, String.class);
	}

	/**
	 * 获取配置值的整数形式
	 * 
	 * @param propName
	 *            配置项
	 * @return 对应的值
	 * @author 余洪禹
	 */
	public static int getInt(String propName)
	{
		return Integer.parseInt((String) getInternal(propName, String.class));
	}

	/**
	 * 获取配置值的双精度形式
	 * 
	 * @param propName
	 *            配置项
	 * @return 对应的值
	 * @author 余洪禹
	 */
	public static double getDouble(String propName)
	{
		return Double.parseDouble((String) getInternal(propName, String.class));
	}
}
