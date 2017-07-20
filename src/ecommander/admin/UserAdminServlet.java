package ecommander.admin;

import ecommander.fwk.Strings;
import ecommander.model.User;
import ecommander.model.UserGroupRegistry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Контроллер Struts 2 для управления пользователями
 * @author EEEE
 *
 */
public class UserAdminServlet extends BasicAdminServlet {
	
	private static final long serialVersionUID = -8865409241079710626L;
	/**
	 * Экшены
	 */
	public static final String USERS_INITIALIZE_ACTION = "admin_users_initialize";
	public static final String SET_USER_ACTION = "admin_set_user";
	public static final String DELETE_USER_ACTION = "admin_delete_user";
	public static final String SAVE_USER_ACTION = "admin_save_user";
	/**
	 * Инпуты
	 */
	public static final String USER_GROUP_INPUT = "userGroup";
	public static final String USER_NAME_INPUT = "userName";
	public static final String PASSWORD_INPUT = "password";
	public static final String DESCRIPTION_INPUT = "description";
	public static final String USER_ID_INPUT = "userId";
	/**
	 * Переходы
	 */
	private static final String USERS = "/admin/admin_users.jsp";
	
	private String userGroup = Strings.EMPTY;
	private String userName = Strings.EMPTY;
	private String password = Strings.EMPTY;
	private String description = Strings.EMPTY;
	private long userId;
	private ArrayList<User> users;
	
	@Override
	protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String result = Strings.EMPTY;
		if (!checkUser(req, resp, USERS_INITIALIZE_ACTION + ".user")) return;
		start(req);
		String actionName = getAction(req);
		if (actionName.equalsIgnoreCase(USERS_INITIALIZE_ACTION))
			result = initialize(req);
		else if (actionName.equalsIgnoreCase(SET_USER_ACTION))
			result = setUser(req);
		else if (actionName.equalsIgnoreCase(DELETE_USER_ACTION))
			result = deleteUser(req);
		else if (actionName.equalsIgnoreCase(SAVE_USER_ACTION))
			result = saveUser(req);
		// Форвард
		req.setAttribute("data", this);
		forward(req, resp, result);
	}
	/**
	 * Действия, необходимые для инициализации
	 * @throws Exception
	 */
	protected void start(HttpServletRequest req) throws Exception {
//		userGroup = Strings.EMPTY;
//		userName = Strings.EMPTY;
//		password = Strings.EMPTY;
//		description = Strings.EMPTY;
//		userId = 0;
//		// Старт приложения, если он еще не был осуществлен
//		StartController.getSingleton().start(getServletContext());
//		users = UserMapper.getAllUsers();
//		if (UserGroupRegistry.getGroupNames().size() == 0)
//			UserMapper.loadUserGroups();
//		userGroup = req.getParameter(USER_GROUP_INPUT);
//		userName = req.getParameter(USER_NAME_INPUT);
//		password = req.getParameter(PASSWORD_INPUT);
//		description = req.getParameter(DESCRIPTION_INPUT);
//		if (!StringUtils.isBlank(req.getParameter(USER_ID_INPUT)))
//			userId = Long.parseLong(req.getParameter(USER_ID_INPUT));
	}
	/**
	 * Начало работы с юзерами - загрузка списка всех юзеров
	 * @return
	 * @throws Exception 
	 */
	protected String initialize(HttpServletRequest req) throws Exception {
		start(req);
		userGroup = Strings.EMPTY;
		userName = Strings.EMPTY;
		password = Strings.EMPTY;
		description = Strings.EMPTY;
		req.setAttribute("message", "Можете создавать нового пользователя");
		return USERS;
	}
	/**
	 * Выбран один из юзеров
	 * @return
	 * @throws Exception 
	 */
	protected String setUser(HttpServletRequest req) throws Exception {
//		start(req);
//		User user = UserMapper.getUser(getUserId());
//		userId = user.getUserId();
//		userName = user.getName();
//		userGroup = user.getGroup();
//		password = user.getPassword();
//		description = user.getDescription();
//		req.setAttribute("message", "Редактирование существующего пользователя");
		return USERS;
	}
	/**
	 * Созранить юзера
	 * @return
	 * @throws Exception 
	 */
	protected String saveUser(HttpServletRequest req) throws Exception {
//		// Старт приложения, если он еще не был осуществлен
//		StartController.start(getServletContext());
//		int groupId = UserGroupRegistry.getGroup(userGroup);
//		User user = new User(userName, password, description, userGroup, userId, groupId);
//		DelayedTransaction transaction = new DelayedTransaction(getCurrentAdmin());
//		if (user.isAnonimous())
//			transaction.addCommandUnit(new SaveNewUserDBUnit(user));
//		else
//			transaction.addCommandUnit(new UpdateUserDBUnit(user));
//		transaction.execute();
//		userGroup = Strings.EMPTY;
//		userName = Strings.EMPTY;
//		password = Strings.EMPTY;
//		description = Strings.EMPTY;
//		req.setAttribute("message", "Пользователь успешно сохранен");
//		users = UserMapper.getAllUsers();
//		if (UserGroupRegistry.getGroupNames().size() == 0)
//			UserMapper.loadUserGroups();
		return USERS;
	}
	/**
	 * Удалить пользователя
	 * @return
	 * @throws Exception 
	 */
	protected String deleteUser(HttpServletRequest req) throws Exception {
//		// Старт приложения, если он еще не был осуществлен
//		StartController.getSingleton().start(getServletContext());
//		User user = UserMapper.getUser(getUserId());
//		DelayedTransaction transaction = new DelayedTransaction(getCurrentAdmin());
//		transaction.addCommandUnit(new DeleteUserDBUnit(user));
//		transaction.execute();
//		userName = Strings.EMPTY;
//		req.setAttribute("message", "Пользователь успешно удален");
//		users = UserMapper.getAllUsers();
//		if (UserGroupRegistry.getGroupNames().size() == 0)
//			UserMapper.loadUserGroups();
		return USERS;
	}
	
	public ArrayList<User> getUsers() {
		return users;
	}
	public Collection<String> getGroupNames() {
		return UserGroupRegistry.getGroupNames();
	}
	public long getUserId() {
		return userId;
	}
	public UserAdminServlet getSelf() {
		return this;
	}
	public String getUserGroup() {
		return userGroup;
	}
	public String getUserName() {
		return userName;
	}
	public String getPassword() {
		return password;
	}
	public String getDescription() {
		return description;
	}
	
}
