/*
 * 	Copyright Partial Update library Contributors.
 *
 * 	Partial Update library is licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package partial.update;

import java.util.Map;

/**
 * Interface for getting modified fields and it's values as {@literal Map<String, Object>}
 */
public interface ChangeLogger {

	/**
	 * Method should return all modified fields with it's values as {@literal Map<String, Object>}
	 * @return modified fields with it's values
	 */
	Map<String, Object> changelog();

}
