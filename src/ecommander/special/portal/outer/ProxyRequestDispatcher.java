package ecommander.special.portal.outer;

import ecommander.controllers.AppContext;
import ecommander.fwk.EcommanderException;
import ecommander.fwk.ErrorCodes;
import ecommander.fwk.Pair;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;

/**
 * Диспетчер распределения запросов по хостам и прокси серверам.
 * Хост нужно указывать, прокси сервера используются автоматически (делать что-то юзеру не нужно)
 */
public class ProxyRequestDispatcher {

    private static final String _GENERAL_ = "_GENERAL_";

    private static ProxyRequestDispatcher instance;

    // Все прокси сервера, которые участвуют в разделении трафика (т.е. которые должны чередоваться для каждого сервера данных)
    private ArrayList<Proxy> allProxies = new ArrayList<>();
    // Все сервера данных (для каждого сервера данных есть своя очередь прокси, которые чередуются, см. выше)
    private HashMap<String, HostThroughProxies> allHosts = new HashMap<>();

    private ProxyRequestDispatcher() {
        String proxiesProp = AppContext.getFwkProperty("fwk.portal.proxies");
        String hostsProp = AppContext.getFwkProperty("fwk.portal.data_hosts");
        int threadsPerProxy = NumberUtils.toInt(AppContext.getFwkProperty("fwk.portal.proxy_threads"), 3);
        String[] allProxiesStr = StringUtils.split(proxiesProp, ", ");
        String[] allHostsStr = StringUtils.split(hostsProp, ", ");
        for (String proxyStr : allProxiesStr) {
            String proxyAddress = StringUtils.lowerCase(StringUtils.normalizeSpace(proxyStr));
            allProxies.add(new Proxy(proxyAddress, threadsPerProxy));
        }
        allHosts.put(_GENERAL_, new HostThroughProxies(_GENERAL_, allProxies));
        for (String hostStr : allHostsStr) {
            String hostNameLower = StringUtils.lowerCase(StringUtils.normalizeSpace(hostStr));
            allHosts.put(hostNameLower, new HostThroughProxies(hostNameLower, allProxies));
        }
    }

    /**
     * Создание и получение экземпляра
     * @return
     */
    private static ProxyRequestDispatcher getInstance() {
        if (instance == null) {
            instance = new ProxyRequestDispatcher();
        }
        return instance;
    }

    /**
     * Отправить запрос (одно действие пользователя)
     * Надо указать хост для общего запроса и все поисковые запросы (когда их несколько)
     * Запрос распределится по прокси серверам.
     * Возвращается экземпляр класса Request. Его потом можно использовать, в частности подождать выполнения
     * @param hostName
     * @param queries
     * @return
     */
    public static Request submitRequest(String hostName, Collection<Pair<String, String>> queries) throws EcommanderException {
        hostName = StringUtils.lowerCase(StringUtils.normalizeSpace(hostName));
        HostThroughProxies host = getInstance().allHosts.get(hostName);
        if (host == null) {
            throw new EcommanderException(ErrorCodes.NO_SPECIAL_ERROR, "host '" + hostName + "' not registered. Can be registered in settings file");
        }
        Request request = new Request(hostName);
        for (Pair<String, String> query : queries) {
            request.addQuery(query.getLeft(), query.getRight());
        }
        request.submit(host);
        return request;
    }

    /**
     * Отправить запрос (одно действие пользователя)
     * Надо указать хост для общего запроса и все поисковые запросы (когда их несколько)
     * Запрос распределится по прокси серверам.
     * Возвращается экземпляр класса Request. Его потом можно использовать, в частности подождать выполнения
     * @param hostName
     * @param urls
     * @return
     * @throws EcommanderException
     */
    public static Request submitRequest(String hostName, String... urls) throws EcommanderException {
        ArrayList<Pair<String, String>> queryTypes = new ArrayList<>();
        for (String query : urls) {
            queryTypes.add(new Pair<>(query, "text/html"));
        }
        return submitRequest(hostName, queryTypes);
    }

    /**
     * Отправляет запрос. В качестве параметров передаются не пользовательские запросы как в методе submitRequest,
     * а полноценные урлы. Они распределяются по прокси серверам.
     * Возвращается экземпляр класса Request. Его потом можно использовать, в частности подождать выполнения
     * @param urls
     * @return
     */
    public static Request submitGeneralUrls(String resultMimeType, String... urls) throws EcommanderException {
        ArrayList<Pair<String, String>> queryTypes = new ArrayList<>();
        for (String url : urls) {
            queryTypes.add(new Pair<>(url, resultMimeType));
        }
        return submitRequest(_GENERAL_, queryTypes);
    }

    /**
     * Отправляет запрос. В качестве параметров передаются не пользовательские запросы как в методе submitRequest,
     * а полноценные урлы. Они распределяются по прокси серверам.
     * Возвращается экземпляр класса Request. Его потом можно использовать, в частности подождать выполнения
     * @param queries
     * @return
     * @throws EcommanderException
     */
    public static Request submitGeneralQueries(Collection<Pair<String, String>> queries) throws EcommanderException {
        return submitRequest(_GENERAL_, queries);
    }
}
