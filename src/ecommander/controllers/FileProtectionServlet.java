package ecommander.controllers;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Сервлет для защиты файлов.
 * Проверяет текущего пользователя и его права на айтем защищенного файла
 * Айтем файла загружается и проверяются права юзера на это айтем.
 * Если проверка прошла успешно, ID этого айтема сохраняется у юзера в сеансе для того, чтобы избежать
 * повторной загрузки айтема
 * Created by E on 1/12/2017.
 */
public class FileProtectionServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// todo
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// todo
	}
}
