package patch.tools;

import patch.tools.models.TestBaseModel;
import patch.tools.models.TestSpecificModel;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ChangeLoggerProducerTest {

    @Test
    public void shouldCreateInstance() {
        ChangeLoggerProducer<TestBaseModel> producer = new ChangeLoggerProducer<>(TestBaseModel.class);

        TestBaseModel model = producer.produceEntity();

        assertThat(model, instanceOf(ChangeLogger.class));
    }

    @Test
    public void shouldCreateInstanceWithArguments() {
        ChangeLoggerProducer<TestSpecificModel> producer =
                new ChangeLoggerProducer<>(TestSpecificModel.class,
                        5L, "Test title", true);

        TestSpecificModel model = producer.produceEntity();

        assertThat(model, instanceOf(ChangeLogger.class));
    }

	@Test
	public void shouldNotCollectChanges() {
		ChangeLoggerProducer<TestBaseModel> producer = new ChangeLoggerProducer<>(TestBaseModel.class);

		TestBaseModel model = producer.produceEntity();

		Map<String, Object> changelog = ((ChangeLogger) model).changelog();
		assertThat(changelog.size(), equalTo(0));
	}

	@Test
	public void shouldCollectChanges() {
	    ChangeLoggerProducer<TestBaseModel> producer = new ChangeLoggerProducer<>(TestBaseModel.class);

	    TestBaseModel model = producer.produceEntity();
	    model.setId(100L);

	    Map<String, Object> changelog = ((ChangeLogger) model).changelog();
	    assertThat(changelog.size(), equalTo(1));
	    assertTrue(changelog.containsKey("id"));
	    assertThat(changelog.get("id"), equalTo((Object) 100L));
    }

	@Test
	public void shouldCollectChangesRecursive() {
		ChangeLoggerProducer<TestBaseModel> producer = new ChangeLoggerProducer<>(TestBaseModel.class);

		TestBaseModel model = producer.produceEntity();
		TestBaseModel internalModel = producer.produceEntity();
		internalModel.setId(100L);
		model.setTestBaseModel(internalModel);

		Map<String, Object> changelog = ((ChangeLogger) model).changelog();
		assertThat(changelog.size(), equalTo(1));
		assertTrue(changelog.containsKey("testBaseModel"));
		Map<String, Object> internalChangelog = (Map<String, Object>) changelog.get("testBaseModel");
		assertThat(internalChangelog.size(), equalTo(1));
		assertTrue(internalChangelog.containsKey("id"));
		assertThat(internalChangelog.get("id"), equalTo((Object) 100L));
	}

}
