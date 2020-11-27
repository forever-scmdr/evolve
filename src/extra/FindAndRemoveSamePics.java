package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.mappers.ItemMapper;
import extra._generated.ItemNames;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FindAndRemoveSamePics extends IntegrateBase implements CatalogConst{
	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception	{
		setOperation("Поиск одинаковых изображений");
		info.setProcessed(0);
		List<Item> products = ItemMapper.loadByName(ItemNames.PRODUCT, 500, 0);
		long id = 0;
		while (products.size() > 0){
			for (Item product : products) {
				id = product.getId();
				HashMap<String, BufferedImage> paramImgMap = new HashMap<>();
				ArrayList<File> pics = product.getFileValues(GALLERY_PARAM, AppContext.getFilesDirPath(product.isFileProtected()));
				if(pics.size() < 2){
					info.increaseProcessed();
					continue;
				}
				boolean needSave = false;
				for(File pic : pics){
					if(pic.isFile()){
						paramImgMap.put(pic.getName(), ImageIO.read(pic));
					}else{
						product.removeEqualValue(GALLERY_PARAM, pic.getName());
						needSave = true;
					}
				}
				Set<String> keys = paramImgMap.keySet();

				for(String k : keys){
					BufferedImage p = paramImgMap.get(k);
					if(p != null){
						for(String k1 : keys){
							if(k.equals(k1)) continue;
							BufferedImage p1 = paramImgMap.get(k1);
							if(p1 == null) continue;
							if(isSamePicture(p,p1)){
								paramImgMap.replace(k1, null);
								product.removeEqualValue(GALLERY_PARAM, k1);
								needSave = true;
							}
						}
					}
				}
				if(needSave){
					executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noFulltextIndex().noTriggerExtra().ignoreUser(true));
				}
				info.increaseProcessed();
			}
			products = ItemMapper.loadByName(ItemNames.PRODUCT, 500, id);
		}
		info.setOperation("Очищение завершено");
	}

	@Override
	protected void terminate() throws Exception {

	}

	private boolean isSamePicture(BufferedImage img1, BufferedImage img2){
		if(img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()){
			return false;
		}
		for(int y = 0; y< img1.getHeight(); y++){
			for(int x = 0; x < img1.getWidth(); x++){
				int color1 = img1.getRGB(x,y);
				int color2 = img2.getRGB(x,y);
				if(color1 != color2){
					return false;
				}
			}
		}

		return true;
	}
}
