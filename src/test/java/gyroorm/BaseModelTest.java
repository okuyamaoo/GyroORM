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
public class BaseModelTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public BaseModelTest( String testName )
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

			} catch (Exception e) {
				e.printStackTrace();
			}
        return new TestSuite( BaseModelTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testMigrate()
    {
			try {

				
			} catch (Exception e) {
				e.printStackTrace();
			}
			assertTrue( true );
    }

    public void testMigrate2()
    {
			try {
					TestTable testTable = new TestTable();
					System.out.println("testTable.existTable(); ======================== " + testTable.existTable());
/*					TestChildData1 t1 = new TestChildData1();
					t1.migrate();

					TestChildData2 t2 = new TestChildData2();
					t2.migrate();

					t1 = new TestChildData1();
					t1.age = 0;
					t1.hobby = "カード";
					t1.save();

					t1 = new TestChildData1();
					t1.age = 1;
					t1.hobby = "カード1";
					t1.save();

					t1 = new TestChildData1();
					t1.age = 2;
					t1.hobby = "カード2";
					t1.save();

					t1 = new TestChildData1();
					t1.age = 3;
					t1.hobby = "カード3";
					t1.save();

					for (int i = 0; i < 10; i++) {
						TestChildData2 testChildData2 = new TestChildData2();
						testChildData2.name = "0000000" + i;
						testChildData2.age = i;
						testChildData2.address = "奈良県生駒市";
						testChildData2.save();
					}


					for (int i = 0; i < 10; i++) {
						TestChildData2 testChildData2 = new TestChildData2();
						testChildData2.name = "ZZ0000000" + i;
						testChildData2.age = i;
						testChildData2.address = "大阪府大阪市";
						testChildData2.save();
					}*/
//					TestChildData3 t3 = new TestChildData3();
//					t3.migrate();
/*
					for (int i = 0; i < 10; i++) {
						TestChildData3 testChildData3 = new TestChildData3();
						testChildData3.address = "奈良県生駒市";
						testChildData3.save();
					}*/
					//TestChildData3 testChildData3 = new TestChildData3();
/*
					List<BaseModel> dataLs = testChildData3.find();
					System.out.println(dataLs);*/
					//testChildData3.address = "奈良県生駒市";
//					testChildData3.setNullUpdate("address"); // Nullを明示的に更新時に指定する場合
					/*String[] address = {"奈良県生駒市"};
					testChildData3.where("address is null").update();

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
					testTable.child1List = testChildData1List;
					testTable.child2List = testChildData2List;
					TransactionFolder folder = TransactionFolder.getInstance();
					testTable.setTransactionFolder(folder);
					testTable.save();

				folder.commitTransaction();
				folder.endTransaction();*/


				TransactionFolder folder = TransactionFolder.getInstance();


				TestTable testTableUp = new TestTable();
				String[] nameWherePrm = {"AAAAAAAAAA1"};
				List<BaseModel> updateTgtList = testTableUp.where("name = ?", nameWherePrm).find();
				for (int i = 0; i < updateTgtList.size(); i++) {
					TestTable tgtModel = (TestTable)updateTgtList.get(i);
					System.out.println(tgtModel);
					tgtModel.age = 99;
					tgtModel.save();
				}


				TestTable testTableDelete = new TestTable();
				String[] nameDelWherePrm = {"AAAAAAAAAA1"};

				List<BaseModel> delModelList = testTableDelete.where("name = ?", nameDelWherePrm).find();
				for (BaseModel delModel : delModelList) {
					delModel.delete();
				}
				System.out.println(delModelList);
/*				testTableDelete.setTransactionFolder(folder);
				String[] nameDelWherePrm = {"ZZ00000009999"};

				testTableDelete.delete();
				folder.rollbackTransaction();
				folder.endTransaction();*/
			} catch (Exception e) {
				e.printStackTrace();
			}
			assertTrue( true );
    }

    /**
     * Rigourous Test :-)
     */
/*    public void testSave()
    {
			try {
				TransactionFolder folder = TransactionFolder.getInstance();
				for (int i = 0; i < 10; i++) {
							TestTable testTable = new TestTable();
							testTable.setTransactionFolder(folder);
							testTable.name = "0000000" + i;
							testTable.age = i;
							testTable.save();
							testTable.update();
				}
				folder.rollbackTransaction();
				folder.endTransaction();
			} catch (Exception e) {
				e.printStackTrace();
			}
			assertTrue( true );
    }*/


    /**
     * Rigourous Test :-)
     */
/*    public void testSave2()
    {
			try {
				TransactionFolder folder = TransactionFolder.getInstance();
				for (int i = 0; i < 10; i++) {
							TestTable testTable = new TestTable();
							testTable.setTransactionFolder(folder);
							testTable.name = "ZZ0000000" + i;
							testTable.age = i;
							testTable.save();
							testTable.update();
				}
				folder.commitTransaction();
				folder.endTransaction();
			} catch (Exception e) {
				e.printStackTrace();
			}
			assertTrue( true );
    }*/

    /**
     * Rigourous Test :-)
     */
    public void testFind()
    {
			try {
				/*TransactionFolder folder2 = TransactionFolder.getInstance();
				for (int i = 0; i < 10000; i++) {
							TestTable testTable = new TestTable();
							testTable.setTransactionFolder(folder2);
							testTable.name = "ZZ0000000" + i;
							testTable.age = i;
							testTable.save();
				}

				for (int i = 0; i < 1000; i++) {
					TestChildData2 testChildData2 = new TestChildData2();
					testChildData2.setTransactionFolder(folder2);

					testChildData2.name = "ZZ0000000" + i;
					testChildData2.age = i;
					testChildData2.address = "oosaka";
					testChildData2.save();
				}
				folder2.commitTransaction();
				folder2.endTransaction();*/

				TransactionFolder folder = TransactionFolder.getInstance();

				TestTable testTable = new TestTable();
				testTable.setTransactionFolder(folder);

				int retSize2 = testTable.findCount();

				long start = System.nanoTime();
				int retSize = testTable.findCount();
				long end2 = System.nanoTime();
				List<BaseModel> list = testTable.limit(50).find();
				long end = System.nanoTime();

				System.out.println("Find result list size=" + list.size() + " CountSize = " + retSize + " Execution time =" + ((end - start) / 1000 /1000) + " Execution count time =" + ((end2 - start) / 1000 /1000));
				for (int idx = 0; idx < list.size(); idx++) {
					TestTable data = (TestTable)list.get(idx);
					System.out.println(data);
/*					System.out.println(data.name);
					System.out.println(data.age);*/	
					System.out.println("-------------------");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			assertTrue( true );
    }

/*    public void testFind2()
    {
			try {
			System.out.println("testFind2 ---------------------");
				TestTable testTable = new TestTable();
				String[] nameVal = {"ZZ00000008","ZZ00000009"};
				Integer[] ageVal = {8,9};
				List<BaseModel> list = testTable.where("name = ? or name = ?", nameVal).where("age = ? or age = ?", ageVal).orderBy("age desc").limit(1).find();
				for (int idx = 0; idx < list.size(); idx++) {
					TestTable data = (TestTable)list.get(idx);
					data.age = 99;
					data.update();
					System.out.println("-------------------");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			assertTrue( true );
    }*/


}

