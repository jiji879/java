package com.tvm.thinkdb.mutipulation;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbutils.ResultSetHandler;

/**
 * 对数据集进行k-v映射的处理
 * 
 * @author 余洪禹
 * @version 2012-12-20 下午3:43:44
 * @param <V>
 */
public class KeyValueHandler<K, V> implements ResultSetHandler<Map<K, V>>
{

	private boolean NOT_SET_KV = false;

	private String key;

	private String value;

	public KeyValueHandler()
	{
		this.NOT_SET_KV = true;
	}

	public KeyValueHandler(String key, String value)
	{
		this.key = key;
		this.value = value;
	}

	public Map<K, V> handle(ResultSet rs) throws SQLException
	{
		if (this.NOT_SET_KV)
		{
			return createMapDefault(rs);
		}
		else
		{
			return createMapWithKV(rs);
		}
	}

	private Map<K, V> createMapDefault(ResultSet rs) throws SQLException
	{
		Map<K, V> map = new HashMap<K, V>();

		ResultSetMetaData rsMeta = rs.getMetaData();
		int columnCount = rsMeta.getColumnCount();
		if (columnCount < 2)
		{
			String key = rsMeta.getColumnLabel(1);
			String value = rsMeta.getColumnLabel(2);

			if (key == null || "".equals(key))
			{
				key = rsMeta.getColumnName(1);
			}

			if (value == null || "".equals(value))
			{
				value = rsMeta.getColumnName(2);
			}

			while (rs.next())
			{
				try
				{
					map.put((K) rs.getObject(key), (V) rs.getObject(value));
				}
				catch (ClassCastException ex)
				{
					throw new SQLException("无法将数据库值转换为指定类型", ex);
				}
			}
		}
		else
		{
			throw new SQLException("数据集包含两个以上的列");
		}

		return map;
	}

	private Map<K, V> createMapWithKV(ResultSet rs) throws SQLException
	{
		Map<K, V> map = new HashMap<K, V>();

		while (rs.next())
		{
			try
			{
				map.put((K) rs.getObject(this.key), (V) rs.getObject(this.value));
			}
			catch (ClassCastException ex)
			{
				throw new SQLException("无法将值转换为指定类型", ex);
			}
		}

		return map;
	}
}
