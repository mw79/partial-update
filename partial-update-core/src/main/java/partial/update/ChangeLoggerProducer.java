/*
 * 	Copyright Partial Update library Contributors.
 *
 * 	Partial Update library is licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package partial.update;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class should be used for instantiation POJO {@literal <T>} wrappers
 * which implemented ChangeLogger interface and collecting POJO instance's
 * modified fields
 * @param <T> is a POJO class for wrapper instantiation
 */
public class ChangeLoggerProducer<T> {

    private static final String SETTER_PREFIX = "set";
    private static final int SETTER_PREFIX_LENGTH = SETTER_PREFIX.length();
    private static final Class[] INTERFACES = new Class[] { ChangeLogger.class };

    private Class<T> superclass;
    private Object[] constructorArguments;
    private Class[] constructorTypes;

	/**
	 * Constructor for ChangeLoggerProducer class which should instantiates wrapped
	 * {@literal <T>} class by calling it's constructor with arguments
	 * @param superclass {@literal Class<T>}
	 * @param constructorArguments arguments for {@literal <T>} class constructor
	 */
	public ChangeLoggerProducer(Class<T> superclass, Object... constructorArguments) {
        this.superclass = superclass;
        this.constructorArguments = constructorArguments;
        this.constructorTypes = PartialUpdateToolsUtil.getArgumentTypes(constructorArguments);
    }

	/**
	 * Method creates wrapped instance of {@literal <T>} class
	 * @return wrapped {@literal <T>} class instance which implements ChangeLogger interface
	 */
	public T produceEntity() {
        Enhancer enhancer = new Enhancer();
        enhancer.setInterceptDuringConstruction(false);
        enhancer.setSuperclass(superclass);
        enhancer.setInterfaces(INTERFACES);
        enhancer.setCallback(new MethodHandler());
        if (constructorArguments != null && constructorArguments.length > 0) {
            return (T) enhancer.create(constructorTypes, constructorArguments);
        } else {
            return (T) enhancer.create();
        }
    }

    /*
     * Class implements methods interception and collects names of modified fields
     */
    private static final class MethodHandler implements MethodInterceptor, ChangeLogger {

        private static final String CHANGELOG_METHOD_NAME = "changelog";

        private Object object;
        private final Set<String> calledSetters = new HashSet<>();

        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            String name = method.getName();
            if (name.length() > SETTER_PREFIX_LENGTH && name.startsWith(SETTER_PREFIX) &&
                    Character.isUpperCase(name.charAt(SETTER_PREFIX_LENGTH))) {
                if (object == null) {
                    object = o;
                }
                calledSetters.add(name);
            } else if (CHANGELOG_METHOD_NAME.equals(name)) {
                return changelog();
            }

            return methodProxy.invokeSuper(o, objects);
        }

        public Map<String, Object> changelog() {
            Map<String, Object> changelog = new HashMap<>();
            if (calledSetters.size() == 0) {
                return changelog;
            }

            for (String methodName: calledSetters) {
                String fieldName = java.beans.Introspector.decapitalize(methodName.substring(3));
                try {
                    Object propertyValue = PropertyUtils.getProperty(object, fieldName);
                    if (propertyValue instanceof ChangeLogger) {
                        propertyValue = ((ChangeLogger) propertyValue).changelog();
                    }
                    changelog.put(fieldName, propertyValue);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    // Ignore and try to get next property value
                }
            }

            return changelog;
        }

    }

}
