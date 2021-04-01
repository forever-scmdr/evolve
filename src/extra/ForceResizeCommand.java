package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.ResizeImagesFactory;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

/**
 * Created by user on 23.10.2018.
 */
public class ForceResizeCommand extends Command {
	private static final HashSet<String> SUPPORTED_FROMATS = new HashSet<String>(){{add("png"); add("jpg"); add("gif");}};
	@Override
	public ResultPE execute() throws Exception {
		try {
			List<Item> products = new ItemQuery(ItemNames.PRODUCT).loadItems();
			for (Item product : products) {
				String folder = AppContext.getFilesDirPath(product.isFileProtected());
				File mainPic = product.getFileValue(ItemNames.product_.MAIN_PIC, folder);
				File smallPic = product.getFileValue("small_pic", folder);
				if (!mainPic.exists() || mainPic.isDirectory()) continue;
				boolean exists = smallPic.exists() && smallPic.isFile();
				boolean needResize = true;
				if (exists) {
					BufferedImage smallImg = ImageIO.read(smallPic);
					needResize = smallImg.getWidth() > 200;
				}
				if (needResize) {
					product.clearValue("small_pic");
					String format = StringUtils.substringAfterLast(mainPic.getName(), ".");
					format = SUPPORTED_FROMATS.contains(format)? format : "jpg";
					String destFileName = "small_" + product.getValue("code") + "." + format;
					File srcImg = (exists)?  smallPic : mainPic;
					ByteArrayOutputStream ostream = ResizeImagesFactory.resize(srcImg, 200, 0);
					Path dest = Paths.get(folder,product.getRelativeFilesPath(), destFileName);
					Path parentDir = dest.getParent();
					if (!Files.exists(parentDir))
						Files.createDirectories(parentDir);
					Files.write(dest, ostream.toByteArray());
					product.setValueUI("small_pic", destFileName);
					if(exists){FileUtils.deleteQuietly(smallPic);}
					executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noFulltextIndex().ignoreFileErrors());
				}
			}
			return getResult("success");
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			ResultPE error = getResult("error");
			error.setVariable("message", sw.toString());
			return error;
		}
	}
}
