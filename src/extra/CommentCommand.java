package extra;

import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.MultipleHttpPostForm;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Created by user on 22.12.2018.
 */
public class CommentCommand extends Command {
	@Override
	public ResultPE execute() throws Exception {
		String formNameStr = getVarSingleValue("form_name");
		try{
			MultipleHttpPostForm postForm = getItemForm();
			if(StringUtils.isNotBlank(postForm.getSingleStringExtra("phone"))) return getResult("success");
			String extraId = postForm.getSingleStringExtra("id");
			String idVar =  getVarSingleValue("id");
			long id = (StringUtils.isNoneBlank(extraId))?Long.parseLong(extraId) : Long.parseLong(idVar);
			Item comment = postForm.getItemTree().getFirstChild().getItem();
			if(StringUtils.isBlank(comment.getStringValue("text"))
					|| StringUtils.isBlank(comment.getStringValue("name"))
					|| StringUtils.isBlank(comment.getStringValue("email"))
					){
				return getResult("error_not_set");
			}
			comment.setValue("date", new Date().getTime());
			//comment.setValue("moderated", (byte)1);
			comment.setContextPrimaryParentId(id);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(comment).noFulltextIndex().ignoreUser());
			ResultPE res = getResult("success");
			res.setVariable("id", String.valueOf(id));
			return res;
		}catch (Exception e){
			saveSessionForm(formNameStr);
			ServerLogger.error(e);
			return getResult("general_error");
		}
	}
}
