package ecommander.pages;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.sun.org.apache.bcel.internal.generic.NEW;
import ecommander.fwk.Strings;
import ecommander.model.Compare;
import org.apache.commons.lang3.StringUtils;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.ValidationException;
import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.model.LOGICAL_SIGN;
import ecommander.pages.filter.AggregationPE;
import ecommander.pages.filter.ParameterCriteriaPE;
import ecommander.pages.filter.FilterPE;
import ecommander.pages.filter.FulltextCriteriaPE;
import ecommander.pages.variables.CookieStaticVariablePE;
import ecommander.pages.variables.ItemVariablePE;
import ecommander.pages.variables.ParameterVariablePE;
import ecommander.pages.variables.ReferenceVariablePE;
import ecommander.pages.variables.SessionStaticVariablePE;
import ecommander.pages.variables.StaticItemVariablePE;
import ecommander.pages.variables.StaticParameterVariablePE;
import ecommander.pages.variables.StaticVariablePE;
import ecommander.pages.var.VariablePE;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.model.UserGroupRegistry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**

	***********************   БАЗОВАЯ ИНФОРМАЦИЯ   ***********************

<page 
	name="mega_company"							// название, которое используется для ссылок на эту страницу
	template="mega_company"						// Если template страницы пустое - это тестовая работа страницы (без использования XSLT)
	authority-groups="group1 group2 group3"		// группы пользователей, которым доступна эта страница
	cacheable="true"							// страница кэшируема (кешируется весь HTML код страницы, учитваются значения переменных)
	cache-vars="device size resolution"			// переменные, которые используются для кеширования. 
												// Если не странице есть другие переменные, КОТОРЫЕ ИМЕЮТ ЗНАЧЕНИЯ, то страница не кешруется и не 
												// берется из кеша. Если этот атрибут пуст, то для кеширования используются значения всех переменных
	schedule="0 30 10-13 ? * WED,FRI"			// запланированное выполнение этой страницы. На странице должны быть команды
>
// cacheable - страница кэшируема
// Если template страницы пустое - это тестовая работа страницы (без использования XSLT)
// authority-groups - группы пользователей, которым доступна эта страница

	
	<headers Content-Type="text/xml" Expires="-1" Cache-Control="no-cache"/>
	// Дополнительные заголовки, отправляемые клиенту этой страницей. Названия заголовков - атрибуты, значения заголовков - значения атрибутов
	// Например Content-Type="text/xml" - MIME тип содержимого страницы (для этого заголовка есть значение по умолчанию - text/html)
	// Expires="-1" - исключение кеширования

	<request>
		// Порядок следования переменных в этом блоке имеет значение при использовании транслитерации переменных
		<var name="device" style="translit"/> // значит этот параметр передается как уникальный строковый идентификатор
		<var name="list" value="100,101,102"/>
		<var name="filter_parameter_value_1"/>
		<var name="filter_parameter_value_2"/>
		<var name="page" value="1"/>
		<var name="resolution" value="1280x1024" scope="session"/> // Значение этой переменной будет сохранено в сеансе
		<var name="color" scope="session"/> // Значение этой переменной будет взято из сеанса, либо сохранено в сеансе, если это значение будет
										    // передано данной странице через ссылку
		<var name="size" scope="session" value=""/> // Значение переменной сеанса с именем size будет очищено
	</request>
	<list
		name="company" 				// название айтема, который извлекается
		id="company"				// страничный ID айтема, с помощью него можно ссылаться на этот айтем
		virtual="true"				// если айтем нужен только для того, чтобы загрузить другие айтемы, его можно не выводить в XML чтобы
									// не перегружать файл ненужным текстом. В таком случае айтем надо обозначить как виртуальный
		>
		
		<item name="device" quantifier="single" id="device" reference-var="device"> // Значит, что ID этого айтема берется из переменной device
			
			// Ссылка на другую страницу. copy-page-vars - необязательный параметр, означает, что в ссылку надо 
			// скопировать все переменные текуще страницы (не надо переписывать по одной)
			<link name="show_device" target="device" copy-page-vars="yes">
				// Объявляемая переменная не может начинаться со знака $. Этот знак зарезервирован для предопределенных переменных.
				<var name="some_var" value="eeee"> // переменная со статическим значением
				<var name="device" item="device"/> // переменная с динамическим значением - ID айтема
				<var name="device" item="device" parameter="producer"/> // переменная с динамическим значением - Значение параметра айтема
				<var name="filter_parameter_value_2" var="filter_parameter_value_1"/> // переменная с динамическим значением - Другая переменная
				<var ... style="translit"/> // Такая переменная может передаваться в формате транслита
				<var ... style="query"/> // Название и значение переменной передается в URL query
				
				<var name="$now" value="yyyy.MM.dd HH.mm"/> // Переменная, которая всегда возвращает текущее время в заданном формате (имя - $now)
				
			</link>
			<link name="show_device_adv target-var="some_var"> // название страницы передается через переменную
				...
			</link>
		</item>
		
		// Значит, что у этого айтема есть параметр code, который его уникально идентифицирует, и значение которого берется из переменной search_code
		<item name="device" quantifier="single" id="device" reference-var="search_code" reference-parameter="code"> 
			...
		</item>
		
		// По ID могут загружаться и множественные айтемы, тогда в переменной передается список ID разделенных запятой
		// в таком случае надо указывать quantifier="mulitple"
		// Такой вид загрузки используестя в основном только в AJAX страницах
		<item name="device" quantifier="mulitple" id="device" reference-var="list" reference-parameter="code"> 
			...
		</item>
		
		<item...>
			<reference var="device"/> // Так reference тоже работает
		</item>
		<item name="applied" quantifier="single">
			<item name="applied_block" quantifier="multiple"/>
		</item>
		
		
		// Когда нужен специальный загрузчик для айтема, имя его класса указывается в атрибуте loader
		// Этот класс должен расширять ExecutableItemPE. В нем для переопределения открыт только один метод - 
		// loadItem. Этот метод отвечает за загрузку айтемов
		<item name="device" tag="special" loader="extra.SpecialItem"/>
	</list>



	***********************   ПРЕДОПРЕДЕЛЕННЫЕ ПЕРЕМЕННЫЕ СТРАНИЦЫ   ***********************
	
	
	
	// Страница может содержать предопределенные переменные, которые можно использовать как обычные переменные, например, в фильтре или в ссылке
	<page>
		...
		<link name="show_userdata">
			<var var="$username"/>		// имя текущего пользователя, если он определен
		</link>
		<item ...>
			<filter>
				<parameter...>
					<var var="$pagename"/>		// имя текущей страницы
				</parameter>
			</filter>
		</item>
		....
	</page>
	// Список предопределенных переменных:
	// $username - имя текущего пользователя (логин) 
	// $usergroup - название группы текущего пользователя
	// $now - текущее время на сервере
	// $pagename - название текущей страницы
	// $pageurl - URL текущей страницы со всеми переменными
	
	
	
	***********************   ГРУППИРОВКА   ***********************
	
	
	
	<item name="device" id="device_by_type">	
		<aggregation parameter="device_group"/> // Группировка айтемов по их параметру (параметр device_group)
		<link name="show_catalog" target="catalog"> 
			<var name="device_type" item="device_by_type" parameter="device_group"/>
		</link>
	</item>
	// Группировка айтемов по их параметру с применением некоторой SQL функции, в данном случае MIN (параметр price)
	// В этом примере выбирается минимальная цена девайса
	<item name="price" quantifier="multiple" id="price_by_device">
		<aggregation function="MIN" parameter="price">
			<parameter name="code" sign="=">
				<var...>
			</parameter>
			<sorting.../> // аналогично фильтру, но могут использоваться только параметры, используемые в этой группировке
		</aggregation>
	</item>
	
	// Такая группировка полностью аналогична группировке SQL, т. е. для каждого значения code находится 
	// минимальное значение цены
	<item name="price" quantifier="multiple" id="price_by_device">
		<aggregation function="MIN" parameter="price">
			<parameter name="code"/>
			<sorting.../> // аналогично фильтру, но могут использоваться только параметры, используемые в этой группировке
		</aggregation>
	</item>
	
	<item name="price" quantifier="multiple">
		<aggregation function="MIN" parameter-var="some_param"> // Название параметра передается через переменную
			...
		</aggregation>
	</item>
	
	
	
	***********************   НЕПРЯМЫЕ ПОТОМКИ И ПРЕДШЕСТВЕННИКИ   ***********************
	
	
	
	// Саксэсор и количество найденных айтемов
	<item name="catalog" quantifier="multiple">
		<successor name="device" quantifier="multiple"/> // Все вложенные айтемы с именем "device" на любом уровне вложенности 
														 //(не только непосредственные наследники)
		<successor name="device" quantifier="multiple"> // Функция - пока без функции (чисто подсчет количества)
			<filter>
				<limit><var value="0"/></limit>
			</filter>
		</successor>
		<item name="section" quantifier="multiple">
			<item name="device" quantifier="single"/>
		</item>
		<parent-of name="main_item" quantifier="multiple"/> // Прямой родитель айтема, а также все айтемы, которые содержатт ссылку на данный айтем
															// !!! parent-of не может содержать фильтр
		<baseItems-of name="main_item" quantifier="multiple"/> // Все предки айтема с определенным названием в порядке иерархии
	</item>
	
	
	
	***********************   ПОЛЯ ВВОДА И ФИЛЬТРЫ   ***********************
	
	
	
	// Поля ввода
	<item name="some_device" quantifier="single" id="item_id">
		<input name="check" item="item_id"/> // Если айтему должны соответствовать какие-нибудь поля ввода 
											 // (например, выделение и указание количества в интернет магазнине)
		
		// Ссылка, которая позволяет отправлять POST запрос (или GET) со значениями, введенными в поля input
		// type="itemvars", используется в <form action="{эта ссылка}">...</form>
		<link name="submit_inputs" target="some_page" type="itemvars">
			<var .../> // любые дополнительные переменные
		</link>
		
		<input name="quantity" item="item_id"/>
		<filter operation="or"> // По умолчанию значение операции - AND
			<parameter name="some_param" sign="=">
				<var...>
				<var...>	// может быть несколько переменных. Тогда значения берутся из всех.
			</parameter>
			<parameter name="other_param" sign="<">
				<var...>
			</parameter>
			
			// Тип сравнения для множественных значений
			// Должен ли параметр соответствовать любому из представленных значений переменной, либо всем значениям одновременно
			// По умолчанию значение - ANY
			// Всего 4 значения - ANY, ALL, SOME, EVERY
			// ANY		параметр должен соответствовать одному из переданного множества образцов (любому из списка).
			// 			Если список образцов пустой, каждый айтем считается соответствующим
			// SOME		параметр должен соответствовать одному из переданного множества образцов (любому из списка).
			// 			Если список образцов пустой, то ни один айтем не считается соответствующим (результат сравнения - пустое множество)
			// ALL		параметр должен соответствовать каждому образцу из переданного множества образцов (всем образцам).
			// 			Если список образцов пустой, каждый айтем считается соответствующим
			// EVERY	параметр должен соответствовать каждому образцу из переданного множества образцов (всем образцам).
			// 			Если список образцов пустой, то ни один айтем не считается соответствующим (результат сравнения - пустое множество)
			<parameter name="cool_param" sign="=" compare="ALL">
				<var...>
				<var...>
			</parameter>
			
			<pages>
				<... variable .../>
			</pages>
			
			// Имя параметра-критерия фильтрации не закодировано жестко, а передается через переменную
			<parameter name-var="filter_parameter_value_2" sign="<="> // name-var - переменная, хранящая имя парамтера
				<var...>
			</parameter>
			
			// Передача через переменную не имени, а ID параметра (когда параметр динамический и его название не известно)
			<parameter id-var="pram_id_var_name" sign="<="> // id-var - переменная, хранящая ID парамтера
				<var...>
			</parameter>

			// Полнотекстовый поиск
			// limit - максимальное количество результатов
			// threshold - часть от рейтинга первого места поиска, результаты с рейтингом ниже которого не считаются релевантными
			<fulltext name="имя полнотекстового параметра" limit="500" threshold="0.5"> // полнотекстовый поиск по определенному параметру
				<... variable .../>
			</fulltext>
			<fulltext limit="500" type="prefix">	// полнотекстовый поиск по всем параметрам. Для разбора запроса и в качестве алгоритма
				<... variable .../>					// поиска используется стандартный класс с зарегистрированным названием "prefix"
			</fulltext>

			<fulltext limit="10" type="ecommander.persistence.itemquery.TermPrefixFulltextCriteria$Factory">	
				<... variable .../>					// используется нестандартный класс (указывается фактори для создания объектов этого класса)
			</fulltext>								// Этот класс должен расширять класс FulltextCriteria
			
			<sorting direction="desc"><var value="date_published"/></sorting>
			
			// Другой вариант sorting. Направление сортировки передается как страничная переменная
			<sorting direction-var="dir"><var value="date_published"/></sorting>
			
			// Иногда нужно отсортировать айтемы в порядке заданном списком значений. В таком случае можно использовать
			// переменную с именем (которое соответсвует имени параметра сортировки) и значением - списком значений для сортировки
			// Например, отсортировать товары по дате обновления цены. В товаре нет поля даты обновления цены. Тогда загружается 
			// айтем с датами обновления цены, сортируется по этой дате, затем загружается айтем товара и сортируется по значению
			// артикула из айтема обновления цены.
			<sorting><var name="code" item="price" parameter="price_code"/></sorting>
			
			// Можно задавать сколько угодно сортировок (элементов sorting)
			
		</filter>
	</item>

	
	
	***********************   ССЫЛКИ НА АЙТЕМЫ И АССОЦИАЦИИ   ***********************
	
	
	
	<item name="device" quantifier="single"/> 
	<item name="copy" quantifier="single" reference-item="device"/>	// Второй раз используется уже загруженный айтем device
																	// Значит, что айтем является копией уже загруженного другого айтема
	<item...>
		<reference item="device"/> // Так reference тоже работает
	</item>
	
	// В этом случае задан не только reference-item, но и reference-parameter.
	// Это значит, что айтем загружается по ассоциации (т. е. в айтеме device есть параметр goes_with, который в свою очередь хранит
	// ID других айтемов, "ассоциированных" с айтемом device. Именно эти айтемы и загружаются)
	<item name="device" reference-item="device" reference-parameter="goes_with" />
	
	
	

	***********************   INCLUDES   ***********************


	// Общие чатси можно объединять в инклюды
	<include-definition name="COMMON">
		<variables> // могут быть переменные
		...
		</variables>
		<item ...> // могут быть айтемы
		...
		</item>
		<link ...> // могут быть ссылки
		...
		</link>	
	</item_include>
	
	<include name="COMMON"/> // этот тэг заменяется содержимым тэга include-definition с таким же именем
	
	
	
	***********************   ВИДЫ ФИЛЬТРАЦИИ И ПРЕДШЕСТВЕННИКИ   ***********************
	
	
	
	// Фильтр по родителю
	// Одновременно может быть несколько родителей (в связи с существованием ссылок)
	<item name="device" quantifier="multiple">
		<filter>
			<predecessor item="parent_item_page_id" sign="NOT IN"/> // Предшественник айтема по иерархии
			<successor item="parent_item_page_id" sign="IN"/> // Потомок айтема по иерархии
		</filter>	
	</item>

	// Продвинутая строковая фильтрация
	// pattern - строка шаблона для сравнения. v - вместо нее (v) подставляется значение переменной
	// Символ $ надо всегда эскейпить символом \ (\$) как в примере
	<filter>
		<parameter name="third_param" sign="rlike" pattern="(^|,|, )+v(,|\$)+">
			<var...>
		</parameter>
	</filter>

	// Фильтрация даты и времени
	// pattern - строка шаблона SimpleDateFormat, с помощью которой преобразуется строковое значение даты в значение Long
	<filter>
		<parameter name="time" sign="&gt;" pattern="yyyy.MM.dd HH:mm">
			<var...>
		</parameter>
	</filter>
	
	// Последовательность предков айтема начиная от непосредственного родителя и до самого первого предка определенного типа
	<item name="device" quantifier="multiple">
		<baseItems name="section" quantifier="multiple">
			<item .../>
			<link .../>
			...
		</baseItems>
	</item>
	
