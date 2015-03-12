package gyroorm.model;

import java.util.*;
import java.lang.reflect.*;

/** 
 * 継承モデルにベース処理を提供する.<br>
 *
 *
 */
public class BaseModel extends DefaultBaseModel {

	public BaseModel() {
		super();
		init(this);
	}
}