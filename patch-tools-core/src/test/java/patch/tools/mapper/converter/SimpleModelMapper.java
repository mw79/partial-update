package patch.tools.mapper.converter;

import patch.tools.mapper.PatchMapper;
import patch.tools.mapper.model.ChangeLoggerSimpleModel;
import patch.tools.mapper.model.SimpleModel;

public interface SimpleModelMapper extends PatchMapper {

	ChangeLoggerSimpleModel toModel(SimpleModel simpleModel);

	void updateModel(SimpleModel dst, SimpleModel src);

	SimpleModel updateModelAndReturn(SimpleModel dst, SimpleModel src);

}
