package ecommander.application.extra;

import ecommander.common.MysqlConnector;
import ecommander.common.ServerLogger;
import ecommander.model.datatypes.DataType;
import ecommander.model.item.Item;
import ecommander.model.item.MultipleParameter;
import ecommander.model.item.ParameterDescription;
import ecommander.model.item.SingleParameter;
import ecommander.persistence.PersistenceCommandUnit;
import ecommander.persistence.TransactionContext;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.ItemMapper;
import ecommander.services.filesystem.ItemFileUnit;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Команда, которая преобразует картинки айтема к определенному формату после сохранения айтема
 * Используется атрибут format определения параметра (ParameterDescription)
 * <p>
 * src - параметр, который хранит исходную картинку, если он пустой - исходная картинка есть сам параметр
 * width - новая ширина картинки (в пикселях), если не установлена, масштабируется пропорционально с высотой
 * height - новая высота картинки (в пикселях), если не установлена, масштабируется пропорционально с шириной
 * format - формат файла (расширение), используется в алгоритмах сжатия. Также осуществляется приведние нестандартных формтов изображения к JPG. Допустиыми форматами являются: jpg, png, gif, svg.
 * crop - если заданы ширина и высота, каким образом обрезать картинку
 * (BOTTOM_CENTER, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER, CENTER_LEFT, CENTER_RIGHT, TOP_CENTER, TOP_LEFT, TOP_RIGHT)
 * parameter - параметр  типа "string", хранящий дополнительные опции. Позволяет переопределять существующие или задавать доплнительные настройки ресайза.
 * <p>
 * <p>
 * format="src:main_img;width:400;height:500;crop:CENTER"
 * картинка делается размерами 400х500 и обрезается по центру (чтобы не деформировалось изображение)
 * <p>
 * format="src:main_img;width:400;height:500"
 * ширина и высота преборазуются таким образом, чтобы максимальный был равен заданному соответствующему,
 * в то же время сохраняются пропорции картинки
 * <p>
 * format="src:main_img;width:400"
 * картинка из параметра main_img преобразуется к ширине 400 и высоте, пропорционально изменению ширины
 * <p>
 * format="height:50;format:gif"
 * картинка самого параметра преобразуется к высоте 400 и ширине, пропорционально изменению высоты
 * <p>
 * Принцип работы:
 * Если картинка для ресайза имеет заполненный src, а также значение - файл (т. е. есть файл картинки для этого параметра),
 * то этот файл не заменяется. Однако, если источником картинки является сам этот параметр, т.е. сам должен ресайзиться,
 * то ресайз происходит всегда, даже когда есть заполненная картинка.
 *
 * @author Anton
 */
public class ResizeAndBackupImagesFactory implements ItemEventCommandFactory {

    //-- ********** названия параметров ресайза *********
    public static final String SRC = "src";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String FORMAT = "format";
    public static final String CROP = "crop";
    public static final String FORMAT_PARAM = "parameter";

    // префикс и постфикс к записи о последнем формате файла
    public static final String S_RSZ = "-srsz-";
    public static final String E_RSZ = "-ersz-";

    //
    public static final String ALLOWED_TYPES = "([^\\s]+(\\.(?i)(jpg|png|gif|svg))$)";
    public static final String OLD = "old-";

    public PersistenceCommandUnit createSaveCommand(Item item) {
        return new ResizeImages(item);
    }

    public PersistenceCommandUnit createDeleteCommand(Item item) {
        return null;
    }

    public PersistenceCommandUnit createUpdateCommand(Item updated, Item initial) {
        return new ResizeImages(updated);
    }

    public static class ResizeImages extends ItemFileUnit {

        private TransactionContext transaction;
        private ArrayList<File> files = new ArrayList<>();
        private String format;
        private String itemFolder;
        private String backupFolder;
        private String currentFormat;
        //-- Чтобы настройки всегда были в одном порядке
        private TreeMap<String, String> resizeSettings = new TreeMap<>();
        private Set<File> junkFiles = new HashSet<File>();

