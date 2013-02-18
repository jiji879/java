package com.tvm.thinkdb.util;

/**
 * 数据库实体列和实体关系的工具类
 * 
 * @author 余洪禹
 * @version 2012-12-18 下午3:46:45
 */
public class ColumnPropertySupporter
{

	/**
	 * 将数据库结果原数据的列标签(或者名称)的下划线'_'分隔转换为Java的驼峰形式
	 * 
	 * @param columnName
	 *            待转换列标签(名称)
	 * @return 驼峰形式的列标志
	 * @author 余洪禹
	 */
	public static String colNameConvent(String columnName)
	{
		StringBuilder humpName = new StringBuilder();
		String[] strs = columnName.trim().split("_");
		humpName.append(strs[0]);
		for (int i = 1; i < strs.length; i++)
		{
			String orginName = strs[i];
			humpName.append(orginName.substring(0, 1).toUpperCase() + orginName.substring(1));
		}
		return humpName.toString();
	}

	/**
	 * 获取数据库字段的getter方法名称
	 * 
	 * @param columnName
	 *            数据库字段名称
	 * @return 对应的getter名
	 * @author 余洪禹
	 */
	public static String getGetterName(String columnName)
	{
		// 转换为驼峰
		String humbName = colNameConvent(columnName);

		StringBuilder sb = new StringBuilder();
		sb.append("get");

		char firstChar = humbName.charAt(0);
		sb.append(Character.toUpperCase(firstChar));

		if (humbName.length() > 1)
		{
			sb.append(humbName.substring(1));
		}

		return sb.toString();
	}
}
