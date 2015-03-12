package gyroorm.model;

import java.util.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.logging.*;

import gyroorm.*;
import gyroorm.persist.*;
import gyroorm.util.*;
import gyroorm.model.annotation.*;

/** 
 * 継承モデルにベース処理を提供する.<br>
 * Save処理に特化した処理を提供
 *
 *
 */
public abstract class DefaultBaseModel implements BaseModelInterface {

	Logger logger = LoggerFactory.getLogger();

	int mode = 1; // 1=RDB, 2=NOSQL

	double id = -1;

	Map nullFieldMap = null;

	private DataPersisterInterface persister = null;

	private TransactionFolder transactionFolder = null;

	private QueryFolder queryFolder = null;

	ModelInfo modelInfo = null;
	Field[] modelFieldList = null;

	BaseModel targetModel = null;

	DefaultBaseModel() { 
	}

	public void init(BaseModel targetModel) {
		this.targetModel = targetModel;
		Class targetMpdeClass = this.targetModel.getClass();

		modelInfo = new ModelInfo();

		// クラスのアノテーション分析
		Annotation[] classAnnotations = targetMpdeClass.getDeclaredAnnotations();

		if (classAnnotations != null && classAnnotations.length > 0) {
			// 設定出来るアノテーションはテーブル名、参照用DBの利用有無
			for (Annotation as : classAnnotations) {
				if (as instanceof Table) {

					// テーブルアノテーション
					Table tableAnnotation = (Table)as;
					String annotationTableName = tableAnnotation.tableName();
					boolean useSelectResource = tableAnnotation.useSelectResource();


					if (annotationTableName != null && !annotationTableName.trim().equals("")) {

						// モデル名をアノテーション設定から取得
						modelInfo.modelName = annotationTableName;
					} else {
						// モデル名をクラス名から取得
						modelInfo.modelName = targetMpdeClass.getSimpleName();
					}

					// 参照DBの利用設定アノテーション
					if (useSelectResource) {
						modelInfo.useSelectResource = true;
					}
				} else {
					System.out.println("Not supported Annotation");
				}
			}

		} else {

			// モデル名をクラス名から取得		
			modelInfo.modelName = targetMpdeClass.getSimpleName();
		}

		// クラス名のフルネームを取得
		modelInfo.modelClassName = targetMpdeClass.getName();

		// モデルのフィールド名を取得
		Field[] fieldList = targetMpdeClass.getDeclaredFields();

		List<Field> maxFieldList = new ArrayList();
		List<String> maxFieldNameList = new ArrayList();
		List<String> maxFieldTypeList = new ArrayList();

		List<Field> maxRelationFieldList = new ArrayList();

		// フィールドデータを分析
		for (int idx = 0; idx < fieldList.length; idx++) {
        try {
						Annotation[] annotations = fieldList[idx].getDeclaredAnnotations();
						// 通常フィールド
						if (annotations == null || annotations.length < 1) {
							maxFieldList.add(fieldList[idx]);
							maxFieldNameList.add(fieldList[idx].getName());
							maxFieldTypeList.add(fieldList[idx].getType().getName());
						} else {
							// アノテーション付きフィールド
							for (Annotation as : annotations) {
								if (as instanceof RelationData) {
									// リレーションアノテーション付きフィールド
									maxRelationFieldList.add(fieldList[idx]);
								} else {
									System.out.println("Not supported Annotation");
								}
							}
						}

        } catch (Exception e) {
						e.printStackTrace();
        }
    }

		modelInfo.fieldList = (Field[])maxFieldList.toArray(new Field[0]);
 		modelInfo.fieldNameList = (String[])maxFieldNameList.toArray(new String[0]);
 		modelInfo.fieldTypeList = (String[])maxFieldTypeList.toArray(new String[0]);
 		modelInfo.relationFieldList = (Field[])maxRelationFieldList.toArray(new Field[0]);

		modelInfo.primaryKeyName = "id";

		// ここでモデルのアノテーションかもしくはグローバル設定からPersisterの種類を判定
		/* TODO:アノテーションからの実装追加
		if (アノテーション != null && アノテーション.PERSIST_MODE == PersisterContext.GLOBAL_PERSIST_MODE_MONGO) {
			
		} else {*/
		// デフォルトのpersister取得
		persister = PersisterFactory.getDefaultPersister();
		//}
		
/*
    for (Field field : targetModel.getClass().getDeclaredFields()) {
        try {
            field.setAccessible(true);
            sb.append(field.getName() + " = " + field.get(this.targetModel) + "\n");
        } catch (IllegalAccessException e) {
            sb.append(field.getName() + " = " + "access denied\n");
        }
    }
		System.out.println(sb.toString());*/
		
	}


