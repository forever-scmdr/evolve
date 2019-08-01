package ecommander.pages;

import ecommander.controllers.ScheduledJob;
import ecommander.controllers.SessionContext;
import ecommander.fwk.*;
import ecommander.model.DomainBuilder;
import ecommander.model.Item;
import ecommander.pages.var.RequestVariablePE;
import ecommander.pages.var.VariablePE;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Хранит все модели страниц (PagePE)
 * @author EEEE
 *
 */
public class PageModelRegistry {
	private static PageModelRegistry REGISTRY = null;

	private Scheduler sched; // Планировщик заданий
	private HashMap<String, PagePE> pageModels;

	public static PageModelRegistry getRegistry() {
		if (REGISTRY == null)
			REGISTRY = new PageModelRegistry();
		return REGISTRY;
	}

	public static PageModelRegistry testAndGetRegistry() throws Exception {
		if (REGISTRY == null)
			REGISTRY = new PageModelRegistry();
		PageModelBuilder.testActuality();
		DomainBuilder.testActuality();
		return REGISTRY;
	}

	static boolean pageExists(String name) {
		return REGISTRY != null && REGISTRY.pageModels.containsKey(name);
	}

	public static PageModelRegistry createInstance() {
		return new PageModelRegistry();
	}

	public static void setNewRegistry(PageModelRegistry newRegistry) throws SchedulerException {
		if (REGISTRY != newRegistry) {
			if (REGISTRY != null && REGISTRY.sched != null)
				REGISTRY.sched.shutdown();
			REGISTRY = newRegistry;
			REGISTRY.schedulePages();
		}
	}
	
	private void schedulePages() throws SchedulerException {
		for (PagePE page : pageModels.values()) {
			if (page.hasSchedule()) {
				if (sched == null) {
					Properties props = new Properties();
					props.put("org.quartz.threadPool.threadCount", "2");
					sched = new StdSchedulerFactory(props).getScheduler();
				}
				String jobName = ScheduledJob.JOB_NAME_PREFIX + page.name;
				String triggerName = ScheduledJob.TRIGGER_PREFIX + page.name;
				JobDetail job = JobBuilder.newJob(ScheduledJob.class).withIdentity(jobName).build();
				Trigger trigger 
					= TriggerBuilder.newTrigger()
						.withIdentity(triggerName)
						.withSchedule(CronScheduleBuilder.cronSchedule(page.getSchedule()))
						.forJob(jobName).build();
				job.getJobDataMap().put(ScheduledJob.PAGE_NAME, page.name);
				sched.scheduleJob(job, trigger);
			}
		}
		if (sched != null && !sched.isStarted())
			sched.start();
	}

	private PageModelRegistry() {
		pageModels = new HashMap<String, PagePE>();
	}
	/**
	 * Добавить модель страницы
	 * @param pageModel
	 */
	public synchronized void addPageModel(PagePE pageModel) {
		pageModels.put(pageModel.getPageName(), pageModel);
	}
	/**
	 * Назначить график регулярного автоматического выполнения страницы с определенным именем
	 * @param pageName
	 * @param schedule
	 * @throws SchedulerException 
	 */
	public synchronized void schedulePageModel(String pageName, String schedule) throws SchedulerException {

	}
	/**
	 * Вернуть модель страницы, которая не используется для загрузки
	 * @param pageName
	 * @return
	 * @throws PageNotFoundException
	 */
	public PagePE getPageModel(String pageName) {
		return pageModels.get(pageName);
	}
	/**
	 * Вернуть загружаемую модель страницы
	 * @param linkUrl
	 * @param urlBase - базовая ссылка, если на странице есть относительные ссылки, то базой для них должен быть этот урл
	 * @param context
	 * @return
	 * @throws PageNotFoundException
	 * @throws UserNotAllowedException 
	 * @throws UnsupportedEncodingException 
	 */
	public Pair<ExecutablePagePE, Item> getExecutablePage(String linkUrl, String urlBase, SessionContext context)
			throws PageNotFoundException, UserNotAllowedException, UnsupportedEncodingException {
		Pair<LinkPE, Item> normalized = normalizeAndCreateLink(linkUrl);
		LinkPE link = normalized.getLeft();
		Item exclusiveItem = normalized.getRight();
		PagePE pageModel = getPageModel(link.getPageName());
		// Если не найдена страница - выбросить исключение
		if (pageModel == null) {
			throw new PageNotFoundException("The page '" + linkUrl + "' is not found");
		}
		// Проверка, разрешен ли пользователю доступ к этой странице
		if (context != null && !pageModel.isUserAuthorized(context.getUser()))
			throw new UserNotAllowedException("Requested page is not allowed for current user");
		if (context != null)
			context.resetIdGenerator();
		return new Pair<>(pageModel.createExecutableClone(context, link, linkUrl, urlBase), exclusiveItem);
	}

