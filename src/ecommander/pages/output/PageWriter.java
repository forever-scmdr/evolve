package ecommander.pages.output;

import ecommander.fwk.FilterProcessException;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.User;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.PageElement;
import ecommander.pages.var.FilterStaticVariable;
import ecommander.pages.var.Variable;

/**
 * Создает XML для страницы
 * 
 * Страница будет в следующем виде (имена айтемов и парамтеров (содержимое тэга <name>) используются в качестве тэгов)
 * Также в качестве тэгов используюстя называния ссылок.
 * Название страницы - атрибут name корневого элемента (тэга <page>)
 * <page name="articles" visual-editing="false">
 * <source_link>catalog/device_type:v:/device_field:v:Маркировка шита/manufacturer:v:Markem</source_link>
 * <base>http://test.forever-ds.com</base>
 * <user id="4455" group="common" visual="false"/>       // visual - находится ли стринца в режие визуального редактирования
 * <variables>
 * 		<page_number>5</page_number>
 * 		<sorting>date</sorting>
 * 		<section>228</section>
 * 		<article_header_filter>крутые насосы</article_header_filter>
 * </variables>
 * <seсtion>
 * 		<id>298</id>
 * 		<name>Насосы</name>
 * 		<text>Крутые насосы</name>
 * 		<section>
 * 			<id>299</id>
 * 			<name>Электрические насосы</name>
 * 			<text>Крутые элекстрические насосы</text>
 * 			<open_section_link>http://site.domain/servletAction?parameter_set_1</open_section_link>
 * 		</section>
 * 		<section>
 *  		<id>300</id>
 * 			<name>Ручные насосы</name>
 * 			<text>Крутые ручные насосы</text>
 * 			<open_section_link>http://site.domain/servletAction?parameter_set_2</open_section_link>
 * 		</section>
 * 		<article>
 * 			<id>301</id>
 * 			<header>Самые крутые насосы</header>
 * 			<text>Наши насосы самые крутые</text>
 * 			<open_article_link>http://site.domain/servletAction?parameter_set_3</open_article_link>
 * 		</article>
 * 		<chechbox_input>chechbox_input:88</chechbox_input>
 * </seсtion>
 * <article>
 * 		<id>187</id>
 * 		<header>Самые крутые насосы 2</header>
 * 		<date>01.10.07</date>
 * 		<text>Наши насосы самые крутые 2</text>
 * </article>
 * <article>
 * 		<id>186</id>
 * 		<header>Самые крутые насосы 3</header>
 * 		<short>Наши насосы самые крутые 3</short>
 * </article>
 * <article>
 * 		<id>185</id>
 * 		<header>Самые крутые насосы 4</header>
 * 		<short>Наши насосы самые крутые 4</short>
 * </article>
 * <article>
 * 		<id>184</id>
 * 		<header>Самые крутые насосы 5</header>
 * 		<short>Наши насосы самые крутые 5</short>
 * </article>
 * <pages>
 * 		<article_next>http://site.domain/servletAction?parameter_set_4</article_next>
 * 		<article_previous>http://site.domain/servletAction?parameter_set_5</article_previous>
 * 		<article_page>
 * 			<number>1</number>
 * 			<link>http://site.domain/servletAction?parameter_set_6</link>
 * 		</article_page>
 * 		<article_page>
 * 			<number>2</number>
 * 			<link>http://site.domain/servletAction?parameter_set_7</link>
 * 		</article_page>
 *		<article_page>
 * 			<number>3</number>
 * 			<link>http://site.domain/servletAction?parameter_set_8</link>
 * 		</article_page>
 * 		<article_page>
 *	 		<number>4</number>
 * 			<link>http://site.domain/servletAction?parameter_set_9</link>
 * 		</article_page>
 * 		<product_current_page>
 * 	     	<number>1</number>
 * 	     	<link>category/category:v:2/page:v:1</link>
 * 		</product_current_page>
 * </pages>
 * </page>
 * 
 * 
 * http://www.roseindia.net/xml/dom/CreatXMLFile.shtml
 * http://www.java2s.com/Code/Java/JDK-6/UsingXMLStreamWritertocreateXMLfile.htm
 * 
 * @author EEEE
 *
 */
public class PageWriter {

