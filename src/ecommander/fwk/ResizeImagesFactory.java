package ecommander.fwk;

import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import ecommander.model.datatypes.DataType.Type;
import ecommander.model.Item;
import ecommander.model.MultipleParameter;
import ecommander.model.ParameterDescription;
import ecommander.model.SingleParameter;
import ecommander.persistence.PersistenceCommandUnit;
import ecommander.persistence.TransactionContext;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.ItemMapper;
import ecommander.filesystem.ItemFileUnit;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
/**
 * Команда, которая преобразует картинки айтема к определенному формату после сохранения айтема
 * Используется атрибут format определения параметра (ParameterDescription)
 * 
 * src - параметр, который хранит исходную картинку, если он пустой - исходная картинка есть сам параметр
 * width - новая ширина картинки (в пикселях), если не установлена, масштабируется пропорционально с высотой
 * height - новая высота картинки (в пикселях), если не установлена, масштабируется пропорционально с шириной
 * format - формат файла (расширение), используется в алгоритмах сжатия
 * crop - если заданы ширина и высота, каким образом обрезать картинку 
 * 		  (BOTTOM_CENTER, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER, CENTER_LEFT, CENTER_RIGHT, TOP_CENTER, TOP_LEFT, TOP_RIGHT)
 * 
 * format="src:main_img;width:400;height:500;crop:CENTER"
 * 		  картинка делается размерами 400х500 и обрезается по центру (чтобы не деформировалось изображение)
 * 
 * format="src:main_img;width:400;height:500"
 * 		  ширина и высота преборазуются таким образом, чтобы максимальный был равен заданному соответствующему,
 * 		  в то же время сохраняются пропорции картинки
 * 
 * format="src:main_img;width:400"
 * 		  картинка из параметра main_img преобразуется к ширине 400 и высоте, пропорционально изменению ширины
 * 
 * format="height:50;format:gif"
 * 		  картинка самого параметра преобразуется к высоте 400 и ширине, пропорционально изменению высоты
 * 
 * Принцип работы:
 * Если картинка для ресайза имеет заполненный src, а также значение - файл (т. е. есть файл картинки для этого параметра),
 * то этот файл не заменяется. Однако, если источником картинки является сам этот параметр, т.е. сам должен ресайзиться,
 * то ресайз происходит всегда, даже когда есть заполненная картинка.
 * 
 * @author E
 *
 */
public class ResizeImagesFactory implements ItemEventCommandFactory {
	public static final String SRC = "src";
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	public static final String FORMAT = "format";
	public static final String CROP = "crop";
	
	public static class ResizeImages extends ItemFileUnit {

		private TransactionContext transaction;
		private ArrayList<File> files = new ArrayList<File>();
		private String format;
		
		private ResizeImages(Item item) {
			super(item);
			format = null;
		}
		
		public ResizeImages(Item item, String format) {
			super(item);
			this.format = format;
		}
		
