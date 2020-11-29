/*
 * 	Copyright PatchTools Contributors.
 *
 * 	PatchTools is licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package patch.tools.mapper;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import static patch.tools.PatchToolsUtil.getArgumentTypes;

public class PatchMapperProducer {

	private final MethodInterceptor invokeHandler;

	public PatchMapperProducer() {
		this.invokeHandler = new PatchMapperMethodInvokeHandler();
	}

	public <T extends PatchMapper> T createMapper(Class<T> mapperClass, Object... constructorArguments) {
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
