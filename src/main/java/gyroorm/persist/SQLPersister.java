package gyroorm.persist;

import gyroorm.*;
import gyroorm.model.*;
import gyroorm.parser.*;
import gyroorm.util.*;
import gyroorm.model.annotation.*;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;


public class SQLPersister implements DataPersisterInterface {

	Logger logger = LoggerFactory.getLogger();

	ConnectionManager connectionManager = new ConnectionManager();

	/**
	 * 指定されたModelにそったテーブルを作成する
	 */
	public int migrate(DefaultBaseModel model) throws DataPersisterException {
		int ret = 0; 
		Connection conn = null;

		try {
			conn = connectionManager.getDbConnection();
			String queryStr = SQLUtility.createMigrateSql(model, true);
			QueryRunner qr = new QueryRunner();
			qr.update(conn, queryStr);
			ret = 1;
		} catch (Exception e) {

			throw new DataPersisterException(e); 
		} finally {
			if (conn != null) {
				try {
					connectionManager.closeConnection(conn);
				} catch (Exception e2) {
					throw new DataPersisterException(e2); 
				}
			}
		}
		return ret;
	}


	/**
	 * 指定されたModelにそったテーブルが存在するか確認
	 */
	public boolean existTable(DefaultBaseModel model) throws DataPersisterException {
		boolean  ret = false;
		Connection conn = null;

		try {
			conn = connectionManager.getDbConnection();

			ModelInfo modelInfo = model.getModelInfo();
			String tableName = modelInfo.modelName.toLowerCase();

			String queryStr = SQLUtility.existTableSql();

			ResultSetHandler<?> resultSetHandler = new MapListHandler();
			QueryRunner qr = new QueryRunner();

			// クエリ実行
			List<Map<String, Object>> selectSet = (List<Map<String, Object>>)qr.query(conn, queryStr, resultSetHandler);

			if (selectSet == null || selectSet.size() == 0) {
				ret = false;
			} else {
				for (Map<String, Object> data : selectSet) {

					for(Map.Entry<String, Object> ent : data.entrySet()) {
						String resultTableName = (String)ent.getValue();
						if (tableName != null && !tableName.trim().equals("") && resultTableName.toLowerCase().equals(tableName)) {
							ret = true;
							break;
						}
					}
					if (ret) break;
				}
			}

		} catch (Exception e) {

			throw new DataPersisterException(e); 
		} finally {
			if (conn != null) {
				try {
					connectionManager.closeConnection(conn);
				} catch (Exception e2) {
					throw new DataPersisterException(e2); 
				}
			}
		}
		return ret;
	}


	public int merge(DefaultBaseModel model) throws DataPersisterException {
		try {
		} catch (Exception e) {
		}
		return 0;
	}

	/**
	 * 指定されたModelをテーブル登録する
	 */
	public int create(DefaultBaseModel model) throws DataPersisterException {
		int ret = 0;
		Connection conn = null;

		try {

			conn = connectionManager.getDbConnection(model);

			SQLQueryFolder sqlQueryFolder = SQLUtility.createInsertSql(model, true);
			QueryRunner qr = new QueryRunner();

			if (qr.update(conn, sqlQueryFolder.getQuery(), sqlQueryFolder.getQueryParams()) == 1) {
				upadteRelationData(model);
				ret = 1;
				connectionManager.commit(model, conn);
			} else {
				throw new DataPersisterException("SQLPersister - create - error");
			}
		} catch (DataPersisterException dpe) {
			try {
				if (conn != null) {
					connectionManager.rollback(model, conn);
				}
			} catch (Exception e2) {
				throw new DataPersisterException(e2);
			}
			throw dpe;
		} catch (Exception e) {
			try {
				if (conn != null) {
					connectionManager.rollback(model, conn);
				}
			} catch (Exception e2) {
				throw new DataPersisterException(e2);
			}
			throw new DataPersisterException(e);
		} finally {
			if (conn != null) {
				try {
					connectionManager.closeConnection(model, conn);
				} catch (Exception e2) {
					throw new DataPersisterException(e2); 
				}
			}
		}
		return ret;

	}

