package gyroorm.model.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RelationData {

	// クラス名をフルネームで表記
	public String modelClassName() default "";

	// Join時のクエリを追記(全て左辺外部結合)
	public String joinQuery() default "";

	// 結合対象データに対してJoin条件外のフィルター条件
	public String whereQuery() default "";

	// 結合対象データに対してJoin条件外のフィルター条件のパラメータ
	public String whereQueryParameter() default "";
	
}