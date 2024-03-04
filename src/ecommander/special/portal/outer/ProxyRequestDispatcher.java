package ecommander.special.portal.outer;

import ecommander.controllers.AppContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * Диспетчер распределения запросов по хостам и прокси серверам.
 * Хост нужно указывать, прокси сервера используются автоматически (делать что-то юзеру не нужно)
 */
public class ProxyRequestDispatcher {

    private static ProxyRequestDispatcher instance;

    private ArrayList<Proxy> allProxies = new ArrayList<>();
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
     * Отправить запрос (одно действие ползователя)
     * Надо указать хост для общего запроса и все поисковые запросы (когда их несколько)
     * Запрос распределится по прокси серверам.
     * Возвращается экземпляр класса Request. Его потом можно испльзовать, в частности подождать выполнения
     * @param hostName
     * @param queries
     * @return
     */
    public static Request submitRequest(String hostName, Collection<String> queries) {
        hostName = StringUtils.lowerCase(StringUtils.normalizeSpace(hostName));
        HostThroughProxies host = getInstance().allHosts.get(hostName);
        if (host == null) {
            throw new IllegalArgumentException("host '" + hostName + "' not registered. Can be registered in settings file");
        }
        Request request = new Request(hostName, queries.toArray(new String[0]));
        request.submit(host);
        return request;
    }

    /**
     * Аналогично submitRequest, но для одного запроса
     * @param hostName
     * @param query
     * @return
     */
    public static Request submitRequest(String hostName, String query) {
        return submitRequest(hostName, Collections.singletonList(query));
    }
}
