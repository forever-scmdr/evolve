package ecommander.model;

import ecommander.controllers.AppContext;
import ecommander.fwk.ValidationException;
import ecommander.pages.ValidationResults;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.commandunits.SaveNewUserDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Разбор файла пользователей
 * @author EEEE
 * 
 */
class UserModelCreateCommandUnit extends DBPersistenceCommandUnit implements UserModelXmlElementNames, DBConstants {

	private String usersFileName;

	public void execute() throws Exception {
		// Перезагрузить из БД реестр групп пользователей
		UserGroupRegistry.clearRegistry();
		UserMapper.loadUserGroups();
		Path usersFile = Paths.get(AppContext.getUsersPath());
		usersFileName = usersFile.getFileName().toString();
		if (Files.exists(usersFile)) {
			String xml = FileUtils.readFileToString(usersFile.toFile(), "UTF-8");
			parseFile(xml);
		} else {
			throw createValidationException("Users file not found", "initial", "Не найден файл групп пользователей");
		}
	}

	private void parseFile(String xml) throws Exception {
		Document doc = Jsoup.parse(xml);
		Element groups = doc.getElementsByTag(GROUPS).first();
		if (groups == null) {
			throw createValidationException("Groups tag not found", usersFileName, "Не найден элемент с группами пользователей");
		}
		for (Element group : groups.getElementsByTag(GROUP)) {
			readGroup(group);
		}
		Element users = doc.getElementsByTag(USERS).first();
		if (users == null) {
			throw createValidationException("Users tag not found", usersFileName, "Не найден элемент с пользователями");
		}
		for (Element user : users.getElementsByTag(USER)) {
			readUser(user);
		}
	}


	private void readGroup(Element group) throws ValidationException, SQLException {
		String name = group.attr(NAME);
		if (!UserGroupRegistry.groupExists(name)) {
			TemplateQuery insertGroup = new TemplateQuery("Save new group");
			insertGroup.INSERT_INTO(Group.GROUPS_TBL, Group.G_NAME).sql(" VALUES (").string(name).sql(")");
			try (PreparedStatement pstmt = insertGroup.prepareQuery(getTransactionContext().getConnection(), true)) {
				pstmt.executeUpdate();
				ResultSet rs = pstmt.getGeneratedKeys();
				rs.next();
				UserGroupRegistry.addGroup(name, rs.getByte(1));
			}
		}
	}

	private void readUser(Element userEl) throws Exception {
		String login = userEl.attr(NAME);
		String password = userEl.attr(PASSWORD);
		String description = userEl.attr(DESCRIPTION);
		if (StringUtils.isBlank(login))
			throw createValidationException("No login specified for user", usersFileName, "Не задано имя пользователя");
		if (StringUtils.isBlank(password))
			throw createValidationException("No password specified for user " + login, usersFileName,
					"Не задан пароль пользователя " + login);
		// Если такой пользователь уже создан - ничего не делать
		if (UserMapper.userNameExists(login, getTransactionContext().getConnection()))
			return;
		// Создание нового пользователя
		User user = new User(login, password, description, 0);
		Elements groupEls = userEl.getElementsByTag(GROUP);
		if (groupEls.size() == 0)
			throw createValidationException("No groups specified for user " + login, usersFileName,
					"Не заданы группы пользователя " + login);
		for (Element groupEl : groupEls) {
			String groupName = groupEl.attr(NAME);
			String roleName = groupEl.attr(ROLE);
			if (!UserGroupRegistry.groupExists(groupName))
				throw createValidationException("No group '" + groupName + "' exists for user " + login, usersFileName,
						"Не найдена группа '" + groupName + "' пользователя " + login);
			if (!StringUtils.equalsIgnoreCase(roleName, ADMIN_VALUE) && !StringUtils.equalsIgnoreCase(roleName, SIMPLE_VALUE))
				throw createValidationException("Illegal role '" + roleName + "' for user " + login, usersFileName,
						"Некорректная роль '" + roleName + "' пользователя " + login);
			user.addGroup(groupName, UserGroupRegistry.getGroup(groupName),
					StringUtils.equalsIgnoreCase(ADMIN_VALUE, roleName) ? (byte)1 : (byte)0);
		}
		executeCommand(new SaveNewUserDBUnit(user).ignoreUser());
	}


	private static ValidationException createValidationException(String errorName, String originator, String message) {
		ValidationResults results = new ValidationResults();
		results.addError(originator, message);
		return new ValidationException(errorName, results);
	}

	/**
	 * Выполнить команду
	 * @throws Exception
	 */
	public static void createUsers() throws Exception {
		DelayedTransaction tr = new DelayedTransaction(null);
		tr.addCommandUnit(new UserModelCreateCommandUnit());
		tr.execute();
	}
}
