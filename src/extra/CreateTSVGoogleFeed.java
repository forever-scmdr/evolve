package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.MysqlConnector;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.mappers.ItemMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CreateTSVGoogleFeed extends IntegrateBase implements CatalogConst {
	private Path feed;
	private static final long THREE_MONTHS = 90L*24L*60L*60L*1000L;
	private static final String FEED_FILE_NAME = "feed.txt";
	private Date now = new Date();
	private Date later = new Date(now.getTime() + THREE_MONTHS);
	private SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("YYYY-MM-dd");
	private SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm:ss");

	@Override
	protected boolean makePreparations() throws Exception {
		feed = Paths.get(AppContext.getContextPath(), FEED_FILE_NAME);
		Files.deleteIfExists(feed);
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		setOperation("Создание фида для Google Merchant");
		long startID = 0;
		ArrayList<Item> products;
		info.setProcessed(0);
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(AppContext.getContextPath(), FEED_FILE_NAME), StandardCharsets.UTF_8)){
			writer.write(generateHeader());

			try (Connection conn = MysqlConnector.getConnection()) {
				while ((products = ItemMapper.loadByName(PRODUCT_ITEM, 500, startID)).size() > 0) {
					for (Item product : products) {
						//fix key-unique
						if(product.getKeyUnique().indexOf('.') != -1){
							product.setKeyUnique(StringUtils.replaceChars(product.getKeyUnique(), '.', '_'));
							executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noTriggerExtra().ignoreUser(true));
						}
						startID = product.getId();
						writer.newLine();
						writer.write(processProduct(product));
						info.increaseProcessed();
					}
				}
			}
		}
	}

	private String processProduct(Item product) {
		StringBuilder sb = new StringBuilder();
		//id
		sb.append(product.getStringValue(CODE_PARAM)).append('\t');
		//title
		sb.append('"'+(product.getStringValue(TYPE_PARAM,"")+" Metabo "+product.getStringValue(NAME_PARAM)).replaceAll("\"", "\"\"").trim() + '"').append('\t');
		//description
		sb.append('"'+ stripHtml(product.getStringValue(DESCRIPTION_PARAM, ""))+'"').append('\t');
		//link
		sb.append(getUrlBase()+ "/" + product.getKeyUnique()).append('\t');
		//image link
		sb.append(getSinglePicture(product, MAIN_PIC_PARAM)).append('\t');
		//additional image link
		sb.append(getMultiplePictures(product, GALLERY_PARAM)).append('\t');
		//availability
		String av = product.getDoubleValue(QTY_PARAM, 0d) > 0.01? "in stock" : "out of stock";
		sb.append(av).append('\t');
		//price
		boolean hasDiscount = product.getDecimalValue(PRICE_OLD_PARAM, BigDecimal.ZERO) != BigDecimal.ZERO;
		String priceParam = hasDiscount? PRICE_OLD_PARAM : PRICE_PARAM;
		BigDecimal price = product.getDecimalValue(priceParam, BigDecimal.ZERO);
		sb.append(price + " BYN").append('\t');
		//discount
		if(hasDiscount){
			sb.append(product.getDecimalValue(PRICE_PARAM)+" BYN").append('\t');
			sb.append(DAY_FORMAT.format(now)+'T'+TIME_FORMAT.format(now) +'/'+ DAY_FORMAT.format(later)+'T'+TIME_FORMAT.format(later)).append('\t');
		}
		else{
			sb.append("\t\t");
		}
		//sb.append(product.getStringValue(CODE_PARAM)).append('\t');
		sb.append("Metabo").append('\t');
		//sb.append("mpn").append('\t');
		sb.append("Adult");
		return sb.toString();
	}

	private String getSinglePicture(Item product, String paramName){
		String picName = product.getStringValue(paramName);
		if(StringUtils.isBlank(picName)) return "";
		return getUrlBase()+ "/"+ AppContext.getFilesUrlPath(product.isFileProtected()) + product.getRelativeFilesPath() + URLEncoder.encode(picName);
	}

	private String getMultiplePictures(Item product, String paramName){
		ArrayList<String> values = product.getStringValues(paramName);
		ArrayList<String> newValues = new ArrayList<>();
		String folder = AppContext.getFilesUrlPath(product.isFileProtected()) + product.getRelativeFilesPath();
		for(int i=0; i < values.size() && i < 10; i++){
			newValues.add(getUrlBase()+ "/" + folder + URLEncoder.encode(values.get(i)));
		}
		return String.join(",",newValues);
	}

	private String stripHtml(String stringValue) {
		if(StringUtils.isBlank(stringValue)) return "";
		return stringValue.replaceAll("<\\w+(\\s+(\\w+=\".*\")?)*(\\s*\\/)?>|<\\/\\w+>", "").replaceAll("\\s+|&nbsp;", " ").replaceAll("\"", "\"\"").trim();
	}


	private String generateHeader(){
		StringBuilder sb = new StringBuilder();
		sb.append("id").append('\t');
		sb.append("title").append('\t');
		sb.append("description").append('\t');
		sb.append("link").append('\t');
		sb.append("image link").append('\t');
		sb.append("additional image link").append('\t');
		sb.append("availability").append('\t');
		sb.append("price").append('\t');
		sb.append("sale price").append('\t');
		sb.append("effective date").append('\t');
		//sb.append("gtin").append('\t');
		sb.append("brand").append('\t');
		//sb.append("mpn").append('\t');
		sb.append("age group");
		return sb.toString();
	}

	@Override
	protected void terminate() throws Exception {

	}
}