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

	/**
	 * 向数据库中插入一条数据,参数通过sql指定,数据通过obj包含的对象指定
	 * 
	 * @param conn
	 *            数据库连接
	 * @param sql
	 *            包含带设置参数的sql
	 * @param obj
	 *            包含了sql语句需要的数据对象
	 * @return 返回插入sql执行的影响行数
	 * @throws SQLException
	 *             如果数据库操作发生异常或者插入的数据不合法则抛出
	 * @author 余洪禹
	 */
	public <T> int insert(Connection conn, String sql, T obj) throws SQLException
	{
		Class<T> clazz = (Class<T>) obj.getClass();
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

	/**
	 * 查询指定sql语句的实体对象
	 * 
	 * @param conn
	 *            数据库连接
	 * @param sql
	 *            待执行sql语句
	 * @param clazz
	 *            需要转换的实体类对象
	 * @return 数据库中指定记录的实体对象
	 * @throws SQLException
	 *             如果数据库操作发生异常或者进行实体关系转换时发生异常则抛出
	 * @author 余洪禹
	 */
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

	/**
	 * 查询指定sql语句的实体对象列表
	 * 
	 * @param conn
	 *            数据库连接
	 * @param sql
	 *            待执行sql语句
	 * @param clazz
	 *            需要转换的实体类对象
	 * @return 数据库返回记录的实体对象列表
	 * @throws SQLException
	 *             如果数据库操作发生异常或者进行实体关系转换时发生异常则抛出
	 * @author 余洪禹
	 */
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
}
