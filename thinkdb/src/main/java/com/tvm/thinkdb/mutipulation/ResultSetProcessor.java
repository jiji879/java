package com.tvm.thinkdb.mutipulation;

import org.apache.commons.dbutils.BeanProcessor;

/**
 * 数据库结果集处理器枚举
 * 
 * @author 余洪禹
 * @version 2012-12-18 下午2:57:57
 */
public class ResultSetProcessor
{

	public static final int HUMB_PROCESSOR = 0;

	public static final int NESTED_PROCESSOR = 1;

	public static BeanProcessor getProcessor(int processorType)
	{
		switch (processorType)
		{
			case HUMB_PROCESSOR:
				return new HumbRowProcessor();
			case NESTED_PROCESSOR:
				return new NestedRowProcessor();
			default:
				throw new IllegalArgumentException("不支持的枚举值:" + processorType);
		}
	}
}
