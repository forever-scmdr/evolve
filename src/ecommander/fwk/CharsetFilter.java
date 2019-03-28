package ecommander.fwk;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * http://java.sun.com/products/servlet/Filters.html
 * Это работает только для POST запросов. Для GET запросов надо устанавливать параметр URIEncoding="UTF-8"
 * в элементе <Connector> в файле Tomcat /conf/server.xml
 * 
 * @version 	1.0
 * @author jjot
 */
public class CharsetFilter implements Filter {
	
	private String encoding = Strings.SYSTEM_ENCODING;


	public void destroy() {

	}


	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
		req.setCharacterEncoding(encoding);
		chain.doFilter(req, resp);
	}

	public void init(FilterConfig arg0) throws ServletException {
		
	}
}