	/**
	 * Приводит URL к нормальному виду. Т.е. добавляет название страницы, если его нет, и
	 * добавляет названия переменных ко всем параметрам key
	 * @param urlString
	 * @return
	 */
	public Pair<LinkPE, Item> normalizeAndCreateLink(String urlString) throws UnsupportedEncodingException {
		if (StringUtils.isBlank(urlString)) {
			return new Pair<>(LinkPE.parseLink(urlString), null);
		}
		// Строка разбивается на path и query
		String path = urlString;
		String query = null;
		int questionIdx = urlString.indexOf(LinkPE.QUESTION_SIGN);
		if (questionIdx > 0) {
			path = urlString.substring(0, questionIdx);
			query = urlString.substring(questionIdx + 1);
		}
		String[] units = StringUtils.split(path, VariablePE.COMMON_DELIMITER);
		if (units.length == 0) {
			return new Pair<>(LinkPE.parseLink(urlString), null);
		}
		String pageName = units[0];
		PagePE pageModel = PageModelRegistry.getRegistry().getPageModel(pageName);
		int lastTranslitPartIndex = -1;
		boolean isExclusive = false;
		Item exclusiveItem = null;
		if (pageModel == null) {
			try {
				LinkedHashMap<String, Item> items = ItemQuery.loadByUniqueKey(units);
				for (int i = units.length - 1; i >= 0 && pageModel == null; i--) {
					Item lastItem = items.get(units[i]);
					if (lastItem != null && lastItem.getItemType().hasDefaultPage()) {
						pageName = lastItem.getItemType().getDefaultPage();
						pageModel = getPageModel(pageName);
						lastTranslitPartIndex = i;
						isExclusive = true;
						exclusiveItem = lastItem;
					}
				}
			} catch (Exception e) {
				ServerLogger.error("Unable to load item by unique key", e);
			}
		} else {
			units = Arrays.copyOfRange(units, 1, units.length);
			if (units.length > 0) {
				HashSet<String> nonKeyPathVarNames = new HashSet<>();
				for (RequestVariablePE var : pageModel.getInitVariablesPEList()) {
					if (var.isStylePath())
						nonKeyPathVarNames.add(var.getName());
				}
				for (String unit : units) {
					if (!nonKeyPathVarNames.contains(unit))
						lastTranslitPartIndex++;
					else
						break;
				}
			}
		}
		if (pageModel == null)
			return new Pair<>(LinkPE.parseLink(urlString), null);
		StringBuilder normalUrl = new StringBuilder();
		Iterator<RequestVariablePE> varReverseIter = pageModel.getPathTranslitVarsReverseOrder().iterator();
		if (varReverseIter.hasNext()) {
			String varName = varReverseIter.next().getName();
			for (int i = 0; i <= lastTranslitPartIndex && i < units.length; i++) {
				normalUrl.append(VariablePE.COMMON_DELIMITER).append(varName).append(VariablePE.COMMON_DELIMITER).append(units[i]);
				if (varReverseIter.hasNext())
					varName = varReverseIter.next().getName();
			}
		}
		for (int i = lastTranslitPartIndex + 1; i < units.length; i++) {
			normalUrl.append(VariablePE.COMMON_DELIMITER + units[i]);
		}
		normalUrl.insert(0, pageName);
		if (StringUtils.isNotBlank(query))
			normalUrl.append(LinkPE.QUESTION_SIGN).append(query);
		LinkPE result = LinkPE.parseLink(normalUrl.toString());
		if (urlString.length() == 0 || urlString.charAt(0) != '/')
			urlString = '/' + urlString;
		result.setOriginalUrl(urlString);
		if (isExclusive)
			result.setType(LinkPE.Type.exclusive);
		return new Pair<>(result, exclusiveItem);
	}

	/**
	 * Валидация всех страниц и возарвщение результатов валидации
	 * @return
	 * @throws ValidationException
	 */
	public synchronized void validate(ValidationResults results) throws ValidationException {
		try (SessionContext sessContext = SessionContext.createSessionContext(null)) {
			for (PagePE page : pageModels.values()) {
				ExecutablePagePE execPage = getPageModel(page.getPageName()).createExecutableClone(sessContext, null, null, null);
				execPage.validate("", results);
			}
		} catch (Exception e) {
			results.setException(e);
		}
		if (!results.isSuccessful())
			throw new ValidationException("validation error", results);
	}
}