</page>


	***********************   ПЕРСОНАЛЬНЫЕ И СЕАНСОВЫЕ АЙТЕМЫ   ***********************

Если есть персональные или сеансовые айтемы, то это определяется следующим образом
<page name="mega" template="mega" authority-groups="group1 group2 group3">
	<item ...> // Обычные айтемы, доступные множеству посетителей (неоперделенной или определенной группы)
		<item .../>
	</item>
	<presonal> // Персональные айтемы определенного пользователя (они хранятся в базе данных). Эти айтемы для каждого пользователя разные
		<item .../> // <presonal> может вкладываться как в <page>, так и в <item>
		<item .../>
	</personal>
	<session> // Айтемы, которые были созданы в сеансе
		<item ...>
			<item .../>
		</item>
	</session>
	<usergroup name="registered_user"> // Айтемы пользователей определенной группы (не той группы, к которой принадлежит текущий пользователь)
		<item ...>
			<item .../>
		</item>
	</usergroup>
	это TODO <enhance>
	<usergroup name="common_registered paid_registered"> // Можно искать среди нескольких групп пользователей (если данные в группах совместимы)
		<item ...>
			<item .../>
		</item>
	</usergroup>
</page>



 ***********************   КОМАНДЫ   ***********************



Если на странице есть команды
<page name="mega" template="mega" authority-groups="group1 group2 group3">
	<variables>
		<var name="result_variable" value="some_url"/>
		<var name="action"/>
		<var name="src_page"/>
	</variables>	
	// Базовый класс команды дает доступ к следюущим данным, которые могут понадобиться в команде:
	// 		- переменным страницы, 
	//		- специальным объектам itemform (ItemHttpPsotForm) и itemvars (ItemVariablesContainer)
	<command class="ecommander.package.SomeCommand"> // Класс команды
		<result name="success" type="forward"/> // На странице должна быть ссылка с названием success (на нее осуществляется переход)
												// forward - внутренний переход, redirect - внешний переход
											   // type="simple" - значит нужен только переход по ссылке
		<result name="error" type="redirect"/>	// Переход на страницу по ссылке error возможен с установлением значений переменных
												// В команде можно явно устанавливать значения переменных
		<result name="new_page" type="xml"/> // В результате выполнения команды получается XML код, который преобразуестя с помощью XSL шаблона 
											 // для текущей страницы (этой страницы)
		<result name="simple_text" type="plain_text"/> // Результат выполнения - простой текст, который выводится без изменений
		
		// Если на странице есть ссылка с именем возвращаемого командой результата, которая должна осуществлять простой редирект,
		// то такой результат явно указывать не обяззательно. Например, команда может вернуть результат с именем "back", в этом
		// случае осушествится переход по ссылке с именем "back"
		
	</command>
	<command  class="..." clear-cache="yes"> // Нужно ли проводить очистку кеша после выполнения этой команды (yes/no) по умолчанию no
		
		<required name="register_form" parameter="name organization phone"/>	// Если команда обрабатывает форму, то можно добавить элемент 
																				// required. В нем хранятся все параметры, которые не должны быть 
																				// пустыми (parameter) в определенной форме (name)
		<required name="email_form" parameter="topic to from"/>	// Если команда обрабатывает несколько форм, может быть несколько элементов
																// required. Элемент используется только в коде команды (можно вызвать метод,
																// который возвращает все незаполненные поля заданной формы)
	
	...
	</command>
	<command  class="..." method-var="action">	// Когда команда должна делать несколько действий.
												// Атрибут method-var хранит название переменной, в которой передается назание действия,
												// которое должна выполнить команда. Класс комманды в таком случае должен иметь методы,
												// совпадающие со значениями этой переменной и возвращающие объект RresultPE.
												// Этот атрибут не обязателен. Если он не указан, выполняется метод execute()
	...
	</command>
	<link name="success" target="catalog"> 
		...
	</link>
	<link name="error" target="catalog">
		...
	</link>
	<link name="back" target-var="src_page">
		...
	</link>
