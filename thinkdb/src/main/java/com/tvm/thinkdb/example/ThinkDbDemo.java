package com.tvm.thinkdb.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.tvm.thinkdb.connection.ConnectionManager;
import com.tvm.thinkdb.connection.DatabaseInfoListException;
import com.tvm.thinkdb.mutipulation.JiJiQueryRunner;
import com.tvm.thinkdb.mutipulation.KeyValueHandler;
import com.tvm.thinkdb.mutipulation.NestedRowProcessor;
import com.tvm.thinkdb.mutipulation.ResultSetProcessor;
import com.tvm.thinkdb.util.ConfigAccessor;

public class ThinkDbDemo
{

	public static void main(String args[]) throws Exception
	{
		ConfigAccessor.init("config.properties", "utf-8");
		ConnectionManager.init("EDITOR_APP", "EPG_APP", "AD_APP", "ERROR_DATA", "AD_MATRIX",
				"MAINTAIN");

		example1();
		example2();
		example3();
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
				ErrorData.class, error));

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
		String sql = "select region_id ,mount_path from region_storage_info";
		Map<Integer, String> map = new QueryRunner().query(
				ConnectionManager.getMysqlConnection("MAINTAIN"), sql,
				new KeyValueHandler<Integer, String>("region_id", "mount_path"));
		Set<Integer> keys = map.keySet();
		for (int key : keys)
		{
			System.out.println(String.format("%s-%s", key, map.get(key)));
		}
	}
}
