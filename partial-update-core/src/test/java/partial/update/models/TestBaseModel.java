package partial.update.models;

public class TestBaseModel {

    private Long id;
    private String title;
	private TestBaseModel testBaseModel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

	public TestBaseModel getTestBaseModel() {
		return testBaseModel;
	}

	public void setTestBaseModel(TestBaseModel testBaseModel) {
		this.testBaseModel = testBaseModel;
	}

}
