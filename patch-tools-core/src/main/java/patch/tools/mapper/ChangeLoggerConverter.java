/*
 * 	Copyright PatchTools Contributors.
 *
 * 	PatchTools is licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package patch.tools.mapper;

import com.fasterxml.jackson.databind.util.StdConverter;
import patch.tools.ChangeLogger;

import java.util.Map;

/**
 * Class overrides StdConverter and returns changed fields as {@literal Map<String, Object>}
 * instead object fields serialization
 */
public class ChangeLoggerConverter extends StdConverter<Object, Map<String, Object>> {

	/**
	 *
	 * @param o POJO instance for serializing
	 * @return all modified POJO fields as {@literal Map<String, Object>} if instance
	 * implemented ChangeLogger interface or null if not
	 */
	@Override
    public Map<String, Object> convert(Object o) {
        return o instanceof ChangeLogger ? ((ChangeLogger) o).changelog() : null;
    }

}
