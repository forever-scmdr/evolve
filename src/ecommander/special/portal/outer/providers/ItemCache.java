package ecommander.special.portal.outer.providers;

import ecommander.fwk.Pair;
import ecommander.model.Item;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.LinkedHashMap;

/**
 * Кеш для айтемов
 * Айтем хранится в кеше в течение 10 секунд, если он хранится дольше и запрошен, он перезагружается
 */
public class ItemCache {

    public interface ItemLoader {
        Item loadItem() throws Exception;
    }

    private static final int MAX_ITEMS = 1000;
    private static final int MAX_SECONDS = 10;

    private static ItemCache instance = new ItemCache(MAX_ITEMS, MAX_SECONDS);

    private int capacity;
    private int seconds;
    private LinkedHashMap<String, Pair<Item, Long>> items = new LinkedHashMap<>();

    private ItemCache(int capacity, int seconds) {
        this.capacity = capacity;
        this.seconds = seconds;
    }

    private synchronized Item getInternal(String key, ItemLoader loader) throws Exception {
        Pair<Item, Long> itemTime = items.get(key);
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
            Item item = loader.loadItem();
            if (items.size() >= capacity) {
                String firstKey = items.keySet().iterator().next();
                items.remove(firstKey);
            }
            items.put(key, new Pair<>(item, System.currentTimeMillis()));
            return item;
        }
    }

    public static Item get(String key, ItemLoader loader) throws Exception {
        return instance.getInternal(key, loader);
    }

}
