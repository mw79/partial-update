package partial.update.mapper.converter;

import partial.update.mapper.model.SimpleModel;

public abstract class SimpleModelMapperImpl implements SimpleModelMapper {

	public SimpleModel updateModelAndReturn(SimpleModel dst, SimpleModel src) {
		updateModel(dst, src);
		return dst;
	}

}
