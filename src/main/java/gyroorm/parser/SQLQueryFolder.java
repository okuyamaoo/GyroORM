package gyroorm.parser;

import java.util.*;

public class SQLQueryFolder {

	private String query = null;

	private List paramsTmp = new ArrayList();


	public SQLQueryFolder() {
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQuery() {
		return this.query;
	}

	public void addQueryParam(Object param) {

		this.paramsTmp.add(param);
	}


	public void addQueryParams(Object[] params) {

		for (int i = 0; i < params.length; i++) {
			this.paramsTmp.add(params[i]);
		}
	}

	public Object[] getQueryParams() {

		return this.paramsTmp.toArray();
	}

	public int getQueryParamSize() {
		return this.paramsTmp.size();
	}

	public String toString() {
		StringBuilder strBuf = new StringBuilder();
		strBuf.append("SQL : ");
		strBuf.append(query);
		strBuf.append("\n");
		strBuf.append("QueryParam : ");
		strBuf.append(paramsTmp);
		return strBuf.toString();
	}
}