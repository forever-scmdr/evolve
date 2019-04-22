package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.LinkedList;

/**
 * Created by user on 18.04.2019.
 */
public class CreateGoogleTSV extends IntegrateBase implements CatalogConst {

	private int lineNumber = 0;
	private int position = 0;
	private LinkedList<Item> sections = new LinkedList<>();
	private static final String FILE_NAME = "catalog.csv";
	private static final char SEP = ',';
	private static final String HEADERS = "ID"+SEP+"ID2"+SEP+"\"Item title\""+SEP+"\"Final URL\""+SEP+"\"Image URL\""+SEP+"Price"+SEP+"\"Item description\""+SEP+"\"Formatted price\""+SEP+"\"Item category\"";

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		setOperation("Создание файла catalog.tsv");
		setProcessed(0);
		setLineNumber(lineNumber);
		Path outputPath = Paths.get(AppContext.getContextPath(), FILE_NAME);
		sections.addAll(new ItemQuery(SECTION_ITEM).loadItems());
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath.toString()), "UTF8"))){
			writer.write(HEADERS);
			Item section;
			while ((section = sections.poll()) != null){
				processSection(section,writer);
			}
		}catch (Exception e){
			addError(e.toString()+" says: "+e.getMessage(), lineNumber, position);
		}
	}

	private void processSection(Item section, BufferedWriter writer) throws Exception{
		if(section == null) return;
		LinkedList<Item> products = new LinkedList<>();
		ItemQuery q = new ItemQuery(PRODUCT_ITEM);
		int pageNumber = 0;
		q.setParentId(section.getId(), false);
		q.setLimit(500, ++pageNumber);
		products.addAll(q.loadItems());
		String sectionName = section.getStringValue(NAME_PARAM).replaceAll("\".+\"", "");
		while (products.size() > 0){
			if(products.size() == 0) return;
			pushLog(sectionName);
			Item product;
			while ((product = products.poll()) != null){
				if(product == null) break;
				String code = product.getStringValue(CODE_PARAM);
				String priceValue = product.outputValue(PRICE_PARAM);
				priceValue = priceValue == null? "" : priceValue;
				String imgUrl = getUrlBase() +'/'+ AppContext.getFilesUrlPath(product.isFileProtected()) + product.getRelativeFilesPath() + product.getStringValue(SMALL_PIC_PARAM);
				imgUrl = StringUtils.isBlank(product.getStringValue(SMALL_PIC_PARAM))? "" : imgUrl;
				String url = getUrlBase() +'/'+ product.getKeyUnique();
				String productName = product.getStringValue(NAME_PARAM).replaceAll("\".+\"", "");
				if(product.getByteValue(HAS_LINE_PRODUCTS, (byte)0) > 0){
					ItemQuery lineProductsQuery = new ItemQuery(LINE_PRODUCT_ITEM);
					lineProductsQuery.setParentId(product.getId(), false);
					for(Item lineProduct : lineProductsQuery.loadItems()){
						lineNumber++;
						code = lineProduct.getStringValue(CODE_PARAM);
						priceValue = lineProduct.outputValue(PRICE_PARAM);
						priceValue = priceValue == null? "" : priceValue;

						imgUrl = getUrlBase() + AppContext.getFilesUrlPath(lineProduct.isFileProtected()) + lineProduct.getRelativeFilesPath() + lineProduct.getStringValue(SMALL_PIC_PARAM);
						imgUrl = StringUtils.endsWith(imgUrl, "null")? "" : imgUrl;
						productName = lineProduct.getStringValue(NAME_PARAM).replaceAll("\".+\"", "");
						String out = join(SEP, code,code,productName,url,imgUrl,fPrice(priceValue)+" BYN", "", fPrice(priceValue).replace('.', ',')+" руб.", sectionName);
						writer.newLine();
						writer.write(out);
						info.increaseProcessed();
					}
				}else{
					lineNumber++;
					String out = join(SEP, code,code,productName,url,imgUrl,fPrice(priceValue)+" BYN", "", fPrice(priceValue).replace('.', ',')+" руб.", sectionName);
					writer.newLine();
					writer.write(out);
					info.increaseProcessed();
				}
			}
			q.setLimit(500, ++pageNumber);
			products.addAll(q.loadItems());
		}


	}

	private String join(char separator, String... args){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i<args.length; i++){
			String s = args[i];
			if(i > 0) sb.append(separator);
			s = s == null? "" : args[i].replaceAll("\"", "");
			s = s.indexOf(SEP) != -1 || s.indexOf(" ") != -1? '"'+args[i]+'"' : s;
			sb.append(s);
		}
		return sb.toString();
	}
	private String fPrice(String priceValue){
		if(StringUtils.isBlank(priceValue))return "";
		float f = Float.parseFloat(priceValue.replace(',','.').replaceAll("[^\\d.]", ""));
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(f).replace(',','.');
	}

	@Override
	protected void terminate() throws Exception {

	}
}
