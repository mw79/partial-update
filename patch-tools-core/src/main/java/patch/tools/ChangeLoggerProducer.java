/*
 * 	Copyright PatchTools Contributors.
 *
 * 	PatchTools is licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package patch.tools;

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

import static patch.tools.PatchToolsUtil.getArgumentTypes;

public class ChangeLoggerProducer<T> {

    private static final String SETTER_PREFIX = "set";
    private static final int SETTER_PREFIX_LENGTH = SETTER_PREFIX.length();
    private static final Class[] INTERFACES = new Class[] { ChangeLogger.class };
    private Class<T> superclass;
    private Object[] constructorArguments;
    private Class[] constructorTypes;

    public ChangeLoggerProducer(Class<T> superclass) {
        this.superclass = superclass;
    }

    public ChangeLoggerProducer(Class<T> superclass, Object... constructorArguments) {
        this(superclass);
        this.constructorArguments = constructorArguments;
        this.constructorTypes = getArgumentTypes(constructorArguments);
    }

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
