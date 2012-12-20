package com.tvm.thinkdb.bean;

public class DatabaseInfo
{

	public static final int MYSQL_DEFAULT_PORT = 3306;

	private String ip;

	private int port;

	private String database;

	private String userName;

	private String password;

	public DatabaseInfo()
	{
		this.port = 3306;
	}

	public DatabaseInfo(String serverIp, String database, String userName, String password)
	{
		this();
		this.ip = serverIp;
		this.database = database;
		this.userName = userName;
		this.password = password;
	}

	public String getIp()
	{
		return this.ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public int getPort()
	{
		return this.port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public String getDatabase()
	{
		return this.database;
	}

	public void setDatabase(String database)
	{
		this.database = database;
	}

	public String getUserName()
	{
		return this.userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getPassword()
	{
		return this.password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String toString()
	{
		return "数据库信息[IP地址=" + this.ip + ", 端口号" + this.port + ", 数据库名称" + this.database + ", 用户名"
				+ this.userName + ", 密码=" + this.password + "]";
	}
}