</page>



	***********************   ФОРМЫ   ***********************



Если на странице есть формы.
<page ...>
	<session>
		<item name="feedback_form" id="feedback"/>
	</session>
	...
	<form item="feedback"/> // Если айтем известен и загружен. Если айтем не найден, то создается форма для нового айтема
							// с типом, определенным соответствующим страничным айтемом (в примере - feedback_form)
	<form item-name="item_name" parent-item="parent_item_page_id"> // Если айтем не известен, но нужно создать новый айтем 
		<parameter name="some_item_param"/> // Если указаны параметры, то только они будут в форме, иначе будут все параметры айтема
		<parameter .../>
		
		// Ссылка, которая позволяет отправлять POST запрос (или GET) со значениями, введенными в поля формы
		// type="itemvars", используется в <form action="{эта ссылка}">...</form>
		<link name="submit_inputs" target="some_page" type="itemform">
			<var .../> // любые дополнительные переменные
		</link>
	</form>
	
	// restore-var - форма в команде может сохранятся в сеансе при помощи вызова соответствующего метода
	// Если нужно, чтобы значения полей формы восстановились из сеанса, значение переменной restore-var 
	// не должно быть пустым (может быть любым). Если переменная не задана, форма не восстанавливается
	<form item-name="item_name" tag="register_form" restore-var="some_var"> // tag - Если нужно использовать тэг, отличный от названия айтем
		...
	</form>
	// !!! На HTML странице в форме айтема могут присутствовать и другие поля, которые не относятся к параметрам айтема. В таком случае
	// эти поля будут добавляться в переменные страницы-обработчика формы.
	...
</page>



	***********************   КЕШИРОВАНИЕ   ***********************



Кеширование XML фрагментов
<page ...>
	<variables>
		<var name="selected_section"/>
		<var name="city"/>
	</variables>
	...
	<item name="section" id="section" cacheable="true"> // Этот айтем и вложенные в него кешируются в виде фрагмента полученного в результате
		<item .../>                                     // загрузки  XML. 
		<item .../>                                     // В качестве имени файла используется name (name="menu") и id (id="section")
	</item>
	<item .../>
	// cache-vars - список переменных с соблюдением порядка следования, который использвуется для уникальной идентификации кешируемого айтема
	// из значений переменных из этого списка строится имя файла с кэшем айтема
	<item reference-var="selected_section" name="section" id="section" cacheable="true" cache-vars="selected_section city">
		...
		<item ...>
			<filter>
				<parameter name="city" sign="=">
					<var name="city"/>
				</parameter>
			</filter>
		</item>
		...
	</item>
</page>



	***********************   ПОЛЬЗОВАТЕЛЬСКИЙ ФИЛЬТР   ***********************



// Пользовательский фильтр (фильтр создан пользвателем и сохранен как значение параметра определенного айтема)
// item - страничный ID айтема, который хранит определение фильтра
// parameter - параметр, который хранит определение фильтра
// var - название страничной переменной, которая хранит пользовательский ввод
// preload-domains - загрузка значений для полей ввода, которые подразумевают выбор значений из списка предопределенных (listbox, select)
			 В процессе загрузки анализируется определение полей ввода фильтра. В поле ввода может быть указан домен, может быть не указан:
 			 домен указан - загружаются значения соответствующего домена (более быстрый способ загрузки)
 			 домен не указан - загружаются актуальные значения параметров, соответствующих полю ввода, 
 			 				   при загрузке учитываются и другие критерии текущего фильтра, а также критерии вложенности айтемов 
 			 				   (требует группировки по каждому параметру поля ввода, более медленный способ)
 			 				   При этом варианте требуется указать переменные для кеширования айтема, содержащего определение фильтра.
 			 				   Это нужно для того, чтобы сложно получаемые результаты загрузки домено можно было корректно кешировать
<filter item="ID айтема" parameter="название параметра" var="название переменной" preload-domains="yes">
	... // может иметь свои дополнительные статические параметры

	// Ссылка для отправки формы фильтра (type="filter"), используется в <form action="{эта ссылка}">...</form>
	<link name="submit_filter" target="catalog" type="filter"/>
</filter>
TODO <enhance> добавить атрибут фильтра, в котором перечислены критерии фильтрации, которые надо учитывать при подгрузке значений доменов

то обозначать это с помощью фигурных скобок, например
<aggregation parameter="code"/> - простое значение
<aggregation parameter="{agg_param}"/> - значение переменной agg_param

<var name="v1" value="mega"/> - простое значение
<var name="v2" value="{v1}"/> - значение переменной v1

 * @author EEEE
 *
 */
public class PageModelBuilder {
	public static final String PERSONAL_ELEMENT = "personal";
	public static final String SESSION_ELEMENT = "session";
	public static final String USER_GROUP_ELEMENT = "usergroup";
	public static final String SINGLE_ELEMENT = "single";
	public static final String LIST_ELEMENT = "list";
	public static final String TREE_ELEMENT = "tree";
	public static final String ANCESTOR_ELEMENT = "ancestor";
	public static final String NEW_ELEMENT = "new";
	public static final String PARENT_ELEMENT = "parent";
	public static final String PARAMETER_ELEMENT = "parameter";
	public static final String SORTING_ELEMENT = "sorting";
	public static final String LIMIT_ELEMENT = "limit";
	public static final String PREDECESSOR_ELEMENT = "predecessor";
	public static final String FILTER_ELEMENT = "filter";
	public static final String PAGE_ELEMENT = "page";
	public static final String PAGES_ELEMENT = "pages";
	public static final String LINK_ELEMENT = "link";
	public static final String VAR_ELEMENT = "var";
	public static final String REFERENCE_ELEMENT = "reference";
	public static final String REQUEST_ELEMENT = "request";
	public static final String PARAMETER_INPUT_ELEMENT = "parameter-input";
	public static final String EXTRA_INPUT_ELEMENT = "extra-input";
	public static final String COMMAND_ELEMENT = "command";
	public static final String RESULT_ELEMENT = "result";
	public static final String INCLUDE_ELEMENT = "include";
	public static final String INCLUDE_DEFINITION_ELEMENT = "include-definition";
	public static final String AGGREGATION_ELEMENT = "aggregation";
	public static final String FULLTEXT_ELEMENT = "fulltext";
	public static final String HEADERS_ELEMENT = "headers";

	public static final String ASSOC_ATTRIBUTE = "assoc";
	public static final String CHILD_ATTRIBUTE = "child";
	public static final String TRANSITIVE_ATTRIBUTE = "name";
	public static final String REF_ATTRIBUTE = "ref";
	public static final String NAME_ATTRIBUTE = "name";
	public static final String ITEM_ATTRIBUTE = "item";
	public static final String FUNCTION_ATTRIBUTE = "function";
	public static final String ID_ATTRIBUTE = "id";
	public static final String PARAMETER_ATTRIBUTE = "parameter";
	public static final String SIGN_ATTRIBUTE = "sign";
	public static final String DIRECTION_ATTRIBUTE = "direction";
	public static final String QUANTIFIER_ATTRIBUTE = "quantifier";
	public static final String TARGET_ATTRIBUTE = "target";
	public static final String TARGET_VAR_ATTRIBUTE = "target-var";
	public static final String VALUE_ATTRIBUTE = "value";
	public static final String TAG_ATTRIBUTE = "tag";
	public static final String TEMPLATE_ATTRIBUTE = "template";
	public static final String AUTHORITY_GROUPS_ATTRIBUTE = "authority-groups";
	public static final String OPERATION_ATTRIBUTE = "operation";
	public static final String PARENT_ITEM_ATTRIBUTE = "parent-item";
	public static final String CACHEABLE_ATTRIBUTE = "cacheable";
	public static final String VIRTUAL_ATTRIBUTE = "virtual";
	public static final String LINK_ATTRIBUTE = "link";
	public static final String PATTERN_ATTRIBUTE = "pattern";
	public static final String REF_PARAMETER_ATTRIBUTE = "ref-parameter";
	public static final String NAME_VAR_ATTRIBUTE = "name-var";
	public static final String ID_VAR_ATTRIBUTE = "id-var";
	public static final String DIRECTION_VAR_ATTRIBUTE = "direction-var";
	public static final String PARAMETER_VAR_ATTRIBUTE = "parameter-var";
	public static final String REFERENCE_ITEM_ATTRIBUTE = "reference-item";
	public static final String CLEAR_CACHE_ATTRIBUTE = "clear-cache";
	public static final String SCOPE_ATTRIBUTE = "scope";
	public static final String STYLE_ATTRIBUTE = "style";
	public static final String CACHE_VARS_ATTRIBUTE = "cache-vars";
	public static final String CLASS_ATTRIBUTE = "class";
	public static final String TYPE_ATTRIBUTE = "type";
	public static final String COMPARE_ATTRIBUTE = "compare";
	public static final String SCHEDULE_ATTRIBUTE = "schedule";
	public static final String PRELOAD_DOMAINS_ATTRIBUTE = "preload-domains";
	public static final String LOADER_ATTRIBUTE = "loader";
	public static final String METHOD_ATTRIBUTE = "method";
	public static final String METHOD_VAR_ATTRIBUTE = "method-var";
	public static final String THRESHOLD_ATTRIBUTE = "threshold";
	public static final String RESTORE_VAR_ATTRIBUTE = "restore-var";
	public static final String COPY_PAGE_VARS_ATTRIBUTE = "copy-page-vars";
	public static final String FORM_ATTRIBUTE = "form";
	
