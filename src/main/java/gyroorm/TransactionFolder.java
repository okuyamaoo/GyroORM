package gyroorm;

import java.sql.*;

public class TransactionFolder {

	private Connection localConnection = null;

	private TransactionFolder() {}

	public static TransactionFolder getInstance() {
		return new TransactionFolder();
	}

	public void setConnection(Connection conn) {
		if (localConnection == null)
			localConnection = conn;
	}


	public Connection getConnection() {
		return localConnection;
	}

	public void commitTransaction() throws SQLException {
		if (localConnection != null) localConnection.commit();
	}

	public void rollbackTransaction() throws SQLException {
		if (localConnection != null) localConnection.rollback();
	}


	public void endTransaction() throws SQLException {
		if (localConnection != null) localConnection.close();
	}

}