package com.tvm.thinkdb.mutipulation;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.handlers.BeanHandler;

/**
 * 驼峰数据库数据集处理器,用于获取一行数据的实体对象
 * 
 * @author 余洪禹
 * @version 2013-2-18 下午2:00:53
 */
public class HumbBeanHandler<T> extends BeanHandler<T>
{

	/**
	 * 创建一个以驼峰形式处理数据库数据集处理器
	 * 
	 * @param type
	 *            待处理实体类对象
	 */
	public HumbBeanHandler(Class<T> type)
	{
		super(type, new BasicRowProcessor(new HumbRowProcessor()));
	}
}
