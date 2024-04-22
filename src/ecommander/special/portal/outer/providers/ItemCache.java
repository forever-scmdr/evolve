package ecommander.special.portal.outer.providers;

import ecommander.fwk.Pair;
import ecommander.model.Item;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Кеш для айтемов
 * Айтем хранится в кеше в течение 10 секунд, если он хранится дольше и запрошен, он перезагружается
 */
public class ItemCache {

    public interface ItemLoader {
        List<Item> loadItems() throws Exception;
    }

    private static final int MAX_ITEMS = 1000;
    private static final int MAX_SECONDS = 100;

    private static ItemCache instance = new ItemCache(MAX_ITEMS, MAX_SECONDS);

    private int capacity;
    private int seconds;
    private LinkedHashMap<String, Pair<List<Item>, Long>> items = new LinkedHashMap<>();

    private ItemCache(int capacity, int seconds) {
        this.capacity = capacity;
        this.seconds = seconds;
    }

    private synchronized List<Item> getInternal(String key, ItemLoader loader) throws Exception {
        Pair<List<Item>, Long> itemTime = items.get(key);
        boolean returnFromCache = itemTime != null;
        if (returnFromCache) {
            DateTime now = DateTime.now(DateTimeZone.UTC);
            DateTime saved = new DateTime(itemTime.getRight(), DateTimeZone.UTC);
            if (saved.plusSeconds(seconds).isBefore(now)) {
                returnFromCache = false;
            }
        }
        if (returnFromCache) {
            items.remove(key);
            items.put(key, new Pair<>(itemTime.getLeft(), System.currentTimeMillis()));
            return itemTime.getLeft();
        } else {
            List<Item> loaded = loader.loadItems();
            if (items.size() >= capacity) {
                String firstKey = items.keySet().iterator().next();
                items.remove(firstKey);
            }
            items.put(key, new Pair<>(loaded, System.currentTimeMillis()));
            return loaded;
        }
    }

    /**
     * Вернуть (если надо загрузить и закешировать) один айтем
     * @param key
     * @param loader
     * @return
     * @throws Exception
     */
    public static Item get(String key, ItemLoader loader) throws Exception {
        List<Item> cached = instance.getInternal(key, loader);
        return cached != null && cached.size() > 0 ? cached.get(0) : null;
    }

    /**
     * Вернуть (если надо загрузить и закешировать) массив айтемов
     * @param key
     * @param loader
     * @return
     * @throws Exception
     */
    public static List<Item> getArray(String key, ItemLoader loader) throws Exception {
        return instance.getInternal(key, loader);
    }
}
