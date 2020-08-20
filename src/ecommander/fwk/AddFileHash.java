package ecommander.fwk;

import ecommander.filesystem.SingleItemDirectoryFileUnit;
import ecommander.model.Item;
import ecommander.model.Parameter;

import java.io.File;

public class AddFileHash extends SingleItemDirectoryFileUnit {

	private static final String PARAM_NAME = "file_hash";

	public AddFileHash(Item item) {
		super(item);
	}

	@Override
	public void execute() throws Exception {
		Parameter fileParam = item.getParameterByName("big_integration");
		if(fileParam != null) {
			if(fileParam.hasChanged() && !fileParam.isEmpty()) {
				File srcFile = new File(this.createItemDirectoryName() + "/" + item.getValue(fileParam.getParamId()));
				item.setValue(PARAM_NAME, srcFile.hashCode());
			}
			else{
				item.clearValue(PARAM_NAME);
			}
		}
	}

	@Override
	public void rollback() throws Exception {

	}
}
