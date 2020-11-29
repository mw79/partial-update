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

public class ChangeLoggerAnnotationIntrospector extends JacksonAnnotationIntrospector {

	private static final String SET_CALLBACK_METHOD_NAME = "setCallbacks";

    @Override
    public Object findValueInstantiator(AnnotatedClass ac) {
        if (_findAnnotation(ac, ChangeLogger.class) != null) {
            return new WrappedValueInstantiator<>(ac.getRawType());
        }
        return super.findValueInstantiator(ac);
    }

    @Override
    public Object findSerializationConverter(Annotated a) {
        if (_findAnnotation(a, ChangeLogger.class) != null) {
            return new ChangeLoggerConverter();
        }
        return super.findSerializationConverter(a);
    }

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