package ecommander.output;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import ecommander.fwk.Strings;
import ecommander.controllers.AppContext;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.ParameterDescription;
import ecommander.pages.ItemHttpPostForm;
import ecommander.model.Domain;
import ecommander.model.DomainRegistry;

/**
 * Создает следующую структуру

	// item_form -фиксированный тэг, user_enquette - название айтем
	// name="article_form" - название формы
	// action="item_post.item" - куда отправляется форма
	// action-url - action с добавленными скрытыми переменными
	// file-path="/var/www/..." - путь к директории данного айтема (где лежат его файлы)
	// id="35322" - ID рекактирукмого айтема
	<item_form 
		caption="Отделка баров" 
		action="item_post.item" 
		action-url="item_post.item?$post_in$=article&$post_ii$=35322&$post_ip$=532" 
		file-path="/var/www/..." id="35322"> 
		<hidden>                                           // hidden поля
			<field input="$post_in$" value="article">       // одно hidden поле
			<field input="$post_ii$" value="35322">
			<field input="$post_ip$" value="532">
		</hidden>
		// Далее идут поля для всех параметров
		<field type="string" quantifier="single" id="34" name="header" user-def="false" input="$param$header@article@35322" caption="..." description="...">
			Ремонт и отделка кафе, баров и ресторанов
		</field>
		
		// Поля параметров для более удобного доступа дублируются в таком виде
		<header input="$param$header@article@35322">Ремонт и отделка кафе, баров и ресторанов</header>
		
		<field type="text" quantifier="single" id="43" name="text" user-def="false" input="$param$short@article@35322" caption="..." description="...">
			Приходя в любое место, все мы в первую очередь обращаем внимание на обстановку....
		</field>
		<text input="$param$short@article@35322">Приходя в любое место, все мы в первую очередь обращаем внимание на обстановку....</text>
			...
		<field type="param_type" quantifier="single" id="p_id" name="param_name" user-def="true" input="input_name" caption="..." description="...">
			input value
		</field>
		<param_name input="input_name">input value</param_name>
		// Множественные параметры содержат все свои значения
		// У параметра может быть домен
		<field type="string" quantifier="multiple" id="55" domain="tags" name="search_tag" user-def="false" input="..." caption="..." description="...">
			<value index="0">ремонт</value>
			<value index="1">отделка</value>
		</field>
		
		<search_tag input="$param$short@article@35322">ремонт</search_tag>
		<search_tag input="$param$short@article@35322">отделка</search_tag>
		
		<field type="string" quantifier="multiple" id="77" name="picture" user-def="false" input="..." caption="..." description="..."/>
			<value index="0">bar_pic.jpg</value>
			<value index="1">bar_pic_2.jpg</value>
		</field>
		
		// Дополнительные поля, параметры, которых нет в айтеме, но которые нужны для дополнительной логики
		
		// Если у полей были домены, то вывести значения этих доменов
		<domain name="tags">
			<value>ремонт</value>
			<value>новости фирм</value>
			<value>отделка</value>
		</domain>
	
	</item_form>

 * 
 * @author EEEE
 *
 */
public class ItemFormMDWriter extends MetaDataWriter {

	private static final String HIDDEN_ELEMENT = "hidden";
	private static final String FIELD_ELEMENT = "field";
	private static final String EXTRA_ELEMENT = "extra";
	private static final String DOMAIN_ELEMENT = "domain";
	private static final String VALUE_ELEMENT = "value";
	
	private static final String NAME_ATTRIBUTE = "name";
	private static final String TYPE_ATTRIBUTE = "type";
	private static final String ACTION_ATTRIBUTE = "action";
	private static final String ACTION_URL_ATTRIBUTE = "action-url";
	private static final String INPUT_ATTRIBUTE = "input";
	private static final String QUANTIFIER_ATTRIBUTE = "quantifier";
	private static final String USER_DEF_ATTRIBUTE = "user-def";
	private static final String FORMAT_ATTRIBUTE = "format";
	private static final String ID_ATTRIBUTE = "id";
	private static final String CAPTION_ATTRIBUTE = "caption";
	private static final String DESCRIPTION_ATTRIBUTE = "description";
	private static final String INDEX_ATTRIBUTE = "index";
	private static final String FILE_PATH_ATTRIBUTE = "file-path";
	private static final String VALUE_ATTRIBUTE = VALUE_ELEMENT;
	private static final String DOMAIN_ATTRIBUTE = DOMAIN_ELEMENT;
	
	private static final String SINGLE_VALUE = "single";
	private static final String MULTIPLE_VALUE = "multiple";
	
	private ItemHttpPostForm form;
	private String tag;
	private String action = "";
	
