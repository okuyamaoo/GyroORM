package gyroorm.testmodel;

import java.util.*;

import gyroorm.model.*;
import gyroorm.model.annotation.*;

@Table(useSelectResource=false)
public class TestTable extends BaseModel {

	public String name = null;
	public int age = 0;

	@RelationData(modelClassName="gyroorm.testmodel.TestChildData1", joinQuery="age=${age}")
	public List<TestChildData1> child1List = null;

	@RelationData(modelClassName="gyroorm.testmodel.TestChildData2", joinQuery=" age = ${age} and name = ${name} ", whereQuery="address like ? or address like ? or address like ?", whereQueryParameter="%東京%,%奈良%,%大阪%")
	public List<TestChildData2> child2List = null;


	public TestTable() {
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("TestTable.name = ")
			 .append("  ")
			 .append(name)
			 .append("\n")
			 .append("TestTable.age = ")
			 .append("  ")
			 .append(age)
			 .append("\n")
			 .append("TestTable.child1List = ")
			 .append("  ")
			 .append(child1List.toString())
			 .append("\n")
			 .append("TestTable.child2List = ")
			 .append("  ")
			 .append(child2List.toString())
			 .append("\n");
		return str.toString();
	}
	
}