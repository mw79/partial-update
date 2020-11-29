package patch.tools.mapper.converter;

import patch.tools.mapper.model.SimpleModel;

public abstract class SimpleModelMapperImpl implements SimpleModelMapper {

	public SimpleModel updateModelAndReturn(SimpleModel dst, SimpleModel src) {
		updateModel(dst, src);
		return dst;
	}

}
