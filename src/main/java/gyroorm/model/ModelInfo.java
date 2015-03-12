package gyroorm.model;

import java.lang.reflect.*;

public class ModelInfo {

	public String modelName;
	public String modelClassName;

	public boolean useSelectResource = false;

	public Field[] fieldList;
	public Field[] relationFieldList;

	public String[] fieldNameList;
	public String[] fieldTypeList;
	public String primaryKeyName;

	ModelInfo() {
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("modelName : ");
		sb.append(modelName);
		sb.append("\n");
		sb.append("modelClassName : ");
		sb.append(modelClassName);
		sb.append("\n");
		sb.append("useSelectResource : ");
		sb.append(useSelectResource);
		sb.append("\n");
		sb.append("primaryKey : ");
		sb.append(primaryKeyName);
		sb.append("\n");

		sb.append("fieldNameList[] : \n");
		for (String fieldName : fieldNameList) {
			sb.append("  ");
			sb.append(fieldName);
			sb.append("\n");
		}

		sb.append("fieldTypeList[] : \n");
		for (String fieldTypeName : fieldTypeList) {
			sb.append("  ");
			sb.append(fieldTypeName);
			sb.append("\n");
		}
		return sb.toString();
	}
}