package com.tvm.thinkdb.mutipulation;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.handlers.BeanHandler;

public class HumbBeanHandler<T> extends BeanHandler<T>
{

	public HumbBeanHandler(Class<T> type)
	{
		super(type, new BasicRowProcessor(new HumbRowProcessor()));
	}
}
