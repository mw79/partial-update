package partial.update.models;

public class TestSpecificModel extends TestBaseModel {

    private Boolean enabled;

    public TestSpecificModel(Long id, String title, Boolean enabled) {
        super();
        this.setId(id);
        this.setTitle(title);
        this.enabled = enabled;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

}
