package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.Strings;
import ecommander.model.Item;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.mappers.ItemMapper;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by E on 25/4/2019.
 */
public class DownloadPics extends IntegrateBase {

	private static final String URL_BASE = "http://titanprofi.ru/art-";

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		ArrayList<Item> products = ItemMapper.loadByName(ItemNames.PRODUCT, 100, 0);
		while (products.size() > 0) {
			for (Item product : products) {
				String url = URL_BASE + product.getStringValue(ItemNames.product_.CODE);
				String picUrl = null;
				try {
					Document doc = Jsoup.parse(new URL(url), 5000);
					Element a = doc.getElementById("zoom1");
					if (a != null) {
						picUrl = a.attr("href");
						if (StringUtils.isBlank(picUrl)) {
							info.addError("Элемент с картинкой не найден:" + url, product.getStringValue(ItemNames.product_.NAME));
							info.increaseProcessed();
							continue;
						}
					}
				} catch (Exception e) {
					info.addError("URL не содержит требуемую информацию:" + url, product.getStringValue(ItemNames.product_.NAME));
					info.increaseProcessed();
					continue;
				}
				if (StringUtils.isBlank(picUrl)) {
					info.addError("URL не содержит требуемую информацию:" + url, product.getStringValue(ItemNames.product_.NAME));
					info.increaseProcessed();
					continue;
				}
				String fileName = Strings.getFileName(picUrl);
				boolean needPic = product.isValueEmpty(ItemNames.product_.MAIN_PIC);
				if (!needPic) {
					File picFile = product.getFileValue(ItemNames.product_.MAIN_PIC, AppContext.getFilesDirPath(product.isFileProtected()));
					needPic = !picFile.exists();
				}
				if (!needPic) {
					needPic = !StringUtils.containsIgnoreCase(product.getStringValue(ItemNames.product_.MAIN_PIC), fileName);
				}
				if (needPic) {
					try {
						product.setValue(ItemNames.product_.MAIN_PIC, new URL(picUrl));
						executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noFulltextIndex());
					} catch (Exception e) {
						info.addError("Картинка не может быть загружена: " + picUrl, product.getStringValue(ItemNames.product_.NAME));
						info.increaseProcessed();
						continue;
					}
				}
				info.increaseProcessed();
			}
			products = ItemMapper.loadByName(ItemNames.PRODUCT, 100, products.get(products.size() - 1).getId());
		}
		info.pushLog("Все товары проверены");
	}

	@Override
	protected void terminate() throws Exception {

	}
}
