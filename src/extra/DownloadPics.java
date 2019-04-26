package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.mappers.ItemMapper;
import extra._generated.ItemNames;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by E on 25/4/2019.
 */
public class DownloadPics extends IntegrateBase {

	private static final String URL_BASE = "http://titanprofi.ru/art-";
	private static final byte LOADING = 1;
	private static final byte SUCCESS = 2;
	private static final byte ERROR = 3;

	private Object lock = new Object();
	private String picUrl = null;
	private volatile byte process = 0;

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		ArrayList<Item> products = ItemMapper.loadByName(ItemNames.PRODUCT, 100, 0);
		WebView webView = new WebView();
		while (products.size() > 0) {
			for (Item product : products) {
				boolean needPic = product.isValueEmpty(ItemNames.product_.MAIN_PIC);
				if (!needPic) {
					File picFile = product.getFileValue(ItemNames.product_.MAIN_PIC, AppContext.getFilesDirPath(product.isFileProtected()));
					needPic = !picFile.exists();
				}
				if (needPic) {
					String url = URL_BASE + product.getStringValue(ItemNames.product_.CODE);
					process = LOADING;
					WebEngine engine = webView.getEngine();
					engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
						@Override
						public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldState, Worker.State newState) {
							boolean finished = false;
							if (newState == Worker.State.SUCCEEDED) {
								finished = true;
								process = SUCCESS;
							} else if (newState == Worker.State.CANCELLED || newState == Worker.State.FAILED) {
								finished = true;
								process = ERROR;
							}
							if (finished) {
								synchronized (lock) {
									lock.notifyAll();
								}
							}
						}
					});
					engine.load(url);
					synchronized (lock) {
						while (process == LOADING)
							lock.wait();
					}
					if (process == SUCCESS) {
						Document doc = engine.getDocument();
						Element linkEl = doc.getElementById("zoom1");
						if (linkEl != null) {
							String picUrl = linkEl.getAttribute("href");
							if (StringUtils.isNotBlank(picUrl)) {
								product.setValue(ItemNames.product_.MAIN_PIC, new URL(picUrl));
								executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noFulltextIndex());
							}
						}
					}
				}
				info.increaseProcessed();
			}
			products = ItemMapper.loadByName(ItemNames.PRODUCT, 100, products.get(products.size() - 1).getId());
		}
	}

	@Override
	protected void terminate() throws Exception {

	}
}
