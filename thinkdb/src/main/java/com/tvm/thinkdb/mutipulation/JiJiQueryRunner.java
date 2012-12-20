package com.tvm.thinkdb.mutipulation;

import com.tvm.thinkdb.util.ColumnPropertySupporter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;

public class JiJiQueryRunner extends QueryRunner
{

	private BeanProcessor processor;

	public JiJiQueryRunner()
	{
		this.processor = ResultSetProcessor.getProcessor(0);
	}

	public JiJiQueryRunner(int processorType)
	{
		this.processor = ResultSetProcessor.getProcessor(processorType);
	}

	public <T> int insert(Connection conn, String sql, Class<T> clazz, T obj) throws SQLException
	{
		PreparedStatement stat = conn.prepareStatement(sql);

		List<String> fieldNames = parseUpdateSql(sql);
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

		return stat.executeUpdate();
	}

	public <T> T query(Connection conn, String sql, Class<T> clazz) throws SQLException
	{
		Statement stat = null;
		ResultSet rs = null;
		try
		{
			stat = conn.createStatement();
			rs = stat.executeQuery(sql);

			return this.processor.toBean(rs, clazz);
		}
		finally
		{
			DbUtils.closeQuietly(null, stat, rs);
		}
	}

	public <T> List<T> queryList(Connection conn, String sql, Class<T> clazz) throws Exception
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
}
