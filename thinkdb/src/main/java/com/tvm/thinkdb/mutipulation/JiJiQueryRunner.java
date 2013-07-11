package com.tvm.thinkdb.mutipulation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;

import com.tvm.thinkdb.util.ColumnPropertySupporter;

/**
 * 重写部分QueryRunner的方法,实现了orm的插入和查询
 * 
 * @author 余洪禹
 * @version 2013-2-18 下午1:43:16
 */
public class JiJiQueryRunner extends QueryRunner
{

	private BeanProcessor processor;

	/**
	 * 创建一个不包含内嵌对象的数据库查询对象
	 */
	public JiJiQueryRunner()
	{
		this.processor = ResultSetProcessor.getProcessor(0);
	}

	/**
	 * 采用指定的处理器模式创建数据库查询对象
	 * 
	 * @param processorType
	 *            处理器类型 @see ResultSetProcessorEnum.java
	 */
	public JiJiQueryRunner(int processorType)
	{
		this.processor = ResultSetProcessor.getProcessor(processorType);
	}

	public <T> int insert(Connection conn, String sql, T obj) throws SQLException
	{
		PreparedStatement stat = conn.prepareStatement(sql);
		List<String> fieldNames = parseUpdateSql(sql);
		setStatParameter(stat, fieldNames, obj);
		return stat.executeUpdate();
	}

	public <T> List<Integer> batchInsert(Connection conn, String sql, List<T> objList)
			throws SQLException
	{
		List<Integer> results = new LinkedList<Integer>();
		PreparedStatement stat = conn.prepareStatement(sql);
		List<String> fieldNames = parseUpdateSql(sql);

		if (objList != null && objList.size() != 0)
		{
			for (T obj : objList)
			{
				setStatParameter(stat, fieldNames, obj);
				results.add(stat.executeUpdate());
			}
		}

		return results;
	}

	public <T> T query(Connection conn, String sql, Class<T> clazz) throws SQLException
	{
		Statement stat = null;
		ResultSet rs = null;
		try
		{
			stat = conn.createStatement();
			rs = stat.executeQuery(sql);

			return rs.next() ? this.processor.toBean(rs, clazz) : null;
		}
		finally
		{
			DbUtils.closeQuietly(null, stat, rs);
		}
	}

	public <T> List<T> queryList(Connection conn, String sql, Class<T> clazz) throws SQLException
	{
		Statement stat = null;
		ResultSet rs = null;
		try
		{
			stat = conn.createStatement();
			rs = stat.executeQuery(sql);

			return this.processor.toBeanList(rs, clazz);
		}
		finally
		{
			DbUtils.closeQuietly(null, stat, rs);
		}
	}

	private List<String> parseUpdateSql(String sql)
	{
		try
		{
			int leftParenthese = sql.indexOf('(');
			int rightParenthese = sql.substring(leftParenthese + 1).indexOf(')');

			String[] fieldStr = sql.substring(leftParenthese + 1,
					leftParenthese + rightParenthese + 1).split(",");
			return Arrays.asList(fieldStr);
		}
		catch (Exception ex)
		{
		}
		throw new IllegalArgumentException("请确保sql的语法正确性:" + sql);
	}

	private <T> void setStatParameter(PreparedStatement stat, List<String> fieldNames, T obj)
			throws SQLException
	{
		Class<T> clazz = (Class<T>) obj.getClass();

		ParameterMetaData paramMeta = stat.getParameterMetaData();
		int paramCount = paramMeta.getParameterCount();
		for (int i = 1; i <= paramCount; i++)
		{
			String columnName = (String) fieldNames.get(i - 1);

			String getterName = ColumnPropertySupporter.getGetterName(columnName);
			try
			{
				Method m = clazz.getMethod(getterName, (Class<?>[]) null);
				Object value = m.invoke(obj, (Object[]) null);

				stat.setObject(i, value);
			}
			catch (NoSuchMethodException nsm)
			{
				throw new SQLException(String.format("无法在类%s中找到列:%s的getter方法%s", clazz.getName(),
						columnName, getterName));
			}
			catch (IllegalArgumentException e)
			{
				throw new SQLException(String.format("无法调用列:%s的getter方法-%s,请确保getter方法没有参数",
						columnName, getterName));
			}
			catch (IllegalAccessException e)
			{
				throw new SQLException(String.format(
						"无法访问列:%s的getter方法-%s,请确保getter方法的权限修饰符为public", columnName, getterName));
			}
			catch (InvocationTargetException e)
			{
				throw new SQLException(String.format("调用列:%s的getter方法-%s,请确保insert方法的对象和sql对应",
						columnName, getterName));
			}
		}
	}
}
