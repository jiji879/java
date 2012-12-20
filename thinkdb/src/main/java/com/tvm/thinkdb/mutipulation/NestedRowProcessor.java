package com.tvm.thinkdb.mutipulation;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.security.Timestamp;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.BeanProcessor;

import com.tvm.thinkdb.util.ColumnPropertySupporter;
import com.tvm.util.StringUtil;

/**
 * 支持嵌套实体关系的数据库结果集处理器
 * 
 * @author 余洪禹
 * @version 2012-12-18 下午3:09:15
 */
public class NestedRowProcessor extends BeanProcessor
{

	private static final Map<Class<?>, Object> primitiveDefaults = new HashMap<Class<?>, Object>();

	static
	{
		primitiveDefaults.put(Integer.TYPE, Integer.valueOf(0));
		primitiveDefaults.put(Short.TYPE, Short.valueOf((short) 0));
		primitiveDefaults.put(Byte.TYPE, Byte.valueOf((byte) 0));
		primitiveDefaults.put(Float.TYPE, Float.valueOf(0f));
		primitiveDefaults.put(Double.TYPE, Double.valueOf(0d));
		primitiveDefaults.put(Long.TYPE, Long.valueOf(0L));
		primitiveDefaults.put(Boolean.TYPE, Boolean.FALSE);
		primitiveDefaults.put(Character.TYPE, Character.valueOf((char) 0));
		primitiveDefaults.put(java.lang.String.class, "");
	}

	class ColumnPropertyEntry
	{

		boolean isEffective = false;

		String columnName;

		PropertyDescriptor prop;

		ColumnPropertyEntry e;

		ColumnPropertyEntry(String columnName, PropertyDescriptor prop, ColumnPropertyEntry e)
		{
			this.columnName = columnName;
			this.prop = prop;
			this.e = e;
		}

		ColumnPropertyEntry(boolean isEffective, String columnName, PropertyDescriptor prop)
		{
			this.isEffective = isEffective;
			this.columnName = columnName;
			this.prop = prop;
		}

		@Override
		public String toString()
		{
			return "ColumnPropertyEntry [isEffective=" + isEffective + ", columnName=" + columnName
					+ ", e=" + e + "]";
		}
	}

	@Override
	public <T> T toBean(ResultSet rs, Class<T> clazz) throws SQLException
	{
		Map<String, PropertyDescriptor> props = this.propertyDescriptors(clazz);

		return rs.next() ? this.createBean(rs, clazz, props) : null;
	}

	@Override
	public <T> List<T> toBeanList(ResultSet rs, Class<T> clazz) throws SQLException
	{
		Map<String, PropertyDescriptor> props = this.propertyDescriptors(clazz);

		List<T> beanList = new LinkedList<T>();
		while (rs.next())
		{
			beanList.add(this.createBean(rs, clazz, props));
		}

		return beanList;
	}

	public <T> T createBean(ResultSet rs, Class<T> clazz, Map<String, PropertyDescriptor> propMap)
			throws SQLException
	{
		T beanObj = null;
		try
		{
			beanObj = clazz.newInstance();

			ResultSetMetaData rsMeta = rs.getMetaData();

			int cols = rsMeta.getColumnCount();
			for (int i = 1; i <= cols; i++)
			{
				String columnName = rsMeta.getColumnLabel(i);

				if (columnName == null || 0 == columnName.length())
				{
					columnName = rsMeta.getColumnName(i);
				}

				ColumnPropertyEntry cpe = this.getColumnPropertyEntry(clazz,
						columnName.split("\\."), propMap);

				this.callSetter(clazz, beanObj, cpe, rs.getObject(columnName));
			}
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			throw new SQLException("无法设置指定的属性", e);
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}

		return beanObj;
	}

