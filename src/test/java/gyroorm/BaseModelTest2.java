package gyroorm;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.*;

import gyroorm.*;
import gyroorm.model.*;
import gyroorm.testmodel.*;
/**
 * Unit test for simple App.
 */
public class BaseModelTest2 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public BaseModelTest2( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
			try {
				GyroORMConfig.setPersisterConfig(GyroORMConfig.PERSISTER_MODE_RDMBS, "com.mysql.jdbc.Driver", "jdbc:mysql://localhost/test", "root", "root");
				GyroORMConfig.addSearchPersisterConfig("jdbc:mysql://localhost/test", "root", "root");
				GyroORMConfig.addSearchPersisterConfig("jdbc:mysql://localhost/test", "root", "root");
			} catch (Exception e) {
				e.printStackTrace();
			}
        return new TestSuite( BaseModelTest2.class );
    }

		private boolean tableMigrate() {
			try {
				TestTable testTable = new TestTable();
				if (!testTable.existTable()) {
					testTable.migrate();
				}

				TestChildData1 testChildData1 = new TestChildData1();
				if (!testChildData1.existTable()) {
					testChildData1.migrate();
				}

				TestChildData2 testChildData2 = new TestChildData2();
				if (!testChildData2.existTable()) {
					testChildData2.migrate();
				}

				TestChildData3 testChildData3 = new TestChildData3();
				if (!testChildData3.existTable()) {
					testChildData3.migrate();
				}

				UserDetailTable userDetailTable = new UserDetailTable();
				if (!userDetailTable.existTable()) {
					userDetailTable.migrate();
				}

				return true;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		public void testInsert() {
			try {
				tableMigrate();

				// 新規レコード作成
				UserDetailTable userDetailTable = new UserDetailTable();

				userDetailTable.name = "name1";
				userDetailTable.age = 1;
				userDetailTable.address = "address1";
				userDetailTable.tel = "010-2222-3333";
				userDetailTable.zip = "888-9999";
				userDetailTable.save();

				assertTrue( true );
			} catch (Exception e) {
				e.printStackTrace();
				assertTrue( false );
			}
		}

		
		/**
     * Rigourous Test :-)
     */
    public void testFind()
    {
			try {
				tableMigrate();

				// リレーション有りのデータを保存
				TestTable testTable = new TestTable();
				testTable.name ="AAAAAAAAAA1";
				testTable.age = 99;
					
				TestChildData1 testChildData1 = new TestChildData1();
				testChildData1.age = 99;
				testChildData1.hobby = "ラジコン";

				TestChildData2 testChildData2 = new TestChildData2();
				testChildData2.age = 99;
				testChildData2.name = "AAAAAAAAAA1";
				testChildData2.address = "東京都";

				TestChildData2 testChildData21 = new TestChildData2();
				testChildData21.age = 99;
				testChildData21.name = "AAAAAAAAAA1";
				testChildData21.address = "東京都港区";

				List<TestChildData1> testChildData1List = new ArrayList();
				List<TestChildData2> testChildData2List = new ArrayList();

				testChildData1List.add(testChildData1);
				testChildData2List.add(testChildData2);
				testChildData2List.add(testChildData21);
				testTable.child1List = testChildData1List; // リレーションデータを作るために関係データをモデルに詰める
				testTable.child2List = testChildData2List; // リレーションデータを作るために関係データをモデルに詰める


				TestTable testTable2 = new TestTable();
				testTable2.name ="AAAAAAAAAA2";
				testTable2.age = 100;
					
				TestChildData1 testChildData12 = new TestChildData1();
				testChildData12.age = 100;
				testChildData12.hobby = "レゴ";

				TestChildData2 testChildData22 = new TestChildData2();
				testChildData22.age = 100;
				testChildData22.name = "AAAAAAAAAA2";
				testChildData22.address = "大阪府";

				TestChildData2 testChildData212 = new TestChildData2();
				testChildData212.age = 100;
				testChildData212.name = "AAAAAAAAAA2";
				testChildData212.address = "大阪府大阪市";

				List<TestChildData1> testChildData1List2 = new ArrayList();
				List<TestChildData2> testChildData2List2 = new ArrayList();

				testChildData1List2.add(testChildData12);
				testChildData2List2.add(testChildData22);
				testChildData2List2.add(testChildData212);
				testTable2.child1List = testChildData1List2; // リレーションデータを作るために関係データをモデルに詰める
				testTable2.child2List = testChildData2List2; // リレーションデータを作るために関係データをモデルに詰める


				// リレーションデータを一度に保存する為にはトランザクションで隔離した状態で全てのデータの
				// 登録成功をもってcommitとする必要があるためトランザクションを利用

				TransactionFolder folder = TransactionFolder.getInstance(); // トランザクション用のFolderの利用開始

				testTable.setTransactionFolder(folder); // Folderを適応したいModelに登録
				testTable2.setTransactionFolder(folder); // Folderを適応したいModelに登録

				testTable.save();  // リレーションデータを含めて保存
				testTable2.save();  // リレーションデータを含めて保存

				folder.commitTransaction(); // コミット
				folder.endTransaction(); // トランザクション終了

				
				// データ件数取得
				TestTable selectTestTable = new TestTable();
				int retSize = selectTestTable.findCount();
				System.out.println("テーブル名[TestTable]のデータ件数取得 = " + retSize + " 件");
				System.out.println("");

				// データの先頭2件取得
				List<BaseModel> list = selectTestTable.limit(2).find();
				System.out.println("テーブル名[TestTable]の先頭2件取得");
				System.out.println("-------------------");
				for (int idx = 0; idx < list.size(); idx++) {
					TestTable data = (TestTable)list.get(idx);
					System.out.println(data);
					System.out.println("-------------------");
				}
				System.out.println("");


				// ageカラムが99のデータを取得
				List<BaseModel> list2 = selectTestTable.newQuery().where("age = ?", 99).find();
				System.out.println("テーブル名[TestTable]のageカラムが99のデータを取得");
				System.out.println("-------------------");
				for (int idx = 0; idx < list2.size(); idx++) {
					TestTable data = (TestTable)list2.get(idx);
					System.out.println(data);
					System.out.println("-------------------");
				}


			} catch (Exception e) {
				e.printStackTrace();
			}
			assertTrue( true );
    }
}

