package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.JsoupUtils;
import ecommander.fwk.Pair;
import ecommander.fwk.Timer;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.datatypes.DataType;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TranslationCommand extends Command {

    private static HashMap<String, String> TRANS = new HashMap();
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

        // Загрузка словаря из файлов
        File translateDir = new File(AppContext.getRealPath(TRANSLATE));
        Collection<File> textFiles = FileUtils.listFiles(translateDir, null, true);
        for (File textFile : textFiles) {
            Long lastEdited = TIMES.get(textFile.getName());
            if (lastEdited == null || lastEdited != textFile.lastModified()) {
                TIMES.put(textFile.getName(), lastEdited);
                List<String> lines = FileUtils.readLines(textFile, StandardCharsets.UTF_8);
                for (String line : lines) {
                    if (!StringUtils.startsWith(line, "#")) {
                        String[] words = StringUtils.splitByWholeSeparator(line, "::");
                        if (words.length == 2) {
                            String english = StringUtils.normalizeSpace(words[0]);
                            String russian = StringUtils.normalizeSpace(words[1]);
                            TRANS.put(english, russian);
                            english = StringUtils.lowerCase(english);
                            TRANS.put(english, russian);
                            TRANS.put(StringUtils.capitalize(english), StringUtils.capitalize(russian));
                            TRANS.put(english.toUpperCase(), russian.toUpperCase());
                        } else if (words.length == 1) {
                            String english = StringUtils.normalizeSpace(words[0]);
                            TRANS.put(english, "");
                            english = StringUtils.lowerCase(english);
                            TRANS.put(english, "");
                            TRANS.put(StringUtils.capitalize(english), "");
                            TRANS.put(english.toUpperCase(), "");
                        }
                    }
                }
            }
        }

        Timer.getTimer().start("TRANSLATION");
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
        // HashSet<String> productExtenders = ItemTypeRegistry.getItemExtenders("abstract_product");
        for (String itemPageId : itemsAndParams.keySet()) {
            LinkedHashMap<Long, Item> items = getLoadedItems(itemPageId);
            for (Item item : items.values()) {
                HashSet<String> itemParams = itemsAndParams.get(itemPageId);
                if (itemParams.size() == 0) {
                    itemParams.addAll(item.getItemType().getParameterNames());
                }
                for (String parameterName : itemParams) {
                    DataType type = item.getItemType().getParameter(parameterName).getDataType();

                    // Если продукт и параметр XML
                    if (type.getType() == DataType.Type.XML/* && productExtenders.contains(item.getTypeName())*/) {
                        String value = item.getStringValue(parameterName);
                        if (StringUtils.isNotBlank(value)) {
                            Document doc = JsoupUtils.parseXml(value);
                            boolean isFilter = doc.select("filter").size() > 0;
                            if (isFilter) {
                                Elements names = doc.select("name");
                                for (Element nameEl : names) {
                                    nameEl.text(translate(nameEl.text()));
                                }
                                Elements caps = doc.select("cap");
                                for (Element capEl : caps) {
                                    capEl.text(translate(capEl.text()));
                                }
                            } else {
                                Elements names = doc.select("name");
                                for (Element nameEl : names) {
                                    nameEl.text(translate(nameEl.text()));
                                }
                                Elements values = doc.select("value");
                                for (Element valueEl : values) {
                                    valueEl.text(translate(valueEl.text()));
                                }
                            }
                            item.clearValue(parameterName);
                            item.setValue(parameterName, JsoupUtils.outputXmlDoc(doc));
                        }
                    }
                    // Если параметр - tuple
                    else if (type.getType() == DataType.Type.TUPLE) {
                        ArrayList<Object> vals = item.getValues(parameterName);
                        item.clearValue(parameterName);
                        for (Object val : vals) {
                            Pair<String, String> tupleVal = (Pair<String, String>) val;
                            Pair<String, String> newVal = new Pair<>(translate(tupleVal.getLeft()), translate(tupleVal.getRight()));
                            item.setValue(parameterName, newVal);
                        }
                    }
                    // все остальные случаи (строки и тексты)
                    else if (type.isBigText() || type.getType() == DataType.Type.STRING) {
                        ArrayList<Object> vals = item.getValues(parameterName);
                        item.clearValue(parameterName);
                        for (Object val : vals) {
                            item.setValue(parameterName, translate((String) val));
                        }
                    }
                }
            }
        }
        Timer.getTimer().stop("TRANSLATION");
        System.out.println("\n\n\n\n\n" + Timer.getTimer().writeTotals());
        Timer.getTimer().finish();

        return null;
    }

    private String translate(String original) {
        String translated = TRANS.get(original);
        if (translated == null)
            return original;
        return translated;
    }
}
