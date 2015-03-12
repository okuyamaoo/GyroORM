package gyroorm.testmodel;

import java.util.*;

import gyroorm.model.*;

public class TestChildData1 extends BaseModel {

	public int age = 0;
	public String hobby = "";

	public TestChildData1() {
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("\n")
		   .append("    TestChildData1.age = ")
			 .append(age)
			 .append(" TestChildData1.hobby = ")
			 .append(hobby);
		return str.toString();
	}
}