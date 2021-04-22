package extra.belchip;

import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.Catalog;
import extra._generated.ItemNames;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;

public class MicroIntegrate extends IntegrateBase {

	private static final String INTEGRATION_DIR = "integrate/";

	private File integration;
	private int hash = 0;
	private Item catalog;
	private DelayedTransaction transaction = new DelayedTransaction(User.getDefaultUser());
	private static final long HOURS = 3 * 60 * 60 * 1000;
	
	@Override
	protected boolean makePreparations() throws Exception {
		catalog = ItemQuery.loadSingleItemByName(ItemNames.CATALOG);
		String fileName = getVarSingleValue("param");
		//integration = catalog.getFileValue(paramName, AppContext.getFilesDirPath(catalog.isFileProtected()));
		integration = new File(AppContext.getRealPath(INTEGRATION_DIR + fileName));
		info.addLog("Разбор файла: "+ integration.getAbsolutePath());
		if(integration == null) { info.addError("parameter not set", "set parameter " + fileName); return false;}
		if(!integration.isFile()) {
			info.addError("unable to reach file", integration.getAbsolutePath());
		}
		return integration.isFile();
	}

	@Override
	protected void integrate() throws Exception {
		hash = integration.hashCode();
		if("yes".equalsIgnoreCase(getVarSingleValue("check_hash")) && integration.isFile()){
			int lastHash = catalog.getIntValue("hash", -1);
			catalog.setValue("hash", hash);
			transaction.addCommandUnit(SaveItemDBUnit.get(catalog).ignoreUser(true).noFulltextIndex());
			transaction.execute();
			if(hash == lastHash){
				catalog.setValue(Catalog.DATE, System.currentTimeMillis() + HOURS);
				info.addLog("Файл не был изменен с момента последнего обновления цен."); return;
			}
		}
		catalog.setValue("hash", hash);
		transaction.addCommandUnit(SaveItemDBUnit.get(catalog).ignoreUser(true).noFulltextIndex());
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		//parser.parse(integration, new LineCounter2());
		parser.parse(integration, new CatalogUpdateHandler(this));
		catalog.setValue(Catalog.DATE, System.currentTimeMillis() + HOURS);
		transaction.addCommandUnit(SaveItemDBUnit.get(catalog).ignoreUser(true).noFulltextIndex());
		transaction.execute();
		PageController.clearCache();
		info.addLog("Завершено");
	}

	@Override
	protected void terminate() throws Exception {
		// TODO Auto-generated method stub
	}
}