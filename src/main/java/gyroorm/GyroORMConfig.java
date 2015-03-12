package gyroorm;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import gyroorm.persist.*;
import gyroorm.*;


public class GyroORMConfig {

	public static int PERSISTER_MODE_RDMBS = PersisterContext.GLOBAL_PERSIST_MODE_RDB;
	public static int PERSISTER_MODE_MONGODB = PersisterContext.GLOBAL_PERSIST_MODE_MONGO;

	public volatile static boolean userLoggerSetting = false;
	public volatile static String LOGGER_NAME = null;

	public volatile static Level LOGGER_LEVEL = Level.WARNING;
	

	private GyroORMConfig() {

	}

	public static void setLoggerProperties(String filePath) throws GyroORMException {
		try {
			LogManager.getLogManager().readConfiguration(GyroORMConfig.class.getResourceAsStream(filePath));
			userLoggerSetting = true;
		} catch (SecurityException e) {
			throw new GyroORMException(e);
		} catch (IOException ie) {
			throw new GyroORMException(ie);
		}
	}

	/**
	 * マスターDBの接続設定
	 *
	 * @param type
	 * @param driverName
	 * @param connectUri
	 * @param user
	 * @param pass
	 */
	public static void setPersisterConfig(int type, String driverName, String connectUri, String user, String pass) throws ClassNotFoundException, GyroORMException {
		if (type == GyroORMConfig.PERSISTER_MODE_RDMBS || type == GyroORMConfig.PERSISTER_MODE_MONGODB) {

			PersisterContext.GLOBAL_PERSIST_MODE = type;
			setPersisterDriver(driverName);
			setPersisterAddr(connectUri);
			setPersisterUser(user);
			setPersisterPass(pass);
		} else {
			throw new GyroORMException("Not supported type");
		}
	}


	/**
	 * 検索DBの接続設定.<br>
	 * 本メソッドは必ずsetPersisterConfig()メソッドを呼び出した後に実行すること<br>
	 * また、typeはPERSISTER_MODE_RDMBSである必要がある
	 *
	 * @param connectUri
	 * @param user
	 * @param pass
	 */
	public static void addSearchPersisterConfig(String connectUri, String user, String pass) throws GyroORMException {
		if (PersisterContext.GLOBAL_PERSIST_MODE == GyroORMConfig.PERSISTER_MODE_RDMBS) {

			PersisterContext.addSelectPersisterContext(connectUri, user, pass);
		} else if (PersisterContext.GLOBAL_PERSIST_MODE == -1) {

			throw new GyroORMException("Please execute after running the setPersisterConfig() method this method");
		} else {

			throw new GyroORMException("Not supported type");
		}
	}


	/**
	 * 接続設定
	 *
	 * @param type
	 * @param address
	 * @param port
	 * @param dbname
	 * @param user
	 * @param pass
	 */
	public static void setPersisterConfig(int type, String address, String dbname, int port, String user, String pass) throws ClassNotFoundException, GyroORMException {
		if (type == GyroORMConfig.PERSISTER_MODE_RDMBS || type == GyroORMConfig.PERSISTER_MODE_MONGODB) {

			PersisterContext.GLOBAL_PERSIST_MODE = type;
			setPersisterAddr(address);
			setPersisterPort(port);
			setPersisterDBName(dbname);
			setPersisterUser(user);
			setPersisterPass(pass);
		} else {
			throw new GyroORMException("Not supported type");
		}
	}


	private static void setPersisterDriver(String driverName) throws  ClassNotFoundException {
		PersisterContext.DB_DRIVER = driverName;
		try {
				if (PersisterContext.GLOBAL_PERSIST_MODE == PERSISTER_MODE_RDMBS) {
						Class.forName(PersisterContext.DB_DRIVER);
				}
		} catch (ClassNotFoundException cnfe) {
			throw cnfe;
		}
	}

	private static void setPersisterAddr(String address) {
		PersisterContext.DB_ADDR = address;
	}

	private static void setPersisterUser(String user) {
		PersisterContext.DB_USER = user;
	}

	private static void setPersisterPass(String pass) {
		PersisterContext.DB_PASS = pass;
	}

	private static void setPersisterPort(int port) {
		PersisterContext.DB_PORT = port;
	}

	private static void setPersisterDBName(String dbname) {
		PersisterContext.DB_NAME = dbname;
	}
}