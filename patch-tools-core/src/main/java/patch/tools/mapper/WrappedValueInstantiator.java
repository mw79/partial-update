/*
 * 	Copyright PatchTools Contributors.
 *
 * 	PatchTools is licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package patch.tools.mapper;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import patch.tools.ChangeLoggerProducer;

public class WrappedValueInstantiator<T> extends ValueInstantiator {

    private final ChangeLoggerProducer<T> changeLoggerProducer;

    public WrappedValueInstantiator(Class<T> targetClass) {
        this.changeLoggerProducer = new ChangeLoggerProducer<>(targetClass);
    }

    @Override
    public boolean canCreateUsingDefault() {
        return true;
    }

    @Override
    public Object createUsingDefault(DeserializationContext ctxt) {
        return changeLoggerProducer.produceEntity();
    }

}
