package patch.tools;

import org.junit.Test;
import patch.tools.models.TestBaseModel;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class PatchToolsUtilTest {

	@Test
	public void shouldReturnNull() {
		assertThat(PatchToolsUtil.getArgumentTypes(null), nullValue());
	}

	@Test
	public void shouldReturnTypes() {
		Object[] objects = new Object[] {17L, "", new TestBaseModel()};

		Class[] types = PatchToolsUtil.getArgumentTypes(objects);

		assertThat(types, notNullValue());
		assertThat(types.length, equalTo(3));
		assertThat(types[0], equalTo((Class) Long.class));
		assertThat(types[1], equalTo((Class) String.class));
		assertThat(types[2], equalTo((Class) TestBaseModel.class));
	}

}
