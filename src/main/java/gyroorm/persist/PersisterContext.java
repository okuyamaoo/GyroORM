package gyroorm.persist;

import java.util.*;

/**
 * Persisterのコンテキスト設定を保持<br>
 *
 */
public class PersisterContext {



	// インスタンスか不可
	private PersisterContext() {}

	public PersisterContext getInstance() {
		return new PersisterContext();
	}

	public volatile static int GLOBAL_PERSIST_MODE = -1; // 1=RDB 2=MongoDB 3=...

	public static int GLOBAL_PERSIST_MODE_RDB = 1;

	public static int GLOBAL_PERSIST_MODE_MONGO = 2;

	public volatile static String DB_DRIVER= null;
	public volatile static String DB_ADDR = null;
	public volatile static int DB_PORT = -1;
	public volatile static String DB_NAME = null;
	public volatile static String DB_USER = null;
	public volatile static String DB_PASS = null;

	private int SELECT_GLOBAL_PERSIST_MODE = -1;
	private String SELECT_DB_DRIVER= null;
	private String SELECT_DB_ADDR = null;
	private int SELECT_DB_PORT = -1;
	private String SELECT_DB_NAME = null;
	private String SELECT_DB_USER = null;
	private String SELECT_DB_PASS = null;


	private static volatile List<PersisterContext> persisterContextList = new ArrayList();


	public static List<PersisterContext> getSelectPersisterContextList() {
		if (persisterContextList.size() < 1) return null;
		return (List<PersisterContext>)new ArrayList(persisterContextList);
	}


	public static void addSelectPersisterContext(String connectUri, String user, String pass) {
		PersisterContext context = new PersisterContext();
		context.SELECT_DB_ADDR = connectUri;
		context.SELECT_DB_USER = user;
		context.SELECT_DB_PASS = pass;
		persisterContextList.add(context);
	}


	public static String getDBAddress() {
		return DB_ADDR;
	}

	public static String getDBUser() {
		return DB_USER;
	}

	public static String getDBPass() {
		return DB_PASS;
	}


	public String getSelectDBAddress() {
		return SELECT_DB_ADDR;
	}

	public String getSelectDBUser() {
		return SELECT_DB_USER;
	}

	public String getSelectDBPass() {
		return SELECT_DB_PASS;
	}

}