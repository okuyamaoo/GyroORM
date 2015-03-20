# GyroORM
GyroORM is O/RMapper  
 
GyroORMはAPIベースで操作を行うJavaで実装されたO/Rマッパーです。  
モデルクラスにGyroORMの規定クラスを継承するだけで簡単にDBへの  
アクセスを行うことが出来るようにしました。  
  
###まず以下に簡単な実装例を示します。  

DB処理を行いたいモデルを定義し　gyroorm.model.BaseModel　を継承
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
  
これでモデルの準備は完了したので、次にDBの接続情報を設定します。  
現在対応しているDBはMySQLのみになります。  
先ほどのモデルクラスを利用するテストコードを定義します。  
````
import java.util.*;
import gyroorm.*;
import gyroorm.model.*;

public class Test {

	public static void main(String[] args) {
		// DBへの接続情報を設定
		// 1度だけ実行する
		try {
			// 先頭からドライバー名、接続文字列、ユーザ、パスワード
			// 先頭のドライバー名は省略できます
			GyroORMConfig.setPersisterConfig("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/test", "testuser", "testpasswd");

			Test test = new Test();
			test.executeTest(); // 処理実行
		}  catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} catch (GyroORMException goe) {
			goe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void executeTest() throws Exception {
			// モデルクラスをインスタンス化
			User user = new User();

			// モデルにマッピングされたテーブルが存在しない場合作成
			if (!user.existTable()) {      // <-テーブルの存在確認メソッド
				user.migrate();             // <-テーブル作成メソッド
			}

			// データを実施
			user.userid = 1L;
			user.name = "user name";
			user.zip = "999-8888";			
			user.address = "大阪府大阪市XX町３１ー５";

			user.save();             // <-データ保存メソッド
			
			// 終了
	}
}
````






