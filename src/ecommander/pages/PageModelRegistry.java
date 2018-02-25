package ecommander.pages;

import ecommander.controllers.ScheduledJob;
import ecommander.controllers.SessionContext;
import ecommander.fwk.PageNotFoundException;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.UserNotAllowedException;
import ecommander.fwk.ValidationException;
import ecommander.model.DomainBuilder;
import ecommander.model.Item;
import ecommander.pages.var.VariablePE;
import ecommander.persistence.itemquery.ItemQuery;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Properties;

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
		LinkPE link = LinkPE.parseLink(linkUrl);
		PagePE pageModel = getPageModel(link.getPageName());
		// Если не найдена страница, возможно название старинцы - это уникальный текстовый ключ айтема
		// со страницей по умолчанию.
		// Загрузить айтем по уникальному ключу, узнать страницу по умолчанию и добавить ее к урлу
		if (pageModel == null) {
			try {
				Item keyItem = ItemQuery.loadByUniqueKey(link.getPageName());
				if (keyItem != null && keyItem.getItemType().hasDefaultPage()) {
					link = LinkPE.parseLink(keyItem.getItemType().getDefaultPage() + VariablePE.COMMON_DELIMITER + linkUrl);
					link.setType(LinkPE.Type.exclusive);
					pageModel = getPageModel(link.getPageName());
				}
			} catch (Exception e) {
				ServerLogger.error("Unable to load item by unique key", e);
			}
		}
		// Если не найдена страница - выбросить исключение
		if (pageModel == null) {
			throw new PageNotFoundException("The page '" + link.getPageName() + "' is not found");
		}
		// Проверка, разрешен ли пользователю доступ к этой странице
		if (context != null && !pageModel.isUserAuthorized(context.getUser()))
			throw new UserNotAllowedException("Requested page is not allowed for current user");
		return pageModel.createExecutableClone(context, link, linkUrl, urlBase);
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
