package gyroorm.parser;

import java.util.*;
import java.util.logging.*;
import java.security.NoSuchAlgorithmException;
import java.lang.reflect.*;
import java.security.SecureRandom;

import gyroorm.model.*;
import gyroorm.util.*;
import gyroorm.persist.*;


public class SQLUtility {

	static Logger logger = LoggerFactory.getLogger();
	
	/**
	 * テーブルを作成するDDL文を作成<br>
	 * 作成時にIDフィールドを作成するかどうか指定可能<br>
	 *
	 * @param model 作成対象となるモデル
	 * @param createIdColumn IDカラムの作成要否
	 * @return DDL文字列
	 */
	public static String createMigrateSql(DefaultBaseModel model, boolean createIdColumn) throws Exception {
		StringBuilder queryBuf = new StringBuilder();
		ModelInfo modelInfo = model.getModelInfo();
		String tableName = modelInfo.modelName;
		queryBuf.append("create table ").append(tableName).append(" ( ");

		String sep = "";

		if (createIdColumn) {
			queryBuf.append(modelInfo.primaryKeyName).append(" BIGINT ").append("auto_increment PRIMARY KEY ");
			sep = ",";
		}

		BaseModel targetModel = model.getTargetModel();

		int idx = 0;
		
    for (Field field : modelInfo.fieldList) {
 				if (!modelInfo.fieldNameList[idx].equals(modelInfo.primaryKeyName)) {
						try {
								field.setAccessible(true);
								queryBuf.append(sep);
								queryBuf.append(modelInfo.fieldNameList[idx]);
								queryBuf.append(" ");
								if (modelInfo.fieldTypeList[idx].equals("String") || modelInfo.fieldTypeList[idx].equals("java.lang.String")) {
									queryBuf.append("varchar(2000)");
								} else if (modelInfo.fieldTypeList[idx].equals("int") || modelInfo.fieldTypeList[idx].equals("java.lang.Integer") || 
														modelInfo.fieldTypeList[idx].equals("short") || modelInfo.fieldTypeList[idx].equals("java.lang.Short")) {
									queryBuf.append("INT");
								} else if (modelInfo.fieldTypeList[idx].equals("long") || modelInfo.fieldTypeList[idx].equals("java.lang.Long")) {
									queryBuf.append("BIGINT");
								} else if (modelInfo.fieldTypeList[idx].equals("doube") || modelInfo.fieldTypeList[idx].equals("java.lang.Double") || 
														modelInfo.fieldTypeList[idx].equals("float") || modelInfo.fieldTypeList[idx].equals("java.lang.Float")) {
									queryBuf.append("DOUBLE");
								} else if (modelInfo.fieldTypeList[idx].toLowerCase().equals("date") || modelInfo.fieldTypeList[idx].equals("java.util.Date") || 
														modelInfo.fieldTypeList[idx].toLowerCase().equals("datetime")) {
									queryBuf.append("DATETIME");
								} else if (modelInfo.fieldTypeList[idx].toLowerCase().equals("timestamp") || modelInfo.fieldTypeList[idx].equals("java.sql.Timestamp") || 
														modelInfo.fieldTypeList[idx].toLowerCase().equals("time")) {
									queryBuf.append("TIMESTAMP");

								} else {
									queryBuf.append("varchar(255)");
								}
						} catch (Exception e) {
								throw new IllegalAccessException(field.getName() + " = " + "access denied\n");
						}
					sep = ",";
				}
				idx++;
    }
		if (createIdColumn) {
			queryBuf.append(", index(");
			queryBuf.append(modelInfo.primaryKeyName);
			queryBuf.append(")");
		}
		queryBuf.append(" )");
		String queryStr = queryBuf.toString();
		logger.fine(queryStr);
		return queryStr;
	}


	public static String existTableSql() {
		String queryStr = "show tables";
		logger.fine(queryStr);
		return queryStr;
	}
	

	/**
	 * Insert文を作成
	 *
	 */
	public static SQLQueryFolder createInsertSql(DefaultBaseModel model, boolean autoId) throws Exception {
		StringBuilder queryBuf = new StringBuilder();
		ModelInfo modelInfo = model.getModelInfo();
		String tableName = modelInfo.modelName;
		BaseModel targetModel = model.getTargetModel();
		SQLQueryFolder sqlQueryFolder = new SQLQueryFolder();

		queryBuf.append("insert into ").append(tableName).append(" (");
		
		String sep = "";
		double generateId = -1.0;
		if (!autoId) {
			/*queryBuf.append(modelInfo.primaryKeyName);
			generateId = generateID(tableName);
			sqlQueryFolder.addQueryParam(generateId);
			model.setId(generateId);
			
			sep = ",";*/
		}

		int idx = 0;

    for (Field field : modelInfo.fieldList) {
 				if (!modelInfo.fieldNameList[idx].equals(modelInfo.primaryKeyName)) {
						try {
								field.setAccessible(true);
								queryBuf.append(sep);
								queryBuf.append(modelInfo.fieldNameList[idx]);
								sqlQueryFolder.addQueryParam(field.get(targetModel));

						} catch (IllegalAccessException e) {
								throw new IllegalAccessException(field.getName() + " = " + "access denied\n");
						}
					sep = ",";
				}
				idx++;
    }
		queryBuf.append(" ) values(");
		sep = "";
		int queryParamCount = sqlQueryFolder.getQueryParamSize();
		for (idx = 0; idx < queryParamCount; idx++) {
			queryBuf.append(sep);
			queryBuf.append("?");
			sep = ",";
		}
		queryBuf.append(" )");
		sqlQueryFolder.setQuery(queryBuf.toString());

		logger.fine(sqlQueryFolder.toString());
		return sqlQueryFolder;
	}


