package ecommander.pages;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.pages.var.ValueOrRef;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * Элемент - команда
 * Дает доступ к переменным страницы и объектам SingleItemHttpPostFormDeprecated и ItemVariablesContainer
 * 
 <required name="register_form" params="name organization phone email"/> - необязательный элемент, в котором указываются параметры айтема, которые не должны быть пустыми
 импользование required - вызов метода getUnsetRequired(name), который возарвщает незаполненные обязательные поля формы

Типы результатов
	simple - название страничной ссылки
	variable - название страничной ссылки и значение определенной переменной, которая должна быть добавлена к этой ссылке
	xml - XML документ, который потом может быть преобразован с помощью XLST
	plain_text - простой текст, который не требует преобразования
	
 *	
 * @author EEEE
 *
 */
public class CommandPE extends PageElementContainer implements ExecutablePE {
	public static final String ELEMENT_NAME = "command";
	
	/**
	 * Интерфейс, который должны реализовывать контейнеры, обрабатывающие добавление ExecutableItemPE особым образом
	 * @author EEEE
	 */
	public static interface CommandContainer {
		void addCommand(CommandPE commandPE);
	}
	
	private Class<Command> commandClass;
	private boolean cacheClearNeeded = false; // Нужно ли проводить очистку кеша после выполнения этой команды
	private ValueOrRef methodVar = null; // Переменная, которая хранит названия методов, которые должны быть в команде и котороые выполняют разные действия
	private ExecutablePagePE parentPage;

	@SuppressWarnings("unchecked")
	public CommandPE(String className, boolean clearCache) throws ClassNotFoundException {
		this.commandClass = (Class<Command>) Class.forName(className);
		this.cacheClearNeeded = clearCache;
	}
	
	private CommandPE(CommandPE base, ExecutablePagePE parentPage) {
		this.commandClass = base.commandClass;
		this.cacheClearNeeded = base.cacheClearNeeded;
		this.parentPage = parentPage;
	}
	
	public final void setMethodVar(String varName) {
		if (!StringUtils.isBlank(varName))
			methodVar = ValueOrRef.newRef(varName);
	}
	
	public final void setMethod(String methodName) {
		if (!StringUtils.isBlank(methodName))
			methodVar = ValueOrRef.newValue(methodName);
	}

	@Override
	protected PageElementContainer createExecutableShallowClone(PageElementContainer container, ExecutablePagePE parentPage) {
		CommandPE clone = new CommandPE(this, parentPage);
		if (methodVar != null)
			clone.methodVar = (ValueOrRef) methodVar.getInited(parentPage);
		if (container != null)
			((CommandContainer)container).addCommand(clone);
		return clone;
	}
	/**
	 * Выполнить команду и вернуть результат ее выполнения
	 * Результатом может быть страничное название сслыки, динамически сгенерированная ссылка либо XML код страницы
	 * @return
	 * @throws Exception
	 */
	public final ResultPE execute() throws Exception {
		try (Command command = commandClass.newInstance()) {
		    command.init(parentPage);
			if (hasNested()) {
				for (PageElement nested : getAllNested()) {
					command.addResult((ResultPE) nested);
				}
			}
			if (methodVar != null) {
				String methodName = methodVar.writeSingleValue();
				if (!StringUtils.isBlank(methodName)) {
					Method method = null;
					try {
						method = commandClass.getMethod(methodName);
					} catch (NoSuchMethodException e) {
						ServerLogger.warn("There is no '" + methodName + "' method in '" + commandClass.getName() + "' class");
						return command.execute();
					}
					return (ResultPE) method.invoke(command);
				}
			}
			return command.execute();
	    }
	}
	
	public boolean isCacheClearNeeded() {
		return cacheClearNeeded;
	}
	
	public void write(XmlDocumentBuilder xml) throws Exception {
		// Команда не выводится
	}

	public String getKey() {
		return "Command";
	}
	
	@Override
	protected boolean validateShallow(String elementPath, ValidationResults results) {
		if (commandClass == null) {
			results.addError(elementPath + " > " + getKey(), "there is no such class found in system ClassPath");
			return false;
		}
//		for (PageElement element : getAllNested()) {
//			if (element instanceof ResultPE) {
//				ResultPE result = (ResultPE) element;
//				if (result.getType() == ResultType.forward || result.getType() == ResultType.redirect) {
//					if (parentPage.getLink(result.getName()) == null)
//						results.addError(elementPath + " > " + getKey(), "there is no '" + result.getName() + "' on current page");
//				}
//			}
//		}
		return true;
	}
	
	public String getElementName() {
		return ELEMENT_NAME;
	}
}
