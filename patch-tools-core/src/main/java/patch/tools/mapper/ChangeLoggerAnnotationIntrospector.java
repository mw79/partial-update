/*
 * 	Copyright PatchTools Contributors.
 *
 * 	PatchTools is licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package patch.tools.mapper;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import net.sf.cglib.proxy.Callback;
import patch.tools.annotation.ChangeLogger;

import java.lang.reflect.Method;

/**
 * This class overrides some methods of JacksonAnnotationIntrospector class
 * and:
 * <ul>
 *     <li>creates Value Instantiators for classes annotated by {@literal @ChangeLogger} annotation;</li>
 *     <li>creates Converters for classes annotated by {@literal @ChangeLogger} annotation</li>
 *     <li>excludes setCallbacks setter of CGlib's Enhancer class from deserialization process;</li>
 * </ul>
 */
public class ChangeLoggerAnnotationIntrospector extends JacksonAnnotationIntrospector {

	private static final String SET_CALLBACK_METHOD_NAME = "setCallbacks";

	/**
	 * Method creates Value Instantiator for classes annotated by {@literal @ChangeLogger} annotation
	 * @param ac contains information about class which should be deserialized
	 * @return WrappedValueInstantiator for class annotated by {@literal @ChangeLogger} annotation
	 * or default value instantiator for others
	 */
	@Override
    public Object findValueInstantiator(AnnotatedClass ac) {
        if (_findAnnotation(ac, ChangeLogger.class) != null) {
            return new WrappedValueInstantiator<>(ac.getRawType());
        }
        return super.findValueInstantiator(ac);
    }

	/**
	 *
	 * @param a contains information about class of field which should be serialized
	 * @return ChangeLoggerConverter instance if class of field annotated by
	 * {@literal @ChangeLogger} annotation or else default converter
	 */
	@Override
    public Object findSerializationConverter(Annotated a) {
        if (_findAnnotation(a, ChangeLogger.class) != null) {
            return new ChangeLoggerConverter();
        }
        return super.findSerializationConverter(a);
    }

	/**
	 * Method excludes setCallbacks setter of CGlib's Enhancer class from deserialization process
	 * @param m contains information about member(field, method or class)
	 * @return true if member is a setCallbacks method with corresponding parameters or
	 * else default value
	 */
	@Override
	public boolean hasIgnoreMarker(AnnotatedMember m) {
    	if (m.getMember() instanceof Method &&
			    SET_CALLBACK_METHOD_NAME.equals(m.getName())) {
    		Method method = (Method) m.getMember();
    		Class<?>[] parameterTypes = method.getParameterTypes();
    		if (parameterTypes.length == 1 &&
				    parameterTypes[0].isArray() &&
				    parameterTypes[0].getComponentType() == Callback.class) {
			    return true;
		    }
        }
        return super.hasIgnoreMarker(m);
    }

}