	/**
	 * テーブル作成
	 */
	public int migrate() throws GyroORMException {
		int ret = 0;
		logger.fine("migrate - start");
		try {
			ret = persister.migrate(this);
		} catch (DataPersisterException dpe) {
			throw new GyroORMException(dpe);
		}
		logger.fine("migrate - end");

		return ret;
	}

	/**
	 * テーブルの存在確認.
	 */
	public boolean existTable() throws GyroORMException {
		boolean ret = false;
		logger.fine("existTable - start");
		try {
			ret = persister.existTable(this);
		} catch (DataPersisterException dpe) {
			throw new GyroORMException(dpe);
		}
		logger.fine("existTable - end");

		return ret;
	}



	/**
	 * 保存メソッド
	 */
	public int save() throws GyroORMException {
		int ret = 0;
		logger.fine("save - start");
		try {
			if (id == -1) {
				ret = persister.create(this);
			} else {
				ret = persister.update(this);
			}
		} catch (DataPersisterException dpe) {
			throw new GyroORMException(dpe);
		}
		logger.fine("save - end");
		return ret;
	}


	/**
	 * 更新メソッド
	 */
	public int update() throws GyroORMException {
		int ret = 0;
		logger.fine("update - start");
		try {

			ret = persister.update(this);
		} catch (DataPersisterException dpe) {
			throw new GyroORMException(dpe);
		}
		logger.fine("update - end");

		return ret;
	}
	/**
	 * 削除メソッド
	 */
	public int delete() throws GyroORMException {
		int ret = 0;
		logger.fine("delete - start");
		try {

			ret = persister.delete(this);
		} catch (DataPersisterException dpe) {
			throw new GyroORMException(dpe);
		}
		logger.fine("delete - end");
		return ret;	
	}

	/**
	 * 取得メソッド
	 */
	public List<BaseModel> find() throws GyroORMException {
		
		return this.find(false);
	}


	/**
	 * 取得メソッド
	 * 一度findを利用すると条件等は全て消える仕様。
	 * そのため連続実行には再設定が必要
	 */
	public List<BaseModel> find(boolean clearParameter) throws GyroORMException {
		if (queryFolder == null) queryFolder = new QueryFolder();
		queryFolder.addFromTable(modelInfo.modelName);

		List<BaseModel> ret = null;
		logger.fine("find - start");
		try {

				ret = persister.find(this);
				if (clearParameter) queryFolder = null;
		} catch (DataPersisterException dpe) {
			throw new GyroORMException(dpe);
		}
		logger.fine("find - end");

		return ret;
	}

	/**
	 * 取得メソッド.
	 * カウントのみ
	 */
	public int findCount() throws GyroORMException {
		int ret = 0;
		logger.fine("findCount - start");

		boolean nullFolder = false;

		if (queryFolder == null) {
			nullFolder = true;
			queryFolder = new QueryFolder();
			queryFolder.addFromTable(modelInfo.modelName);
		}

		try {

				ret = persister.findCount(this);
		} catch (DataPersisterException dpe) {
			throw new GyroORMException(dpe);
		} finally {
			if (nullFolder) {
				queryFolder = null;
			}
		}
		logger.fine("findCount - end");

		return ret;
	}



	/**
	 * select句を特別指定したい場合のみ指定
	 * 指定しない場合の取得カラムを統べてのカラム
	 */
	public BaseModel select(String query) {
		if (queryFolder == null) queryFolder = new QueryFolder();

		queryFolder.addSelect(query);
		return targetModel;
	}