	public static final String SINGLE_VALUE = "single";
	public static final String MULTIPLE_VALUE = "multiple";
	public static final String TRUE_VALUE = "true";
	public static final String YES_VALUE = "yes";
	public static final String SESSION_VALUE = "session";
	public static final String COOKIE_VALUE = "cookie";
	public static final String PAGE_VALUE = "page";
	
	public static final long MODIFIED_TEST_INTERVAL = 10000; // время, через которое проводится проверка обновления pages.xsl
	private static long fileLastChecked = 0;
	private static long fileLastModified = 0;
	
	// Айтемы, которые имеют идентификаторы в рамках страницы (для конторля уникальности идентификаторов)
	private Set<String> itemIdentifiers = new HashSet<String>();
	private Set<String> pageVariables = new HashSet<String>();
	private PagePE page = null; // указывает на обрабатываемую в данный момент страницу
	private LinkedList<String> itemIdsStack = new LinkedList<String>(); // стек ID страничных айтемов. Нужен для определения, является ли 
																		// переменная в ссылке итерируемой
	private static final Object SEMAPHORE = new Object();
	
	private static class PrimaryValidationException extends Exception {
		private static final long serialVersionUID = 6095618236291964810L;
		private final String originator;
		private final String message;
		private PrimaryValidationException(String originator, String message) {
			super();
			this.originator = originator;
			this.message = message;
		}
	}
	
