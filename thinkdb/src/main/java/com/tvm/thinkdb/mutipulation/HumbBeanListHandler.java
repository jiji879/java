package com.tvm.thinkdb.mutipulation;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.handlers.BeanListHandler;

/**
 * 驼峰形式处理数据库结果集的处理器对象,用于将多行数据记录转换为实体列表
 * 
 * @author 余洪禹
 * @version 2013-2-18 下午2:19:10
 */
public class HumbBeanListHandler<T> extends BeanListHandler<T>
{

	/**
	 * 创建一个驼峰形式处理数据库结果集的处理器对象
	 * 
	 * @param type
	 *            需要转换的实体类对象
	 */
	public HumbBeanListHandler(Class<T> type)
	{
		super(type, new BasicRowProcessor(new HumbRowProcessor()));
	}
}
