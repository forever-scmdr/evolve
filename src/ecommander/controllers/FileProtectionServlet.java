package ecommander.controllers;

import ecommander.fwk.MysqlConnector;
import ecommander.model.Item;
import ecommander.model.ItemBasics;
import ecommander.model.Security;
import ecommander.persistence.mappers.ItemMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashSet;

/**
 * Сервлет для защиты файлов.
 * Проверяет текущего пользователя и его права на айтем защищенного файла
 * Айтем файла загружается и проверяются права юзера на это айтем.
 * Если проверка прошла успешно, ID этого айтема сохраняется у юзера в сеансе для того, чтобы избежать
 * повторной загрузки айтема
 * Created by E on 1/12/2017.
 */
public class FileProtectionServlet extends BasicServlet {

	private static final String SECURITY_CHECKED_ITEM_IDS = "sec_checked_ids";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}

	private void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		SessionContext sess = SessionContext.createSessionContext(req);
		try {
			String url = getUserUrl(req);
			Long itemId = Item.getItemIdFromPath(url, AppContext.getFilesUrlPath(true), true);
			if (itemId != null) {
				HashSet<Long> checkedIds = (HashSet<Long>) sess.getVariableObject(SECURITY_CHECKED_ITEM_IDS);
				ItemBasics item;
				if (checkedIds == null || !checkedIds.contains(itemId)) {
					try (Connection conn = MysqlConnector.getConnection()) {
						item = ItemMapper.loadItemBasics(itemId, conn);
					}
					Security.testPrivileges(sess.getUser(), item);
					if (checkedIds == null) {
						checkedIds = new HashSet<>();
					}
					if (!checkedIds.contains(itemId)) {
						checkedIds.add(itemId);
						sess.setVariableObject(SECURITY_CHECKED_ITEM_IDS, checkedIds);
					}
				}
				sendFile(resp, url, true);
			}
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
			resp.getWriter().println("<html><body><p>Content for authorized users only!</p></body></html>");
		}
	}
}
