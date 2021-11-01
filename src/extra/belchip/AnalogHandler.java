package extra.belchip;

import ecommander.model.Item;
import ecommander.model.User;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import extra._generated.Product;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AnalogHandler {

	public static final String DB_TIMER_NAME = "AnalogHandler_DB";

	private DelayedTransaction transaction = new DelayedTransaction(User.getDefaultUser());
	private Integrate_2.Info info;
	
	public AnalogHandler(Integrate_2.Info info) {
		this.info = info;
	}

	public void parse(Path AnalogList) throws Exception {
		 List<String> lines = Files.readAllLines(AnalogList, StandardCharsets.UTF_8);
		 info.setLineNumber(0);
		 info.setToProcess(lines.size());
		 info.setProcessed(0);
		 int lineCount = 0;
		 int productsCreated = 0;
		 for(String line : lines){
			 lineCount++;
			 info.setLineNumber(lineCount);
			 String analogs = StringUtils.substringBefore(line, "/");
			 String related = StringUtils.substringAfter(line, "/");
			 ArrayList<String> analogCodes = new ArrayList<String>();
			 for(String s : analogs.split(",")){
				 analogCodes.add(s.trim());
			 }
			 List<Item> products = ItemQuery.loadByParamValue(ItemNames.PRODUCT, Product.CODE, analogCodes);
			 String[] relArr = related.split(",");
			 for(Item product : products) {
				 //product.removeValue(ItemNames.product.ANALOG_CODE);
				 product.clearValue(Product.ANALOG_SEARCH);
				 ArrayList<String> existingAnalogs = product.getStringValues(Product.ANALOG_CODE);
				 for(String analogCode : analogCodes) {
					 if(!product.getStringValue(Product.CODE).equals(analogCode) && !existingAnalogs.contains(analogCode)){
						 product.setValue(Product.ANALOG_CODE, analogCode);
						 for(Item a : products){
							 if(a.getValue(Product.CODE).equals(analogCode)){
								 String analogName = a.getStringValue(Product.NAME, "")+ " " +a.getStringValue(Product.NAME_EXTRA, "");
								 product.setValue(Product.ANALOG_SEARCH, BelchipStrings.fromRtoE(analogName));
								 product.setValue(Product.ANALOG_SEARCH, BelchipStrings.preanalyze(analogName));
							 }
						 }
					 }
				 }
				 for(String rel : relArr){
					 product.setValue(Product.REL_CODE, rel.trim());
				 }
				 info.getTimer().start(DB_TIMER_NAME);
				 transaction.addCommandUnit(SaveItemDBUnit.get(product).noFulltextIndex().ignoreUser(true).ignoreFileErrors(true).noTriggerExtra());
				 transaction.execute();
				 info.getTimer().stop(DB_TIMER_NAME);
				 info.increaseProcessed();
			 }
		 }
	}

}
