package lunacrawler;

import edu.uci.ics.crawler4j.url.WebURL;

/**
 * Модификатор урла
 * Иногда нужно модифицировать урл при парсинге
 * Created by E on 25/4/2018.
 */
public interface UrlModifier {
	/**
	 * Модифицирует урл (можно устаналвивать новый урл, который будет проходиться)
	 * @param url
	 */
	void modifyUrl(WebURL url);
}
