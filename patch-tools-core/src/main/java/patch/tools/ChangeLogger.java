/*
 * 	Copyright PatchTools Contributors.
 *
 * 	PatchTools is licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package patch.tools;

import java.util.Map;

public interface ChangeLogger {

    Map<String, Object> changelog();

}
