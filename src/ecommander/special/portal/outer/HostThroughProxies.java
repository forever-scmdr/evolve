package ecommander.special.portal.outer;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Список хостов (серверов с данными - сайтов комплектующих или серверов API с нужными данными)
 * с ассоциированными с каждым из хостов очередями прокси серверов.
 *
 * Есть один общий список прокси серверов. Каждый из хостов добавляет в свою очередь элементы из
 * этого общего списка. Таким образом очередей несколько, но хранят они одни и те же общие элементы.
 *
 * Это нужно чтобы чередовались прокси сервера для каждого хоста.
 *
 * Этот класс представляет один такой хост. Т.е. один сервер данных (простой или API)
 * На момент создания есть два сервера - findchips.com (сайт) и oemsecrets.com (API)
 */
public class HostThroughProxies {

    public enum Status {
        ONLINE,     // работает
        OFFLINE     // не работает
    }

    private LinkedList<Proxy> proxyQueue = new LinkedList<>();
    private String name;

    public HostThroughProxies(String name, Collection<Proxy> allProxies) {
        this.name = name;
        for (Proxy proxy : allProxies) {
            proxyQueue.addFirst(proxy);
        }
    }

    /**
     * Выбрать следующий прокси по очереди.
     * Выбранный прокси возвращается, а также помещается назад в очередь на последнее место
     * @return
     */
    public synchronized Proxy getNextProxyAndRotate() {
        Proxy first = proxyQueue.poll();
        proxyQueue.addLast(first);
        return first;
    }

    public String getName() {
        return name;
    }
}
