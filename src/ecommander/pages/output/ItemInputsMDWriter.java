package ecommander.pages.output;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import ecommander.fwk.Strings;
import ecommander.controllers.AppContext;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.*;
import ecommander.model.datatypes.DataType;
import ecommander.model.datatypes.FileDataType;
import ecommander.pages.ItemInputName;
import ecommander.pages.ItemInputs;
import org.apache.commons.lang3.StringUtils;

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
 */
public class ItemInputsMDWriter extends MetaDataWriter {

	private static final String FIELD_ELEMENT = "field";
	private static final String EXTRA_ELEMENT = "extra";
	private static final String DOMAIN_ELEMENT = "domain";
	private static final String VALUE_ELEMENT = "value";
	private static final String INPUT_ELEMENT = "input";
	
	private static final String NAME_ATTRIBUTE = "name";
	private static final String TYPE_ATTRIBUTE = "type";
	private static final String ACTION_URL_ATTRIBUTE = "action-url";
	private static final String INPUT_ATTRIBUTE = "input";
	private static final String QUANTIFIER_ATTRIBUTE = "quantifier";
	private static final String USER_DEF_ATTRIBUTE = "user-def";
	private static final String FORMAT_ATTRIBUTE = "format";
	private static final String ID_ATTRIBUTE = "id";
	private static final String KEY_ATTRIBUTE = "key";
	private static final String CAPTION_ATTRIBUTE = "caption";
	private static final String DESCRIPTION_ATTRIBUTE = "description";
	private static final String INDEX_ATTRIBUTE = "index";
	private static final String FILE_PATH_ATTRIBUTE = "file-path";
	private static final String DOMAIN_ATTRIBUTE = DOMAIN_ELEMENT;
	private static final String VALIDATION_ERROR_ATTRIBUTE = "validation-error";
	
	private static final String SINGLE_VALUE = "single";
	private static final String MULTIPLE_VALUE = "multiple";
	
	private ItemInputs inputs;
	private String tag;
	private String actionUrl = "";
	
	public ItemInputsMDWriter(ItemInputs inputs, String tag) {
		this.inputs = inputs;
		this.tag = tag;
	}

	public ItemInputsMDWriter(ItemInputs inputs) {
		this.inputs = inputs;
	}

	public void setActionUrl(String actionUrl) {
		this.actionUrl = actionUrl;
	}

	private boolean hasActionUrl() {
		return StringUtils.isNotBlank(actionUrl);
	}

