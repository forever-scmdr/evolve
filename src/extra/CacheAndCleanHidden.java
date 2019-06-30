package extra;

import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ExecutablePagePE;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.itemquery.ItemQuery;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class CacheAndCleanHidden extends IntegrateBase implements CatalogConst {
	public static final String CACHED_DIR = "WEB-INF/_cache_eternal";


	public CacheAndCleanHidden() {
	}

	public CacheAndCleanHidden(Command outer) {
		super(outer);
	}

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	public void integrate() throws Exception {
		long lastProductId = 0;
		int hiddenCount = 0;
		File cacheDir = new File(AppContext.getContextPath() + CACHED_DIR);
		cacheDir.mkdirs();
		ItemQuery proudctsQuery = new ItemQuery(PRODUCT_ITEM, Item.STATUS_HIDDEN).setLimit(1000);
		List<Item> hiddenProducts;
		info.setProcessed(0);
		do {
			hiddenProducts = proudctsQuery.setIdSequential(lastProductId).loadItems();
			String lastName = "";
			for (Item hiddenProduct : hiddenProducts) {
				// Создать файл кеша
				try {
					executeAndCommitCommandUnits(ItemStatusDBUnit.restore(hiddenProduct));
					ExecutablePagePE page = getExecutablePage(hiddenProduct.getKeyUnique());
					File cache = getEternalCachedFile(hiddenProduct.getKeyUnique());
					if (cache.exists())
						cache.delete();
					cache.createNewFile();
					FileOutputStream fos = new FileOutputStream(cache, false);
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					PageController.newSimple().executePage(page, bos);
					bos.writeTo(fos);
					fos.flush();
					fos.close();
				} catch (Exception e) {
					ServerLogger.error(e.getLocalizedMessage(), e);
					info.addError("Невозможно создать кеш для товара", hiddenProduct.getKey());
					executeAndCommitCommandUnits(ItemStatusDBUnit.hide(hiddenProduct));
					continue;
				}
				// Удалить
				executeAndCommitCommandUnits(ItemStatusDBUnit.delete(hiddenProduct));
				lastProductId = hiddenProduct.getId();
				lastName = hiddenProduct.getKey();
				info.increaseProcessed();
			}
			hiddenCount += hiddenProducts.size();
			info.setCurrentJob("последний скрытый товар? " + lastName + " * скрыто товаров " + hiddenCount);
		} while (hiddenProducts.size() > 0);
	}

	@Override
	protected void terminate() throws Exception {

	}

	public static final File getEternalCachedFile(String productKeyUnique) {
		return new File(AppContext.getContextPath() + CACHED_DIR + "/" + productKeyUnique);
	}

	/*

	[ERROR]-[15:57:45]-[Thread-6] - ecommander.persistence.mappers.SessionItemMapper$SessionStorageImp cannot be cast to ecommander.persistence.mappers.SessionObjectStorage
java.lang.ClassCastException: ecommander.persistence.mappers.SessionItemMapper$SessionStorageImp cannot be cast to ecommander.persistence.mappers.SessionObjectStorage
	at ecommander.controllers.SessionContext.close(SessionContext.java:57)
	at ecommander.pages.ExecutablePagePE.execute(ExecutablePagePE.java:209)
	at ecommander.controllers.PageController.processSimplePage(PageController.java:210)
	at ecommander.controllers.PageController.processPageInt(PageController.java:134)
	at ecommander.controllers.PageController.executePage(PageController.java:124)
	at extra.CacheAndCleanHidden.integrate(CacheAndCleanHidden.java:58)
	at ecommander.fwk.IntegrateBase.lambda$0(IntegrateBase.java:328)
	at java.lang.Thread.run(Thread.java:748)

	 */
}
