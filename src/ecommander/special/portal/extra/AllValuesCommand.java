package ecommander.special.portal.extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.MysqlConnector;
import ecommander.fwk.Pair;
import ecommander.fwk.ServerLogger;
import ecommander.model.datatypes.TupleDataType;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashSet;

public class AllValuesCommand extends Command implements DBConstants {

    private LinkedHashSet<String> keys;
    private LinkedHashSet<String> values;

    public static final int STEP = 50000;
    public static final int PARAM_ID = 20666;
    public static final int MIN_MAX_ITEM_ID = 2000000;


    @Override
    public ResultPE execute() throws Exception {
        keys = new LinkedHashSet<>();
        values = new LinkedHashSet<>();
        boolean hadResult = true;
        int minItemId = 0;
        while (hadResult || minItemId <= MIN_MAX_ITEM_ID) {
            hadResult = false;
            int maxItemId = minItemId + STEP;
            TemplateQuery query = new TemplateQuery("select");
            query.sql("SELECT " + ItemIndexes.II_VALUE)
                    .FROM(ItemIndexes.STRING_INDEX_TBL).WHERE().col(ItemIndexes.II_PARAM).int_(PARAM_ID)
                    .AND().col(ItemIndexes.II_ITEM_ID, ">").int_(minItemId)
                    .AND().col(ItemIndexes.II_ITEM_ID, "<=").int_(maxItemId)
                    .sql(" GROUP BY " + ItemIndexes.II_VALUE);
            try (Connection conn = MysqlConnector.getConnection();
                 PreparedStatement pstmt = query.prepareQuery(conn)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String line = rs.getString(1);
                    Pair<String, String> paramValue = TupleDataType.parse(line, TupleDataType.DEFAULT_SEPARATOR);
                    String key = StringUtils.trim(paramValue.getLeft());
                    String value = StringUtils.trim(paramValue.getRight());
                    addKeyValue(key, value);
                    hadResult = true;
                }
            }
            minItemId = maxItemId;
            ServerLogger.warn("Now processing: " + minItemId);
        }
        String fileName = AppContext.getFilesDirPath(false) + "values.txt";
        FileWriter fileWriter = new FileWriter(fileName);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println("SPEC KEYS:");
        printWriter.println();
        for (String key : keys) {
            printWriter.println(key);
        }
        printWriter.println();
        printWriter.println("SPEC VALUES:");
        printWriter.println();
        for (String value : values) {
            printWriter.println(value);
        }
        printWriter.close();
        return null;
    }

    private void addKeyValue(String key, String value) {
        if (containsWords(key))
            keys.add(key);
        if (containsWords(value))
            values.add(value);
    }


    private boolean containsWords(String line) {
        String[] words = line.split("[^\\w']+");
        for (String word : words) {
            if (word.length() > 2 && StringUtils.isAlpha(word)) {
                for (int i = 1; i < word.length(); i++) {
                    // большая буква в середине слова
                    if (Character.isUpperCase(word.charAt(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
