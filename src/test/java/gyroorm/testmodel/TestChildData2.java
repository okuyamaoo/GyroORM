package gyroorm.testmodel;

import java.util.*;

import gyroorm.model.*;

public class TestChildData2 extends BaseModel {

	public int age = 0;
	public String name = "";
	public String address = "";

	public TestChildData2() {
	}
	
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("\n")
		   .append("    TestChildData2.age = ")
			 .append(age)
			 .append(" TestChildData2.name = ")
			 .append(name)
			 .append(" TestChildData2.address = ")
			 .append(address);
		return str.toString();
	}

}