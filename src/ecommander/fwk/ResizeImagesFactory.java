package ecommander.fwk;

import ecommander.filesystem.SingleItemDirectoryFileUnit;
import ecommander.model.Item;
import ecommander.model.MultipleParameter;
import ecommander.model.ParameterDescription;
import ecommander.model.SingleParameter;
import ecommander.model.datatypes.DataType.Type;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.common.TransactionContext;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.ItemMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
/**
 * Команда, которая преобразует картинки айтема к определенному формату после сохранения айтема
 * Используется атрибут format определения параметра (ParameterDescription)
 * 
 * src - параметр, который хранит исходную картинку, если он пустой - исходная картинка есть сам параметр
 * width - новая ширина картинки (в пикселях), если не установлена, масштабируется пропорционально с высотой
 * height - новая высота картинки (в пикселях), если не установлена, масштабируется пропорционально с шириной
 * format - формат файла (расширение), используется в алгоритмах сжатия
 * crop ( НЕ РАБОТАЕТ ) - если заданы ширина и высота, каким образом обрезать картинку
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
 */
public class ResizeImagesFactory implements ItemEventCommandFactory, DBConstants.ItemTbl {
	public static final String SRC = "src";
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	public static final String FORMAT = "format";
	public static final String CROP = "crop";

	public static class ResizeImages extends SingleItemDirectoryFileUnit {

		private TransactionContext transaction;
		private ArrayList<File> files = new ArrayList<>();
		private String format;
		
		public ResizeImages(Item item) {
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
					//Positions crop = null;
					
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
							else if (vals[0].trim().equals(CROP)) {}
							//	crop = Positions.valueOf(vals[1].trim());
							else throw new Exception("Image resize parameter '" + vals[0] + "' is undefiled");
						}
						if (StringUtils.isBlank(src))
							src = param.getName();
						srcParam = item.getItemType().getParameter(src);
						if (srcParam == null || srcParam.getType() != Type.PICTURE)
							throw new Exception("There is no picture parameter '" + src + "' in an item");
					} catch (Exception e) {
						throw new EcommanderException(ErrorCodes.VALIDATION_FAILED, "String resize format error. Item: '"
								+ item.getTypeName() + "', parameter: '" + param.getName()
								+ ", format: " + param.getFormat(), e);
					}
					
