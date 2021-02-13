/*
 * 	Copyright Partial Update library Contributors.
 *
 * 	Partial Update library is licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package partial.update.mapper;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import partial.update.ChangeLoggerProducer;

/**
 * Class based on ValueInstantiator which overrides some methods for
 * {@literal <T>} POJOs wrappers instantiation
 */
public class WrappedValueInstantiator<T> extends ValueInstantiator {

    private final ChangeLoggerProducer<T> changeLoggerProducer;

	/**
	 * Constructor for WrappedValueInstantiator
	 * @param targetClass {@literal <T>} class for wrapper instantiation
	 */
	public WrappedValueInstantiator(Class<T> targetClass) {
        this.changeLoggerProducer = new ChangeLoggerProducer<>(targetClass);
    }

	/**
	 * Method implementation makes possible to use createUsingDefault method
	 * for POJO wrapper instantiation
	 * @return true
	 */
	@Override
    public boolean canCreateUsingDefault() {
        return true;
    }

	/**
	 * Method instantiate wrapper for POJO
	 * @param ctxt deserialization context
	 * @return POJO wrapped entity
	 */
	@Override
    public Object createUsingDefault(DeserializationContext ctxt) {
        return changeLoggerProducer.produceEntity();
    }

}
