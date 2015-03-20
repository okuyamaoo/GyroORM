# GyroORM
GyroORM is O/RMapper  
　   
GyroORMはAPIベースで操作を行うJavaで実装されたO/Rマッパーです。  
モデルクラスにGyroORMの規定クラスを継承するだけで簡単にDBへの  
アクセスを行うことが出来るようにしました。  
　  
　  
###簡単な実装例を交えて使い方を説明します。  
　  
####●モデル定義
DB処理を行いたいモデルを定義し　gyroorm.model.BaseModel　を**継承**  
**User.java**
````
import gyroorm.model.*;
import gyroorm.model.annotation.*;

public class User  extends BaseModel {

	// 会員ID
	public long userid = 0L;
	
	// ユーザ名
	public String name = null;

	// 郵便番号
	public String zip = null;

	// 住所
	public String address = null;
}
````
これでモデルの準備は完了しました。  
　  
　  
####●モデルクラスの保存  
次に先ほどのモデルクラスを保存するテストコードを定義します。  

**Test.java**  
````
import java.util.*;
import gyroorm.*;
import gyroorm.model.*;

public class Test {

	public static void main(String[] args) {
		try {
			// DBへの接続情報を設定
			// 1度だけ実行する
			// 先頭からドライバー名、接続文字列、ユーザ、パスワード
			// 先頭のドライバー名は省略できます
			GyroORMConfig.setPersisterConfig("com.mysql.jdbc.Driver", 
														"jdbc:mysql://localhost/test", 
															"testuser", 
																"testpasswd");

			Test test = new Test();
			test.executeCreate(); // 処理実行
		}  catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} catch (GyroORMException goe) {
			goe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 作成したモデルクラスを利用しデータを保存します。
	public void executeCreate() throws Exception {
			// モデルクラスをインスタンス化
			User user = new User();

			// モデルにマッピングされたテーブルが存在しない場合作成
			if (!user.existTable()) {      // <-テーブルの存在確認メソッド
				user.migrate();             // <-テーブル作成メソッド
			}

			// モデルへ保存したいデータを設定
			user.userid = 1L;
			user.name = "user name";
			user.zip = "999-8888";			
			user.address = "大阪府大阪市XX町３１ー５";

			// データ保存
			user.save();
			
			// 終了
	}
}
````
　  
　  
上記のサンプルではまずDBの接続情報を設定します。  
現在対応しているDBはMySQLのみになります。  
````
// DBへの接続情報を設定
// 1度だけ実行する
// 先頭からドライバー名、接続文字列、ユーザ、パスワード
// 先頭のドライバー名は省略できます
GyroORMConfig.setPersisterConfig("com.mysql.jdbc.Driver", 
										"jdbc:mysql://localhost/test", 
											"testuser", 
												"testpasswd");
````
　  
次にモデルを保存するテーブルの存在確認を  
existTableメソッドで行い、migrateメソッドによりテーブルを作成しています。   
GyroORMは内部的に主キーをシーケンス値を用いて自動的作成します。  
````
// モデルにマッピングされたテーブルが存在しない場合作成
if (!user.existTable()) {      // <-テーブルの存在確認メソッド
	user.migrate();            // <-テーブル作成メソッド
}
````
　   
その後、値を設定しモデルをDBへ保存するメソッドを呼び出しています。  
````
// データ保存
user.save();
````
上記のようにDBを意識せずモデルの操作で処理を完了しています。  
　  
　  
　  
####●モデルの件数と全データを取得
次に、DBからモデルの件数と全データを取得するサンプルです。  
先ほどの**Test.java**に以下のメソッドを追加します。  
````
// モデルクラスを取得します
public void executeFind() throws Exception {
		// モデルクラスをインスタンス化
		User user = new User();

		// データ件数を取得
		int dataCount = user.findCount(); // <-件数取得メソッド
		System.out.println("テーブル名[User]のデータ件数 = " + dataCount + " 件");

		// データを全件取得
		List<BaseModel> list = user.find(); // <-データリスト取得メソッド

		// データ表示
		for (int idx = 0; idx < list.size(); idx++) {
			User resultUser = (User)list.get(idx); // モデルクラスへキャスト

			// モデルの属性値表示
			System.out.println(resultUser.userid);
			System.out.println(resultUser.name);
			System.out.println(resultUser.zip);
			System.out.println(resultUser.address);
			System.out.println("-------------------");
		}
}
````
　  
上記のサンプルではまずUserモデルの件数を取得しています。
findCountメソッドを利用することでuserモデルが格納されているテーブルの件数を取得可能です。  

````
// データ件数を取得
int dataCount = user.findCount(); // <-件数取得メソッド
````

　  
　  
次にUserモデルのデータを全件取得しています。
````
// データを全件取得
List<BaseModel> list = user.find(); // <-データリスト取得メソッド
````
findメソッドによりUserモデルが格納されているテーブルの全データを取得可能です。  
取得されたデータはListに格納されジェネリクスはBaseModelとなります。  
　　  
　　  
表示する場合は以下のようになります。  
````
// データ表示
for (int idx = 0; idx < list.size(); idx++) {
	User resultUser = (User)list.get(idx); // モデルクラスへクラスキャスト

	// モデルの属性値表示
	System.out.println(resultUser.userid);
	System.out.println(resultUser.name);
	System.out.println(resultUser.zip);
	System.out.println(resultUser.address);
	System.out.println("-------------------");
}
````
クラスキャストを行っている部分に注意してください。  
　  
　  
　  
####●条件指定を行ってモデルを取得する
次に、条件指定を行ったうえでDBからモデルを取得するサンプルです。  
先ほどの**Test.java**に以下のメソッドを追加します。  
````
// モデルクラスを取得します
public void executeSearch() throws Exception {
		// モデルクラスをインスタンス化
		User user = new User();

		// 条件を設定してデータ取得
		List<BaseModel> list2 = selectTestTable.where("zip like ?", "999%").find(); // <- 属性値のzipが999から始まるデータを取得
		System.out.println("テーブル名[User]のzip属性が999で始まるデータを取得");
		for (int idx = 0; idx < list2.size(); idx++) {

			User resultUser = (User)list2.get(idx); // モデルクラスへキャスト

			// モデルの属性値表示
			System.out.println(resultUser.userid);
			System.out.println(resultUser.name);
			System.out.println(resultUser.zip);
			System.out.println(resultUser.address);
			System.out.println("-------------------");
		}
		// 条件を変更してデータを取得
		List<BaseModel> list3 = selectTestTable.newQuery()  // <-古いクエリ条件を削除
	                                                              .where("zip like ?", "999%")         // <- 属性値のzipが999から始まるデータを取得
	                                                              .where("address = ?", "大阪府大阪市XX町３１ー５")  // <- 属性値の住所がマッチするデータを取得
	                                                              .find(); 

		System.out.println("テーブル名[User]のAddress属性が　大阪府大阪市XX町３１ー５　のデータを取得");
		for (int idx = 0; idx < list3.size(); idx++) {
			User resultUser = (User)list3.get(idx); // モデルクラスへキャスト

			// モデルの属性値表示
			System.out.println(resultUser.userid);
			System.out.println(resultUser.name);
			System.out.println(resultUser.zip);
			System.out.println(resultUser.address);
			System.out.println("-------------------");
		}
		// 終了
}
````
　  
上記サンプルでは検索条件としてzip属性に部分一致検索であるLIKE検索を行い  
パラメータとして　999%　を渡しています。  
一般的なプリペアードステートメントと同様の表記法となります。  
````
// 条件を設定してデータ取得
List<BaseModel> list2 = selectTestTable.where("zip like ?", "999%").find(); // <- 属性値のzipが999から始まるデータを取得
````
　  
　  
複数の条件を設定する場合は条件分whereメソッドを呼び出します。
````
// 条件を変更してデータを取得
List<BaseModel> list3 = selectTestTable.newQuery()  // <-古いクエリ条件を削除
                                              .where("zip like ?", "999%")         // <- 属性値のzipが999から始まるデータを取得
                                              .where("address = ?", "大阪府大阪市XX町３１ー５")  // <- 属性値の住所がマッチするデータを取得
                                              .find(); 
````
また、1つのwhereメソッド内に複数のパラメータを含めたい場合(in句等の場合)はパラメータをObjectの配列にして渡します。  
以下のようになります。
````
String[] parameters = {"999-0001", "999-0002", "999-0003"};
List<BaseModel> list3 = selectTestTable.newQuery()  // <-古いクエリ条件を削除
                                              .where("zip in (?, ?, ?)", parameters)         // 複数のパラメータを指定
                                              .where("address = ?", "大阪府大阪市XX町３１ー５")
                                              .find();
````
　  
　  
　  
####●データを更新する(1件づつ更新します)
一度取得したデータは更新することが可能です。  
以下は更新処理サンプルになります。  
````
// モデルクラスを更新します
public void executeSimpleUpdate() throws Exception {
		// モデルクラスをインスタンス化
		User user = new User();

		// 一度データを取得します。条件はaddressの前方一致かつzip属性値がnullのデータ
		List<BaseModel> list = user.where("address like ?", "兵庫県神戸市%").where("zip is null").find();

		// 取得したモデルの値を変更し1件づつ更新
		for (int idx = 0; idx < list.size(); idx++) {
			User updateUser = (User)list.get(idx); // モデルクラスへキャスト
			updateUser.zip = "666-3333"; // モデルのzip属性値へ値を代入
			updateUser.save(); // 更新実行
		}
}
````
　  
まず条件指定にて更新対象のモデルを取得します。  
````
// 一度データを取得します。条件はaddressの前方一致かつzip属性値がnullのデータ
List<BaseModel> list = user.where("address like ?", "兵庫県神戸市%").where("zip is null").find();
````
取得したモデルのデータを変更し更新実行
````
// 取得したモデルの値を変更し1件づつ更新
for (int idx = 0; idx < list.size(); idx++) {
	User updateUser = (User)list.get(idx); // モデルクラスへキャスト
	updateUser.zip = "666-3333"; // モデルのzip属性値へ値を代入
	updateUser.save(); // 更新実行
}
````
取得したモデルに対してsaveメソッドを呼び出すことで更新がDBに反映されます。
　  
　  
　  
####●データを更新する(条件に適応するデータを全て更新します)
条件指定の範囲のデータをまとめて更新することが可能です  
以下は更新処理サンプルになります。  
````
// モデルクラスを更新します
public void executeFilterUpdate() throws Exception {
		// モデルクラスをインスタンス化
		User user = new User();
		
		// モデルクラスへ更新したい属性値を代入
		user.zip = "666-3333";

		// 更新処理を実行
		int updateCount = user.where("address like ?", "兵庫県神戸市%").where("zip is null").update();

		// 更新件数
		System.out.println("Update 件数 =" + updateCount);
}
````
　  
まず更新したい属性値をモデルクラスにセットします。  
````
// モデルクラスへ更新したい属性値を代入
user.zip = "666-3333";
````
更新したいデータを条件指定し、updateメソッドにて更新実行
````
// 更新処理を実行
int updateCount = user.where("address like ?", "兵庫県神戸市%").where("zip is null").update();
````
この処理の場合はまとめて更新処理を行うことが可能です  
　  
　  
　  
####●データを削除する(1件づつ削除します)
一度取得したデータは削除することが可能です。  
以下は削除処理サンプルになります。  
````
// モデルクラスを削除します
public void executeSimpleDelete() throws Exception {
		// モデルクラスをインスタンス化
		User user = new User();

		// 一度データを取得します。条件はaddressの前方一致かつzip属性値がnullのデータ
		List<BaseModel> list = user.where("address like ?", "兵庫県神戸市%").where("zip is null").find();

		// 取得したモデルを1件づつ削除します
		for (int idx = 0; idx < list.size(); idx++) {
			User updateUser = (User)list.get(idx); // モデルクラスへキャスト
			updateUser.delete(); // 削除実行
		}
}
````
　  
まず条件指定にて削除対象のモデルを取得します。  
````
// 一度データを取得します。条件はaddressの前方一致かつzip属性値がnullのデータ
List<BaseModel> list = user.where("address like ?", "兵庫県神戸市%").where("zip is null").find();
````
取得したモデルを1件づつ削除します。
````
// 取得したモデルを1件づつ削除します
for (int idx = 0; idx < list.size(); idx++) {
	User updateUser = (User)list.get(idx); // モデルクラスへキャスト
	updateUser.delete(); // 削除実行
}
````
取得したモデルに対してdeleteメソッドを呼び出すことで更新がDBに反映されます。
　  
　  
　  
####●データを削除する(条件に適応するデータを全て削除します)
条件指定の範囲のデータをまとめて削除することが可能です  
以下は削除処理サンプルになります。  
````
// モデルクラスを削除します
public void executeFilterDelete() throws Exception {
		// モデルクラスをインスタンス化
		User user = new User();
		
		// 条件を指定し対象モデルデータの削除処理を実行
		int deleteCount = user.where("address like ?", "兵庫県神戸市%").where("zip is null").delete();

		// 削除件数
		System.out.println("Delete 件数 =" + deleteCount);
}
````
　  
削除したいデータを条件指定し、deleteメソッドにて削除実行
````
// 削除処理を実行
int deleteCount = user.where("address like ?", "兵庫県神戸市%").where("zip is null").delete();
````
この処理の場合はまとめて削除処理を行うことが可能です  
　  
　  
　  
####●トランザクションを利用する
トランザクションにより処理範囲を隔離することが可能です。
ここまでのサンプルでは処理メソッド実行毎にトランザクションを実行するため、  
処理成功時は必ずcommitとなり、失敗時は自動的にrollbackとなります。  
　  
以下のサンプルはトランザクション適応範囲を広げる方法になります。  
更新処理をサンプルとします。  
````
// モデルクラスを更新し全て成功した場合commitし、一部失敗した場合はrollbackとします。  
public void executeTransactionUpdate() throws Exception {
		TransactionFolder folder = 	null;
		try {
			// Transaction制御オフジェクトを取得します。
			folder = TransactionFolder.getInstance();

			// モデルクラスをインスタンス化
			User user = new User();

			// 同一トランザクション内で実行したい場合モデルオブジェクトにFolderをセット
			user.setTransactionFolder(folder);

			// データを取得します。
			List<BaseModel> list = user.where("address like ?", "兵庫県神戸市%").where("zip is null").find();

			// 取得されたデータは全て取得時に利用されたObjectがおつFolderのトランザクション範囲化になります。
			// 1件づつ更新
			for (int idx = 0; idx < list.size(); idx++) {
				User updateUser = (User)list.get(idx); // モデルクラスへキャスト
				updateUser.zip = "666-3333"; // モデルのzip属性値へ値を代入
				updateUser.save(); // 更新実行
			}

			// 正常なのでcommit
			folder.commitTransaction(); 		
	} catch (Exception e) {
		// 異常ケースの場合rollback
		if (folder != null) folder.rollbackTransaction(); 
	} finally {
		//  トランザクション終了
		if (folder != null) folder.endTransaction();
	}
}
````
　  
トランザクションを制御するためにTransactionFolderを利用します。  
````
TransactionFolder folder = 	null;
try {
	// Transaction制御オフジェクトを取得します。
	folder = TransactionFolder.getInstance();
````
　  
取得したFolderインスタンスをトランザクションを適応したいモデルへセットします。
````
// 同一トランザクション内で実行したい場合モデルオブジェクトにFolderをセット
user.setTransactionFolder(folder);
````
取得したFolderインスタンスを利用している範囲は同一のトランザクションとなります。  
　  
更新処理実行後、commit or rollbackを行い、最後にトランザクションを終了します。
````
// 正常なのでcommit
folder.commitTransaction(); 		
````
````
// 異常ケースの場合rollback
if (folder != null) folder.rollbackTransaction(); 
````
````
//  トランザクション終了
if (folder != null) folder.endTransaction();
````
　  
　  
新規データ登録時のトランザクション適応方法は以下のようになります。  
````
// Transaction制御オフジェクトを取得します。
folder = TransactionFolder.getInstance();

// モデルクラスをインスタンス化
User userHoge = new User();
User userFuga = new User();

// 同一トランザクション内で実行したい場合モデルオブジェクトにFolderをセット
userHoge.setTransactionFolder(folder);
userFuga.setTransactionFolder(folder);

userHoge.name = "hoge";
userFuga.name = "fuga";

userHoge.save();
userFuga.save();

if  (???) {
	//　必要に応じてRollback
	folder.rollbackTransaction(); 
} else {
	//　必要に応じてコミット
	folder.commitTransaction(); 
}

//  トランザクション終了
folder.endTransaction();
````
　  
新規登録時等は取得したモデルではない場合は  
インスタンス化したモデルにFolderをセットして処理を行います。  
````
// 同一トランザクション内で実行したい場合モデルオブジェクトにFolderをセット
userHoge.setTransactionFolder(folder);
userFuga.setTransactionFolder(folder);
````