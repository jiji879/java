package com.tvm.thinkdb.mutipulation;

import com.tvm.thinkdb.util.ColumnPropertySupporter;
import java.beans.PropertyDescriptor;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import org.apache.commons.dbutils.BeanProcessor;

public class HumbRowProcessor extends BeanProcessor
{

	protected int[] mapColumnsToProperties(ResultSetMetaData rsmd, PropertyDescriptor[] props)
			throws SQLException
	{
		int cols = rsmd.getColumnCount();
		int[] columnToProperty = new int[cols + 1];
		Arrays.fill(columnToProperty, -1);

		for (int col = 1; col <= cols; col++)
		{
			String columnName = rsmd.getColumnLabel(col);
			if ((columnName == null) || (columnName.length() == 0))
			{
				columnName = rsmd.getColumnName(col);
			}
			columnName = ColumnPropertySupporter.colNameConvent(columnName);
			for (int i = 0; i < props.length; i++)
			{
				if (!columnName.equalsIgnoreCase(props[i].getName()))
					continue;
				columnToProperty[col] = i;
				break;
			}

		}

		return columnToProperty;
	}
}
