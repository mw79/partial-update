package patch.tools.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import patch.tools.ChangeLogger;
import patch.tools.ChangeLoggerProducer;
import patch.tools.mapper.model.ChangeLoggerSimpleModel;
import patch.tools.mapper.model.SimpleModel;

import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class ChangeLoggerAnnotationIntrospectorTest {

	private ObjectMapper mapper;

	@Before
	public void init() {
		mapper = new ObjectMapper().setAnnotationIntrospector(new ChangeLoggerAnnotationIntrospector());
	}

	@Test
	public void shouldReturnChangeLoggerInstance() throws JsonProcessingException {
		SimpleModel model = mapper.readValue("{}", ChangeLoggerSimpleModel.class);

		assertThat(model, notNullValue());
		assertThat(model, instanceOf(ChangeLogger.class));
	}

	@Test
	public void shouldReturnNotChangeLoggerInstance() throws JsonProcessingException {
		SimpleModel model = mapper.readValue("{}", SimpleModel.class);

		assertThat(model, notNullValue());
		assertFalse(model instanceof ChangeLogger);
	}

	@Test
	public void shouldConvertChangeLogger() {
		ChangeLoggerProducer<ChangeLoggerSimpleModel> producer = new ChangeLoggerProducer<>(ChangeLoggerSimpleModel.class);
		SimpleModel model = producer.produceEntity();
		model.setTitle("Title");
		SimpleModel convertedModel = mapper.convertValue(model, ChangeLoggerSimpleModel.class);

		assertThat(convertedModel, notNullValue());
		assertThat(convertedModel, instanceOf(ChangeLogger.class));
		Map<String, Object> changelog = ((ChangeLogger) convertedModel).changelog();
		assertThat(changelog, notNullValue());
		assertThat(changelog.size(), equalTo(1));
		assertTrue(changelog.containsKey("title"));
		assertThat(changelog.get("title"), equalTo((Object)"Title"));
	}

	@Test
	public void shouldConvertSimpleClass() {
		SimpleModel model = new SimpleModel();
		model.setTitle("Title");
		SimpleModel convertedModel = mapper.convertValue(model, ChangeLoggerSimpleModel.class);

		assertThat(convertedModel, notNullValue());
		assertThat(convertedModel, instanceOf(ChangeLogger.class));
		Map<String, Object> changelog = ((ChangeLogger) convertedModel).changelog();
		assertThat(changelog, notNullValue());
		assertThat(changelog.size(), equalTo(2));
		assertTrue(changelog.containsKey("id"));
		assertTrue(changelog.containsKey("title"));
		assertThat(changelog.get("id"), nullValue());
		assertThat(changelog.get("title"), equalTo((Object)"Title"));
	}

}
