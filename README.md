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
　  