	/**
	 * Update文を作成
	 *
	 */
	public static SQLQueryFolder createUpdateSql(DefaultBaseModel model, QueryFolder folder) throws Exception {
		StringBuilder queryBuf = new StringBuilder();
		StringBuilder queryWhereBuf = new StringBuilder();
		List queryWhereParams = new ArrayList();

		ModelInfo modelInfo = model.getModelInfo();
		String tableName = modelInfo.modelName;
		BaseModel targetModel = model.getTargetModel();
		SQLQueryFolder sqlQueryFolder = new SQLQueryFolder();

		queryBuf.append("update ").append(tableName).append(" set ");
		
		String sep = "";
		double generateId = -1.0;
		
		// 条件付加されていない場合は引数のModelの主キーを使って更新
		if (folder == null || folder.whereList == null || folder.whereList.size() < 1) {
			// 主キーを使って更新
			queryWhereBuf.append(" where ");
			queryWhereBuf.append(modelInfo.primaryKeyName).append(" = ? ");
			queryWhereParams.add(model.getId());
		} else {

			// QueryFolder内のWhere句を使って更新
			String whereSep = "where ";
			for (Map<String, Object[]> whereMap : folder.whereList) {
				queryWhereBuf.append(whereSep);
	
				for(Map.Entry<String, Object[]> entry : whereMap.entrySet()) {
	
					queryWhereBuf.append("(");
					queryWhereBuf.append(entry.getKey());
					Object[] paramList = entry.getValue();
					if (paramList != null) {
							for (Object param : paramList) {
								queryWhereParams.add(param);
							}
					}
					queryWhereBuf.append(")");
				}
				whereSep = " and ";
			}
		}

		int idx = 0;

    for (Field field : modelInfo.fieldList) {
 				if (!modelInfo.fieldNameList[idx].equals(modelInfo.primaryKeyName)) {
						try {
							field.setAccessible(true);
							Object fieldValue = field.get(targetModel);

							if (fieldValue == null && !model.isNullUpdate(modelInfo.fieldNameList[idx])) {
								idx++;
								continue;
							}
							queryBuf.append(sep);
							queryBuf.append(modelInfo.fieldNameList[idx]);
							queryBuf.append(" = ? ");
							sqlQueryFolder.addQueryParam(fieldValue);

						} catch (IllegalAccessException e) {
							throw new IllegalAccessException(field.getName() + " = " + "access denied\n");
						}
					sep = ",";
				}
				idx++;
    }

		queryBuf.append(queryWhereBuf);
		sqlQueryFolder.setQuery(queryBuf.toString());
		sqlQueryFolder.addQueryParams(queryWhereParams.toArray(new Object[0]));

		logger.fine(sqlQueryFolder.toString());
		return sqlQueryFolder;
	}


	/**
	 * Delete文を作成
	 *
	 */
	public static SQLQueryFolder createDeleteSql(DefaultBaseModel model, QueryFolder folder) throws Exception {
		StringBuilder queryBuf = new StringBuilder();
		StringBuilder queryWhereBuf = new StringBuilder();
		List queryWhereParams = new ArrayList();

		ModelInfo modelInfo = model.getModelInfo();
		String tableName = modelInfo.modelName;
		BaseModel targetModel = model.getTargetModel();
		SQLQueryFolder sqlQueryFolder = new SQLQueryFolder();

		queryBuf.append("delete from ").append(tableName).append(" ");
		
		String sep = "";
		double generateId = -1.0;
		
		// 条件付加されていない場合は引数のModelの主キーを使って更新
		if (folder == null || folder.whereList == null || folder.whereList.size() < 1) {
			// 主キーを使って更新
			queryWhereBuf.append(" where ");
			queryWhereBuf.append(modelInfo.primaryKeyName).append(" = ? ");
			queryWhereParams.add(model.getId());
		} else {

			// QueryFolder内のWhere句を使って更新
			String whereSep = "where ";
			for (Map<String, Object[]> whereMap : folder.whereList) {
				queryWhereBuf.append(whereSep);
	
				for(Map.Entry<String, Object[]> entry : whereMap.entrySet()) {
	
					queryWhereBuf.append("(");
					queryWhereBuf.append(entry.getKey());
					Object[] paramList = entry.getValue();
					if (paramList != null) {
							for (Object param : paramList) {
								queryWhereParams.add(param);
							}
					}
					queryWhereBuf.append(")");
				}
				whereSep = " and ";
			}
		}

		queryBuf.append(queryWhereBuf);
		sqlQueryFolder.setQuery(queryBuf.toString());
		sqlQueryFolder.addQueryParams(queryWhereParams.toArray(new Object[0]));

		logger.fine(sqlQueryFolder.toString());
		return sqlQueryFolder;
	}

