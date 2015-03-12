package gyroorm.persist;

import java.util.*;

import gyroorm.model.*;


public interface DataPersisterInterface {

	/**
	 * 指定されたModelにそったテーブルを作成する
	 */
	public int migrate(DefaultBaseModel model) throws DataPersisterException;

	public boolean existTable(DefaultBaseModel model) throws DataPersisterException;

	public int merge(DefaultBaseModel model) throws DataPersisterException;

	public int create(DefaultBaseModel model) throws DataPersisterException;

	public int update(DefaultBaseModel model) throws DataPersisterException;

	public int delete(DefaultBaseModel model) throws DataPersisterException;

	public List<BaseModel> find(DefaultBaseModel model) throws DataPersisterException;

	public int findCount(DefaultBaseModel model) throws DataPersisterException;
	
	
}