	@Override
	public XmlDocumentBuilder write(XmlDocumentBuilder xml) {
		ItemType itemDesc = inputs.getItem().getItemType();
		// <item_form>
		if (hasActionUrl()) {
			xml.startElement(tag,
					ACTION_URL_ATTRIBUTE, actionUrl,
					FILE_PATH_ATTRIBUTE, FileDataType.getItemFileUrl(inputs.getItem()),
					ID_ATTRIBUTE, inputs.getItem().getId(),
					CAPTION_ATTRIBUTE, itemDesc.getCaption(),
					KEY_ATTRIBUTE, inputs.getItem().getKey());
		} else {
			xml.startElement(INPUT_ELEMENT);
		}
		HashSet<String> domains = new HashSet<>();
		for (ItemInputName inputKey : inputs.getAllInputNames()) {
			if (inputKey.isParameter()) {
				ParameterDescription paramDesc = itemDesc.getParameter(inputKey.getParamId());
				ItemType ownerItemType = ItemTypeRegistry.getItemType(paramDesc.getOwnerItemId());
				if (paramDesc.isVirtual())
					continue;
				String[] attrsArr = {
						ID_ATTRIBUTE, paramDesc.getId() + "",
						TYPE_ATTRIBUTE, paramDesc.getType().toString(),
						NAME_ATTRIBUTE, paramDesc.getName(),
						USER_DEF_ATTRIBUTE, ownerItemType.isUserDefined() + "",
						FORMAT_ATTRIBUTE, paramDesc.getFormat(),
						CAPTION_ATTRIBUTE, paramDesc.getCaption(),
						DESCRIPTION_ATTRIBUTE, paramDesc.getDescription(),
						INPUT_ATTRIBUTE, inputKey.getInputName(),
						QUANTIFIER_ATTRIBUTE, paramDesc.isMultiple() ? MULTIPLE_VALUE : SINGLE_VALUE
				};
				List<String> attrs = new ArrayList<>(Arrays.asList(attrsArr));
				if (paramDesc.hasDomain()) {
					domains.add(paramDesc.getDomainName());
					attrs.add(DOMAIN_ATTRIBUTE);
					attrs.add(paramDesc.getDomainName());
				}
				if (inputs.hasNotPassedValidation(paramDesc.getName())) {
					attrs.add(VALIDATION_ERROR_ATTRIBUTE);
					attrs.add(inputs.getValidationErrorMessage(paramDesc.getName()));
				}
				// <field id="34" name="header" input="$param$header@article@35322" quantifier="single" type="string">
				// Ремонт и отделка кафе, баров и ресторанов
				// </field>
				List<String> values = inputs.getInputValues(inputKey);//item.getValues(paramDesc.getName());
				xml.startElement(FIELD_ELEMENT, attrs.toArray(new Object[0]));
				if (paramDesc.isMultiple()) {
					if (paramDesc.getType() == DataType.Type.TUPLE) {
						int i = 0;
						for (SingleParameter sp : ((MultipleParameter) inputs.getItem().getParameter(paramDesc.getId())).getValues()) {
							ArrayList<String> metas = sp.getAllMetas();
							xml.startElement(VALUE_ELEMENT, INDEX_ATTRIBUTE, i++).insertAttributes(metas.toArray(new String[0])).addText(sp.outputValue()).endElement();
						}
					} else {
						for (int i = 0; i < values.size(); i++) {
							xml.startElement(VALUE_ELEMENT, INDEX_ATTRIBUTE, i).addText(values.get(i)).endElement();
						}
					}
				} else {
					for (String value : values) {
						xml.addText(value);//xml.addText(item.getValue(paramDesc.getName()));
					}
				}
				xml.endElement();

				// <header input="$param$header@article@35322">Ремонт и отделка кафе, баров и ресторанов</header>
				String elementName = Strings.createXmlElementName(paramDesc.getName());
				for (String value : values) {
					xml.startElement(elementName, INPUT_ATTRIBUTE, inputKey.getInputName());
					if (inputs.hasNotPassedValidation(paramDesc.getName()))
						xml.insertAttributes(VALIDATION_ERROR_ATTRIBUTE, inputs.getValidationErrorMessage(paramDesc.getName()));
					xml.addText(value).endElement();
				}
				if (values.size() == 0) {
					// пустое поле, когда значений еще нет
					xml.addEmptyElement(elementName, INPUT_ATTRIBUTE, inputKey.getInputName());
				}
			} else {
				List<String> vals = inputs.getInputValues(inputKey);
				if (vals != null && vals.size() > 0) {
					for (Object val : vals) {
						if (hasActionUrl()) {
							// <extra input="some_name">some_value</extra>
							xml.startElement(EXTRA_ELEMENT, NAME_ATTRIBUTE, inputKey.getVarName(), INPUT_ATTRIBUTE,
									inputKey.getInputName()).addText(val).endElement();
						} else {
							// <some_name input="some_name">some_value</some_name>
							xml.startElement(inputKey.getVarName(), INPUT_ATTRIBUTE, inputKey.getInputName()).addText(val).endElement();
						}
					}
				} else {
					if (hasActionUrl()) {
						// <extra input="some_name">some_value</extra>
						xml.startElement(EXTRA_ELEMENT, NAME_ATTRIBUTE, inputKey.getVarName(), INPUT_ATTRIBUTE,
								inputKey.getInputName()).endElement();
					} else {
						// <some_name input="some_name">some_value</some_name>
						xml.startElement(inputKey.getVarName(), INPUT_ATTRIBUTE, inputKey.getInputName()).endElement();
					}
				}
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

}
