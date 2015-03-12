package gyroorm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD })
public @interface Column {
	String columnName();
	String columnType()  default "String";
	String columnSize();
	boolean isPrimaryKey ()  default false;

}