	private ColumnPropertyEntry getColumnPropertyEntry(Class<?> clazz, String[] columnNameSplits,
			Map<String, PropertyDescriptor> propMap) throws SQLException
	{
		ColumnPropertyEntry e = null;

		String token = "";
		try
		{
			token = columnNameSplits[0];
		}
		catch (IndexOutOfBoundsException ex)
		{
			throw new SQLException("请确保sql中指定查询的列标签正确:" + StringUtil.join(columnNameSplits, "."));
		}
		String propName = ColumnPropertySupporter.colNameConvent(token);

		if (propMap.containsKey(propName))
		{
			PropertyDescriptor curProp = propMap.get(propName);

			if (primitiveDefaults.containsKey(curProp.getPropertyType()))
			{
				e = new ColumnPropertyEntry(true, token, curProp);
			}
			else
			{
				Class<?> nestedClazz = curProp.getPropertyType();
				Map<String, PropertyDescriptor> nestedPropsMap = this
						.propertyDescriptors(nestedClazz);

				e = new ColumnPropertyEntry(token, curProp, this.getColumnPropertyEntry(
						nestedClazz,
						Arrays.copyOfRange(columnNameSplits, 1, columnNameSplits.length),
						nestedPropsMap));
			}
		}
		else
		{
			throw new SQLException(String.format("在类%s中不存在指定属性名称%s,对应的数据库列标签为%s", clazz.getName(),
					propName, StringUtil.join(columnNameSplits, ".")));
		}

		return e;
	}

	private <T> void callSetter(Class<?> clazz, T beanObj, ColumnPropertyEntry e, Object value)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, SQLException
	{
		if (beanObj == null)
		{
			beanObj = (T) clazz.newInstance();
		}

		if (!e.isEffective)
		{
			PropertyDescriptor prop = e.prop;
			Object nestedObj = prop.getReadMethod().invoke(beanObj, new Object[] {});

			if (nestedObj == null)
			{
				nestedObj = prop.getPropertyType().newInstance();
			}

			this.callSetter(prop.getPropertyType(), nestedObj, e.e, value);

			e.prop.getWriteMethod().invoke(beanObj, new Object[] { nestedObj });
		}
		else
		{
			Class<?> type = e.prop.getPropertyType();

			Object actValue = this.convertType(type, value);
			e.prop.getWriteMethod().invoke(beanObj, new Object[] { actValue });
		}
	}

	private Map<String, PropertyDescriptor> propertyDescriptors(Class<?> c) throws SQLException
	{
		BeanInfo beanInfo = null;
		try
		{
			beanInfo = Introspector.getBeanInfo(c);

		}
		catch (IntrospectionException e)
		{
			throw new SQLException("Bean introspection failed: " + e.getMessage());
		}

		PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
		Map<String, PropertyDescriptor> propMap = new HashMap<String, PropertyDescriptor>();
		for (PropertyDescriptor prop : props)
		{
			propMap.put(prop.getName(), prop);
		}

		return propMap;
	}

	protected Object convertType(Class<?> type, Object value) throws SQLException
	{
		if (primitiveDefaults.containsKey(type) && value == null)
		{
			return primitiveDefaults.get(type);
		}

		try
		{
			if (type.equals(Integer.TYPE) || type.equals(Byte.TYPE) || type.equals(Short.TYPE))
			{
				return Integer.parseInt(value.toString());
			}
			else if (type.equals(String.class))
			{
				return value.toString();
			}
			else if (type.equals(Double.TYPE))
			{
				return Double.parseDouble(value.toString());
			}
			else if (type.equals(Long.TYPE))
			{
				return Long.parseLong(value.toString());
			}
			else if (type.equals(Character.TYPE))
			{
				return Character.toChars(Integer.parseInt(value.toString()));
			}
			else if (type.equals(Timestamp.class))
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return new java.sql.Timestamp(sdf.parse(value.toString()).getTime());
			}
			else if (type.equals(Date.class))
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return sdf.parse(value.toString());
			}
			else
			{
				return value;
			}
		}
		catch (Exception ex)
		{
			throw new SQLException(String.format("无法将值%s转换为类型%s", value.toString(), type));
		}
	}
}
