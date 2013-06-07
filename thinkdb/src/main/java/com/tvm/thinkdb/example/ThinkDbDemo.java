package com.tvm.thinkdb.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.tvm.thinkdb.connection.ConnectionManager;
import com.tvm.thinkdb.connection.DatabaseInfoListException;
import com.tvm.thinkdb.mutipulation.HumbBeanListHandler;
import com.tvm.thinkdb.mutipulation.JiJiQueryRunner;
import com.tvm.thinkdb.mutipulation.KeyValueHandler;
import com.tvm.thinkdb.mutipulation.NestedRowProcessor;
import com.tvm.thinkdb.mutipulation.ResultSetProcessor;
import com.tvm.thinkdb.util.ConfigAccessor;

public class ThinkDbDemo
{

	public static void main(String args[]) throws Exception
	{
		ConfigAccessor conAccer = ConfigAccessor.init("config.properties", "utf-8");
		ConnectionManager.init(conAccer, "EDITOR_APP", "EPG_APP", "AD_APP", "ERROR_DATA",
				"AD_MATRIX", "MAINTAIN", "JIJI");

		// example1();
		// example2();
		// example3();
		// example4();
		exampleInsert();
	}

	/**
	 * 〈简单描述〉 〈功能详细描述〉
	 * 
	 * @author 余洪禹
	 * @throws DatabaseInfoListException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	private static void exampleInsert() throws ClassNotFoundException, SQLException, DatabaseInfoListException
	{
		Connection conn = ConnectionManager.getMysqlConnection("JIJI");
		JiJiQueryRunner runner = new JiJiQueryRunner();
		String sql = "insert into group_by_table(duration, period) values(?,?)";
		List<C1> l = new LinkedList<C1>();
		C1 c = new C1(1, "1-1");
		l.add(c);
		l.add(new C1(2, "2-2"));
		runner.insert(conn, sql, c);
		
		runner.batchInsert(conn, sql, l);
		
	}

	public static class C1
	{

		private int duration;

		private String period;

		public C1(int duration, String period)
		{
			this.duration = duration;
			this.period = period;
		}

		public int getDuration()
		{
			return duration;
		}

		public void setDuration(int duration)
		{
			this.duration = duration;
		}

		public String getPeriod()
		{
			return period;
		}

		public void setPeriod(String period)
		{
			this.period = period;
		}

	}

	private static void example1() throws SQLException, ClassNotFoundException,
			DatabaseInfoListException
	{
		JiJiQueryRunner runner = new JiJiQueryRunner();
		String sql = "insert into sync_error_data(primary_key,sync_sql,table_name) values (?,?,?)";
		ErrorData error = new ErrorData();
		error.setPrimaryKey("primary_key");
		error.setSyncSql("error sql");
		error.setTableName("table_name");
		System.out.println(runner.insert(ConnectionManager.getMysqlConnection("ERROR_DATA"), sql,
				error));

		QueryRunner query = new QueryRunner();
		Map<Integer, String> errorDatas = query.query(
				ConnectionManager.getMysqlConnection("ERROR_DATA"),
				"select * from sync_error_data", new ResultSetHandler<Map<Integer, String>>() {

					public Map<Integer, String> handle(ResultSet rs) throws SQLException
					{
						Map<Integer, String> sqls = new HashMap<Integer, String>();
						while (rs.next())
						{
							sqls.put(rs.getInt("id"), rs.getString("sync_sql"));
						}

						return sqls;
					}

				});
		System.out.println(errorDatas);
	}

	private static void example2() throws ClassNotFoundException, SQLException,
			DatabaseInfoListException, Exception
	{
		JiJiQueryRunner quer = new JiJiQueryRunner(ResultSetProcessor.NESTED_PROCESSOR);

		String sql = "SELECT base.id as id,ad_uuid,base.ad_data_uuid AS ad_data_uuid,channel_cname,title,company_name,domain_name,base.play_start_time AS play_start_time,base.play_end_time AS play_end_time,base.duration AS duration,ad_segment_index,np.program_name AS 'neighbor_program.program_name',np.type as 'neighbor_program.type', na.ad_name AS 'neighbor_ad.ad_name',na.type as 'neighbor_ad.type', area "
				+ "FROM ad_base_info base "
				+ "LEFT JOIN neighbor_program_info np ON base.ad_data_uuid = np.ad_data_uuid "
				+ "LEFT JOIN neighbor_ad_info na ON base.ad_data_uuid = na.ad_data_uuid "
				+ " where ad_uuid = 'f69b03cf-db91-4b41-8a0f-7d8dfd94d498'";

		List<AdBaseInfo> bases = quer.queryList(ConnectionManager.getMysqlConnection("AD_MATRIX"),
				sql, AdBaseInfo.class);
		for (AdBaseInfo base : bases)
		{
			System.out.println(base);
		}

		QueryRunner runner = new QueryRunner();
		AdBaseInfo base = runner.query(ConnectionManager.getMysqlConnection("AD_MATRIX"), sql,
				new BeanHandler<AdBaseInfo>(AdBaseInfo.class, new BasicRowProcessor(
						new NestedRowProcessor())));
		System.out.println(base);
	}

	private static void example3() throws Exception
	{
		String sql = "select region_id ,storage_path from region_storage_info";
		Map<Integer, String> map = new QueryRunner().query(
				ConnectionManager.getMysqlConnection("MAINTAIN"), sql,
				new KeyValueHandler<Integer, String>("region_id", "storage_path"));
		Set<Integer> keys = map.keySet();
		for (int key : keys)
		{
			System.out.println(String.format("%s-%s", key, map.get(key)));
		}
	}

	public static void example4()
	{
		List<CrawlerUrl> urls = null;
		Connection conn = null;
		String sql = "select name,url as base_url,ignore_depth,regex as filter_regex from filter_url u left join"
				+ " url_regex r on r.id = u.url_regex_id";
		QueryRunner quer = new QueryRunner();
		try
		{
			conn = ConnectionManager.getMysqlConnection("JIJI");
			urls = quer.query(conn, sql, new HumbBeanListHandler<CrawlerUrl>(CrawlerUrl.class));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(conn);
		}

		System.out.println(urls);
	}
}
