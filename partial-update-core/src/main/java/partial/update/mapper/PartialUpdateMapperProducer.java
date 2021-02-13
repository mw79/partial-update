/*
 * 	Copyright Partial Update library Contributors.
 *
 * 	Partial Update library is licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package partial.update.mapper;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import static partial.update.PartialUpdateToolsUtil.getArgumentTypes;

/**
 * Class for creation wrapped instances for classes/interfaces which
 * implements/extends PartialUpdateMapper interface
 */
public class PartialUpdateMapperProducer {

	private final MethodInterceptor invokeHandler;

	public PartialUpdateMapperProducer() {
		this.invokeHandler = new PartialUpdateMapperMethodInvokeHandler();
	}

	/**
	 * Method creates wrapped instance for class/interface which
	 * implements/extends PartialUpdateMapper interface
	 * @param mapperClass class/interface for which instance should be created;
	 *                    should implements/extends PartialUpdateMapper interface
	 * @param constructorArguments {@literal <T>} class constructor's arguments
	 * @return wrapped instance of mapper
	 */
	public <T extends PartialUpdateMapper> T createMapper(Class<T> mapperClass, Object... constructorArguments) {
		Enhancer enhancer = new Enhancer();
		enhancer.setInterceptDuringConstruction(false);
		enhancer.setSuperclass(mapperClass);
		enhancer.setCallback(invokeHandler);
		if (constructorArguments != null && constructorArguments.length > 0) {
			return (T) enhancer.create(getArgumentTypes(constructorArguments), constructorArguments);
		} else {
			return (T) enhancer.create();
		}
	}

}