	private static final String ROOT_ELEMENT = "page";
	private static final String NAME_ATTRIBUTE = "name";
	private static final String BASE_ELEMENT = "base";
	private static final String VARIABLES_ELEMENT = "variables";
	private static final String SOURCE_LINK_ELEMENT = "source_link";
	private static final String USER_ELEMENT = "user";
	private static final String ID_ATTRIBUTE = "id";
	private static final String ROLE_ATTRIBUTE = "admin";
	private static final String GROUP_ELEMENT = "group";
	private static final String VISUAL_ATTRIBUTE = "visual";

	// Для фильтра
	private static final String INPUT_TAG = "input";
	private static final String SORTING_TAG = "sorting";
	private static final String PAGE_TAG = "page";
	//	private static final String LIMIT_TAG = "limit";
	private static final String DIRECTION_ATTR = "direction";
	private static final String PARAM_ATTR = "param";
	private static final String ID_ATTR = "id";
	
	private ExecutablePagePE page;
	
	public PageWriter(ExecutablePagePE page) {
		this.page = page;
	}
	
	public XmlDocumentBuilder generateXml() throws Exception {
		// Создание корневого элемента
		XmlDocumentBuilder xml = XmlDocumentBuilder.newDoc();
		// <page>
		xml.startElement(ROOT_ELEMENT, NAME_ATTRIBUTE, page.getPageName());
		// <source_link>catalog/device_type:v:/device_field:v:Маркировка шита/manufacturer:v:Markem</source_link>
		xml.startElement(SOURCE_LINK_ELEMENT).addText(page.getRequestLink().getOriginalUrl()).endElement();
		// <user id="4455">
		//      <group name="common" id="0" admin="1"/>
		//      ...
		// </user>
		User user = page.getSessionContext().getUser();
		xml.startElement(USER_ELEMENT, ID_ATTRIBUTE, user.getUserId(), NAME_ATTRIBUTE, user.getName(),
				VISUAL_ATTRIBUTE, page.getSessionContext().isContentUpdateMode());
		for (User.Group group : user.getGroups()) {
			xml.addEmptyElement(GROUP_ELEMENT, NAME_ATTRIBUTE, group.name, ID_ATTRIBUTE, group.id, ROLE_ATTRIBUTE, group.role);
		}
		xml.endElement();
		// <base>http://test.forever-ds.com</base>
		xml.startElement(BASE_ELEMENT).addText(page.getUrlBase()).endElement();
		// <variables>
		xml.startElement(VARIABLES_ELEMENT);
		// Выводятся переменные
		for (Variable var : page.getAllVariables()) {
			if (!var.getName().startsWith("$")) {
				for (String value : var.writeAllValues()) {
					xml.startElement(var.getName());
					xml.addText(value);
					xml.endElement();
				}
			}
			if (var instanceof FilterStaticVariable) {
				writeFilterVaraible(xml, (FilterStaticVariable) var);
			}
		}
		// </variables>
		xml.endElement();
		// Выводятся все элементы кроме переменных
		for (PageElement element : page.getAllNested()) {
			PageElementWriter writer = PageElementWriterRegistry.getWriter(element);
			writer.write(element, xml);
		}
		// </page>
		xml.endElement();
		return xml;
	}

	/**
	 * Вывести фильтр (значения, заполненные пользователем)
	 * @param xml
	 * @param filterVar
	 */
	private void writeFilterVaraible(XmlDocumentBuilder xml, FilterStaticVariable filterVar) {
		try {
			xml.startElement(filterVar.getName());
			for (Integer inputId : filterVar.getPostedInputs()) {
				for (String value : filterVar.getValue(inputId)) {
					xml.startElement(INPUT_TAG, ID_ATTR, inputId).addText(value).endElement();
				}
			}
			if (filterVar.hasSorting())
				xml.addEmptyElement(SORTING_TAG, DIRECTION_ATTR, filterVar.getSortingDirection(), PARAM_ATTR, filterVar.getSortingParamId());
			xml.startElement(PAGE_TAG).addText(filterVar.getPageNumber()).endElement();

			xml.endElement();
		} catch (FilterProcessException e) {
			ServerLogger.error("unable to process filter", e);
		}
	}
	
}
