package gyroorm.persist;

import gyroorm.*;
import gyroorm.model.*;
import gyroorm.parser.*;

import java.io.*;
import java.util.*;
import java.sql.*;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;


public class ConnectionManager {
	

	public Connection getDbConnection() throws Exception {
	
		Connection conn = DriverManager.getConnection(PersisterContext.getDBAddress(), 
																			PersisterContext.getDBUser(), 
																				PersisterContext.getDBPass());
		conn.setAutoCommit(false);
		return conn;
	}
	
	
	public Connection getSelectDbConnection(DefaultBaseModel model) throws Exception {
		// Select専用のConnectionの取得を要求されている

		Connection conn = null;
		if (model.hasTransactionFolder()) {
			// TransactionFolderをもっているので、トランザクション内のコネクションを使う必要があるため
			// 通常のgetDbConnectionへチェーン
			return getDbConnection(model);
		} else {
			// TransactionFolderを持たないためトランザクション外での単発のselectとなる
			// そのためアノテーションにより参照DBの設定がある場合はこちらを利用
			if (model.getModelInfo().useSelectResource) {

				// 参照DBの設定あり
				// 参照DBの設定のConfig設定がされている場合のみそちらのコネクションを利用する
				List<PersisterContext> selectPersisterContextList = PersisterContext.getSelectPersisterContextList();
				if (selectPersisterContextList != null) {

					int selectPersisterContextListSize = selectPersisterContextList.size();
					Random rnd = new Random();
					PersisterContext selectPersisterContext = null;

					for (int idx = 0; idx < selectPersisterContextListSize; idx++) {
						try {
							selectPersisterContext = selectPersisterContextList.remove(rnd.nextInt(selectPersisterContextList.size()));
	
							conn = DriverManager.getConnection(selectPersisterContext.getSelectDBAddress(), 
																									selectPersisterContext.getSelectDBUser(), 
																										selectPersisterContext.getSelectDBPass());
						} catch (Exception ee) {
							conn = null;
							ee.printStackTrace();
						}
					}
				}
			}

			if (conn == null) {
					conn = DriverManager.getConnection(PersisterContext.getDBAddress(), 
																								PersisterContext.getDBUser(), 
																									PersisterContext.getDBPass());
			}
			conn.setAutoCommit(false);
		}

		return conn;
	}

	public Connection getDbConnection(DefaultBaseModel model) throws Exception {
		Connection conn = null;
		if (model.hasTransactionFolder()) {
			TransactionFolder folder = model.getTransactionFolder();
			conn = folder.getConnection();

			if (conn == null) {
				conn = DriverManager.getConnection(PersisterContext.getDBAddress(), 
																						PersisterContext.getDBUser(), 
																							PersisterContext.getDBPass());
				conn.setAutoCommit(false);
				folder.setConnection(conn);
			}
		} else {
				conn = DriverManager.getConnection(PersisterContext.getDBAddress(), 
																						PersisterContext.getDBUser(), 
																							PersisterContext.getDBPass());
				conn.setAutoCommit(false);
		}

		return conn;
	}


	public void commit(DefaultBaseModel model, Connection conn) throws Exception {
		if (!model.hasTransactionFolder()) {
			conn.commit();
		}
	}

	public void commit(Connection conn) throws Exception {
		conn.commit();
	}

	public void rollback(DefaultBaseModel model, Connection conn) throws Exception {
		if (!model.hasTransactionFolder()) {
			conn.rollback();
		}
	}

	public void rollback(Connection conn) throws Exception {
		conn.rollback();
	}

	public void closeConnection(DefaultBaseModel model, Connection conn) throws Exception {
		if (!model.hasTransactionFolder()) {
			conn.close();
		}
	}

	public void closeConnection(Connection conn) throws Exception {
		conn.close();
	}
}