package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.MysqlConnector;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
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
import java.util.ArrayList;
import java.util.Date;

public class CreateTSVGoogleFeed extends IntegrateBase implements CatalogConst {
	private Path feed;
	private static final long THREE_MONTHS = 90L*24L*60L*60L*1000L;
	@Override
	protected boolean makePreparations() throws Exception {
		feed = Paths.get(AppContext.getContextPath(), "feed.txt");
		Files.deleteIfExists(feed);
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		setOperation("Создание фида для Google Merchant");
		long startID = 0;
		ArrayList<Item> products;
		info.setProcessed(0);
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(AppContext.getContextPath(), "feed.tsv"), StandardCharsets.UTF_8)){
			writer.write(generateHeader());

			try (Connection conn = MysqlConnector.getConnection()) {
				while ((products = ItemMapper.loadByName(PRODUCT_ITEM, 500, startID)).size() > 0) {
					for (Item product : products) {
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
		sb.append(product.getStringValue(CODE_PARAM)).append('\t');
		sb.append(product.getStringValue(TYPE_PARAM)+" Metabo "+product.getStringValue(NAME_PARAM)).append('\t');
		sb.append(StripHtml(product.getStringValue(DESCRIPTION_PARAM))).append('\t');
		sb.append(getUrlBase() + product.getKeyUnique()).append('\t');
		sb.append(getUrlBase() + product.getKeyUnique()).append('\t');
		sb.append(getSinglePicture(product, MAIN_PIC_PARAM)).append('\t');
		sb.append(getMultiplePictures(product, GALLERY_PARAM)).append('\t');
		String inStock = product.getDoubleValue(QTY_PARAM, 0d) > 0.1? "in stock" : "out of stock";
		sb.append(inStock).append('\t');
		String priceParam = product.getDecimalValue(PRICE_OLD_PARAM, BigDecimal.ZERO) != BigDecimal.ZERO? PRICE_OLD_PARAM : PRICE_PARAM;
		String price = product.getDecimalValue(priceParam, BigDecimal.ZERO) + " BYN";
		String discountPrice = PRICE_OLD_PARAM.equals(priceParam)? product.getDecimalValue(PRICE_PARAM) + " BYN": "";
		sb.append(price).append('\t');
		sb.append(discountPrice).append('\t');
		if(StringUtils.isBlank(discountPrice)){
			sb.append('\t');
		}else{
			Date now = new Date();
			Date later = new Date(now.getTime() + THREE_MONTHS);
			sb.append(now + "/" + later).append('\t');
		}
		sb.append("Metabo").append('\t');
		sb.append(product.getStringValue(CODE_PARAM)).append('\t');
		sb.append(product.getStringValue(CODE_PARAM)).append('\t');
		sb.append("no");
		return sb.toString();
	}

	private String getSinglePicture(Item product, String paramName){
		String picName = product.getStringValue(paramName);
		if(StringUtils.isBlank(picName)) return "";
		return AppContext.getFilesUrlPath(product.isFileProtected()) + product.getRelativeFilesPath() + URLEncoder.encode(picName);
	}

	private String getMultiplePictures(Item product, String paramName){
		ArrayList<String> values = product.getStringValues(paramName);
		ArrayList<String> newValues = new ArrayList<>();
		String folder = AppContext.getFilesUrlPath(product.isFileProtected()) + product.getRelativeFilesPath();
		for(int i=0; i < values.size() && i < 10; i++){
			newValues.add(folder + URLEncoder.encode(values.get(i)));
		}
		return String.join(",",newValues);
	}

	private String StripHtml(String stringValue) {
		if(StringUtils.isBlank(stringValue)) return "";
		return stringValue.replaceAll("<\\w+(\\s+(\\w+=\".*\")?)*(\\s*\\/)?>|<\\/\\w+>", "").replaceAll("\\s+", " ").trim();
	}


	private String generateHeader(){
		StringBuilder sb = new StringBuilder();
		sb.append("id").append('\t');
		sb.append("title").append('\t');
		sb.append("description").append('\t');
		sb.append("link").append('\t');
		sb.append("image_link").append("\t");
		sb.append("additional_image_link").append("\t");
		sb.append("availability").append('\t');
		sb.append("price").append('\t');
		sb.append("sale_price").append("\t");
		sb.append("sale_price_effective_date").append("\t");
		sb.append("sale_price_effective_date").append("\t");
		sb.append("brand").append('\t');
		sb.append("gtin").append('\t');
		sb.append("MPN").append('\t');
		sb.append("adult");
		return sb.toString();
	}

	@Override
	protected void terminate() throws Exception {

	}
}
