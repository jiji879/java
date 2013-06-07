package com.tvm.thinkdb.connection;

import com.tvm.thinkdb.bean.DatabaseInfo;
import com.tvm.thinkdb.util.ConfigAccessor;
import com.tvm.util.DbUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ConnectionManager
{

	private static DatabaseInfoList dbList;

	private static ConfigAccessor conAccer;

	public static synchronized void init(ConfigAccessor conAccer, String... prefix)
	{
		ConnectionManager.conAccer = conAccer;
		if (dbList == null)
		{
			dbList = new DatabaseInfoList();
		}

		dbList.init(prefix);
	}

	public static DatabaseInfo getDatabaseInfo(String prefix) throws DatabaseInfoListException
	{
		if (!dbList.init)
		{
			throw new DatabaseInfoListException(
					"数据库信息还未进行初始化,请使用ConnectionProvider.init(String... prefix)");
		}

		DatabaseInfo dbInfo = dbList.get(prefix);
		if (dbInfo == null)
		{
			throw new DatabaseInfoListException("数据库信息集合中没有" + prefix
					+ "前缀的数据,请使用ConnectionProvider.init(String... prefix)");
		}

		return dbInfo;
	}

	public static Connection getMysqlConnection(String prefix) throws ClassNotFoundException,
			SQLException, DatabaseInfoListException
	{
		DatabaseInfo dbInfo = getDatabaseInfo(prefix);

		String driverName = "com.mysql.jdbc.Driver";

		String url = getDbUrl(dbInfo, "mysql");

		String user = dbInfo.getUserName();

		String password = dbInfo.getPassword();
		DbUtil db = new DbUtil();

		return db.getConnection(driverName, url, user, password, true);
	}

	public static Connection getOracleConnection(String prefix) throws ClassNotFoundException,
			SQLException, DatabaseInfoListException
	{
		DatabaseInfo dbInfo = getDatabaseInfo(prefix);

		String driverName = "oracle.jdbc.driver.OracleDriver";

		String url = getDbUrl(dbInfo, "oracle");

		String user = dbInfo.getUserName();
		String password = dbInfo.getPassword();

		return new DbUtil().getConnection(driverName, url, user, password, true);
	}

	public static String getDbUrl(DatabaseInfo dbInfo, String dbType)
	{
		String dbIp = dbInfo.getIp();

		String dbName = dbInfo.getDatabase();
		String port = Integer.toString(dbInfo.getPort());

		String dbUrl = "";
		if ((dbType != null) && ("mysql".equals(dbType)))
		{
			dbUrl = String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf8",
					new Object[] { dbIp, port, dbName });
		}
		else
		{
			dbUrl = String
					.format("jdbc:oracle:thin:@%s:%s:%s", new Object[] { dbIp, port, dbName });
		}

		return dbUrl;
	}

	static class DatabaseInfoList
	{

		Map<String, DatabaseInfo> dbInfoMap = new HashMap<String, DatabaseInfo>();

		boolean init = false;

		void put(String prefix, DatabaseInfo dbInfo)
		{
			this.dbInfoMap.put(prefix, dbInfo);
		}

		DatabaseInfo get(String prefix)
		{
			return (DatabaseInfo) this.dbInfoMap.get(prefix);
		}

		void init(String[] prefixs)
		{
			for (String prefix : prefixs)
			{
				DatabaseInfo dbInfo = new DatabaseInfo();
				dbInfo.setIp(conAccer.getString(prefix + ".DB_IP"));
				dbInfo.setDatabase(conAccer.getString(prefix + ".DB_NAME"));
				dbInfo.setUserName(conAccer.getString(prefix + ".DB_USERNAME"));
				dbInfo.setPassword(conAccer.getString(prefix + ".DB_PASSWORD"));
				dbInfo.setPort(conAccer.getInt(prefix + ".DB_PORT"));

				put(prefix, dbInfo);
			}

			this.init = true;
		}
	}
}
