package gyroorm.testmodel;

import java.util.*;

import gyroorm.model.*;
import gyroorm.model.annotation.*;

public class UserDetailTable extends BaseModel {

	public String name = null;
	public int age = 0;
	public String address = null;
	public String tel = null;
	public String zip = null;



	public UserDetailTable() {
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
			 .append("\n");
		return str.toString();
	}
	
}