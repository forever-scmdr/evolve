package ecommander.fwk;

import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.pages.Command;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.ResultPE;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateErrorJspCommand extends Command {

	private static final String PAGE = "page_404";
	private static final String FILE = "error.jsp";

	private static final String JSP_BEGIN = "<%@page import=\"ecommander.pages.ValidationResults.LineMessage\"%>\n" +
			"<%@page import=\"ecommander.pages.ValidationResults.StructureMessage\"%>\n" +
			"<%@page import=\"ecommander.controllers.BasicServlet\"%>\n" +
			"<%@page import=\"java.util.ArrayList\"%>\n" +
			"<%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\" pageEncoding=\"UTF-8\"%>\n" +
			"<%@ page trimDirectiveWhitespaces=\"true\" %>";

	private static final String JSP_VARS = "<%\n" +
			"\tArrayList<LineMessage> lineErrors = (ArrayList<LineMessage>)request.getAttribute(BasicServlet.MODEL_ERRORS_NAME);\n" +
			"\tArrayList<StructureMessage> structErrors = (ArrayList<StructureMessage>)request.getAttribute(BasicServlet.PAGES_ERRORS_NAME);\n" +
			"\tString e = (String)request.getAttribute(BasicServlet.EXCEPTION_NAME);\n" +
			"%>";

	private static final String ERROR_OUTPUT = "<%\n" +
			"\t\t\tif (lineErrors != null && lineErrors.size() > 0) {\n" +
			"\t\t%>\n" +
			"\t\t<h2>Информационная модель сайта</h2>\n" +
			"\t\t<table class=\"structure\">\n" +
			"\t\t\t<%\n" +
			"\t\t\t\tfor (LineMessage error : lineErrors) {\n" +
			"\t\t\t%>\n" +
			"\t\t\t<tr>\n" +
			"\t\t\t\t<td class=\"side\"><%=error.lineNumber%>:</td>\n" +
			"\t\t\t\t<td class=\"info\"><%=error.message%></td>\n" +
			"\t\t\t</tr>\n" +
			"\t\t\t<%\n" +
			"\t\t\t\t}\n" +
			"\t\t\t%>\n" +
			"\t\t</table>\n" +
			"\t\t<%\n" +
			"\t\t\t}\n" +
			"\t\t\tif (structErrors != null && structErrors.size() > 0) {\n" +
			"\t\t%>\n" +
			"\t\t<h2>Страницы сайта</h2>\n" +
			"\t\t<table class=\"pages\">\n" +
			"\t\t\t<%\n" +
			"\t\t\t\tfor (StructureMessage error : structErrors) {\n" +
			"\t\t\t%>\n" +
			"\t\t\t<tr>\n" +
			"\t\t\t\t<td class=\"side\"><%=error.originator%></td>\n" +
			"\t\t\t\t<td class=\"info\"><%=error.message%></td>\n" +
			"\t\t\t</tr>\n" +
			"\t\t\t<%\n" +
			"\t\t\t\t}\n" +
			"\t\t\t%>\n" +
			"\t\t</table>\n" +
			"\t\t<%\n" +
			"\t\t\t}\n" +
			"\t\t\tif (e != null) {\n" +
			"\t\t%>\n" +
			"\t\t<table class=\"exeption\">\n" +
			"\t\t\t<h2>Exception</h2>\n" +
			"\t\t\t<p>\n" +
			"\t\t\t<pre><%=e %></pre>\n" +
			"\t\t\t</p>\n" +
			"\t\t</table>\n" +
			"\t\t<% } %>";

	@Override
	public ResultPE execute() throws Exception {
		ExecutablePagePE page = getExecutablePage(PAGE);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PageController.newSimple().executePage(page, bos);
		String pageContent = bos.toString("UTF-8");

		int idx = pageContent.indexOf("id=\"bug_info\"");
		idx = pageContent.indexOf('>', idx);

		String before = pageContent.substring(0, idx);
		String after  = pageContent.substring(idx+1);

		pageContent = JSP_BEGIN + JSP_VARS + before +'>'+ ERROR_OUTPUT + after;

		Path dest = Paths.get(AppContext.getContextPath(), FILE);

		Files.deleteIfExists(dest);
		Files.write(dest, pageContent.getBytes(StandardCharsets.UTF_8));

		return null;
	}
}
