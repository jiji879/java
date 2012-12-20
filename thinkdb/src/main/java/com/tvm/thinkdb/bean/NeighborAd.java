/*
 * 文件名：NeighborAd.java
 * 版权：  Copyright ©2012 Tvmining Corporation. All rights reserved. 
 * 描述：相邻广告实体类
 * 修改人：余洪禹
 * 修改时间：2012-12-10
 * 修改编号：0001
 * 修改内容：创建
 */
package com.tvm.thinkdb.bean;

/**
 * 相邻广告实体类
 * 
 * @author 余洪禹
 * @version 2012-12-10 下午4:25:57
 */
public class NeighborAd
{

	/**
	 * 广告名称
	 */
	private String adName;

	/**
	 * 相邻类型(0-前,1-后)
	 */
	private int type;

	public String getAdName()
	{
		return adName;
	}

	public void setAdName(String adName)
	{
		this.adName = adName;
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
		return "NeighborAd [adName=" + adName + ", type=" + type + "]";
	}

}
