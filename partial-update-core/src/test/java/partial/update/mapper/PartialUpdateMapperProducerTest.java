package partial.update.mapper;

import org.junit.Before;
import org.junit.Test;
import partial.update.ChangeLogger;
import partial.update.ChangeLoggerProducer;
import partial.update.mapper.converter.SimpleModelMapper;
import partial.update.mapper.converter.SimpleModelMapperImpl;
import partial.update.mapper.model.ChangeLoggerSimpleModel;
import partial.update.mapper.model.SimpleModel;

import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class PartialUpdateMapperProducerTest {

	private SimpleModelMapper mapper;

	@Before
	public void init() {
		mapper = new PartialUpdateMapperProducer().createMapper(SimpleModelMapperImpl.class);
	}

	@Test
	public void shouldInvokeAbstractConvertPatternMethod() {
		ChangeLoggerProducer<ChangeLoggerSimpleModel> producer = new ChangeLoggerProducer<>(ChangeLoggerSimpleModel.class);
		SimpleModel src = producer.produceEntity();
		src.setId(15L);

		ChangeLoggerSimpleModel dst = mapper.toModel(src);

		assertThat(dst, notNullValue());
		assertThat(dst, instanceOf(ChangeLogger.class));
		Map<String, Object> changelog = ((ChangeLogger) dst).changelog();
		assertThat(changelog.size(), equalTo(1));
		assertTrue(changelog.containsKey("id"));
		assertThat(changelog.get("id"), equalTo((Object) 15L));
	}

	@Test
	public void shouldInvokeAbstractUpdatePatternMethod() {
		ChangeLoggerProducer<ChangeLoggerSimpleModel> producer = new ChangeLoggerProducer<>(ChangeLoggerSimpleModel.class);
		SimpleModel src = producer.produceEntity();
		src.setId(15L);
		SimpleModel dst = producer.produceEntity();
		src.setTitle("Title");

		mapper.updateModel(dst, src);

		assertThat(dst, notNullValue());
		assertThat(dst, instanceOf(ChangeLogger.class));
		Map<String, Object> changelog = ((ChangeLogger) dst).changelog();
		assertThat(changelog.size(), equalTo(2));
		assertTrue(changelog.containsKey("id"));
		assertThat(changelog.get("id"), equalTo((Object) 15L));
		assertTrue(changelog.containsKey("title"));
		assertThat(changelog.get("title"), equalTo((Object) "Title"));
	}

	@Test
	public void shouldInvokeImplementedMethod() {
		ChangeLoggerProducer<ChangeLoggerSimpleModel> producer = new ChangeLoggerProducer<>(ChangeLoggerSimpleModel.class);
		SimpleModel src = producer.produceEntity();
		src.setId(15L);
		SimpleModel dst = producer.produceEntity();
		src.setTitle("Title");

		SimpleModel result = mapper.updateModelAndReturn(dst, src);

		assertThat(dst, notNullValue());
		assertThat(result, equalTo(dst));
		assertThat(dst, instanceOf(ChangeLogger.class));
		Map<String, Object> changelog = ((ChangeLogger) result).changelog();
		assertThat(changelog.size(), equalTo(2));
		assertTrue(changelog.containsKey("id"));
		assertThat(changelog.get("id"), equalTo((Object) 15L));
		assertTrue(changelog.containsKey("title"));
		assertThat(changelog.get("title"), equalTo((Object) "Title"));
	}

}
