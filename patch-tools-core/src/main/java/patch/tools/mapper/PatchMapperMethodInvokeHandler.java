/*
 * 	Copyright PatchTools Contributors.
 *
 * 	PatchTools is licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package patch.tools.mapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Class implements logic for both mapper method types (convert and update) and
 * MethodInterceptor interface for handling methods calling
 */
public class PatchMapperMethodInvokeHandler implements MethodInterceptor {

	private static final Constructor<MethodHandles.Lookup> LOOKUP_CONSTRUCTOR;
	static {
		try {
			LOOKUP_CONSTRUCTOR = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);
			LOOKUP_CONSTRUCTOR.setAccessible(true);
		} catch (NoSuchMethodException e) {
			throw new NoSuchMethodError(e.getMessage());
		}
	}

    private final Map<Method, ObjectMapper> converters = new HashMap<>();

	/**
	 * Method is handler for all wrapper's methods. It calls original method if it is not abstract
	 * or choose one of two implementations based on arguments and return type
 	 * @param o "this", the enhanced object
	 * @param method intercepted Method
	 * @param objects argument array; primitive types are wrapped
	 * @param methodProxy used to invoke super (non-intercepted method); may be called as many times as needed
	 * @return any value compatible with the signature of the proxied method. Method returning void will ignore this value.
	 * @throws Throwable any exception may be thrown; if so, super method will not be invoked
	 */
	@Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
    	return isMethodAbstract(method) ?
    		invokeAbstractMethod(o, method, objects) :
	      invokeImplementedMethod(o, method, objects);
    }

    private boolean isMethodAbstract(Method method) {
	    return (method.getModifiers() & Modifier.ABSTRACT) != 0;
    }

    private Object invokeImplementedMethod(Object o, Method method, Object[] objects) throws Throwable {
	    Class<?> declaringClass = method.getDeclaringClass();
	    return LOOKUP_CONSTRUCTOR
			    .newInstance(declaringClass)
			    .unreflectSpecial(method, declaringClass)
			    .bindTo(o)
			    .invokeWithArguments(objects);
    }

    private Object invokeAbstractMethod(Object o, Method method, Object[] objects) throws JsonMappingException {
	    ObjectMapper converter = converters.get(method);

	    if (converter == null) {
		    converter = createConverter(method);
		    converters.put(method, converter);
	    }

	    return void.class.equals(method.getReturnType()) ?
			    updateAll(converter, objects) :
			    convert(converter, method.getReturnType(), objects);
    }

    private ObjectMapper createConverter(Method method) {
        return new ObjectMapper()
		        .setAnnotationIntrospector(new ChangeLoggerAnnotationIntrospector())
		        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private Object convert(ObjectMapper converter, Class<?> returnType, Object[] objects) throws JsonMappingException {
	    if (objects == null || objects.length < 1) {
		    return null;
	    }

    	Object result = converter.convertValue(objects[0], returnType);

	    return objects.length < 2 ? result : updateAll(converter, result, Arrays.copyOfRange(objects, 1, objects.length));
    }

    private Object updateAll(final ObjectMapper converter, Object[] objects) throws JsonMappingException {
    	return objects == null || objects.length < 1 ?
			    null :
			    updateAll(converter, objects[0], Arrays.copyOfRange(objects, 1, objects.length));
    }

    private Object updateAll(final ObjectMapper converter, final Object valueToUpdate, Object... objects) throws JsonMappingException {
    	if (objects == null || objects.length < 1) {
    		return valueToUpdate;
	    }

	    for (Object object : objects) {
	    	converter.updateValue(valueToUpdate, object);
	    }

	    return valueToUpdate;
    }

}
