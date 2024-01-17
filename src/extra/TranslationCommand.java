package extra;

import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.datatypes.DataType;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TranslationCommand extends Command {

    private static HashMap<String, String> TRANS = new HashMap();
    private static String[] ENGLISH;
    private static String[] RUSSIAN;
    private static HashMap<String, Long> TIMES = new HashMap();
    public static final String TRANSLATE = "translate";

    @Override
    public ResultPE execute() throws Exception {

        // надо или не надо делать перевод
        String translateStr = getVarSingleValueDefault("translate", "false");
        boolean doTranslate = StringUtils.equalsAnyIgnoreCase(translateStr, "true", "yes");
        if (!doTranslate) {
            return null;
        }

        // Загрузка слваря из файлов
        File translateDir = new File(AppContext.getRealPath(TRANSLATE));
        Collection<File> textFiles = FileUtils.listFiles(translateDir, null, true);
        boolean hasChanged = false;
        for (File textFile : textFiles) {
            Long lastEdited = TIMES.get(textFile.getName());
            if (lastEdited == null || lastEdited != textFile.lastModified()) {
                TIMES.put(textFile.getName(), lastEdited);
                List<String> lines = FileUtils.readLines(textFile, StandardCharsets.UTF_8);
                hasChanged = true;
                for (String line : lines) {
                    if (!StringUtils.startsWith(line, "#")) {
                        String[] words = StringUtils.splitByWholeSeparator(line, "::");
                        if (words.length == 2) {
                            String english = StringUtils.normalizeSpace(words[0]);
                            String russian = StringUtils.normalizeSpace(words[1]);
                            english = StringUtils.lowerCase(english);
                            russian = StringUtils.lowerCase(russian);
                            TRANS.put(english, russian);
                            TRANS.put(StringUtils.capitalize(english), StringUtils.capitalize(russian));
                            TRANS.put(english.toUpperCase(), russian.toUpperCase());
                        } else if (words.length == 1) {
                            String english = StringUtils.normalizeSpace(words[0]);
                            english = StringUtils.lowerCase(english);
                            TRANS.put(english, "");
                            TRANS.put(StringUtils.capitalize(english), "");
                            TRANS.put(english.toUpperCase(), "");
                        }
                    }
                }
            }
        }
        if (hasChanged) {
            ENGLISH = new String[TRANS.size()];
            RUSSIAN = new String[TRANS.size()];
            int i = 0;
            for (String e : TRANS.keySet()) {
                String r = TRANS.get(e);
                ENGLISH[i] = e;
                RUSSIAN[i] = r;
                i++;
            }
        }

        // Поиск всех нужных айтемов и параметров и замена
        String idsString = getVarSingleValue("translate_items");
        String[] itemsAndParamsString = StringUtils.split(idsString, ", ");
        HashMap<String, HashSet<String>> itemsAndParams = new HashMap<>();
        for (String itemAndParams : itemsAndParamsString) {
            itemAndParams = StringUtils.normalizeSpace(itemAndParams);
            String[] parts = StringUtils.split(itemAndParams, ":");
            HashSet<String> params = new HashSet<>();
            for (int i = 1; i < parts.length; i++) {
                params.add(StringUtils.normalizeSpace(parts[i]));
            }
            itemsAndParams.put(parts[0], params);
        }
        for (String itemPageId : itemsAndParams.keySet()) {
            LinkedHashMap<Long, Item> items = getLoadedItems(itemPageId);
            for (Item item : items.values()) {
                HashSet<String> itemParams = itemsAndParams.get(itemPageId);
                if (itemParams.size() == 0) {
                    itemParams.addAll(item.getItemType().getParameterNames());
                }
                for (String parameterName : itemParams) {
                    DataType type = item.getItemType().getParameter(parameterName).getDataType();
                    if (type.isBigText() || type.getType() == DataType.Type.STRING) {
                        ArrayList<Object> vals = item.getValues(parameterName);
                        item.clearValue(parameterName);
                        for (Object val : vals) {
                            item.setValue(parameterName, translate((String) val));
                        }
                    }
                }
            }
        }

        return null;
    }

    private String translate(String original) {
        //return StringUtils.replaceEach(original, ENGLISH, RUSSIAN);
        for (int i = 0; i < ENGLISH.length; i++) {
            String english = ENGLISH[i];
            String russian = RUSSIAN[i];
            original = original.replaceAll("\\b" + english + "\\b", russian);
        }
        return original;
    }
}
