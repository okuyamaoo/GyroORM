package gyroorm.model;

import java.util.*;

import gyroorm.*;
import gyroorm.persist.*;

public interface BaseModelInterface {

	/**
	 * テーブル作成
	 */
	public int migrate() throws GyroORMException;

	/**
	 * テーブルの存在確認.
	 */
	public boolean existTable() throws GyroORMException;

	/**
	 * 保存メソッド
	 */
	public int save() throws GyroORMException;

	/**
	 * 再保存メソッド
	 */
	public int update() throws GyroORMException;


	/**
	 * 削除メソッド
	 */
	public int delete() throws GyroORMException;

	/**
	 * 取得メソッド
	 */
	public List<BaseModel> find() throws GyroORMException;

	/**
	 * 取得メソッド
	 * 一度findを利用すると条件等は全て消える仕様。
	 * そのため連続実行には再設定が必要
	 */
	public List<BaseModel> find(boolean clearParameter) throws GyroORMException;


	/**
	 * 取得メソッド
	 * カウントのみ
	 * 呼出し後も条件は消えない
	 */
	public int findCount() throws GyroORMException;



	/**
	 * select句を特別指定したい場合のみ指定
	 * 指定しない場合の取得カラムを統べてのカラム
	 */
	public BaseModel select(String query);

	/**
	 * select文でのWhere句を特別指定したい場合のみ指定
	 * 指定しない場合はWhere句なし
	 */
	public BaseModel where(String query);

	/**
	 * select文でのWhere句を特別指定したい場合のみ指定
	 * 指定しない場合はWhere句なし
	 */
	public BaseModel where(String query, Object[] params);

	/**
	 * select文でのlimit句を特別指定したい場合のみ指定
	 * 指定しない場合は全件
	 */
	public BaseModel limit(int size);


	/**
	 * select文でのoffset句を特別指定したい場合のみ指定
	 * 指定しない場合は全件
	 */
	public BaseModel offset(int size);


	/**
	 * select文でのorder by句を特別指定したい場合のみ指定
	 * 指定しない場合は指定なし
	 */
	public BaseModel orderBy(String query);

	/**
	 * select文でのgroup by句を特別指定したい場合のみ指定
	 * 指定しない場合は指定なし
	 */
	public BaseModel groupBy(String query);

	/**
	 * select文でのHaving句を特別指定したい場合のみ指定
	 * 指定しない場合はHaving句なし
	 */
	public BaseModel having(String query);

	/**
	 * select文でのHaving句を特別指定したい場合のみ指定
	 * 指定しない場合はHaving句なし
	 * Havin句にパラメータを指定する
	 */
	public BaseModel having(String query, Object[] params);


	/**
	 * 自動プライマリーキー取得 
	 */
	public long getId();

	/**
	 * 自動プライマリーキー取得 
	 */
	public void setId(long id);

	/**
	 * Update時に明示的にNullでアップデートしたい場合にこのメソッドに対象カラム名を渡しておく.<br>
	 * このメソッドを呼ばずにModelをnewしてupdate()メソッドを呼び出すとnullがセットされている<br>
	 * フィールドは更新対象から外される
	 */
	public void setNullUpdate(String nullFieldName);

	public boolean isNullUpdate(String fieldName);

	/**
	 *
	 */
	public void setTransactionFolder(TransactionFolder folder);

	/**
	 *
	 */
	public TransactionFolder getTransactionFolder();

	/**
	 *
	 */
	public boolean hasTransactionFolder();
}