	/**
	 * 指定されたModelを更新する
	 */
	public int update(DefaultBaseModel model) throws DataPersisterException {
		int ret = 0;
		Connection conn = null;

		try {
			conn = connectionManager.getDbConnection(model);

			QueryFolder folder = model.getQueryFolder();
			SQLQueryFolder sqlQueryFolder = SQLUtility.createUpdateSql(model, folder);
			QueryRunner qr = new QueryRunner();
			ret = qr.update(conn, sqlQueryFolder.getQuery(), sqlQueryFolder.getQueryParams());
			if (ret > 0) {
				upadteRelationData(model);
			}
			connectionManager.commit(model, conn);
		} catch (DataPersisterException dpe) {
			try {
				if (conn != null) {
					connectionManager.rollback(model, conn);
				}
			} catch (Exception e2) {
				throw new DataPersisterException(e2);
			}
			throw dpe;
		} catch (Exception e) {
			try {
				if (conn != null) {
					connectionManager.rollback(model, conn);
				}
			} catch (Exception e2) {
				throw new DataPersisterException(e2);
			}
			throw new DataPersisterException(e);
		} finally {
			if (conn != null) {
				try {
					connectionManager.closeConnection(model, conn);
				} catch (Exception e2) {
					throw new DataPersisterException(e2); 
				}
			}
		}
		return ret;
	}


	public int delete(DefaultBaseModel model) throws DataPersisterException {
		int ret = 0;
		Connection conn = null;

		try {
			conn = connectionManager.getDbConnection(model);

			QueryFolder folder = model.getQueryFolder();
			SQLQueryFolder sqlQueryFolder = SQLUtility.createDeleteSql(model, folder);
			QueryRunner qr = new QueryRunner();
			ret = qr.update(conn, sqlQueryFolder.getQuery(), sqlQueryFolder.getQueryParams());
			if (ret > 0) {
				deleteRelationData(model);
			}
			connectionManager.commit(model, conn);
		} catch (DataPersisterException dpe) {
			try {
				if (conn != null) {
					connectionManager.rollback(model, conn);
				}
			} catch (Exception e2) {
				throw new DataPersisterException(e2);
			}
			throw dpe;
		} catch (Exception e) {
			try {
				if (conn != null) {
					connectionManager.rollback(model, conn);
				}
			} catch (Exception e2) {
				throw new DataPersisterException(e2);
			}
			throw new DataPersisterException(e);
		} finally {
			if (conn != null) {
				try {
					connectionManager.closeConnection(model, conn);
				} catch (Exception e2) {
					throw new DataPersisterException(e2); 
				}
			}
		}
		return ret;
	}

	public int findCount(DefaultBaseModel model) throws DataPersisterException {
		List tmpList = find(model, true);
		if (tmpList == null) return 0;
		return tmpList.size();
	}

	public List<BaseModel> find(DefaultBaseModel model) throws DataPersisterException {
		return (List<BaseModel>)find(model, false);
	}

	private List find(DefaultBaseModel model, boolean useCount) throws DataPersisterException {
		List<BaseModel> retList = null;
		Connection conn = null;

		try {
			QueryFolder folder = model.getQueryFolder();
			if (folder == null) throw new DataPersisterException("Query parameter not found");

			conn = connectionManager.getSelectDbConnection(model);

			SQLQueryFolder sqlQueryFolder = SQLUtility.createSelectSql(model, folder);
			ResultSetHandler<?> resultSetHandler = new MapListHandler();
			QueryRunner qr = new QueryRunner();

			// クエリ実行
			List<Map<String, Object>> selectSet = (List<Map<String, Object>>)qr.query(conn, sqlQueryFolder.getQuery(), resultSetHandler, sqlQueryFolder.getQueryParams());
			// カウント指定の場合はここで返却
			if (useCount) return selectSet;

			// DBからの結果をModelへ変換
			retList = ObjectMapper.mappingObject(model, selectSet);


			// Model内にリレーションを持つ場合リレーション部分を作成し再構築を行う
			if (retList.size() > 0) {
				for (BaseModel targetModel : retList) {
					buildRelationData(targetModel);
				}
			}
		} catch (DataPersisterException dpe) {
			try {
				if (conn != null) {
					connectionManager.rollback(model, conn);
				}
			} catch (Exception e2) {
				throw new DataPersisterException(e2);
			}
			throw dpe;
		} catch (Exception e) {
			try {
				if (conn != null) {
					connectionManager.rollback(model, conn);
				}
			} catch (Exception e2) {
				throw new DataPersisterException(e2);
			}
			throw new DataPersisterException(e);
		} finally {
			if (conn != null) {
				try {
					connectionManager.closeConnection(model, conn);
				} catch (Exception e2) {
					throw new DataPersisterException(e2); 
				}
			}
		}
		return retList;
	}


