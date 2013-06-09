package com.tvm.thinkdb.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public final class ConfigAccessor
{

	private static Properties props;

	private static Map<String, Object> properties = new HashMap<String, Object>();

	public static void init(String configPath, String encoding) throws IOException
	{
		init(configPath, encoding, true);
	}

	public static void init(String configPath, String encoding, boolean ignoreEmpty)
			throws IOException
	{
		props = new Properties();

		try
		{
			props.load(new InputStreamReader(new FileInputStream(configPath), encoding));
		}
		catch (UnsupportedEncodingException ex)
		{
			throw new IOException(
					String.format("加载配置文件异常:%s,配置文件:%s", ex.getMessage(), configPath), ex);
		}
		catch (FileNotFoundException ex)
		{
			throw new IOException(
					String.format("加载配置文件异常:%s,配置文件:%s", ex.getMessage(), configPath), ex);
		}
		catch (IOException ex)
		{
			throw new IOException(
					String.format("加载配置文件异常:%s,配置文件:%s", ex.getMessage(), configPath), ex);
		}

		try
		{
			validConfig(ignoreEmpty);
		}
		catch (Exception ex)
		{
			throw new IOException(String.format("配置文件校验失败:%s", ex.getMessage()));
		}
	}

	protected static void validConfig(boolean ignoreEmpty)
	{
		Set<Object> keys = props.keySet();
		Iterator<Object> i = keys.iterator();
		while (i.hasNext())
		{
			Object key = i.next();
			try
			{
				Object obj = props.getProperty(key.toString());
				String strObj = obj.toString();
				if (strObj.length() == 0)
				{
					if (ignoreEmpty)
					{
						properties.put(key.toString(), "");
					}
					else
					{
						throw new IllegalArgumentException(
								String.format("配置项:%s为空", key.toString()));
					}
				}
				properties.put(key.toString(), obj);
			}
			catch (Exception ex)
			{
				if (ignoreEmpty)
				{
					properties.put(key.toString(), "");
				}
				else
				{
					throw new IllegalArgumentException(String.format("配置项:%s为空", key.toString()));
				}
			}

		}
	}

	public static boolean isLoaded()
	{
		return props != null;
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
