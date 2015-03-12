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

    /**
     * Rigourous Test :-)
     */
    public void testFind()
    {
			try {

				// テーブル用のモデルを生成
				TestTable testTable = new TestTable();

				// データ件数取得
				int retSize = testTable.findCount();
				System.out.println("テーブル名[TestTable]のデータ件数取得 = " + retSize + " 件");
				System.out.println("");

				// データの先頭2件取得
				List<BaseModel> list = testTable.limit(2).find();
				System.out.println("テーブル名[TestTable]の先頭2件取得");
				System.out.println("-------------------");
				for (int idx = 0; idx < list.size(); idx++) {
					TestTable data = (TestTable)list.get(idx);
					System.out.println(data);
					System.out.println("-------------------");
				}
				System.out.println("");


				// ageカラムが47か48のデータを取得
				List<BaseModel> list2 = testTable.newQuery().where("age = 47 or age = 48").find();
				System.out.println("テーブル名[TestTable]のageカラムが47か48のデータを取得");
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