	/**
	 * Select文を作成
	 *
	 */
	public static SQLQueryFolder createSelectSql(DefaultBaseModel model, QueryFolder queryFolder) throws Exception {
		StringBuilder queryBuf = new StringBuilder();
		StringBuilder querySelectBuf = new StringBuilder();
		StringBuilder queryFromBuf = new StringBuilder();
		StringBuilder queryWhereBuf = new StringBuilder();
		StringBuilder queryOrderByBuf = new StringBuilder();
		StringBuilder queryGroupByBuf = new StringBuilder();
		StringBuilder queryHavingBuf = new StringBuilder();
		StringBuilder queryLimitByBuf = new StringBuilder();
		StringBuilder queryOffsetByBuf = new StringBuilder();

		SQLQueryFolder sqlQueryFolder = new SQLQueryFolder();

		// Select句作成
		String selectSep = "select ";
		if (queryFolder.selectList != null) {
				for (String selectQuery : queryFolder.selectList) {
					querySelectBuf.append(selectSep);
					querySelectBuf.append(selectQuery);
					selectSep = ",";
				}
		} else {
			querySelectBuf.append("select * ");
		}

		// From句作成
		String fromSep = "from ";
		for (Map<String, String> fromMap : queryFolder.fromList) {
			queryFromBuf.append(fromSep);

			for(Map.Entry<String, String> entry : fromMap.entrySet()) {
				queryFromBuf.append(entry.getKey());
			}
			fromSep = ",";
		}


		// Where句作成
		if (queryFolder.whereList != null) {
			String whereSep = "where ";
			for (Map<String, Object[]> whereMap : queryFolder.whereList) {
				queryWhereBuf.append(whereSep);
	
				for(Map.Entry<String, Object[]> entry : whereMap.entrySet()) {
	
					queryWhereBuf.append("(");
					queryWhereBuf.append(entry.getKey());
					Object[] paramList = entry.getValue();
					if (paramList != null) {
							for (Object param : paramList) {
								sqlQueryFolder.addQueryParam(param);
							}
					}
					queryWhereBuf.append(")");
				}
				whereSep = " and ";
			}
		}

		// Group By句作成
		if (queryFolder.groupByList != null) {
			String groupBySep = "group by ";
			for (String groupQuery : queryFolder.groupByList) {
				queryGroupByBuf.append(groupBySep);
				queryGroupByBuf.append(groupQuery);
				groupBySep = ",";
			}
		}


		// Having句作成
		if (queryFolder.havingList != null) {
			String havingSep = " having ";
			for (Map<String, Object[]> havingMap : queryFolder.havingList) {
				queryHavingBuf.append(havingSep);
	
				for(Map.Entry<String, Object[]> entry : havingMap.entrySet()) {
	
					queryHavingBuf.append("(");
					queryHavingBuf.append(entry.getKey());
					Object[] paramList = entry.getValue();
					if (paramList != null) {
							for (Object param : paramList) {
								sqlQueryFolder.addQueryParam(param);
							}
					}
					queryHavingBuf.append(")");
				}
				havingSep = " and ";
			}
		}


		// Order By句作成
		if (queryFolder.orderByList != null) {
			String orderBySep = "order by ";
			for (String orderQuery : queryFolder.orderByList) {
				queryOrderByBuf.append(orderBySep);
				queryOrderByBuf.append(orderQuery);
				orderBySep = ",";
			}
		}

		// limit句作成
		if (queryFolder.limit != -1) { 
			queryLimitByBuf.append(" limit ");
			queryLimitByBuf.append(queryFolder.limit);
		}

		// offset句作成
		if (queryFolder.offset != -1) { 
			queryOffsetByBuf.append(" offset ");
			queryOffsetByBuf.append(queryFolder.offset);
		}

		// 完成SQLを組み立て
		queryBuf.append(querySelectBuf.toString())
		        .append(" ")
		        .append(queryFromBuf.toString())
		        .append(" ")
		        .append(queryWhereBuf.toString())
		        .append(" ")
		        .append(queryGroupByBuf.toString())
		        .append(" ")
		        .append(queryHavingBuf.toString())
		        .append(" ")
						.append(queryOrderByBuf.toString())
		        .append(" ")
						.append(queryLimitByBuf.toString())
		        .append(" ")
		        .append(queryOffsetByBuf.toString());

		sqlQueryFolder.setQuery(queryBuf.toString());

		logger.fine(sqlQueryFolder.toString());
		return sqlQueryFolder;
	}

	private static Double generateID(String tableName) throws NoSuchAlgorithmException {
		SecureRandom secRandom = null;

    try {
	    secRandom = SecureRandom.getInstance("SHA1PRNG");
    } catch (NoSuchAlgorithmException e) {
			throw e;
    }        
  	return new Double(tableName.hashCode() + System.nanoTime() + secRandom.nextDouble());
	}

}