	/**
	 * Проверяет, были ли произведены изменения в модели страниц, и если были, выполняет обновление реестра страниц
	 * @throws Exception 
	 */
	public static void testActuality() throws Exception {
		if (System.currentTimeMillis() - fileLastChecked > MODIFIED_TEST_INTERVAL) {
			synchronized (SEMAPHORE) {
				if (System.currentTimeMillis() - fileLastChecked > MODIFIED_TEST_INTERVAL) {
					File pages_xml = new File(AppContext.getPagesModelPath());
					ArrayList<File> files = findPagesFiles(pages_xml, null);
					long modified = 0;
					for (File file : files) {
						if (file.lastModified() > modified) modified = file.lastModified();
					}
					if (modified > fileLastModified) {
						PageController.clearCache();
						new PageModelBuilder().readAndCreateModel();
						fileLastModified = modified;
					}
					fileLastChecked = System.currentTimeMillis();
				}
			}
		}
	}
	/**
	 * Инициировать перезагрузку модели страниц при следующем обращении к ней
	 */
	public static void invalidate() {
		synchronized (SEMAPHORE) {
			fileLastChecked = 0;
			fileLastModified = 0;
		}
	}
	/**
	 * Парсит документ модели страниц и сохраняет его в оперативной памяти до следующей перезагрузки сервера. Чтение файла происходит прм первом
	 * обращении к модели страниц
	 */
	private void readAndCreateModel() throws Exception {
		// Результаты валидации
		ValidationResults results = new ValidationResults();
		// Очистить реестр страниц
		PageModelRegistry pageRegistry = PageModelRegistry.createInstance();
		// Прасить документ
		ArrayList<File> pageModelFiles = findPagesFiles(new File(AppContext.getPagesModelPath()), null);
		HashMap<String, Element> includes = new HashMap<>();
		ArrayList<Document> docs = new ArrayList<>();
		for (File file : pageModelFiles) {
			Document document = Jsoup.parse(file, Strings.SYSTEM_ENCODING);
			readIncludes(document, includes);
			docs.add(document);
		}
		try {
			for (Document document : docs) {
				Elements pages = document.getElementsByTag(PAGE_ELEMENT);
				for (Element pageNode : pages) {
					// Очистка списка ID айтемов
					itemIdentifiers.clear();
					pageVariables.clear();
					boolean cacheable = StringUtils.equalsIgnoreCase(pageNode.attr(CACHEABLE_ATTRIBUTE), TRUE_VALUE);
					String schedule = pageNode.attr(SCHEDULE_ATTRIBUTE);
					String cacheVarsStr = pageNode.attr(CACHE_VARS_ATTRIBUTE);
					ArrayList<String> cacheVars = new ArrayList<>();
					if (!StringUtils.isBlank(cacheVarsStr)) {
						cacheVars = new ArrayList<>(Arrays.asList(StringUtils.split(cacheVarsStr, ' ')));
					}
					page = new PagePE(pageNode.attr(NAME_ATTRIBUTE), pageNode.attr(TEMPLATE_ATTRIBUTE), cacheable, cacheVars);
					// Группы пользователей, которым разрешен просмотр
					String authority = pageNode.attr(AUTHORITY_GROUPS_ATTRIBUTE);
					if (!StringUtils.isBlank(authority)) {
						String[] groups = StringUtils.split(authority, ' ');
						for (String group : groups) {
							page.addAuthorityGroup(group.trim());
						}
					}
					if (!StringUtils.isBlank(schedule))
						page.setSchedule(schedule);
					// Собрать сведения обо всех айтемах, переменых и ссылках страницы
					for (Element element : pageNode.getAllElements()) {
						// Переменные
						if (StringUtils.equalsIgnoreCase(element.tagName(), REQUEST_ELEMENT)) {
							readVariables(element, page, includes);
						}
						// Айтем
						if (isNodeItemPE(element)) {
							page.addElement(readItem(element, ItemPE.ItemRootType.COMMON, null, includes, page.getKey()));
						// Ссылка
						} else if (StringUtils.equalsIgnoreCase(element.tagName(), LIMIT_ELEMENT)) {
							page.addElement(readLink(element, includes));
						// Команда
						} else if (StringUtils.equalsIgnoreCase(element.tagName(), COMMAND_ELEMENT)) {
							page.addElement(readCommand(element));
						// Форма
						} else if (StringUtils.equalsIgnoreCase(element.tagName(), PARAMETER_INPUT_ELEMENT)) {
							page.addElement(InputSetPE.createParams(element.attr(REF_ATTRIBUTE), element.attr(FORM_ATTRIBUTE),
									element.attr(RESTORE_VAR_ATTRIBUTE), StringUtils.split(element.attr(NAME_ATTRIBUTE), ' ')));
							// Инпут
						} else if (StringUtils.equalsIgnoreCase(element.tagName(), EXTRA_INPUT_ELEMENT)) {
							page.addElement(InputSetPE.createExtra(element.attr(REF_ATTRIBUTE), element.attr(FORM_ATTRIBUTE),
									element.attr(RESTORE_VAR_ATTRIBUTE), StringUtils.split(element.attr(NAME_ATTRIBUTE), ' ')));
						// <personal>
						} else if (StringUtils.equalsIgnoreCase(element.tagName(), PERSONAL_ELEMENT)) {
							for (Element subEl : pageNode.getAllElements()) {
								if (isNodeItemPE(subEl))
									page.addElement(readItem(subEl, ItemPE.ItemRootType.PERSONAL, null, includes, page.getKey()));
							}
						// <session>
						} else if (StringUtils.equalsIgnoreCase(element.tagName(), SESSION_ELEMENT)) {
							for (Element subEl : pageNode.getAllElements()) {
								if (isNodeItemPE(subEl))
									page.addElement(readItem(subEl, ItemPE.ItemRootType.SESSION, null, includes, page.getKey()));
							}
							// <usergroup name="some_group">
						} else if (StringUtils.equalsIgnoreCase(element.tagName(), USER_GROUP_ELEMENT)) {
							String groupName = element.attr(NAME_ATTRIBUTE);
							for (Element subEl : pageNode.getAllElements()) {
								if (isNodeItemPE(subEl)) {
									if (UserGroupRegistry.groupExists(groupName))
										page.addElement(readItem(subEl, ItemPE.ItemRootType.GROUP, groupName, includes, page.getKey()));
									else 
										page.addElement(readItem(subEl, ItemPE.ItemRootType.COMMON, null, includes, page.getKey()));
								}
							}
						// Инклюдес
						} else if (StringUtils.equalsIgnoreCase(element.tagName(), INCLUDE_ELEMENT)) {
							appendInclude(pageNode, element, includes);
						// Дополнительные заголовки HEADERS
						} else if (StringUtils.equalsIgnoreCase(element.tagName(), HEADERS_ELEMENT)) {
							Attributes attrs = element.attributes();
							for (Attribute attr : attrs) {
								page.addHeader(attr.getKey(), attr.getValue());
							}
						// Не допускать другие тэги
						} else {
							throw new PrimaryValidationException(page.getKey(), "'" + element.tagName()
									+ "' is not a valid subelement of &lt;page&gt; element");
						}
					}
					pageRegistry.addPageModel(page);					
				}
			}
		} catch (PrimaryValidationException pve) {
			results.addError(pve.originator, pve.message);
			throw new ValidationException("primary validation error", results);
		}
		PageModelRegistry.setNewRegistry(pageRegistry);
		pageRegistry.validate(results);
	}
	/**
	 * Является ли Node одним из тэгов айтемов (parent, item, child и т.д.)
	 * @param element
	 * @return
	 */
	private boolean isNodeItemPE(Element element) {
		return StringUtils.containsAny(element.tagName(), SINGLE_ELEMENT, LIST_ELEMENT,
				TREE_ELEMENT, ANCESTOR_ELEMENT, NEW_ELEMENT);
	}
	/**
	 * Считывает все include_definition в HashMap
	 * @param document
	 * @param includesMap
	 */
	private void readIncludes(Document document, HashMap<String, Element> includesMap) {
		Elements includes = document.getElementsByTag(INCLUDE_DEFINITION_ELEMENT);
		for (Element include : includes) {
			includesMap.put(include.attr(NAME_ATTRIBUTE), include);
		}
	}
	/**
	 * Добавляет include в место вставки
	 * @param parentNode
	 * @param includeRef
	 * @param includes
	 * @throws PrimaryValidationException 
	 */
	private void appendInclude(Element parentNode, Element includeRef, HashMap<String, Element> includes) throws PrimaryValidationException {
		Element include = includes.get(includeRef.attr(NAME_ATTRIBUTE));
		if (include == null)
			throw new PrimaryValidationException(parentNode.tagName() + " '" + ((Element) parentNode).attr(NAME_ATTRIBUTE) + "'",
					"There is no include with name '" + includeRef.attr(NAME_ATTRIBUTE) + "'");
		for (Element includeSubnode : include.getAllElements()) {
			Element importedNode = includeSubnode.clone();
			includeRef.before(importedNode);
		}
		includeRef.remove();
	}
	/**
	 * Рекурсивно считывает все айтемы и сабайтемы
	 * @param itemNode
	 * @param rootType - тип корня этого айтема
	 * @param groupName - если корень GROUP, то название группы айтема
	 * @param includes
	 * @return
	 * @throws Exception
	 * TODO <usability> добавить проверку на наличие названия айтема (name)
	 */
	private ItemPE readItem(Element itemNode, ItemPE.ItemRootType rootType, String groupName, HashMap<String, Element> includes, String pageName)
			throws Exception {
		// Считвание базовых параметров страничного айтема
		String itemNodeName = itemNode.getNodeName();
		String itemName = itemNode.getAttribute(NAME_ATTRIBUTE);
		String pageItemId = itemNode.getAttribute(ID_ATTRIBUTE);
		String tagName = itemNode.getAttribute(TAG_ATTRIBUTE);
		String referenceItem = itemNode.getAttribute(REFERENCE_ITEM_ATTRIBUTE);
		String referenceVar = itemNode.getAttribute(REFERENCE_VAR_ATTRIBUTE);
		String referenceParam = itemNode.getAttribute(REFERENCE_PARAMETER_ATTRIBUTE);
		boolean quantifierSingle = itemNode.getAttribute(QUANTIFIER_ATTRIBUTE).equalsIgnoreCase(SINGLE_VALUE);
		boolean cacheable = itemNode.getAttribute(CACHEABLE_ATTRIBUTE).equalsIgnoreCase(TRUE_VALUE);
		boolean virtual = itemNode.getAttribute(VIRTUAL_ATTRIBUTE).equalsIgnoreCase(TRUE_VALUE);
		String cacheVarsStr = itemNode.getAttribute(CACHE_VARS_ATTRIBUTE);
		String specialLoader = itemNode.getAttribute(LOADER_ATTRIBUTE);
		ArrayList<String> cacheVars = new ArrayList<String>();
		if (!StringUtils.isBlank(cacheVarsStr)) {
			cacheVars = new ArrayList<String>(Arrays.asList(StringUtils.split(cacheVarsStr, ' ')));				
		}
		// Проветка, является ли идентификатор уникальным
		if (!StringUtils.isBlank(pageItemId)) {
			if (itemIdentifiers.contains(pageItemId)) {
				throw new PrimaryValidationException(pageName + " > Item '" + itemName + "'", "This page contains duplicate item ID '"
						+ pageItemId + "'");
			}
			itemIdentifiers.add(pageItemId);
		}
		// Создание айтема
		ItemQuery.Type queryType = ItemQuery.Type.getValue(itemNodeName);
		ItemPE pageItem = new ItemPE(queryType, itemName, pageItemId, tagName, rootType, groupName, quantifierSingle, cacheable, virtual,
				cacheVars);
		// Сохранение ID айтема в стеке
		itemIdsStack.push(StringUtils.defaultString(pageItemId, "<no id>"));
		// Особый загрузчик для айтема
		if (!StringUtils.isBlank(specialLoader))
			pageItem.setSpecialLoader(specialLoader);
		// Второй способ добавления референса
		if (!StringUtils.isBlank(referenceItem)) { 
			if (!StringUtils.isBlank(referenceParam))
				pageItem.addElement(ReferencePE.createAssociatedReference(referenceItem, referenceParam));
			else
				pageItem.addElement(ReferencePE.createPageReference(referenceItem));
		} else if (!StringUtils.isBlank(referenceVar)) {
			if (!StringUtils.isBlank(referenceParam))
				pageItem.addElement(ReferencePE.createParameterUrlReference(referenceVar, referenceParam));
			else
				pageItem.addElement(ReferencePE.createUrlReference(referenceVar));
		}
		// Заполнение параметров айтема
		for (int j = 0; j < itemNode.getChildNodes().getLength(); j++) {
			Node itemSubnode = itemNode.getChildNodes().item(j);
			// Референс
			if (itemSubnode.getNodeType() == Node.ELEMENT_NODE && itemSubnode.getNodeName().equalsIgnoreCase(REFERENCE_ELEMENT)) {
				Element reference = (Element) itemSubnode;
				if (!StringUtils.isBlank(reference.getAttribute(ITEM_ATTRIBUTE)))
					pageItem.addElement(ReferencePE.createPageReference(reference.getAttribute(ITEM_ATTRIBUTE)));
				else if (!StringUtils.isBlank(reference.getAttribute(VAR_ATTRIBUTE)))
					pageItem.addElement(ReferencePE.createUrlReference(reference.getAttribute(VAR_ATTRIBUTE)));
			// Инпут
			} else if (itemSubnode.getNodeType() == Node.ELEMENT_NODE && itemSubnode.getNodeName().equalsIgnoreCase(INPUT_ELEMENT)) {
				Element inputNode = (Element) itemSubnode;
				pageItem.addElement(new InputPE(inputNode.getAttribute(NAME_ATTRIBUTE), inputNode.getAttribute(ITEM_ATTRIBUTE)));
			// Фильтр
			} else if (itemSubnode.getNodeType() == Node.ELEMENT_NODE && itemSubnode.getNodeName().equalsIgnoreCase(FILTER_ELEMENT)) {
				if (queryType == ItemQuery.Type.PARENT_OF || queryType == ItemQuery.Type.PREDECESSORS_OF)
					throw new PrimaryValidationException(pageName + " > Item '" + itemName + "'",
							"filters are not allowed in 'parent-of' and 'baseItems-of' elemets");
				pageItem.addElement(readFilter((Element) itemSubnode, includes));
			// Группировка
			} else if (itemSubnode.getNodeType() == Node.ELEMENT_NODE && itemSubnode.getNodeName().equalsIgnoreCase(AGGREGATION_ELEMENT)) {
				pageItem.addElement(readAggregation((Element) itemSubnode, includes));
			}
			// Сабайтем
			else if (isNodeItemPE(itemSubnode)) {
				pageItem.addElement(readItem((Element) itemSubnode, rootType, groupName, includes, pageName));
			// <personal>
			} else if (itemSubnode.getNodeType() == Node.ELEMENT_NODE && itemSubnode.getNodeName().equalsIgnoreCase(PERSONAL_ELEMENT)) {
				for (Node persSubnode = itemSubnode.getFirstChild(); persSubnode != null; persSubnode = persSubnode.getNextSibling()) {
					if (isNodeItemPE(persSubnode))
						pageItem.addElement(readItem((Element) persSubnode, ItemRootType.PERSONAL, groupName, includes, pageName));
				}
			// <usergroup>
			} else if (itemSubnode.getNodeType() == Node.ELEMENT_NODE && itemSubnode.getNodeName().equalsIgnoreCase(USER_GROUP_ELEMENT)) {
				groupName = ((Element)itemSubnode).getAttribute(NAME_ATTRIBUTE);
				for (Node groupSubnode = itemSubnode.getFirstChild(); groupSubnode != null; groupSubnode = groupSubnode.getNextSibling()) {
					if (isNodeItemPE(groupSubnode)) {
						if (UserGroupRegistry.groupExists(groupName))
							pageItem.addElement(readItem((Element) groupSubnode, ItemRootType.GROUP, groupName, includes, pageName));
						else 
							pageItem.addElement(readItem((Element) groupSubnode, ItemRootType.COMMON, null, includes, pageName));
					}
				}
			// Ссылка
			} else if (itemSubnode.getNodeType() == Node.ELEMENT_NODE && itemSubnode.getNodeName().equalsIgnoreCase(LINK_ELEMENT)) {
				pageItem.addElement(readLink((Element) itemSubnode, includes));
			// Форма
			} else if (itemSubnode.getNodeType() == Node.ELEMENT_NODE && itemSubnode.getNodeName().equalsIgnoreCase(FORM_ELEMENT)) {
				pageItem.addElement(readForm((Element) itemSubnode, includes));
			// Инклюдес
			} else if (itemSubnode.getNodeType() == Node.ELEMENT_NODE && itemSubnode.getNodeName().equalsIgnoreCase(INCLUDE_ELEMENT)) {
				appendInclude(itemNode, (Element) itemSubnode, includes);
			// Не допускать другие тэги
			} else if (itemSubnode.getNodeType() == Node.ELEMENT_NODE) {
				throw new PrimaryValidationException(pageName + " > Item '" + itemName + "'", "'" + itemSubnode.getNodeName()
						+ "' is not a valid subelement of &lt;item&gt; element");
			}
		}
		// Извлечение ID текущего айтема из стека
		itemIdsStack.pop();
		return pageItem;
	}
	/**
	 * Считывает фильтр
	 * @param filterNode
	 * @param includes
	 * @return
	 * @throws PrimaryValidationException 
	 */
	private FilterPE readFilter(Element filterNode, HashMap<String, Element> includes) throws PrimaryValidationException {
		FilterPE filter = new FilterPE();
		String userFilterItemId = filterNode.getAttribute(ITEM_ATTRIBUTE);
		String userFilterParamName = filterNode.getAttribute(PARAMETER_ATTRIBUTE);
		String userFilterVarName = filterNode.getAttribute(VAR_ATTRIBUTE);
		String operationString = filterNode.getAttribute(OPERATION_ATTRIBUTE);
		String preloadString = filterNode.getAttribute(PRELOAD_DOMAINS_ATTRIBUTE);
		boolean preload = preloadString != null && preloadString.equals(YES_VALUE);
		if (!StringUtils.isBlank(operationString))
			filter.setOperation(LOGICAL_SIGN.valueOf(operationString));
		if (!StringUtils.isBlank(userFilterItemId) && !StringUtils.isBlank(userFilterParamName) && !StringUtils.isBlank(userFilterVarName)) {
			filter.setUserFilter(userFilterItemId, userFilterParamName, userFilterVarName, preload);
		}
		for (int j = 0; j < filterNode.getChildNodes().getLength(); j++) {
			Node filterSubnode = filterNode.getChildNodes().item(j);
			// Параметр
			if (filterSubnode.getNodeType() == Node.ELEMENT_NODE && filterSubnode.getNodeName().equalsIgnoreCase(PARAMETER_ELEMENT)) {
				filter.addCriteria(readFilterCriteria((Element) filterSubnode));
			}
			// Полнотекстовый критерий
			else if (filterSubnode.getNodeType() == Node.ELEMENT_NODE && filterSubnode.getNodeName().equalsIgnoreCase(FULLTEXT_ELEMENT)) {
				filter.setFulltext(readFulltextCriteria((Element) filterSubnode));
			}
			// Сортировка
			else if (filterSubnode.getNodeType() == Node.ELEMENT_NODE && filterSubnode.getNodeName().equalsIgnoreCase(SORTING_ELEMENT)) {
				Element sortingNode = (Element) filterSubnode;
				String direction = sortingNode.getAttribute(DIRECTION_ATTRIBUTE);
				String directionVar = sortingNode.getAttribute(DIRECTION_VAR_ATTRIBUTE);
				// Значение сортировки (переменная)
				for (Node sortSubnode = sortingNode.getFirstChild(); sortSubnode != null; sortSubnode = sortSubnode.getNextSibling()) {
					if (sortSubnode.getNodeType() == Node.ELEMENT_NODE && sortSubnode.getNodeName().equalsIgnoreCase(VAR_ELEMENT)) {
						filter.addSorting(readVariable((Element)sortSubnode), direction, directionVar);
					}
				}
			}
			// Лимит
			else if (filterSubnode.getNodeType() == Node.ELEMENT_NODE && filterSubnode.getNodeName().equalsIgnoreCase(LIMIT_ELEMENT)) {
				Element limitNode = (Element) filterSubnode;
				// Значение лимита (переменная)
				for (Node limitSubnode = limitNode.getFirstChild(); limitSubnode != null; limitSubnode = limitSubnode.getNextSibling()) {
					if (limitSubnode.getNodeType() == Node.ELEMENT_NODE && limitSubnode.getNodeName().equalsIgnoreCase(VAR_ELEMENT)) {
						filter.addLimit(readVariable((Element)limitSubnode));
					}
				}
			}
			// Ссылка (когда есть пользовательскй фильтр)
			else if (filterSubnode.getNodeType() == Node.ELEMENT_NODE && filterSubnode.getNodeName().equalsIgnoreCase(LINK_ELEMENT)) {
				filter.addElement(readLink((Element) filterSubnode, includes));
			}
			// Предшественник
			else if (filterSubnode.getNodeType() == Node.ELEMENT_NODE && filterSubnode.getNodeName().equalsIgnoreCase(PREDECESSOR_ELEMENT)) {
				Compare compare = null;
				Element predecessor = (Element) filterSubnode;
				if (!StringUtils.isBlank(predecessor.getAttribute(COMPARE_ATTRIBUTE))) {
					try {
						compare = Compare.valueOf(predecessor.getAttribute(COMPARE_ATTRIBUTE));
					} catch (Exception e) {
						throw new PrimaryValidationException(page.getPageName() + " > filter", " criteria '" + COMPARE_ATTRIBUTE
								+ "' has invalid value");
					}
				}
				filter.addPredecessor(predecessor.getAttribute(ITEM_ATTRIBUTE), predecessor.getAttribute(SIGN_ATTRIBUTE), compare);
			}
			// Последователь
			else if (filterSubnode.getNodeType() == Node.ELEMENT_NODE && filterSubnode.getNodeName().equalsIgnoreCase(SUCCESSOR_ELEMENT)) {
				Compare compare = null;
				Element successor = (Element) filterSubnode;
				if (!StringUtils.isBlank(successor.getAttribute(COMPARE_ATTRIBUTE))) {
					try {
						compare = Compare.valueOf(successor.getAttribute(COMPARE_ATTRIBUTE));
					} catch (Exception e) {
						throw new PrimaryValidationException(page.getPageName() + " > filter", " criteria '" + COMPARE_ATTRIBUTE
								+ "' has invalid value");
					}
				}
				filter.addSuccessors(successor.getAttribute(ITEM_ATTRIBUTE), successor.getAttribute(SIGN_ATTRIBUTE), compare);
			}
			// Номер страницы
			else if (filterSubnode.getNodeType() == Node.ELEMENT_NODE && filterSubnode.getNodeName().equalsIgnoreCase(PAGES_ELEMENT)) {
				Element pageNode = (Element) filterSubnode;
				// Переменная страницы
				for (Node pageSubnode = pageNode.getFirstChild(); pageSubnode != null; pageSubnode = pageSubnode.getNextSibling()) {
					if (pageSubnode.getNodeType() == Node.ELEMENT_NODE && pageSubnode.getNodeName().equalsIgnoreCase(VAR_ELEMENT)) {
						filter.addPage(readVariable((Element)pageSubnode));
					}
				}
			}
			// <include>
			else if (filterSubnode.getNodeType() == Node.ELEMENT_NODE && filterSubnode.getNodeName().equalsIgnoreCase(INCLUDE_ELEMENT)) {
				appendInclude(filterNode, (Element) filterSubnode, includes);
			// Не допускать другие тэги
			} else if (filterSubnode.getNodeType() == Node.ELEMENT_NODE) {
				throw new PrimaryValidationException(page.getPageName() + " > filter", "'" + filterSubnode.getNodeName()
						+ "' is not a valid subelement of &lt;filter&gt; element");
			}
		}
		return filter;
	}
	/**
	 * Прочитать критерий фильтра
	 * @param criteriaNode
	 * @return
	 * @throws PrimaryValidationException 
	 */
	private ParameterCriteriaPE readFilterCriteria(Element criteriaNode) throws PrimaryValidationException {
		String paramName = criteriaNode.getAttribute(NAME_ATTRIBUTE);
		String paramNameVar = criteriaNode.getAttribute(NAME_VAR_ATTRIBUTE);
		String paramIdVar = criteriaNode.getAttribute(ID_VAR_ATTRIBUTE);
		String paramSign = criteriaNode.getAttribute(SIGN_ATTRIBUTE);
		String paramPattern = criteriaNode.getAttribute(PATTERN_ATTRIBUTE);
		String compareTypeStr = criteriaNode.getAttribute(COMPARE_ATTRIBUTE);
		Compare compare = null;
		if (!StringUtils.isBlank(compareTypeStr)) {
			try {
				compare = Compare.valueOf(compareTypeStr);
			} catch (Exception e) {
				throw new PrimaryValidationException(page.getPageName() + " > filter", " criteria '" + COMPARE_ATTRIBUTE
						+ "' has invalid value");
			}
		}
		// Критерий фильтра
		ParameterCriteriaPE crit = ParameterCriteriaPE.create(paramName, paramNameVar, paramIdVar, paramSign, paramPattern, compare);
		// Значение параметра (переменная)
		for (Node filterSubnode = criteriaNode.getFirstChild(); filterSubnode != null; filterSubnode = filterSubnode.getNextSibling()) {
			if (filterSubnode.getNodeType() == Node.ELEMENT_NODE && filterSubnode.getNodeName().equalsIgnoreCase(VAR_ELEMENT)) {
				crit.addValue(readVariable((Element) filterSubnode));
			// Не допускать другие тэги
			} else if (filterSubnode.getNodeType() == Node.ELEMENT_NODE) {
				throw new PrimaryValidationException(page.getPageName() + " > filter", "'" + filterSubnode.getNodeName()
						+ "' is not a valid subelement of &lt;parameter&gt; element");
			}
		}
		return crit;
	}
	/**
	 * Считывает фильтр
	 * @param filterNode
	 * @param includes
	 * @return
	 * @throws PrimaryValidationException 
	 */
	private AggregationPE readAggregation(Element aggregationNode, HashMap<String, Element> includes) throws PrimaryValidationException {
		String parameter = aggregationNode.getAttribute(PARAMETER_ATTRIBUTE);
		String parameterVar = aggregationNode.getAttribute(PARAMETER_VAR_ATTRIBUTE);
		String function = aggregationNode.getAttribute(FUNCTION_ATTRIBUTE);
		String operationString = aggregationNode.getAttribute(OPERATION_ATTRIBUTE);
		if (StringUtils.isBlank(parameter) && StringUtils.isBlank(parameterVar))
			throw new PrimaryValidationException("Aggregation",	"Aggregation parameter is not set");
		VariablePE paramVar = null;
		if (!StringUtils.isBlank(parameter))
			paramVar = new StaticVariablePE("parameter", parameter);
		else
			paramVar = new ReferenceVariablePE("parameter", parameterVar);
		AggregationPE agg = new AggregationPE(paramVar);
		if (!StringUtils.isBlank(operationString))
			agg.setOperation(LOGICAL_SIGN.valueOf(operationString));
		if (!StringUtils.isBlank(function))
			agg.setFunction(function);
		for (int j = 0; j < aggregationNode.getChildNodes().getLength(); j++) {
			Node aggSubnode = aggregationNode.getChildNodes().item(j);
			if (aggSubnode.getNodeType() == Node.ELEMENT_NODE && aggSubnode.getNodeName().equalsIgnoreCase(PARAMETER_ELEMENT)) {
				agg.addGroupBy(readFilterCriteria((Element) aggSubnode));
			} else if (aggSubnode.getNodeType() == Node.ELEMENT_NODE && aggSubnode.getNodeName().equalsIgnoreCase(INCLUDE_ELEMENT)) {
				appendInclude(aggregationNode, (Element) aggSubnode, includes);
			}
			// Сортировка
			else if (aggSubnode.getNodeType() == Node.ELEMENT_NODE && aggSubnode.getNodeName().equalsIgnoreCase(SORTING_ELEMENT)) {
				Element sortingNode = (Element) aggSubnode;
				String direction = sortingNode.getAttribute(DIRECTION_ATTRIBUTE);
				String directionVar = sortingNode.getAttribute(DIRECTION_VAR_ATTRIBUTE);
				VariablePE sortingParam = null;
				// Значение сортировки (переменная)
				for (Node sortSubnode = sortingNode.getFirstChild(); sortSubnode != null; sortSubnode = sortSubnode.getNextSibling()) {
					if (sortSubnode.getNodeType() == Node.ELEMENT_NODE && sortSubnode.getNodeName().equalsIgnoreCase(VAR_ELEMENT)) {
						sortingParam = readVariable((Element)sortSubnode);
					}
				}
				agg.addSorting(sortingParam, direction, directionVar);
			// Не допускать другие тэги
			} else if (aggSubnode.getNodeType() == Node.ELEMENT_NODE) {
				throw new PrimaryValidationException(page.getPageName() + " > aggregation", "'" + aggSubnode.getNodeName()
						+ "' is not a valid subelement of &lt;aggregation&gt; element");
			}
		}
		return agg;
	}
	/**
	 * Считывает критерий фильтра fulltext
	 * @param fulltextNode
	 * @return
	 * @throws PrimaryValidationException
	 */
	private FulltextCriteriaPE readFulltextCriteria(Element fulltextNode) throws PrimaryValidationException {
		String paramName = fulltextNode.getAttribute(NAME_ATTRIBUTE);
		String type = fulltextNode.getAttribute(TYPE_ATTRIBUTE);
		String compareTypeStr = fulltextNode.getAttribute(COMPARE_ATTRIBUTE);
		String thresholdStr = fulltextNode.getAttribute(THRESHOLD_ATTRIBUTE);
		Compare compare = null;
		if (!StringUtils.isBlank(compareTypeStr)) {
			try {
				compare = Compare.valueOf(compareTypeStr);
			} catch (Exception e) {
				throw new PrimaryValidationException(page.getPageName() + " > filter", " fulltext '" + COMPARE_ATTRIBUTE
						+ "' has invalid value");
			}
		}
		float threshold = -1;
		if (!StringUtils.isBlank(thresholdStr)) {
			try {
				threshold = Float.parseFloat(thresholdStr);
			} catch (Exception e) {
				throw new PrimaryValidationException(page.getPageName() + " > filter > fulltext", "'threshold' attribute must have a valid value");
			}
		}
		int limit = 0;
		try {
			limit = Integer.parseInt(fulltextNode.getAttribute(LIMIT_ELEMENT));
		} catch (Exception e) {
			throw new PrimaryValidationException(page.getPageName() + " > filter > fulltext", "'limit' attribute must have a valid value");
		}
		// Значение параметра (переменная)
		VariablePE queryVar = null;
		for (Node ftSubnode = fulltextNode.getFirstChild(); ftSubnode != null; ftSubnode = ftSubnode.getNextSibling()) {
			if (ftSubnode.getNodeType() == Node.ELEMENT_NODE && ftSubnode.getNodeName().equalsIgnoreCase(VAR_ELEMENT)) {
				queryVar = readVariable((Element) ftSubnode);
			// Не допускать другие тэги
			} else if (ftSubnode.getNodeType() == Node.ELEMENT_NODE) {
				throw new PrimaryValidationException(page.getPageName() + " > filter", "'" + ftSubnode.getNodeName()
						+ "' is not a valid subelement of &lt;parameter&gt; element");
			}
		}
		return new FulltextCriteriaPE(type, queryVar, limit, paramName, compare, threshold);
	}
	/**
	 * Считывает ссылку
	 * @param linkNode
	 * @param includes
	 * @return
	 * @throws PrimaryValidationException
	 */
	private LinkPE readLink(Element linkNode, HashMap<String, Element> includes) throws PrimaryValidationException {
		String target = linkNode.getAttribute(TARGET_ATTRIBUTE);
		String targetVar = linkNode.getAttribute(TARGET_VAR_ATTRIBUTE);
		String typeStr = linkNode.getAttribute(TYPE_ATTRIBUTE);
		boolean copyVars = StringUtils.equalsIgnoreCase(linkNode.getAttribute(COPY_PAGE_VARS_ATTRIBUTE), YES_VALUE);
		LinkPE.Type type = null;
		try {
			if (!StringUtils.isBlank(typeStr))
				type = LinkPE.Type.valueOf(typeStr);
			// Проверка, правильно ли указан стиль переменной
		} catch (Exception e) {
			throw new PrimaryValidationException(page.getPageName() + " > link", "'" + linkNode.getAttribute(STYLE_ATTRIBUTE)
					+ "' is not a valid type of &lt;link&gt; element");
		}
		boolean isFilterLink = linkNode.getParentNode().getNodeName().equals(FILTER_ELEMENT);
		boolean isItemFormLink = linkNode.getParentNode().getNodeName().equals(FORM_ELEMENT);
		// Проверка, правильно ли вложена ссылка для формы
		if (type == LinkPE.Type.itemform && !isItemFormLink) {
			throw new PrimaryValidationException(page.getPageName() + " > link", "'itemform' link must be located inside &lt;form&gt; element");
		} 
		// Проверка, правильно ли вложена ссылка для фильтра
		else if (type == LinkPE.Type.filter && !isFilterLink) {
			throw new PrimaryValidationException(page.getPageName() + " > link", "'filter' link must be located inside &lt;filter&gt; element");
		}
		LinkPE link = null;
		if (!StringUtils.isBlank(target))
			link = LinkPE.newDirectLink(linkNode.getAttribute(NAME_ATTRIBUTE), target, copyVars);
		else if (!StringUtils.isBlank(targetVar))
			link = LinkPE.newVarLink(linkNode.getAttribute(NAME_ATTRIBUTE), targetVar, copyVars);
		else
			throw new PrimaryValidationException(page.getPageName() + " > link " + linkNode.getAttribute(NAME_ATTRIBUTE), "target is not set");
		// Установка типа ссылки
		link.setType(type);
		// Принудительная установка типа ссылки, если она находится внутри формы или внутри фильтра
		if (isFilterLink)
			link.setType(LinkPE.Type.filter);
		else if (isItemFormLink)
			link.setType(LinkPE.Type.itemform);
		for (int j = 0; j < linkNode.getChildNodes().getLength(); j++) {
			Node linkSubnode = linkNode.getChildNodes().item(j);
			// Переменные
			if (linkSubnode.getNodeType() == Node.ELEMENT_NODE && linkSubnode.getNodeName().equalsIgnoreCase(VAR_ELEMENT)) {
				link.addVariablePE(readVariable((Element) linkSubnode));
			} else if (linkSubnode.getNodeType() == Node.ELEMENT_NODE && linkSubnode.getNodeName().equalsIgnoreCase(INCLUDE_ELEMENT)) {
				appendInclude(linkNode, (Element) linkSubnode, includes);
			// Не допускать другие тэги
			} else if (linkSubnode.getNodeType() == Node.ELEMENT_NODE) {
				throw new PrimaryValidationException(page.getPageName() + " > " + link.getKey(), "'" + linkSubnode.getNodeName()
						+ "' is not a valid subelement of &lt;link&gt; element");
			}
		}
		return link;
	}
	/**
	 * Считывает переменную
	 * @param variableNode
	 * @return
	 * @throws PrimaryValidationException 
	 */
	private VariablePE readVariable(Element variableNode) throws PrimaryValidationException {
		String varName = variableNode.getAttribute(NAME_ATTRIBUTE);
		String itemId = variableNode.getAttribute(ITEM_ATTRIBUTE);
		String otherVar = variableNode.getAttribute(VAR_ATTRIBUTE);
		String parameter = variableNode.getAttribute(PARAMETER_ATTRIBUTE);
		String value = variableNode.getAttribute(VALUE_ATTRIBUTE);
		String styleStr = variableNode.getAttribute(STYLE_ATTRIBUTE);
		VariablePE.Style style = null;
		try {
			if (!StringUtils.isBlank(styleStr))
				style = VariablePE.Style.valueOf(styleStr);
			// Проверка, правильно ли указан стиль переменной
		} catch (Exception e) {
			throw new PrimaryValidationException(page.getPageName() + " > link ", "'" + variableNode.getAttribute(STYLE_ATTRIBUTE)
					+ "' is not a valid style of &lt;var&gt; element");
		}
		// Проветка, существует ли айтем с используемым идентификатором
		if (!StringUtils.isBlank(itemId) && !itemIdentifiers.contains(itemId))
			ServerLogger.warn("\n\n\nThere is no item with ID '" + itemId + "'.   Maybe in includes\n\n\n\n");
		// Проверка, существует ли страничная переменная с используемым идентификатором
		if (!StringUtils.isBlank(otherVar) && !pageVariables.contains(otherVar))
			ServerLogger.warn("\n\n\nThere is no page variable named '" + otherVar + "'.   Maybe in includes\n\n\n\n");
		// Создание определеннной переменной
		VariablePE variable = null;
		if (!StringUtils.isBlank(value)) {
			variable = new StaticVariablePE(varName, value);
		} else if (!StringUtils.isBlank(otherVar))
			variable = new ReferenceVariablePE(varName, otherVar);
		else if (!StringUtils.isBlank(itemId)) {
			boolean isIterable = itemIdsStack.contains(itemId);
			if (!StringUtils.isBlank(parameter)) {
				if (isIterable)
					variable = new ParameterVariablePE(varName, itemId, parameter);
				else
					variable = new StaticParameterVariablePE(varName, itemId, parameter);
			}
			else {
				if (isIterable)
					variable = new ItemVariablePE(varName, itemId);
				else
					variable = new StaticItemVariablePE(varName, itemId);
			}
		} else {
			// Если явно не задано значение переменной - пустая строка, то это ошибка в файле pages.xml
			if (variableNode.hasAttribute(VALUE_ATTRIBUTE))
				variable = new StaticVariablePE(varName, "");
			else
				throw new PrimaryValidationException(page.getPageName() + " > varialbe '" + varName + "'", "variable value is not defined. "
						+ "If this variable should have empty value it must be set explicitly");
		}
		variable.setStyle(style);
		return variable;
	}	
	/**
	 * Считывает переменные страницы
	 * @param variablesNode
	 * @param model
	 * @throws PrimaryValidationException 
	 */
	private void readVariables(Element variablesNode, PagePE model, HashMap<String, Element> includes) throws PrimaryValidationException {
		for (int j = 0; j < variablesNode.getChildNodes().getLength(); j++) {
			Node variablesSubnode = variablesNode.getChildNodes().item(j);
			// Переменные
			String name = null;
			if (variablesSubnode.getNodeType() == Node.ELEMENT_NODE && variablesSubnode.getNodeName().equalsIgnoreCase(VAR_ELEMENT)) {
				Element varNode = (Element) variablesSubnode;
				name = varNode.getAttribute(NAME_ATTRIBUTE);
				Attr valueAttr = varNode.getAttributeNode(VALUE_ATTRIBUTE);
				String scope = varNode.getAttribute(SCOPE_ATTRIBUTE);
				if (StringUtils.isBlank(name)) continue;
				String styleStr = varNode.getAttribute(STYLE_ATTRIBUTE);
				VariablePE.Style style = null;
				try {
					if (!StringUtils.isBlank(styleStr))
						style = VariablePE.Style.valueOf(styleStr);
					// Проверка, правильно ли указан стиль переменной
				} catch (Exception e) {
					throw new PrimaryValidationException(page.getPageName() + " > variables ", "'" + varNode.getAttribute(STYLE_ATTRIBUTE)
							+ "' is not a valid style of &lt;var&gt; element");
				}
				String value = null;
				if (valueAttr != null)
					value = valueAttr.getValue();
				String var = varNode.getAttribute(VAR_ATTRIBUTE);
				// Сеансовая переменная и куки переменная
				VariablePE variablePE = null;
				if (scope.equalsIgnoreCase(SESSION_VALUE)) {
					if (valueAttr != null)
						variablePE = new SessionStaticVariablePE(name, valueAttr.getValue());
					else 
						variablePE = new SessionStaticVariablePE(name, null);
				} else if (scope.equalsIgnoreCase(COOKIE_VALUE)) {
					if (valueAttr != null)
						variablePE = new CookieStaticVariablePE(name, valueAttr.getValue());
					else 
						variablePE = new CookieStaticVariablePE(name, null);
				// Переменная страницы (не сеансовая)
				} else if (!StringUtils.isBlank(var)) {
					variablePE = new ReferenceVariablePE(name, var);
				} else {
					variablePE = new StaticVariablePE(name, value);
				}
				variablePE.setStyle(style);
				model.addVariablePE(variablePE);
			} else if (variablesSubnode.getNodeType() == Node.ELEMENT_NODE && variablesSubnode.getNodeName().equalsIgnoreCase(INCLUDE_ELEMENT)) {
				appendInclude(variablesNode, (Element) variablesSubnode, includes);
			}
			pageVariables.add(name);
		}
	}
	/**
	 * Считывает форму
	 * @param variablesNode
	 * @param model
	 * @throws PrimaryValidationException 
	 */
	private ItemFormPE readForm(Element formNode, HashMap<String, Element> includes) throws PrimaryValidationException {
		String itemId = formNode.getAttribute(ITEM_ATTRIBUTE);
		String parentId = formNode.getAttribute(PARENT_ITEM_ATTRIBUTE);
		String itemName = formNode.getAttribute(ITEM_NAME_ATTRIBUTE);
		String restoreVar = formNode.getAttribute(RESTORE_VAR_ATTRIBUTE);
		ItemFormPE formElement = null;
		formElement = new ItemFormPE(itemName, itemId, parentId, restoreVar);
		formElement.setTag(formNode.getAttribute(TAG_ATTRIBUTE));
		for (int j = 0; j < formNode.getChildNodes().getLength(); j++) {
			Node formSubnode = formNode.getChildNodes().item(j);
			// Переменные
			if (formSubnode.getNodeType() == Node.ELEMENT_NODE && formSubnode.getNodeName().equalsIgnoreCase(PARAMETER_ELEMENT))
				formElement.addParameter(((Element)formSubnode).getAttribute(NAME_ATTRIBUTE));
			// Ссылка
			else if (formSubnode.getNodeType() == Node.ELEMENT_NODE && formSubnode.getNodeName().equalsIgnoreCase(LINK_ELEMENT))
				formElement.addElement(readLink((Element) formSubnode, includes));
			// include
			else if (formSubnode.getNodeType() == Node.ELEMENT_NODE && formSubnode.getNodeName().equalsIgnoreCase(INCLUDE_ELEMENT))
				appendInclude(formSubnode, (Element) formSubnode, includes);
			// не допускать другие тэги
			else if (formSubnode.getNodeType() == Node.ELEMENT_NODE)
				throw new PrimaryValidationException(page.getPageName() + " > Form '" + itemName + "':" + itemId, "'" + formSubnode.getNodeName()
						+ "' is not a valid subelement of &lt;form&gt; element");
		}
		return formElement;
	}
	/**
	 * Считывает команду
	 * @param commandNode
	 * @param model
	 * @throws PrimaryValidationException 
	 */
	private CommandPE readCommand(Element commandNode) throws PrimaryValidationException {
		boolean clearCache = commandNode.getAttribute(CLEAR_CACHE_ATTRIBUTE).equalsIgnoreCase(YES_VALUE);
		String className = commandNode.getAttribute(CLASS_ATTRIBUTE);
		String method = commandNode.getAttribute(METHOD_ATTRIBUTE);
		String methodVar = commandNode.getAttribute(METHOD_VAR_ATTRIBUTE);
		CommandPE command = null;
		try {
			command = new CommandPE(className, clearCache);
			command.setMethod(method);
			command.setMethodVar(methodVar);
		} catch (ClassNotFoundException e) {
			throw new PrimaryValidationException(page.getPageName() + " > command", "there is no '" + className + "' class in system CLASS_PATH");
		}
		for (Node commandSubnode = commandNode.getFirstChild(); commandSubnode != null; commandSubnode = commandSubnode.getNextSibling()) {
			// Результат
			if (commandSubnode.getNodeType() == Node.ELEMENT_NODE && commandSubnode.getNodeName().equalsIgnoreCase(RESULT_ELEMENT)) {
				Element resultNode = (Element) commandSubnode;
				command.addElement(new ResultPE(resultNode.getAttribute(NAME_ATTRIBUTE), resultNode.getAttribute(TYPE_ATTRIBUTE)));
			// Обязательные поля форм
			} else if (commandSubnode.getNodeType() == Node.ELEMENT_NODE && commandSubnode.getNodeName().equalsIgnoreCase(REQUIRED_ELEMENT)) {
				Element resultNode = (Element) commandSubnode;
				command.addRequired(resultNode.getAttribute(NAME_ATTRIBUTE), resultNode.getAttribute(PARAMETER_ATTRIBUTE));
			// Не допускать другие тэги
			} else if (commandSubnode.getNodeType() == Node.ELEMENT_NODE) {
				throw new PrimaryValidationException(page.getPageName() + " > " + command.getKey(), "'" + commandSubnode.getNodeName()
						+ "' is not a valid subelement of &lt;command&gt; element");
			}
		}
		return command;
	}
	/**
	 * Находит все файлы pages.xml
	 * @param startFile
	 * @param files
	 * @return
	 */
	private static ArrayList<File> findPagesFiles(File startFile, ArrayList<File> files) {
		if (files == null) {
			files = new ArrayList<>();
			if (startFile.isFile())
				files.add(startFile);
			else
				return findPagesFiles(startFile, files);
		} else {
			if (startFile.isDirectory()) {
				File[] filesList = startFile.listFiles();
				for (File file : filesList) {
					if (file.isFile())
						files.add(file);
					else if (file.isDirectory())
						findPagesFiles(file, files);
				}
			}
		}
		return files;
	}
}
