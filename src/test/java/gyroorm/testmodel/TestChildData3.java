package gyroorm.testmodel;

import java.util.*;

import gyroorm.model.*;

public class TestChildData3 extends BaseModel {

	public String address = null;
	public String zipcode = null;

	public TestChildData3() {
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("\n")
		   .append("    TestChildData3.address = ")
			 .append(address)
			 .append(" TestChildData3.zipcode = ")
			 .append(zipcode);
		return str.toString();
	}
}