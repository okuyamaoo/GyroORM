package gyroorm.persist;

import java.util.*;

public class QueryFolder {

	public List<String> selectList = null;

	public List<Map<String, String>> fromList = new ArrayList();

	public List<Map<String, Object[]>> whereList = null;

	public List<Map<String, Object[]>> havingList = null;

	public List<String> orderByList = null;

	public List<String> groupByList = null;

	public int limit = -1;

	public int offset = -1;


	public QueryFolder() {
	}


	public void addFromTable(String tableName) {
		Map<String, String> fromTable = new HashMap();
		fromTable.put(tableName, tableName); // 最終的にはテーブル別名を格納
		fromList.add(fromTable);
	}

	public void addSelect(String query) {
		if (selectList == null) selectList = new ArrayList();
		selectList.add(query);
	}

	public void addWhere(String query, Object[] params) {
		if (whereList == null) whereList = new ArrayList();
		Map<String, Object[]> whereParam = new HashMap();
		whereParam.put(query, params);

		whereList.add(whereParam);
	}

	public void addHaving(String query, Object[] params) {
		if (havingList == null) havingList = new ArrayList();
		Map<String, Object[]> havingParam = new HashMap();
		havingParam.put(query, params);

		havingList.add(havingParam);
	}

	public void addOrderBy(String query) {
		if (orderByList == null) orderByList = new ArrayList();
		orderByList.add(query);
	}

	public void addGroupBy(String query) {
		if (groupByList == null) groupByList = new ArrayList();
		groupByList.add(query);
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}


	public void setOffset(int offset) {
		this.offset = offset;
	}

	public String toString() {
		StringBuilder strBuf = new StringBuilder();

		strBuf.append("fromList : ");
		strBuf.append(fromList);
		strBuf.append("\n");
		strBuf.append("whereList : ");
		strBuf.append(whereList);
		strBuf.append("\n");
		strBuf.append("gourpByList : ");
		strBuf.append(groupByList);
		strBuf.append("\n");
		strBuf.append("havingList : ");
		strBuf.append(havingList);
		strBuf.append("\n");
		strBuf.append("orderByList : ");
		strBuf.append(orderByList);
		strBuf.append("\n");
		strBuf.append("limit : ");
		strBuf.append(limit);
		strBuf.append("\n");
		strBuf.append("offset : ");
		strBuf.append(offset);
	
	return strBuf.toString();	
	}
}