        private ResizeImages(Item item) {
            super(item);
            format = null;
        }

        public ResizeImages(Item item, String format) {
            super(item);
            this.format = format;
        }

        public void execute() throws Exception {

            itemFolder = createItemFileDirectoryName();
            backupFolder = itemFolder + "backup/";

            for (ParameterDescription param : item.getItemType().getParameterList()) {
                if (param.getType() == DataType.Type.PICTURE && (param.hasFormat() || format != null)) {

                    resizeSettings = new TreeMap<String, String>();

                    currentFormat = format;
                    if (param.hasFormat())
                        currentFormat = param.getFormat();
                    // Составление списка настроек ресайза
                    String[] sa = currentFormat.split("[;]");
                    String[] externalFormat = null;
                    for (String s : sa) {
                        String[] sa2 = s.split("[:]");
                        sa2[0] = sa2[0].trim();
                        sa2[1] = sa2[1].trim();
                        //если есть внешний параметр с форматом
                        if (sa2[0].equalsIgnoreCase(FORMAT_PARAM) && StringUtils.isNotBlank(sa2[1])) {
                            externalFormat = sa2.clone();
                        } else {
                            resizeSettings.put(sa2[0], sa2[1]);
                        }
                    }
                    if (externalFormat != null) {
                        currentFormat = item.getStringValue(externalFormat[1]);
                        if (StringUtils.isNotBlank(currentFormat)) {
                            sa = currentFormat.split("[;]");
                            for (String s : sa) {
                                String[] sa2 = s.split("[:]");
                                sa2[0] = sa2[0].trim();
                                sa2[1] = sa2[1].trim();
                                resizeSettings.put(sa2[0], sa2[1]);
                            }
                        }
                    }
                    currentFormat = "";
                    for (Map.Entry<String, String> e : resizeSettings.entrySet()) {
                        currentFormat += e.getKey() + "-" + e.getValue() + "_";
                    }

                    boolean selfResize = StringUtils.isBlank(resizeSettings.get(SRC));

                    //Намеренно другой файл или уже существует
                    if (!selfResize) {
                        String destValue = (String) item.getValue(param.getId());
                        if (StringUtils.isNotBlank(destValue)) {
                            selfResize = true;
                        }
                    }

                    //-- Окночательно и бесповоротно обпределиться с SRC PARAM
                    String srcParamName = (selfResize) ? param.getName() : resizeSettings.get(SRC);

                    //-- Определить, является ли SRC PARAM рисунком
                    ParameterDescription srcParam = item.getItemType().getParameter(srcParamName);
                    if (srcParam.getType() != DataType.Type.PICTURE) {
                        throw new Exception("There is no picture parameter '" + srcParamName + "' in itemtype \"" + item.getTypeName() + "\"");
                    }

                    //-- Если не задано значение - то переходим к следующему параметру.
                    String currentValue = (String) item.getValue(srcParamName);
                    if (StringUtils.isBlank(currentValue)) {
                        continue;
                    }

                    File srcFile = new File(itemFolder + item.getValue(srcParamName));

                    // Одиночный параметр
                    if (!srcParam.isMultiple()) {
                        try {
                            //-- Предыдущие настройки ресайза
                            String previousFormat = getPreviousFormat(currentValue);


                            //-- Определяем типы файлов бэкапа и нового файла.
                            String backupFileName = getBackupFileName(currentValue);

                            //-- Задан ли формат
                            String imgFormat = getImgFormat(backupFileName);

                            boolean resizeNeeded = StringUtils.isBlank(currentValue) || !currentFormat.equalsIgnoreCase(previousFormat);

                            File backupFile = new File(backupFolder + backupFileName);
                            if (!backupFile.exists()) {
                                if (!srcFile.exists()) {
                                    continue;
                                }
                                FileUtils.moveFile(srcFile, backupFile);
                            }
                            if (!resizeNeeded) continue;



                            //-- Собственно ресайз
                            String destFileName = resizePic(backupFile, imgFormat);

                            //-- Заноасим старый файл в список файлов на удаление.
                            junkFiles.add(new File(itemFolder + item.getValue(param.getId())));

                            //-- Задаем новое значение параметра
                            item.setValueUI(param.getId(), destFileName);

                        } catch (Exception e) {
                            ServerLogger.error("Error resizing image", e);
                        }
                    }

                    // Множественный параметр
                    else if (param.isMultiple() && item.getItemType().getParameter(srcParamName).isMultiple()) {

                        boolean resizeNeeded = false;
                        MultipleParameter destVals = (MultipleParameter) item.getParameter(param.getId());

                        // Если саморесайз и пустые
                        if (selfResize && destVals.isEmpty()) continue;

                        // НЕВОЗМОЖНО будет создать заданные пользователем маленькие картинки.
                        if (!selfResize) {

                            //--************************ ОПРЕДЕЛЯЕМ, НУЖЕН ЛИ РЕСАЙЗ *********************
                            String previousFormat = "";
                            MultipleParameter srcVals = (MultipleParameter) item.getParameter(item.getItemType().getParameter(srcParamName).getId());
                            Collection<SingleParameter> oldVals = destVals.getValues();
                            Collection<SingleParameter> newVals = srcVals.getValues();
                            //-- Если нету кратинок в SRC-param - не делаем ничего. Ресайз не нужен.
                            if (newVals.isEmpty()) continue;
                            //-- Если нету в DEST-param - то однозначено нужен.
                            if (oldVals.isEmpty()) {
                                resizeNeeded = true;
                            } else {

                                //-- Если есть SRC и DEST - сравниваем формат ресайза и запись о последнем формате из иени файла.
                                //-- Если они не совпадают - ресайз нужен
                                SingleParameter[] oldArr = new SingleParameter[oldVals.size()];
                                oldVals.toArray(oldArr);
                                // Arrays.sort(oldArr);
                                for (int i = 0; i < oldArr.length; i++) {
                                    String fileName = oldArr[i].outputValue();
                                    previousFormat = getPreviousFormat(fileName);
                                    if (StringUtils.isBlank(previousFormat) || !StringUtils.substringBefore(previousFormat, OLD).equals(currentFormat)) {
                                        resizeNeeded = true;
                                        break;
                                    }
                                }
                                if (!resizeNeeded) {
                                    ArrayList<String> o = new ArrayList<String>();
                                    if (StringUtils.isBlank(previousFormat) || !StringUtils.substringBefore(previousFormat, OLD).equals(currentFormat)) {
                                        resizeNeeded = true;
                                    }

                                    //-- Если формтат совпадает - проверяем имена фалов до ресайза. Если есть различия - нужен ресайз
                                    for (SingleParameter p : oldArr) {
                                        String x = StringUtils.substringBefore(p.outputValue(), S_RSZ);
                                        o.add(x);
                                    }

                                    SingleParameter[] newArr = new SingleParameter[newVals.size()];
                                    oldVals.toArray(newArr);
                                    ArrayList<String> n = new ArrayList<String>();
                                    for (SingleParameter p : newArr) {
                                        String x = StringUtils.substringBefore(p.outputValue(), S_RSZ);
                                        n.add(x);
                                    }
                                    if (!o.equals(n)) {
                                        resizeNeeded = true;
                                    }
                                }
                            }
                            //-- ОПРЕДЕЛИЛИ
                            if (!resizeNeeded) continue;

                            //-- СОБСТВЕННО РЕСАЙЗ
                            // удаляем DEST файлы.
                            for (SingleParameter p : oldVals) {
                                String name = p.getValue().toString();
                                File f = new File(itemFolder + name);
                                f.delete();
                            }
                            // очищаем значение парамета
                            item.removeValue(param.getId());

                            // бэкапим и ресайзим SRC - файлы
                            for (SingleParameter p : newVals) {
                                String name = p.getValue().toString();
                                File src = new File(itemFolder + name);

                                //Бэкап
                                String backupFileName = getBackupFileName(name);
                                File backupFile = new File(backupFolder + backupFileName);

                                if (!src.exists()) {
                                    item.removeEqualValue(srcParamName, name);
                                    continue;
                                }
                                if (!backupFile.exists()) {
                                    FileUtils.copyFile(src, backupFile);
                                }

                                //Ресайз
                                String imgFormat = getImgFormat(backupFileName);

                                String destFileName = resizePic(backupFile, imgFormat);
                                item.setValueUI(param.getId(), destFileName);
                            }
                        } else {

                            //--************************ ОПРЕДЕЛЯЕМ, НУЖЕН ЛИ РЕСАЙЗ (при саморесайзе) *************
                            Collection<SingleParameter> oldVals = destVals.getValues();
                            if (oldVals.isEmpty()) continue;
                            SingleParameter[] oldArr = new SingleParameter[oldVals.size()];
                            oldVals.toArray(oldArr);
                            for (int i = 0; i < oldArr.length; i++) {
                                String fileName = oldArr[i].outputValue();
                                String previousFormat = getPreviousFormat(fileName);
                                if (StringUtils.isBlank(previousFormat) || !StringUtils.substringBefore(previousFormat, OLD).equals(currentFormat)) {
                                    resizeNeeded = true;
                                    break;
                                }
                            }
                            //-- ОПРЕДЕЛИЛИ
                            if (!resizeNeeded) continue;

                            //-- СОБСТВЕННО (само)РЕСАЙЗ
                            // очищаем значение парамета
                            item.removeValue(param.getId());
                            for (SingleParameter p : oldVals) {
                                String name = p.getValue().toString();
                                File src = new File(itemFolder + name);
                                if (!src.exists()) {
                                    continue;
                                }

                                //Бэкап
                                String backupFileName = getBackupFileName(name);
                                File backupFile = new File(backupFolder + backupFileName);

                                if (!backupFile.exists()) {
                                    FileUtils.moveFile(src, backupFile);
                                }else{
                                    junkFiles.add(src);
                                }
                                //-- Задан ли формат
                                String destFileName = resizePic(backupFile, getImgFormat(backupFileName));
                                item.setValueUI(param.getId(), destFileName);

                            }
                        }
                    }

                }
            }

            // Очистить бэкап
            purgeFolder();

            // Апдейт базы данных (сохранение новых параметров айтема)
            if (!item.isConsistent()) {
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

        private void purgeFolder(){
            File bck = new File(backupFolder);
            File sd = new File(itemFolder);
            Iterator<File> iter = FileUtils.iterateFiles(bck,null,false);

            while(iter.hasNext()){
                File b = iter.next();
                String envName = StringUtils.substringBeforeLast(b.getName(),".");
                String fType = StringUtils.substringAfterLast(b.getName(),".");
                Collection<File> f = FileUtils.listFiles(sd, new WildcardFileFilter(envName + "*" + OLD + fType + "*"), TrueFileFilter.TRUE);
                if(f == null || f.size() == 0){
                   // junkFiles.add(b);
                    b.delete();
                }
            }
            for(File fl : junkFiles){
                fl.delete();
            };
        }

        /**
         * Ишет в имени файла запись о предыдущем ресайзе.
         * Возвращает строку между "-srsz-" и "-ersz-" или пустую строку.
         *
         * @param fileName
         */
        private String getPreviousFormat(String fileName) {
            return (fileName.lastIndexOf(S_RSZ) != -1 && fileName.lastIndexOf(S_RSZ) < fileName.lastIndexOf(E_RSZ)) ?
                    fileName.substring(fileName.lastIndexOf(S_RSZ) + S_RSZ.length(), fileName.lastIndexOf(E_RSZ)) : "";
        }

        /**
         * Определяет тип файла бэкапа.
         * Возвращает строку после "old-" в записи о ресайзе или строку после последней точки.
         *
         * @param fileName
         */
        private String getBackupFileType(String fileName) {
            String prevFormat = getPreviousFormat(fileName);
            return (StringUtils.isNotBlank(prevFormat) && prevFormat.indexOf(OLD) != -1) ?
                    StringUtils.substringAfterLast(prevFormat, OLD) :
                    StringUtils.substringAfterLast(fileName, ".");
        }

        /**
         * Определяет имя файла бэкапа.
         * Соствляет имя из строки до "-srsz-" и типа файла бэкапа;
         *
         * @param fileName
         */
        private String getBackupFileName(String fileName) {
            String oldFileType = getBackupFileType(fileName);
            return (fileName.indexOf(S_RSZ) != -1 || fileName.indexOf(E_RSZ + ".") != -1) ? StringUtils.substringBeforeLast(fileName, S_RSZ) + "." + oldFileType : fileName;
        }

        /**
         * Определяет тип файла после ресайза.
         * Возвращает значение из праметров ресайза, тип файл бэкапа имени файла бэкапа, если параметр не задан или "jpg", если тип файла бэкапа не являтся допустимым.
         *
         * @param backupFileName
         */
        private String getImgFormat(String backupFileName) {

            String oldFileType = StringUtils.substringAfterLast(backupFileName, ".");

            //-- Задан ли формат
            String imgFormat = resizeSettings.get(FORMAT);

            //-- Сброс формата (формат берется из бэкапа)
            imgFormat = (StringUtils.isNotBlank(imgFormat) && imgFormat.equalsIgnoreCase("auto")) ? oldFileType : imgFormat;

            //-- Определяем, нужен ли автоподбор формата
            if (StringUtils.isBlank(imgFormat)) {

                Pattern allowedImgTypes = Pattern.compile(ALLOWED_TYPES);
                Matcher matcher = allowedImgTypes.matcher("a." + oldFileType);

                imgFormat = (matcher.matches()) ? oldFileType : "jpg";
                if (!matcher.matches() || !imgFormat.equals(oldFileType)) {
                    currentFormat += FORMAT + "-" + imgFormat + "_";
                }
            }
            currentFormat =(currentFormat.indexOf(OLD) != -1)? StringUtils.substringBefore(currentFormat, OLD) + OLD + oldFileType : currentFormat + OLD + oldFileType;
            return imgFormat;
        }

        /**
         * Подготавливает и производит ресайз файла.
         *
         * @param src       - файл бэкапа
         * @param imgFormat - тип после ресайза
         */
        private String resizePic(File src, String imgFormat) throws IOException {
            BufferedImage srcImg = ImageIO.read(src);
            String backupFileName = src.getName();
            String destFileName = StringUtils.substringBeforeLast(backupFileName, ".") + S_RSZ + currentFormat + E_RSZ + "." + imgFormat;
            File destFile = new File(itemFolder + destFileName);
            int width = (StringUtils.isBlank(resizeSettings.get(WIDTH)) || resizeSettings.get(WIDTH).equalsIgnoreCase("auto")) ? 0 : Integer.parseInt(resizeSettings.get(WIDTH));
            int height = (StringUtils.isBlank(resizeSettings.get(HEIGHT)) || resizeSettings.get(HEIGHT).equalsIgnoreCase("auto")) ? 0 : Integer.parseInt(resizeSettings.get(HEIGHT));

            Positions crop = (resizeSettings.get(CROP) != null) ? Positions.valueOf(resizeSettings.get(CROP)) : null;

            try {
                resize(srcImg, destFile, width, height, imgFormat, crop);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return destFileName;
        }


        private void resize(BufferedImage srcImg, File destFile, int width, int height, String format, Positions crop) throws Exception {
            // Если исходный файл и файл назначения совпадают (т.е. ресайзится картинка одного параметра)
            // то надо удалить старый файл
            if (destFile.exists() && !destFile.delete())
                throw new Exception("File '" + destFile.getName() + "' can not be deleted");
            Thumbnails.Builder<BufferedImage> thumbnailer = Thumbnails.of(srcImg);
            // Если заданы и высота и ширина, выбрать один из этих параметров
            // на основании соотношения сторон исходного изображения
            if (height > 0) {
                thumbnailer.height(height);
            }
            if (width > 0) {
                thumbnailer.width(width);
            }
            if (crop != null) {
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

}
