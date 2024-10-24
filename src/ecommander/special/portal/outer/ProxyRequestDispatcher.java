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

    private static final String _GENERAL_ = "_general_";
    private static final String _ROTATING_ = "_rotating_";

    private static ProxyRequestDispatcher instance;

    // Все прокси сервера, которые участвуют в разделении трафика (т.е. которые должны чередоваться для каждого сервера данных)
    private ArrayList<Proxy> allProxies = new ArrayList<>();
    // Все сервера данных (для каждого сервера данных есть своя очередь прокси, которые чередуются, см. выше)
    private HashMap<String, HostThroughProxies> allHosts = new HashMap<>();
    // Единственный прокси, но с постоянно меняющимся IP (в таком случае не нужно много прокси)
    private Proxy rotatingProxy;

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
        String rotatingProp = AppContext.getFwkProperty("fwk.portal.rotating_proxy");
        allHosts.put(_ROTATING_, new HostThroughProxies(_ROTATING_, Collections.singletonList(new Proxy(rotatingProp, threadsPerProxy))));
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
        return submitRequest(hostName, createQueryTypePairs("text/html", urls));
    }

    /**
     * Отправляет запрос. В качестве параметров передаются не пользовательские запросы как в методе submitRequest,
     * а полноценные урлы. Они распределяются по прокси серверам.
     * Возвращается экземпляр класса Request. Его потом можно использовать, в частности подождать выполнения
     * @param urls
     * @return
     */
    public static Request submitGeneralUrls(String resultMimeType, String... urls) throws EcommanderException {
        return submitRequest(_GENERAL_, createQueryTypePairs(resultMimeType, urls));
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

    /**
     * Отправляет запрос на единственный прокси, который использует карусель IP адресов
     * Сделано для единообразия
     * @param resultMimeType
     * @param urls
     * @return
     * @throws EcommanderException
     */
    public static Request submitRotaitngUrls(String resultMimeType, String... urls) throws EcommanderException {
        return submitRequest(_ROTATING_, createQueryTypePairs(resultMimeType, urls));
    }

    /**
     * Создает структуру, нужную основному метода submitRequest
     * Заполняет пары каждым из переданных урлов и единым для всех миме типом
     * @param resultMimeType
     * @param urls
     * @return
     */
    private static ArrayList<Pair<String, String>> createQueryTypePairs(String resultMimeType, String... urls) {
        ArrayList<Pair<String, String>> queryTypes = new ArrayList<>();
        for (String url : urls) {
            queryTypes.add(new Pair<>(url, resultMimeType));
        }
        return queryTypes;
    }
}
