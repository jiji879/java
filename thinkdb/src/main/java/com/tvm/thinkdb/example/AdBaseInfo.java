/*
 * 文件名：AdBaseInfo.java
 * 版权：  Copyright ©2012 Tvmining Corporation. All rights reserved. 
 * 描述：广告基础信息实体类
 * 修改人：余洪禹
 * 修改时间：2012-12-10
 * 修改编号：0001
 * 修改内容：创建
 */
package com.tvm.thinkdb.example;


/**
 * 广告基础信息实体类,记录了所有广告相关的基础信息
 * 
 * @author 余洪禹
 * @version 2012-12-10 下午4:12:55
 */
public class AdBaseInfo implements Cloneable
{

	/**
	 * 主键id
	 */
	private int id;

	/**
	 * 广告uuid
	 */
	private String adUuid;

	/**
	 * 广告样片uuid
	 */
	private String adDataUuid;

	/**
	 * 所属频道中文名
	 */
	private String channelCname;

	/**
	 * 精编广告标题
	 */
	private String title;

	/**
	 * 公司名称
	 */
	private String companyName;

	/**
	 * 行业名称
	 */
	private String domainName;

	/**
	 * 广告播放开始时间
	 */
	private String playStartTime;

	/**
	 * 广告播放结束时间
	 */
	private String playEndTime;

	/**
	 * 播放时长
	 */
	private long duration;

	/**
	 * 所在广告段中的索引位置(1,2,3...)
	 */
	private int adSegmentIndex;

	/**
	 * 相邻节目信息
	 */
	private NeighborProgram neighborProgram;

	/**
	 * 相邻广告信息
	 */
	private NeighborAd neighborAd;

	/**
	 * 广告播放频道的所在地域
	 */
	private String area;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getAdUuid()
	{
		return adUuid;
	}

	public void setAdUuid(String adUuid)
	{
		this.adUuid = adUuid;
	}

	public String getAdDataUuid()
	{
		return adDataUuid;
	}

	public void setAdDataUuid(String adDataUuid)
	{
		this.adDataUuid = adDataUuid;
	}

	public String getChannelCname()
	{
		return channelCname;
	}

	public void setChannelCname(String channelCname)
	{
		this.channelCname = channelCname;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getCompanyName()
	{
		return companyName;
	}

	public void setCompanyName(String companyName)
	{
		this.companyName = companyName;
	}

	public String getDomainName()
	{
		return domainName;
	}

	public void setDomainName(String domainName)
	{
		this.domainName = domainName;
	}

	public String getPlayStartTime()
	{
		return playStartTime;
	}

	public void setPlayStartTime(String playStartTime)
	{
		this.playStartTime = playStartTime;
	}

	public String getPlayEndTime()
	{
		return playEndTime;
	}

	public void setPlayEndTime(String playEndTime)
	{
		this.playEndTime = playEndTime;
	}

	public long getDuration()
	{
		return duration;
	}

	public void setDuration(long duration)
	{
		this.duration = duration;
	}

	public int getAdSegmentIndex()
	{
		return adSegmentIndex;
	}

	public void setAdSegmentIndex(int adSegmentIndex)
	{
		this.adSegmentIndex = adSegmentIndex;
	}

	public NeighborProgram getNeighborProgram()
	{
		return neighborProgram;
	}

	public void setNeighborProgram(NeighborProgram neighborProgram)
	{
		this.neighborProgram = neighborProgram;
	}

	public NeighborAd getNeighborAd()
	{
		return neighborAd;
	}

	public void setNeighborAd(NeighborAd neighborAd)
	{
		this.neighborAd = neighborAd;
	}

	public String getArea()
	{
		return area;
	}

	public void setArea(String area)
	{
		this.area = area;
	}

	@Override
	public String toString()
	{
		return "AdBaseInfo [id=" + id + ", adUuid=" + adUuid + ", adSampleUuid=" + adDataUuid
				+ ", channelCname=" + channelCname + ", title=" + title + ", companyName="
				+ companyName + ", domainName=" + domainName + ", playStartTime=" + playStartTime
				+ ", playEndTime=" + playEndTime + ", duration=" + duration + ", adSegmentIndex="
				+ adSegmentIndex + ", neighborProgram=" + neighborProgram + ", neighborAd="
				+ neighborAd + ", area=" + area + "]";
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}
