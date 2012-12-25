package com.tvm.thinkdb.mutipulation;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.handlers.BeanListHandler;

public class HumbBeanListHandler<T> extends BeanListHandler<T>
{

	public HumbBeanListHandler(Class<T> type)
	{
		super(type, new BasicRowProcessor(new HumbRowProcessor()));
	}
}