					// Одиночный параметр - более сложная логика (нужна проверка, если файл существует)
					if (!srcParam.isMultiple())	{
						// Ничего не делать если параметр-источник не поменялся, а у изменяемого параметра уже есть занчение
						if (!item.getParameter(srcParam.getId()).hasChanged() && !item.getParameter(param.getId()).isEmpty())
							continue;
						try {
							boolean selfResize = srcParam.getId() == param.getId();
							File destFile = null;
							if (!item.getParameter(param.getId()).isEmpty())
								destFile = new File(createItemDirectoryName() + "/" + item.getValue(param.getId()));
							File srcFile = new File(createItemDirectoryName() + "/" + item.getValue(srcParam.getId()));
							// ничего не делать в случае если исходной картинки нет
							if (!srcFile.isFile())
								continue;
							// не производить ресайз, если картинка уже есть (т.к. она может быть намеренно другой)
							if (destFile != null && destFile.isFile() && srcParam.getId() != param.getId())
								continue;
							if (height <= 0 && width <= 0)
								continue;
							if (format == null)
								format = StringUtils.substringAfterLast(srcFile.getName(), ".");
							
							// Сначала прочитать файл, перед тем как он может быть удален
							BufferedImage srcImg = ImageIO.read(srcFile);
			
							// Проверка, нужен ли ресайз
							boolean resizeNeeded = (width > 0 && srcImg.getWidth() > width) || (height > 0 && srcImg.getHeight() > height);
							//Если нужен ресайз - ресайзим.
							if (resizeNeeded) {
							String fileName = StringUtils.substringBeforeLast(srcFile.getName(), ".") + '.' + format;
							if (!selfResize)
								fileName = param.getName() + "_" + fileName;
							destFile = new File(createItemDirectoryName() + "/" + fileName);
							resize(srcImg, destFile, width, height, format);
							// Установка значения параметра
							item.setValueUI(param.getId(), fileName);
							}
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
						// Ничего не делать если меняется размер одного и того же параметра, но он сам не был изменен
						if (selfResize && !item.getParameter(srcParam.getId()).hasChanged())
							continue;

						// Удалить все файлы и значения параметра в случае если параметр назначения не совпадает с параметром источника
						MultipleParameter destVals = (MultipleParameter) item.getParameter(param.getId());
						if (!selfResize) {
							for (SingleParameter val : destVals.getValues()) {
								File deleteFile = new File(createItemDirectoryName() + "/" + val.getValue());
								if (deleteFile.isFile() && !deleteFile.delete())
									throw new Exception("File '" + deleteFile.getName() + "' can not be deleted");
							}
							destVals.clear();
						}
						ArrayList<SingleParameter> vals = new ArrayList<>(((MultipleParameter) item.getParameter(srcParam
								.getId())).getValues());
						for (SingleParameter srcVal : vals) {
							File srcFile = new File(createItemDirectoryName() + "/" + srcVal.getValue());
							if (srcFile.isFile()) {
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
									File destFile = new File(createItemDirectoryName() + "/" + fileName);
									resize(srcImg, destFile, width, height, format);
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
					Connection conn = getTransactionContext().getConnection();
					// Сохранить новое ключевое значение и параметры в основную таблицу
					String sql = "UPDATE " + ITEM_TBL + " SET " + I_KEY + "=?, " + I_T_KEY + "=?, " + I_PARAMS + "=?, "
							+ I_UPDATED + "=NULL WHERE " + I_ID + "=" + item.getId();
				try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
					pstmt.setString(1, item.getKey());
					pstmt.setString(2, item.getKeyUnique());
					pstmt.setString(3, item.outputValues());
					pstmt.executeUpdate();
					pstmt.close();
					
					// Выполнить запросы для сохранения параметров
					ItemMapper.insertItemParametersToIndex(item, ItemMapper.Mode.UPDATE, getTransactionContext());
				}
			}
		}
		
		private void resize(BufferedImage srcImg, File destFile, int width, int height, String format) throws Exception {
			// Если исходный файл и файл назначения совпадают (т.е. ресайзится картинка одного параметра)
			// то надо удалить старый файл
			if (destFile.isFile() && !destFile.delete())
				throw new Exception("File '" + destFile.getName() + "' can not be deleted");

			//			Thumbnails.Builder<BufferedImage> thumbnailer = Thumbnails.of(srcImg);
//			// Если заданы и высота и ширина, выбрать один из этих параметров
//			// на основании соотношения сторон исходного изображения
//			if (width > 0 && height > 0) {
//
//			}
//			if (width > 0)
//				thumbnailer.width(width);
//			if (height > 0) {
//				thumbnailer.height(height);
//				if (width > 0 && crop != null)
//					thumbnailer.crop(crop);
//			}
//			thumbnailer.outputFormat(format).toFile(destFile);

			BufferedImage result = ResizeImages.getScaledInstance(srcImg, width, height, 1.5);
			ImageIO.write(result, format, destFile);

			// Добавить файл для удаления в случае отката команды
			files.add(destFile);
		}


		public static BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint, double stepQuotient) {
			if (targetHeight > 0 && targetWidth <= 0) {
				double quotient = ((double) img.getHeight()) / ((double) targetHeight);
				targetWidth = (int) (img.getWidth() / quotient);
			} else if (targetWidth > 0 && targetHeight <= 0) {
				double quotient = ((double) img.getWidth()) / ((double) targetWidth);
				targetHeight = (int) (img.getHeight() / quotient);
			} else if (targetHeight > 0 && targetWidth > 0) {
				double ratio = ((double) img.getWidth()) / ((double) img.getHeight());
				double quotientX = ((double) img.getWidth()) / ((double) targetWidth);
				double quotientY = ((double) img.getHeight()) / ((double) targetHeight);
				// обрезка по Y
				if (quotientX > quotientY) {
					targetHeight = (int) (targetWidth / ratio);
				}
				// обрезка по X
				else {
					targetWidth = (int) (targetHeight * ratio);
				}
			} else {
				return img;
			}

			BufferedImage ret = img;

			int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
			int w, h;
			if (stepQuotient > 1) {
				// Use multi-step technique: start with original size, then
				// scale down in multiple passes with drawImage()
				// until the target size is reached
				w = img.getWidth();
				h = img.getHeight();
			} else {
				// Use one-step technique: scale directly from original
				// size to target size with a single drawImage() call
				w = targetWidth;
				h = targetHeight;
			}
			if((w <= targetWidth || h <= targetHeight) && stepQuotient > 1) return  ret;
			int emergencyStopper = 20;
			int em = 0;
			do {
				em++;

				if (stepQuotient > 1) {
					if (w > targetWidth) {
						w /= stepQuotient;
						if (w < targetWidth) {
							w = targetWidth;
						}
					}

					if (h > targetHeight) {
						h /= stepQuotient;
						if (h < targetHeight) {
							h = targetHeight;
						}
					}
				}

				BufferedImage tmp = new BufferedImage(w, h, type);
				Graphics2D g2 = tmp.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
				g2.drawImage(ret, 0, 0, w, h, null);
				g2.dispose();

				ret = tmp;
			} while ((w != targetWidth || h != targetHeight) && (em < emergencyStopper));

			return ret;
		}


