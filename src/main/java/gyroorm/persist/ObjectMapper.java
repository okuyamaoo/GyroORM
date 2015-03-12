package gyroorm.persist;

import java.util.*;
import java.lang.reflect.*;

import gyroorm.model.*;
import gyroorm.parser.*;
import gyroorm.util.*;


public class ObjectMapper {

	public static List<BaseModel> mappingObject(DefaultBaseModel model, 
																					List<Map<String, Object>> resultList) 
																									throws DataPersisterException {

		List<BaseModel> mappingList = new ArrayList();
		if (resultList == null) return mappingList;
		try {
			String modelClassName = model.getModelInfo().modelClassName;

			for (Map<String, Object> result : resultList) {
				BaseModel mappingModel = (BaseModel)Class.forName(modelClassName).newInstance();
				
				BaseModel mappingBaseModel = mappingModel.getTargetModel();
				
				// 主キー設定
				mappingModel.setId((Double)result.get("id"));
				// トランザクションを取得時に利用したモデルから取得し引き継ぎ
				mappingModel.setTransactionFolder(model.getTransactionFolder());

				int idx = 0;
				ModelInfo modelInfo = mappingModel.getModelInfo();

		    for (Field field : modelInfo.fieldList) {
 					if (!modelInfo.fieldNameList[idx].equals(modelInfo.primaryKeyName)) {
						try {
							field.setAccessible(true);
							Object value = result.get(modelInfo.fieldNameList[idx]);
							field.set(mappingBaseModel, value);

						} catch (IllegalAccessException e) {
								throw new IllegalAccessException(field.getName() + " = " + "access denied\n");
						}
					}
					idx++;
    		}
				mappingList.add(mappingModel);
			}
		} catch(Exception e) {
			throw new DataPersisterException(e);
		}
		return mappingList;
	}
}