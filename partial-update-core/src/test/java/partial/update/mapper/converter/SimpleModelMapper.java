package partial.update.mapper.converter;

import partial.update.mapper.PartialUpdateMapper;
import partial.update.mapper.model.ChangeLoggerSimpleModel;
import partial.update.mapper.model.SimpleModel;

public interface SimpleModelMapper extends PartialUpdateMapper {

	ChangeLoggerSimpleModel toModel(SimpleModel simpleModel);

	void updateModel(SimpleModel dst, SimpleModel src);

	SimpleModel updateModelAndReturn(SimpleModel dst, SimpleModel src);

}
