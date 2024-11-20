package ecommander.controllers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import ecommander.common.ServerLogger;
import ecommander.controllers.output.XmlDocumentBuilder;
import ecommander.model.datatypes.DateDataType;
import ecommander.pages.elements.ExecutablePagePE;
import ecommander.pages.elements.PageModelRegistry;
import ecommander.pages.elements.ResultPE;
import ecommander.pages.elements.ResultPE.ResultType;
/**
 * Класс, который выполняет определенную страницу, имеющую команды, согласно расписанию
 * Quartz
 * @author E
 *
 */
@DisallowConcurrentExecution
public class ScheduledJob implements Job {
	
	public static final String PAGE_NAME = "page_name";
	public static final String JOB_NAME_PREFIX = "exec_page_";
	public static final String TRIGGER_PREFIX = "trigger_";
	
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		SessionContext sessContext = null;
		try {
			String pageName = ctx.getJobDetail().getJobDataMap().getString(PAGE_NAME);
			ServerLogger.debug("Start scheduled job page '" + pageName + "'");
			sessContext = SessionContext.createSessionContext(null);
			ExecutablePagePE executable = PageModelRegistry.testAndGetRegistry().getExecutablePage(pageName, null, sessContext);
			ResultPE result = executable.execute();
			if (result != null && result.getType() != ResultType.none) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				if (result.getType() == ResultType.plain_text) {
					out.write(result.getValue().getBytes());
				} else if (result.getType() == ResultType.xml && StringUtils.isNotBlank(result.getValue())) {
					XmlDocumentBuilder xml = XmlDocumentBuilder.newDocFull(result.getValue());
					String xslFileName = AppContext.getStylesDirPath() + executable.getTemplate() + ".xsl";
					if (executable.transformationNeeded() && new File(xslFileName).exists())
						XmlXslOutputController.outputXmlTransformed(out, xml, xslFileName);
					else
						XmlXslOutputController.outputXml(out, xml);
				}
				String reportFileName = AppContext.getFilesDirPath() + executable.getPageName() + "/"
						+ DateDataType.outputDate(System.currentTimeMillis(), DateDataType.REPORT_FORMATTER) + ".html";
				File reportFile = new File(reportFileName);
				reportFile.getParentFile().mkdirs();
				Files.write(reportFile.toPath(), out.toByteArray(), StandardOpenOption.WRITE, StandardOpenOption.CREATE,
						StandardOpenOption.TRUNCATE_EXISTING);
			}
			ServerLogger.debug("Scheduled job page '" + pageName + "' SUCCESS");
		} catch (Exception e) {
			ServerLogger.error(e);
			throw new JobExecutionException(e);
		} finally {
			if (sessContext != null)
				sessContext.closeDBConnection();		
		}
	}

}