		public void execute() throws Exception {
			for (ParameterDescription param : item.getItemType().getParameterList()) {
				if (param.getType() == Type.PICTURE && (param.hasFormat() || format != null)) {
					String currentFormat = format;
					if (param.hasFormat())
						currentFormat = param.getFormat();
					String[] opts = StringUtils.split(currentFormat, ';');
					String src = null;
					ParameterDescription srcParam = null;
					int height = 0;
					int width = 0;
					String format = null;
					Positions crop = null;
					
					// Разбор параметров ресайза
					try {
						for (String opt : opts) {
							String[] vals = StringUtils.split(opt.trim(), ':');
							if (vals[0].trim().equals(SRC))
								src = vals[1].trim();
							else if (vals[0].trim().equals(WIDTH))
								width = Integer.parseInt(vals[1].trim());
							else if (vals[0].trim().equals(HEIGHT))
								height = Integer.parseInt(vals[1].trim());
							else if (vals[0].trim().equals(FORMAT))
								format = vals[1].trim();
							else if (vals[0].trim().equals(CROP))
								crop = Positions.valueOf(vals[1].trim());
							else throw new Exception("Image resize parameter '" + vals[0] + "' is undefiled");
						}
						if (StringUtils.isBlank(src))
							src = param.getName();
						srcParam = item.getItemType().getParameter(src);
						if (srcParam == null || srcParam.getType() != Type.PICTURE)
							throw new Exception("There is no picture parameter '" + src + "' in an item");
					} catch (Exception e) {
						throw new EcommanderException("String resize format error. Item: '" + item.getTypeName() + "', parameter: '"
								+ param.getName() + ", format: " + param.getFormat(), e);
					}
					
					// Одиночный параметр - более сложная логика (нужна проверка, если файл существует)
					if (!srcParam.isMultiple())	{
						try {
							boolean selfResize = srcParam.getId() == param.getId();
							File destFile = new File(createItemFileDirectoryName() + item.getValue(param.getId()));
							File srcFile = new File(createItemFileDirectoryName() + item.getValue(srcParam.getId()));
							// ничего не делать в случае если исходной картинки нет
							if (!srcFile.exists())
								continue;
							// не производить ресайз, если картинка уже есть (т.к. она может быть намеренно другой)
							if (destFile.exists() && srcParam.getId() != param.getId())
								continue;
							if (height <= 0 && width <= 0)
								continue;
							if (format == null)
								format = StringUtils.substringAfterLast(srcFile.getName(), ".");
							
							// Сначала прочитать файл, перед тем как он может быть удален
							BufferedImage srcImg = ImageIO.read(srcFile);
			
							// Проверка, нужен ли ресайз
							boolean resizeNeeded = (width > 0 && srcImg.getWidth() > width) || (height > 0 && srcImg.getHeight() > height);
							if (!resizeNeeded)
								continue;
							
							String fileName = StringUtils.substringBeforeLast(srcFile.getName(), ".") + '.' + format;
							if (!selfResize)
								fileName = param.getName() + "_" + fileName;
							destFile = new File(createItemFileDirectoryName() + fileName);
							resize(srcImg, destFile, width, height, format, crop);
							// Установка значения параметра
							item.setValueUI(param.getId(), fileName);
						} catch (Exception e) {
							ServerLogger.error("Error resizing image", e);
						}
					}
					
					// Множественный параметр - всегда удаляются и пересоздаются все производные файлы
					else {
						boolean selfResize = srcParam.getId() == param.getId();
						// Ничего не делать, если параметр назначения не множественный
						if (!param.isMultiple())
							continue;
						// Удалить все файлы и значения параметра в случае если параметр назначения не совпадает с параметром источника
						MultipleParameter destVals = (MultipleParameter) item.getParameter(param.getId());
						if (!selfResize) {
							for (SingleParameter val : destVals.getValues()) {
								File deleteFile = new File(createItemFileDirectoryName() + val.getValue());
								if (deleteFile.exists() && !deleteFile.delete())
									throw new Exception("File '" + deleteFile.getName() + "' can not be deleted");
							}
							destVals.clear();
						}
						ArrayList<SingleParameter> vals = new ArrayList<SingleParameter>(((MultipleParameter) item.getParameter(srcParam
								.getId())).getValues());
						for (SingleParameter srcVal : vals) {
							File srcFile = new File(createItemFileDirectoryName() + srcVal.getValue());
							if (srcFile.exists()) {
								try {
									if (format == null)
										format = StringUtils.substringAfterLast(srcFile.getName(), ".");
									String fileName = StringUtils.substringBeforeLast(srcFile.getName(), ".") + '.' + format;
									if (srcParam.getId() != param.getId())
										fileName = param.getName() + "_" + fileName;
									// Сначала прочитать файл, перед тем как он может быть удален
									BufferedImage srcImg = ImageIO.read(srcFile);
									// Удалить значение из параметра назначения
									if (destVals.containsValue(fileName)) {
										destVals.deleteValue(fileName);
									}
									File destFile = new File(createItemFileDirectoryName() + fileName);
									resize(srcImg, destFile, width, height, format, crop);
									item.setValueUI(param.getId(), fileName);
								} catch (Exception e) {
									ServerLogger.error("Error resizing image", e);
								}
							}
						}
					}
				}
			}
			// Апдейт базы данных (сохранение новых параметров айтема)
			if (item.hasChanged()) {
				PreparedStatement pstmt = null;
				try {
					Connection conn = getTransactionContext().getConnection();
					// Сохранить новое ключевое значение и параметры в основную таблицу
					String sql 
							= "UPDATE " + DBConstants.Item.TABLE + " SET " + DBConstants.Item.KEY + "=?, " 
							+ DBConstants.Item.TRANSLIT_KEY + "=?, "
							+ DBConstants.Item.PARAMS + "=?, " 
							+ DBConstants.Item.UPDATED + "=NULL WHERE " + DBConstants.Item.REF_ID + "=" + item.getId();
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, item.getKey());
					pstmt.setString(2, item.getKeyUnique());
					pstmt.setString(3, item.outputValues());
					pstmt.executeUpdate();
					pstmt.close();
					
					// Выполнить запросы для сохранения параметров
					ItemMapper.insertItemParametersToIndex(item, true, getTransactionContext());
				} finally {
					MysqlConnector.closeStatement(pstmt);
				}
			}
		}
		
		private void resize(BufferedImage srcImg, File destFile, int width, int height, String format, Positions crop) throws Exception {
			// Если исходный файл и файл назначения совпадают (т.е. ресайзится картинка одного параметра)
			// то надо удалить старый файл
			if (destFile.exists() && !destFile.delete())
				throw new Exception("File '" + destFile.getName() + "' can not be deleted");
			Thumbnails.Builder<BufferedImage> thumbnailer = Thumbnails.of(srcImg);
			// Если заданы и высота и ширина, выбрать один из этих параметров
			// на основании соотношения сторон исходного изображения
			if (width > 0 && height > 0) {
				
			}
			if (width > 0)
				thumbnailer.width(width);
			if (height > 0) {
				thumbnailer.height(height);
				if (width > 0 && crop != null)
					thumbnailer.crop(crop);
			}
			thumbnailer.outputFormat(format).toFile(destFile);
			// Добавить файл для удаления в случае отката команды
			files.add(destFile);
		}
		
		public void rollback() throws Exception {
			for (File file : files) {
				ServerLogger.debug("Deleting file '" + file.getAbsolutePath() + "' - " + FileUtils.deleteQuietly(file));
			}
		}

		@Override
		public TransactionContext getTransactionContext() {
			return transaction;
		}

		@Override
		public void setTransactionContext(TransactionContext context) {
			this.transaction = context;
		}
	
	}

	@Override
	public PersistenceCommandUnit createCommand(Item item, Item initialVersion) throws Exception {
		return new ResizeImages(item);
	}

}
