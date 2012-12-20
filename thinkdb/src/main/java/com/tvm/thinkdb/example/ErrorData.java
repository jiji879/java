package com.tvm.thinkdb.example;

public class ErrorData
{

	private int id;

	private String primaryKey;

	private String syncSql;

	private String tableName;

	private String remark;

	private int status;

	private String updateTime;

	public int getId()
	{
		return this.id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getPrimaryKey()
	{
		return this.primaryKey;
	}

	public void setPrimaryKey(String primaryKey)
	{
		this.primaryKey = primaryKey;
	}

	public String getSyncSql()
	{
		return this.syncSql;
	}

	public void setSyncSql(String syncSql)
	{
		this.syncSql = syncSql;
	}

	public String getTableName()
	{
		return this.tableName;
	}

	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	public String getRemark()
	{
		return this.remark;
	}

	public void setRemark(String remark)
	{
		this.remark = remark;
	}

	public int getStatus()
	{
		return this.status;
	}

	public void setStatus(int status)
	{
		this.status = status;
	}

	public String getUpdateTime()
	{
		return this.updateTime;
	}

	public void setUpdateTime(String updateTime)
	{
		this.updateTime = updateTime;
	}

	public String toString()
	{
		return "ErrorData [id=" + this.id + ", primaryKey=" + this.primaryKey + ", syncSql="
				+ this.syncSql + ", tableName=" + this.tableName + ", remark=" + this.remark
				+ ", status=" + this.status + ", updateTime=" + this.updateTime + "]";
	}
}
