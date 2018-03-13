package ecommander.pages;

import ecommander.controllers.ScheduledJob;
import ecommander.controllers.SessionContext;
import ecommander.fwk.PageNotFoundException;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.UserNotAllowedException;
import ecommander.fwk.ValidationException;
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
	public ExecutablePagePE getExecutablePage(String linkUrl, String urlBase, SessionContext context)
			throws PageNotFoundException, UserNotAllowedException, UnsupportedEncodingException {
		linkUrl = normalizeUrl(linkUrl);
		LinkPE link = LinkPE.parseLink(linkUrl);
		PagePE pageModel = getPageModel(link.getPageName());
		// Если не найдена страница - выбросить исключение
		if (pageModel == null) {
			throw new PageNotFoundException("The page '" + linkUrl + "' is not found");
		}
		// Проверка, разрешен ли пользователю доступ к этой странице
		if (context != null && !pageModel.isUserAuthorized(context.getUser()))
			throw new UserNotAllowedException("Requested page is not allowed for current user");
		return pageModel.createExecutableClone(context, link, linkUrl, urlBase);
	}

	/**
	 * Приводит URL к нормальному виду. Т.е. добавляет название страницы, если его нет, и
	 * добавляет названия переменных ко всем параметрам translit
	 * @param urlString
	 * @return
	 */
	public String normalizeUrl(String urlString) {
		if (StringUtils.isBlank(urlString)) {
			return urlString;
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
			return urlString;
		}
		String pageName = units[0];
		PagePE pageModel = PageModelRegistry.getRegistry().getPageModel(pageName);
		int lastTranslitPartIndex = 1;
		if (pageModel == null) {
			try {
				for (int i = units.length - 1; i >= 0 && pageModel == null; i--) {
					Item keyItem = ItemQuery.loadByUniqueKey(units[i]);
					if (keyItem != null && keyItem.getItemType().hasDefaultPage()) {
						pageName = keyItem.getItemType().getDefaultPage();
						pageModel = getPageModel(pageName);
						lastTranslitPartIndex = i;
					}
				}
			} catch (Exception e) {
				ServerLogger.error("Unable to load item by unique key", e);
			}
		}
		if (pageModel == null)
			return urlString;
		StringBuilder sb = new StringBuilder();
		Iterator<RequestVariablePE> varReverseIter = pageModel.getPathTranslitVarsReverseOrder().iterator();
		for (int i = lastTranslitPartIndex; i >= 0 && varReverseIter.hasNext(); i--) {
			String varName = varReverseIter.next().getName();
			sb.insert(0, VariablePE.COMMON_DELIMITER + varName + VariablePE.COMMON_DELIMITER + units[i]);
		}
		for (int i = lastTranslitPartIndex + 1; i < units.length; i++) {
			sb.append(VariablePE.COMMON_DELIMITER + units[i]);
		}
		sb.insert(0, pageName);
		if (StringUtils.isNotBlank(query))
			sb.append(LinkPE.QUESTION_SIGN).append(query);
		return sb.toString();
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
