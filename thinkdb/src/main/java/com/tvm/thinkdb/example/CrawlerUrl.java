package com.tvm.thinkdb.example;

import java.util.regex.Pattern;

public class CrawlerUrl
{

	private String name;

	private String baseUrl;

	private String filterRegex;

	private boolean ignoreDepth;

	/**
	 * url���,Ĭ�ϴ�1��ʼ
	 */
	private int depth = 1;

	public boolean match(String anchor)
	{
		return Pattern.matches(filterRegex, anchor);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getBaseUrl()
	{
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl)
	{
		this.baseUrl = baseUrl;
	}

	public String getFilterRegex()
	{
		return filterRegex;
	}

	public void setFilterRegex(String filterRegex)
	{
		this.filterRegex = filterRegex;
	}

	public boolean getIgnoreDepth()
	{
		return ignoreDepth;
	}

	public void setIgnoreDepth(int ignoreDepth)
	{
		this.ignoreDepth = ignoreDepth == 0 ? false : true;
	}

	public int getDepth()
	{
		return depth;
	}

	public void setDepth(int depth)
	{
		this.depth = depth;
	}
	
	public CrawlerUrl(){
		
	}

	public CrawlerUrl(String name, String baseUrl, String filterRegex, boolean ignoreDepth,
			int depth)
	{
		super();
		this.name = name;
		this.baseUrl = baseUrl;
		this.filterRegex = filterRegex;
		this.ignoreDepth = ignoreDepth;
		this.depth = depth;
	}

	@Override
	public String toString()
	{
		return "CrawlerUrl [name=" + name + ", baseUrl=" + baseUrl + ", ignoreDepth=" + ignoreDepth
				+ ", filterRegex = " + filterRegex + "]";
	}
}
