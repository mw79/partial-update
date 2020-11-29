/*
 * 	Copyright PatchTools Contributors.
 *
 * 	PatchTools is licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package patch.tools.mapper;

import com.fasterxml.jackson.databind.util.StdConverter;
import patch.tools.ChangeLogger;

import java.util.Map;

public class ChangeLoggerConverter extends StdConverter<Object, Map<String, Object>> {

    @Override
    public Map<String, Object> convert(Object o) {
        return o instanceof ChangeLogger ? ((ChangeLogger) o).changelog() : null;
    }

}