	/**
	 * select文でのWhere句を特別指定したい場合のみ指定
	 * 指定しない場合はWhere句なし
	 */
	public BaseModel where(String query) {
		return where(query, null);
		
	}

	/**
	 * select文でのWhere句を特別指定したい場合のみ指定
	 * 指定しない場合はWhere句なし
	 */
	public BaseModel where(String query, Object[] params) {
		if (queryFolder == null) queryFolder = new QueryFolder();

		queryFolder.addWhere(query, params);
		return targetModel;
	}

	/**
	 * select文でのlimit句を特別指定したい場合のみ指定
	 * 指定しない場合は全件
	 */
	public BaseModel limit(int size) {
		if (queryFolder == null) queryFolder = new QueryFolder();

		queryFolder.setLimit(size);
		return targetModel;
	}


	/**
	 * select文でのoffset句を特別指定したい場合のみ指定
	 * 指定しない場合は全件
	 */
	public BaseModel offset(int size) {
		if (queryFolder == null) queryFolder = new QueryFolder();

		queryFolder.setOffset(size);
		return targetModel;
	}


	/**
	 * select文でのorder by句を特別指定したい場合のみ指定
	 * 指定しない場合は指定なし
	 */
	public BaseModel orderBy(String query) {
		if (queryFolder == null) queryFolder = new QueryFolder();

		queryFolder.addOrderBy(query);
		return targetModel;
	}

	/**
	 * select文でのgroup by句を特別指定したい場合のみ指定
	 * 指定しない場合は指定なし
	 */
	public BaseModel groupBy(String query) {
		if (queryFolder == null) queryFolder = new QueryFolder();

		queryFolder.addGroupBy(query);
		return targetModel;
	}

	/**
	 * select文でのHaving句を特別指定したい場合のみ指定
	 * 指定しない場合はHaving句なし
	 */
	public BaseModel having(String query) {
		return having(query, null);
	}

	/**
	 * select文でのHaving句を特別指定したい場合のみ指定
	 * 指定しない場合はHaving句なし
	 * Havin句にパラメータを指定する
	 */
	public BaseModel having(String query, Object[] params) {
		if (queryFolder == null) queryFolder = new QueryFolder();

		queryFolder.addHaving(query, params);
		return targetModel;

	}

	public BaseModel newQuery() {
		queryFolder = new QueryFolder();
		return targetModel;
	}

	public QueryFolder getQueryFolder() {
		return queryFolder;
	}

	public ModelInfo getModelInfo() {
		return this.modelInfo;
	}


	public BaseModel getTargetModel() {
		return this.targetModel;
	}

	public void setId(double idData) {
		id = idData;
	}

	public double getId() {
		return id;
	}


	/**
	 * Update時に明示的にNullでアップデートしたい場合にこのメソッドに対象カラム名を渡しておく.<br>
	 * このメソッドを呼ばずにModelをnewしてupdate()メソッドを呼び出すとnullがセットされている<br>
	 * フィールドは更新対象から外される
	 */
	public void setNullUpdate(String nullFieldName) {
		if (nullFieldMap == null) nullFieldMap = new HashMap();

		nullFieldMap.put(nullFieldName, null);
	}

	public boolean isNullUpdate(String fieldName) {
		if (nullFieldMap == null) return false;

		return nullFieldMap.containsKey(fieldName);
	}


	public Object getFieldData(String fieldName) throws IllegalAccessException {
		Object ret = null;
		int idx = 0;

		for (Field field : modelInfo.fieldList) {
			try {
				field.setAccessible(true);
				if (modelInfo.fieldNameList[idx].equals(fieldName)) {
					ret = field.get(targetModel);
				}
			} catch (IllegalAccessException e) {
				throw new IllegalAccessException(field.getName() + " = " + "access denied\n");
			}
			idx++;
		}
		return ret;
	}


	/**
	 *
	 */
	public void setTransactionFolder(TransactionFolder folder) {
		transactionFolder = folder;
	}

	/**
	 *
	 */
	public TransactionFolder getTransactionFolder() {
		return transactionFolder;
	}

	/**
	 *
	 */
	public boolean hasTransactionFolder() {
		if (transactionFolder != null) return true;
		return false;
	}

}
