/*
 * 文件名：NeighborProgram.java
 * 版权：  Copyright ©2012 Tvmining Corporation. All rights reserved. 
 * 描述：相邻节目实体类
 * 修改人：余洪禹
 * 修改时间：2012-12-10
 * 修改编号：0001
 * 修改内容：创建
 */
package com.tvm.thinkdb.bean;

/**
 * 相邻节目实体类
 * 
 * @author 余洪禹
 * @version 2012-12-10 下午4:22:40
 */
public class NeighborProgram
{

	/**
	 * 节目名称
	 */
	private String programName;

	/**
	 * 相邻类型(0-前,1-后)
	 */
	private int type;

	public String getProgramName()
	{
		return programName;
	}

	public void setProgramName(String programName)
	{
		this.programName = programName;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	@Override
	public String toString()
	{
		return "NeighborProgram [programName=" + programName + ", type=" + type + "]";
	}

}
