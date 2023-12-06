package ecommander.fwk;

import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.datatypes.FileDataType;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.ItemMapper;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by E on 17/1/2019.
 */
public class CreateWaterMarks extends IntegrateBase {
	private static final String ORIGINAL_GALLERY = "original_gallery";
	private static final String GALLERY = "gallery";
	private static final String WATERMARK_FILE = "watermark_filename";
	private static final String WATERMARK_MODIFIED = "watermark_file_modified";

	private BufferedImage watermark;
	private long watermarkModified;
	private String watermarkName;
	private boolean forceWatermark = false;
	private ItemType backupType;

	@Override
	protected boolean makePreparations() throws Exception {
		backupType = ItemTypeRegistry.getItemType(ORIGINAL_GALLERY);
		String wm = getVarSingleValue("watermark");
		forceWatermark = StringUtils.equalsAnyIgnoreCase(getVarSingleValue("force"), "yes", "true");
		if (StringUtils.isNotBlank(wm)) {
			File wmFile = new File(AppContext.getRealPath(wm));
			watermarkName = wmFile.getName();
			watermarkModified = wmFile.lastModified();
			if (wmFile.exists()) {
				try {
					watermark = ImageIO.read(wmFile);
					return true;
				} catch (Exception e) {
					ServerLogger.error("unable to read watermark file", e);
					info.pushError(e.getLocalizedMessage(), wm);
				}
			}
		}
		return false;
	}

	@Override
	protected void integrate() throws Exception {
		ArrayList<Item> items;
		long startFrom = 0;
		info.setProcessed(0);
		do {
			DelayedTransaction transaction = new DelayedTransaction(getInitiator());
			items = ItemMapper.loadByName(GALLERY, 10, startFrom);
			for (Item item : items) {
				Item backup = new ItemQuery(ORIGINAL_GALLERY).setParentId(item.getId(), false).loadFirstItem();
				boolean watermarkNeeded = forceWatermark || backup == null;
				if (!watermarkNeeded && backup != null) {
					watermarkNeeded |= !StringUtils.equalsAnyIgnoreCase(watermarkName, backup.getStringValue(WATERMARK_FILE));
					watermarkNeeded |= watermarkModified != backup.getLongValue(WATERMARK_MODIFIED, (long)0);
				}
				if (watermarkNeeded) {
					// Создать резервную копию картинок
					if (backup == null) {
						backup = Item.newChildItem(backupType, item);
						backup.setValue(WATERMARK_FILE, watermarkName);
						backup.setValue(WATERMARK_MODIFIED, new Long(watermarkModified));
						ArrayList<File> pics = item.getFileValues(GALLERY, AppContext.getFilesDirPath(item.isFileProtected()));
						for (File pic : pics) {
							backup.setValue(GALLERY, pic);
						}
						transaction.addCommandUnit(SaveItemDBUnit.get(backup));
						transaction.execute();
					}
					// Удалить картинки
					item.clearValue(GALLERY);
					transaction.addCommandUnit(SaveItemDBUnit.get(item));
					transaction.execute();
					ArrayList<File> pics = backup.getFileValues(GALLERY, AppContext.getFilesDirPath(backup.isFileProtected()));
					for (File pic : pics) {
						try {
							BufferedImage buffer = ImageIO.read(pic);
							BufferedImage withWatermark = createWatermarkedImage(buffer);
							String fileType = StringUtils.substringAfterLast(pic.getName(), ".");
							item.setValue(GALLERY, new FileDataType.BufferedPic(withWatermark, pic.getName(), fileType));
						} catch (Exception e) {
							ServerLogger.error("unable to apply watermark", e);
							info.pushError("Невозможно применить водяной знак к картинке",
									item.getStringValue("name") + " " + pic.getName());
							item.setValue(GALLERY, pic);
						}
					}
					transaction.addCommandUnit(SaveItemDBUnit.get(item));
					transaction.execute();
					info.increaseProcessed();
				}
				startFrom = item.getId();
			}
		} while (items.size() > 0);
	}


	private BufferedImage createWatermarkedImage(BufferedImage original) throws IOException {
		BufferedImage newWater = Thumbnails.of(watermark).size(original.getWidth(), original.getHeight()).asBufferedImage();
		return Thumbnails.of(original).scale(1f).watermark(Positions.CENTER, newWater, 1.0f).asBufferedImage();
	}

	@Override
	protected void terminate() throws Exception {

	}
}