	// Relationが存在する場合にに更新もしくは新規作成を行う
	protected void upadteRelationData(DefaultBaseModel targetModel) throws DataPersisterException {
		try {
			if (targetModel.getModelInfo().relationFieldList.length > 0) {
				Field[] annotationFieldList = targetModel.getModelInfo().relationFieldList;
				for (Field annoField : annotationFieldList) {
					Annotation[] annotations = annoField.getDeclaredAnnotations();
					for (Annotation as : annotations) {
						if (as instanceof RelationData) {
							List<BaseModel> relationDataList = (List<BaseModel>)annoField.get(targetModel);
							if(relationDataList != null) {
								for (BaseModel relationModel : relationDataList) {
									relationModel.setTransactionFolder(targetModel.getTransactionFolder());
									// Join部分のモデルを処理
									relationModel.save();
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new DataPersisterException(e);
		} 
	}



	// Relationが存在する場合にに更新もしくは新規作成を行う
	protected void deleteRelationData(DefaultBaseModel targetModel) throws DataPersisterException {
		try {
			if (targetModel.getModelInfo().relationFieldList.length > 0) {
				Field[] annotationFieldList = targetModel.getModelInfo().relationFieldList;
				for (Field annoField : annotationFieldList) {
					Annotation[] annotations = annoField.getDeclaredAnnotations();
					for (Annotation as : annotations) {
						if (as instanceof RelationData) {
							List<BaseModel> relationDataList = (List<BaseModel>)annoField.get(targetModel);
							if (relationDataList != null) {
								for (BaseModel relationModel : relationDataList) {
									relationModel.setTransactionFolder(targetModel.getTransactionFolder());
									// Join部分のモデルを処理
									relationModel.delete();
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new DataPersisterException(e);
		} 
	}

	// Relationが存在する場合に作成
	protected void buildRelationData(BaseModel targetModel) throws DataPersisterException {
		try {
			if (targetModel.getModelInfo().relationFieldList.length > 0) {
				Field[] annotationFieldList = targetModel.getModelInfo().relationFieldList;
				for (Field annoField : annotationFieldList) {
					Annotation[] annotations = annoField.getDeclaredAnnotations();

					// アノテーション付きフィールド
					for (Annotation as : annotations) {
						if (as instanceof RelationData) {

							// リレーションアノテーション付きフィールド
							RelationData relationData = (RelationData)as;
							StringBuilder relationWhereQuery = new StringBuilder();
							List relationWhereQueryParams = new ArrayList();
							
							String modelClassName = relationData.modelClassName();
							String joinQuery = relationData.joinQuery();

							if ((modelClassName == null || modelClassName.trim().equals("")) ||
										(joinQuery == null || joinQuery.trim().equals(""))) {
								throw new DataPersisterException("Relation Annotation 'modelClassName' and 'joinQuery' required !");
							}

							BaseModel relationModel = (BaseModel)Class.forName(modelClassName).newInstance();

							// トランザクションを取得時に利用したモデルから取得し引き継ぎ
							relationModel.setTransactionFolder(targetModel.getTransactionFolder());

							// Join部分をクエリ文字列とパラメータへ変換
							String targetStr3 = new String(joinQuery);
							Pattern pattern3 = Pattern.compile("(\\$\\{[0-9,a-z,A-Z]*\\})");
							Matcher matcher3 = pattern3.matcher(targetStr3);
							String joinQueryStr = matcher3.replaceAll(" ? ");
							relationWhereQuery.append(joinQueryStr);

							// 結合に使用する親データのカラム名を取り出し
							Pattern pattern = Pattern.compile("\\$\\{[0-9,a-z,A-Z]*\\}");
							Matcher matcher = pattern.matcher(joinQuery);
							List<String> list = new ArrayList();
							while (matcher.find()) {

								relationWhereQueryParams.add(targetModel.getFieldData(matcher.group().replaceAll("\\$", "").replaceAll("\\{", "").replaceAll("\\}", "")));
							}
							relationModel.where(relationWhereQuery.toString(), relationWhereQueryParams.toArray(new Object[0]));
							
							// Join時の対象データのFilter用のWhere句を分析し追加
							relationWhereQuery = new StringBuilder();
							relationWhereQueryParams = new ArrayList();
							String whereQuery = relationData.whereQuery();
							String whereQueryParameter = relationData.whereQueryParameter();
							if (whereQuery != null && !whereQuery.trim().equals("")) {
								relationWhereQuery.append(whereQuery);
								if (whereQueryParameter != null && !whereQueryParameter.trim().equals("")) {
									String[] params = whereQueryParameter.split(",");
									for (String paramStr : params) {
										relationWhereQueryParams.add(paramStr.trim());
									}
								}
								relationModel.where(relationWhereQuery.toString(), relationWhereQueryParams.toArray(new Object[0]));
							}

							List<BaseModel> relationDataList = null;
							try {
								relationDataList = relationModel.find();
							
								annoField.set(targetModel, relationDataList);
							} catch (IllegalArgumentException ilae) {
								ilae.printStackTrace();
								annoField.set(targetModel, relationDataList.toArray(new BaseModel[0]));
							}

						} else {
							System.out.println("Not supported Annotation");
						}
					}
				}
			}
		} catch (Exception e) {
			throw new DataPersisterException(e);
		} 
	}
}