		public static BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight, double stepQuotient) {
			return getScaledInstance(img, targetWidth, targetHeight, RenderingHints.VALUE_INTERPOLATION_BILINEAR ,stepQuotient);
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
	public PersistenceCommandUnit createCommand(Item item) throws Exception {
		return new ResizeImages(item);
	}

	public static ByteArrayOutputStream resize(File src, int width, int height) throws IOException {
		String format = StringUtils.substringAfterLast(src.getName(), ".");
		BufferedImage srcImg = ImageIO.read(src);

//		Thumbnails.Builder<BufferedImage> thumbnailer = Thumbnails.of(srcImg);
//		if (width > 0)
//			thumbnailer.width(width);
//		if (height > 0)
//			thumbnailer.height(height);
//		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
//		thumbnailer.outputFormat(format).toOutputStream(ostream);

		BufferedImage result = ResizeImages.getScaledInstance(srcImg, width, height, 1.5);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(result, format, out);
		return out;
	}


	public static void main(String[] args) throws IOException {
		BufferedImage first = ImageIO.read(new File("G:/test/1.jpg"));
		BufferedImage second = ImageIO.read(new File("G:/test/2.jpg"));
		ImageIO.write(ResizeImages.getScaledInstance(first, 300, 0, 1.5), "JPEG", new File("G:/test/1_300_X.jpg"));
		ImageIO.write(ResizeImages.getScaledInstance(second, 300, 0, 1.5), "JPEG", new File("G:/test/2_300_X.jpg"));
		ImageIO.write(ResizeImages.getScaledInstance(first, 0, 300, 1.5), "JPEG", new File("G:/test/1_X_300.jpg"));
		ImageIO.write(ResizeImages.getScaledInstance(second, 0, 300, 1.5), "JPEG", new File("G:/test/2_X_300.jpg"));
		ImageIO.write(ResizeImages.getScaledInstance(first, 300, 300, 1.5), "JPEG", new File("G:/test/1_300_300.jpg"));
		ImageIO.write(ResizeImages.getScaledInstance(second, 300, 300, 1.5), "JPEG", new File("G:/test/2_300_300.jpg"));
	}
}
