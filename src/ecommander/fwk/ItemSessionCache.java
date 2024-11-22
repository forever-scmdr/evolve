package ecommander.fwk;

import ecommander.controllers.SessionContext;
import ecommander.model.Item;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * Кеш айтемов для сеанса одного пользователя
 * Сюда можно сохранять айтемы, которые часто надо загружать в процессе одного сеанса пользователя
 */
public class ItemSessionCache {
    public static final String SESSION_VAR_NAME = "personal_session_cache";

    private HashMap<String, Collection<Item>> items = new HashMap<>();

    public interface Loader {
        Collection<Item> load();
    }

    /**
     * Возвращает из сеанса айтемы, если их еще нет - загружает и возвращает
     * @param name
     * @param session
     * @param loader
     * @return
     */
    public static Collection<Item> get(String name, SessionContext session, Loader loader) {
        ItemSessionCache cache = getCache(session);
        Collection<Item> items = cache.items.get(name);
        if (items == null) {
            items = loader.load();
            if (items == null) {
                items = CollectionUtils.emptyCollection();
            }
            cache.items.put(name, items);
        }
        return items;
    }

    /**
     * Возвращает из сеанса один айтем, если его еще нет - загружает и возвращает
     * @param name
     * @param session
     * @param loader
     * @return
     */
    public static Item getSingle(String name, SessionContext session, Loader loader) {
        Collection<Item> items = get(name, session, loader);
        if (items.size() == 0)
            return null;
        return items.stream().iterator().next();
    }

    /**
     * Сохранить в явном виде объект (без загрузки)
     * @param name
     * @param object
     * @param session
     */
    public static void put(String name, Item object, SessionContext session) {
        getCache(session).items.put(name, Collections.singletonList(object));
    }

    private static ItemSessionCache getCache(SessionContext session) {
        ItemSessionCache cache = (ItemSessionCache) session.getObject(SESSION_VAR_NAME);
        if (cache == null) {
            cache = new ItemSessionCache();
            session.setObject(SESSION_VAR_NAME, cache);
        }
        return cache;
    }
}
