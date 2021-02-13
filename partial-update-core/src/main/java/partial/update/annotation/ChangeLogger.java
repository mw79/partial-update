/*
 * 	Copyright Partial Update library Contributors.
 *
 * 	Partial Update library is licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package partial.update.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation used by ChangeLoggerAnnotationIntrospector class.
 * If class which should be created while deserializing is annotated by this annotation
 * then wrapped class will be created, so this class will implemented ChangeLogger interface
 * and will intercept all setters calls.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ChangeLogger {
}