	public ItemFormMDWriter(ItemHttpPostForm form, String tag) {
		this.form = form;
		this.tag = tag;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	@Override
	public XmlDocumentBuilder write(XmlDocumentBuilder xml) {
		ItemType itemDesc = ItemTypeRegistry.getItemType(form.getItemTypeId());
		// <item_form>
		xml.startElement(tag, 
				ACTION_ATTRIBUTE, action, 
				ACTION_URL_ATTRIBUTE, createActionUrl(),
				FILE_PATH_ATTRIBUTE, AppContext.getCommonFilesUrlPath() + form.getPredIdPath(),
				ID_ATTRIBUTE, form.getItemId(),
				CAPTION_ATTRIBUTE, form.getItemCaption());
		// <hidden>
		xml.startElement(HIDDEN_ELEMENT);
		Map<String, String> hiddenFields = form.getHiddenFields();
		// Заполняются hidden поля
		for (String inputName : hiddenFields.keySet()) {
			// <field name="..." value="..."/>
			xml.addEmptyElement(FIELD_ELEMENT, INPUT_ATTRIBUTE, inputName, VALUE_ATTRIBUTE, hiddenFields.get(inputName));
		}
		// </hidden>
		xml.endElement();
		HashSet<String> domains = new HashSet<String>();
		for (Integer paramId : form.getParameterIds()) {
			ParameterDescription paramDesc = itemDesc.getParameter(paramId);
			ItemType ownerItemType = ItemTypeRegistry.getItemType(paramDesc.getOwnerItemId());
			if (paramDesc.isVirtual())
				continue;
			String[] attrsArr = {
					ID_ATTRIBUTE, paramId + "", 
					TYPE_ATTRIBUTE, paramDesc.getType().toString(),
					NAME_ATTRIBUTE, paramDesc.getName(), 
					USER_DEF_ATTRIBUTE, ownerItemType.isUserDefined() + "",
					FORMAT_ATTRIBUTE, paramDesc.getFormat(),
					CAPTION_ATTRIBUTE, paramDesc.getCaption(), 
					DESCRIPTION_ATTRIBUTE, paramDesc.getDescription(), 
					INPUT_ATTRIBUTE, form.getParamFieldName(paramId), 
					QUANTIFIER_ATTRIBUTE, paramDesc.isMultiple() ? MULTIPLE_VALUE : SINGLE_VALUE
				};
			List<String> attrs = new ArrayList<String>(Arrays.asList(attrsArr));
			if (paramDesc.hasDomain()) {
				domains.add(paramDesc.getDomainName());
				attrs.add(DOMAIN_ATTRIBUTE);
				attrs.add(paramDesc.getDomainName());
			}
			// <field id="34" name="header" input="$param$header@article@35322" quantifier="single" type="string">
			// Ремонт и отделка кафе, баров и ресторанов
			// </field>
			xml.startElement(FIELD_ELEMENT, attrs.toArray(new Object[0]));
			if (paramDesc.isMultiple()) {
				List<String> values = form.getValuesStr(paramId);
				for (int i = 0; i < values.size(); i++ ) {
					xml.startElement(VALUE_ELEMENT, INDEX_ATTRIBUTE, i).addText(values.get(i)).endElement();
				}
			} else {
				xml.addText(form.getValueStr(paramId));
			}
			xml.endElement();
			
			// <header input="$param$header@article@35322">Ремонт и отделка кафе, баров и ресторанов</header>
			String elementName = Strings.createXmlElementName(paramDesc.getName());
			if (paramDesc.isMultiple()) {
				List<String> values = form.getValuesStr(paramId);
				for (String val : values) {
					xml.startElement(elementName, INPUT_ATTRIBUTE, form.getParamFieldName(paramId)).addText(val).endElement();
				}
				// пустое поле, когда значений еще нет
				xml.addEmptyElement(elementName, INPUT_ATTRIBUTE, form.getParamFieldName(paramId));
			} else {
				xml
					.startElement(elementName, INPUT_ATTRIBUTE, form.getParamFieldName(paramId))
					.addText(form.getValueStr(paramId))
					.endElement();
			}
		}
		// <extra input="some_name">some_value</extra>
		for (Object extra : form.getExtras()) {
			List<Object> vals = form.getMultipleExtra(extra);
			for (Object val : vals) {
				xml.startElement(EXTRA_ELEMENT, INPUT_ATTRIBUTE, extra).addText(val).endElement();
			}
		}
		for (String domain : domains) {
			// <domain name="...">
			xml.startElement(DOMAIN_ELEMENT, NAME_ATTRIBUTE, domain);
			Domain dom = DomainRegistry.getDomain(domain);
			if (dom != null) {
				for (String value : dom.getValues()) {
					// <value>...</value>
					xml.startElement(VALUE_ELEMENT).addText(value).endElement();
				}
			}
			// </domain>
			xml.endElement();
		}
		writeSubwriters(xml);
		// </item_form>
		xml.endElement();
		return xml;
	}
	/**
	 * Создать предполагаемый URL для отправки формы
	 * @return
	 */
	private String createActionUrl() {
		StringBuilder url = new StringBuilder(action);
		char symbol = '?';
		if (form.getHiddenFields().size() > 0) {
			if (action.indexOf('?') > 0)
				symbol = '&';
		}
		for (String field : form.getHiddenFields().keySet()) {
			url.append(symbol).append(field).append('=').append(form.getHiddenFields().get(field));
			symbol = '&';
		}
		return url.toString();
	}
	
}
