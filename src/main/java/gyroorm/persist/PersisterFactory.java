package gyroorm.persist;

public class PersisterFactory {

	private static PersisterFactory factoryInstance = null;

	private static Object bootSync = new Object();

	private PersisterFactory() throws IllegalArgumentException {
		
		if (PersisterContext.GLOBAL_PERSIST_MODE == -1)
			throw new IllegalArgumentException("Database config not found");
		
	}
	
	
	public static DataPersisterInterface getDefaultPersister() throws UnsupportedOperationException {
		if (factoryInstance == null) {
			synchronized(bootSync) {
				if (factoryInstance == null) {
					factoryInstance = new PersisterFactory();
				}
			}
		}

		DataPersisterInterface persister = null;
		if (PersisterContext.GLOBAL_PERSIST_MODE == PersisterContext.GLOBAL_PERSIST_MODE_RDB) {
			// RDBにてPersister作成
			persister = factoryInstance.createRdbmsPersister();
		} else if (PersisterContext.GLOBAL_PERSIST_MODE == PersisterContext.GLOBAL_PERSIST_MODE_MONGO) {
			// MongoDBにてPersister作成
			persister = factoryInstance.createMongodbPersister();
		} else {
			throw new UnsupportedOperationException("Database type not support");
		}
		return persister;
	}

	private DataPersisterInterface createRdbmsPersister() {
		return new SQLPersister();
	}


	private DataPersisterInterface createMongodbPersister() {
		return null;
	}


}