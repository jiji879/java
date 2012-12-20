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

	public static String getString(String propName)
	{
		return (String) getInternal(propName, String.class);
	}

	public static int getInt(String propName)
	{
		return Integer.parseInt((String) getInternal(propName, String.class));
	}

	public static double getDouble(String propName)
	{
		return Double.parseDouble((String) getInternal(propName, String.class));
	}
}
