package ecommander.model;

import java.io.File;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.output.UserParameterDescriptionMDWriter;
import ecommander.output.XmlDocumentBuilder;
import ecommander.model.datatypes.DataType.Type;

/**
 * Такие действия, как добавление (или удаление) сабайтема в айтем должны происходить следующим образом:
 * сначала добавление происходит на уровне базы данных, а потом перегружается весь соответствующий сабайтем.
 * 
 * Принадлежность (доступность для редактирования) айтемов.
 * 
 * Айтемы могуть быть либо общими либо персональными.
 * У общих айтемов нет определенного владельца (USER_ID = 0), но есть определенная группа (USER_GROUP != 0).
 * У персональных айтемов есть как владелец (USER_ID != 0), так и группа (USER_GROUP != 0)
 * 
 * 
 * @author E
 */
public class Item implements ItemBasics {
	
	private static final String PARAM_TAG = "param";
	public static final String ID_ATTRIBUTE = "id";

	private static final int DIR_NAME_LENGTH = 3;
	private static final char FINAL_DIR_CHAR = 'f';

	public static final long DEFAULT_ID = 0;
	public static final int WEIGHT_STEP = 64;

	public static final byte STATUS_NORMAL = (byte) 0;
	public static final byte STATUS_NIDDEN = (byte) 1;
	public static final byte STATUS_DELETED = (byte) 2;

	private static final int _NO_PARAM_ID  = -1;

	private long id;

	/*
	 * Контекстный предок и контекстная ассоциация в некоторых случаях иметю значение, а в некоторых не имеют и
	 * в принципе могут вообще не задаваться.
	 */
	private long contextParentId; 	// ID родителя айтема в данном некотором контексте.
									// Например, при загрузке непрямых наследников, предок всех этих наследников будет их родителем в данном контексте
									// (контектсе загрузки непрямых наследников)
									// Также при создании нового айтема. Тогда этот предок будет непосредственным предком
									// айтема, что в совокупности с ассоциацией определяет где этот айтем будет размещен в БД
									// (контекст создания айтема)
	private Assoc contextAssoc = AssocRegistry.PRIMARY;     // Ассоция айтема в текущем контектсе.
									// Та ассоциация, которой айтем связан с контекстным предком
									// Айтем может быть ассоциирован со многими предками многими ассоцациями (в т.ч. несколькими с одним предком),
									// но в каждом определенном контексте у айтема имеет значение только один предок и одна ассоциация.
	private byte ownerGroupId = User.NO_GROUP_ID; // Группа пользователей установлена в любом случае. И для общих и для персональных айтемов
	private int ownerUserId = User.ANONYMOUS_ID; // ID юзера владельца этого айтема. Не равен 0 только в случае, если айтем является персональным
	private String key = null; // Составляется из параметров айтема, чтобы юзер в системе управления понимал, что это за айтем
	private String keyUnique = null; // уникальный текстовый ключ
	private String oldKeyUnique = null; // старый уникальный ключ (нужен для корректного возвращения согласованной версии айтема, поскольку не хранится в строке XML)
	private ItemType itemType = null; // Тип айтема
	private Boolean areFilesInitiallyProtected = false; // Защищены ил файлы айтема вначале, т.е. после загрузки из БД.
	private boolean areFilesProtected = false; // Защищены ли файлы айтема от анонимного доступа
	private LinkedHashMap<Integer, Parameter> paramMap = new LinkedHashMap<>(); // Все параметры (не только одиночные)
																				// параметры (имя параметра => объект
																				// Parameter)
	private HashMap<String, String> extras; // дополнительные значения (не параметры). Они существуют только в памяти, в БД не хранятся
											// могут использоваться когда айтем создается в сеансе или в форме (поля extra формы переписываются сюда)
	private String parametersXML = Strings.EMPTY; // Все параметры, записанные в виде XML
	private String filesPath = null; // Относительный путь к файлам айтема

	private byte status = STATUS_NORMAL; // статус айтема (нормальный, айтем удален, айтем скрыт)

	private long timeUpdated; // время последнего обновления или создания айтема
	
	private boolean mapConsistent = false; // находится ли отображение параметров в актуальном состоянии (загружены ли значения из строки)
	private boolean stringConsistent = true; // находится ли строка в согласованном состоянии
	/**
	 * Простой конструктор копирования, не выполняет глубокое копирование
	 * @param src
	 */
	public Item(Item src) {
		this.id = src.id;
		this.contextAssoc = src.contextAssoc;
		this.contextParentId = src.contextParentId;
		this.ownerGroupId = src.ownerGroupId;
		this.ownerUserId = src.ownerUserId;
		this.key = src.key;
		this.keyUnique = src.keyUnique;
		this.oldKeyUnique = src.oldKeyUnique;
		this.itemType = src.itemType;
		this.extras = src.extras;
		this.parametersXML = src.parametersXML;
		this.status = src.status;
		this.areFilesProtected = src.areFilesProtected;
		this.timeUpdated = src.timeUpdated;
		this.paramMap.putAll(src.paramMap);
		this.mapConsistent = src.mapConsistent;
		this.stringConsistent = src.stringConsistent;
	}
	
	private Item(ItemType itemDesc, Assoc contextAssoc, long parentId, int userId, byte groupId, byte status,
	             boolean filesProtected) {
		this.itemType = itemDesc;
		this.ownerUserId = userId;
		this.ownerGroupId = groupId;
		this.contextAssoc = contextAssoc;
		this.contextParentId = parentId;
		this.status = status;
		this.areFilesProtected = filesProtected;
		this.id = DEFAULT_ID;
		this.key = itemDesc.getCaption();
		this.mapConsistent = true;
		this.stringConsistent = true;
		// Добавить все параметры, которые содержатся в типа айтема.
		// Все параметры (даже пустые) нужны для отслеживания изменений параметров.
		for (ParameterDescription paramDesc : itemType.getParameterList()) {
			paramMap.put(paramDesc.getId(), paramDesc.createParameter());
		}
	}

	private Item(ItemType itemDesc, long itemId, Assoc contextAssoc, long parentId, int userId, byte groupId,
	             byte status, String key, String parametersXML, String keyUnique, long timeUpdated,
	             boolean filesProtected) {
		this.id = itemId;
		this.contextAssoc = contextAssoc;
		this.contextParentId = parentId;
		this.itemType = itemDesc;
		this.ownerUserId = userId;
		this.ownerGroupId = groupId;
		this.key = key;
		this.keyUnique = keyUnique;
		this.oldKeyUnique = keyUnique;
		this.timeUpdated = timeUpdated;
		this.status = status;
		this.areFilesInitiallyProtected = filesProtected;
		this.areFilesProtected = filesProtected;
		this.parametersXML = parametersXML;
		this.mapConsistent = false;
		this.stringConsistent = true;
		// Добавить все параметры, которые содержатся в типа айтема.
		// Все параметры (даже пустые) нужны для отслеживания изменений параметров.
		for (ParameterDescription paramDesc : itemType.getParameterList()) {
			paramMap.put(paramDesc.getId(), paramDesc.createParameter());
		}
		setFilesPath();
	}

	/**
	 * Констркуктор для создания новых айтемов в случае когда есть загруженный предок.
	 * Владелец айтема будет таким же, как и у предка.
	 * @param itemDesc
	 * @param parent
	 * @return
	 */
	public static Item newChildItem(ItemType itemDesc, Item parent) {
		return new Item(itemDesc, AssocRegistry.PRIMARY, parent.getId(), parent.getOwnerUserId(),
				parent.getOwnerGroupId(), parent.status, parent.areFilesProtected);
	}

	/**
	 * Конструктор для создания новых айтемов (которых еще нет в БД или сеансе)
	 * @param itemDesc
	 * @param parentId
	 * @param userId
	 * @param groupId
	 * @param status
	 * @param filesProtected
	 * @return
	 */
	public static Item newItem(ItemType itemDesc, long parentId, int userId, byte groupId, byte status,
	                           boolean filesProtected) {
		return new Item(itemDesc, AssocRegistry.PRIMARY, parentId, userId, groupId, status, filesProtected);
	}

	/**
	 * Констркуктор для создания новых сеансовых корневых айтемов
	 * @param itemDesc
	 * @return
	 */
	public static Item newSessionRootItem(ItemType itemDesc) {
		return new Item(itemDesc, AssocRegistry.PRIMARY, 0, User.ANONYMOUS_ID, User.NO_GROUP_ID, STATUS_NORMAL, false);
	}

	/**
	 * Создание айтема при загрузке айтемов из базона или из сеанса (когда айтем не новый, а уже существующий)
	 * @param itemDesc
	 * @param itemId
	 * @param assoc
	 * @param parentId
	 * @param userId
	 * @param groupId
	 * @param status
	 * @param key
	 * @param parametersXML
	 * @param keyUnique
	 * @param timeUpdated
	 * @param filesProtected
	 * @return
	 */
	public static Item existingItem(ItemType itemDesc, long itemId, Assoc assoc, long parentId, int userId, byte groupId,
	                                byte status, String key, String parametersXML, String keyUnique,
	                                long timeUpdated, boolean filesProtected) {
		return new Item(itemDesc, itemId, assoc, parentId, userId, groupId, status, key, parametersXML, keyUnique,
				timeUpdated, filesProtected);
	}
	/**
	 * Является ли айтем новым
	 * @return
	 */
	public final boolean isNew() {
		return id == DEFAULT_ID;
	}
	/**
	 * Установка или добавление парамтера.
	 * Этот метод предназначен для установки параметров, полученных 
	 * из интерфейса пользователя (т. е. все значения строковые)
	 *
	 * @param paramId
	 * @param value
	 * @throws Exception
	 */
	public final void setValueUI(int paramId, String value) throws Exception {
		getParameter(paramId).createAndSetValue(value, isNew());
		stringConsistent = false;
	}
	/**
	 * Установка или добавление парамтера.
	 * Этот метод предназначен для установки параметров, полученных 
	 * из интерфейса пользователя (т. е. все значения строковые)
	 * 
	 * @param paramName
	 * @param value
	 * @throws Exception
	 */
	public final void setValueUI(String paramName, String value) throws Exception {
		setValueUI(itemType.getParameter(paramName).getId(), value);
	}
	/**
	 * Прямая установка параметра. Используется когда сразу есть значение параметра соответствующего типа
	 * и не нужно преобразование из строки
	 * @param paramId
	 * @param value
	 */
	public final void setValue(int paramId, Object value) {
		if (value == null) {
			// Если добавляется пустое значение к множественному параметру - ничего не делать
			if (itemType.getParameter(paramId).isMultiple())
				return;
			// Удалить параметр, если значение равно null
			clearParameter(paramId);
		} else {
			getParameter(paramId).setValue(value);
		}
		stringConsistent = false;
	}
	/**
	 * Прямая установка параметра. Используется когда сразу есть значение параметра соответствующего типа
	 * и не нужно преобразование из строки
	 * @param paramName
	 * @param value
	 */
	public final void setValue(String paramName, Object value) {
		setValue(itemType.getParameter(paramName).getId(), value);
	}
	/**
	 * Содержит параметр айтема определенное значение
	 * @param paramName
	 * @param value
	 * @return
	 */
	public final boolean containsValue(String paramName, Object value) {
		return getParameterByName(paramName).containsValue(value);
	}
	/**
	 * Добавить значение параметра только в случае, если параметр не еще не содержит такое значение 
	 * @param paramName
	 * @param value
	 */
	public final void setValueUnique(String paramName, Object value) {
		if (!containsValue(paramName, value))
			setValue(paramName, value);
	}
	/**
	 * Возвращает параметр по его ID.
	 * Метод нельзя использовать для установки новых значений параметра, так как
	 * вновь установленное значение параметра не сохранится при сохранении айтема.
	 * Создает и добавляет параметр с определенным названием и пустым значением
	 * @param paramId
	 * @return
	 */
	public final Parameter getParameter(int paramId) {
		populateMap();
		return getParameterFromMap(paramId);
	}

	/**
	 * Возвращает параметр из карты параметров. Если параметр не найден в карте, выбрасывается исключение.
	 * @param paramId
	 * @return
	 */
	private Parameter getParameterFromMap(int paramId) {
		Parameter param = paramMap.get(paramId);
		if (param == null)
			throw new IllegalArgumentException("There is no parameter #" + paramId + " in '" + itemType.getName() + "' item");
		return param;
	}
	/**
	 * Возвращает параметр по его названию.
	 * Метод нельзя использовать для установки новых значений параметра, так как
	 * вновь установленное значение параметра не сохранится при сохранении айтема.
	 * Создает и добавляет параметр с определенным названием и пустым значением в случае если
	 * айтем еще не содержит такой параметр.
	 * @param paramName
	 * @return
	 */
	public final Parameter getParameterByName(String paramName) {
		return getParameter(itemType.getParameter(paramName).getId());
	}

	/**
	 * Заполнить значения параметров из строки параметров XML в отображение (paramMap)
	 */
	private void populateMap() {
		if (stringConsistent && !mapConsistent) {
			try {
				XmlDocumentBuilder doc = XmlDocumentBuilder.newDoc();
				doc.startElement("params").addElements(parametersXML).endElement();
				DefaultHandler handler = new DefaultHandler() {
					int level = 0;
					int paramId;
					String paramName = "";
					ParameterDescription currentParamDesc = null;
					StringBuilder paramValue = new StringBuilder();
					
					@Override
					public void characters(char[] ch, int start, int length) throws SAXException {
						paramValue.append(ch, start, length);
					}
	
					@Override
					public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
						if (level > 0) {
							if (qName.equals(PARAM_TAG)) {
								String idStr = attributes.getValue(ID_ATTRIBUTE);
								paramId = Integer.parseInt(idStr);
							} else {
								ParameterDescription paramDesc = itemType.getParameter(qName);
								if (paramDesc != null) {
									paramId = itemType.getParameter(qName).getId();
									paramName = qName;
									currentParamDesc = paramDesc;
								} else {
									paramId = _NO_PARAM_ID;
								}
							}
							paramValue = new StringBuilder();
						}
						level++;
					}

					@Override
					public void endElement(String uri, String localName, String qName) throws SAXException {
						if (qName.equals(paramName) || qName.equals(PARAM_TAG)) {
							String strValue = paramValue.toString().trim();
							if (!StringUtils.isBlank(strValue) && paramId != _NO_PARAM_ID) {
								try {
									Parameter param = getParameterFromMap(paramId);
									if (currentParamDesc.getType() == Type.XML)
										strValue = StringEscapeUtils.unescapeXml(strValue);
									param.createAndSetValue(strValue, true);
								} catch (Exception e) {
									throw new RuntimeException("ITEM params population from XML failed", e);
								}
							}
						}
						level--;
					}
				};
				// Прасить параметры
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				InputSource is = new InputSource(new StringReader(doc.toString()));
				parser.parse(is, handler);
			} catch (Exception e) {
				ServerLogger.error("ITEM params population from XML failed", e);
			}
			mapConsistent = true;
		}
	}
	/**
	 * Создать XML представление всех парамтеров айтема на базе отображения в памяти (paramMap)
	 */
	private void createXML() {
		try {
			if (mapConsistent && !stringConsistent) {
				XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
				for (ParameterDescription paramDesc : itemType.getParameterList()) {
					Parameter param = paramMap.get(paramDesc.getId());
					if (param != null && !param.isEmpty()) {
						boolean isUserDefined = ItemTypeRegistry.getItemType(param.getDesc().getOwnerItemId()).isUserDefined();
						if (param.isMultiple()) {
							for (SingleParameter singleParam : ((MultipleParameter)param).getValues())
								createParamXML(singleParam, xml, isUserDefined);
						} else
							createParamXML((SingleParameter)param, xml, isUserDefined);
					}
				}
				parametersXML = xml.toString();
				stringConsistent = true;
			}
		} catch (Exception e) {
			ServerLogger.error("Can not serialize item parameters", e);
			throw new RuntimeException(e);
			// никогда не происходит
		}
	}

	private void createParamXML(SingleParameter param, XmlDocumentBuilder xml, boolean isUserDefined) throws SQLException {
		if (!isUserDefined) {
			xml.startElement(param.getName());
			if (param.getDesc().getDataType().hasMeta()) {
				HashMap<String, String> meta = param.getDesc().getDataType().getMeta(param.getValue());
				ArrayList<String> attrs = new ArrayList<>();
				for (String attr : meta.keySet()) {
					attrs.add(attr);
					attrs.add(meta.get(attr));
				}
				xml.insertAttributes(attrs.toArray(new String[0]));
			}
			xml.addText(param.outputValue()).endElement();
		} else {
			UserParameterDescriptionMDWriter paramWriter = new UserParameterDescriptionMDWriter(param.getDesc(), param.outputValue());
			paramWriter.write(xml);
		}
	}
	/**
	 * Удалить параметр с заданным ID
	 * Можно удалить множественный или одиночный параметр
	 * @param paramIndex
	 */
	public final void removeMultipleParamValue(int paramId, int paramIndex) {
		// Значит айтем изменялся пользователем
		populateMap();
		Parameter param = paramMap.get(paramId);
		if (param.isMultiple()) {
			((MultipleParameter) param).deleteValue(paramIndex);
		} else {
			throw new IllegalArgumentException("Unable to delete multiple value from single parameter " + param.getName());
		}
		stringConsistent = false;
	}

	/**
	 * Удаляет параметр с заданным ID
	 * @param paramId
	 */
	public final void clearParameter(int paramId) {
		populateMap();
		getParameterFromMap(paramId).clear();
		stringConsistent = false;
	}
	/**
	 * Удалить все значения определенного параметра по его названию
	 * @param paramName
	 */
	public final void clearParameter(String paramName) {
		clearParameter(itemType.getParameter(paramName).getId());
	}
	/**
	 * Удалить определенное значение параметра
	 * @param paramName
	 * @param paramValue
	 */
	public final void removeEqualValue(String paramName, Object paramValue) {
		int paramId = itemType.getParameter(paramName).getId();
		Parameter param = getParameter(paramId);
		if (param.isMultiple()) {
			((MultipleParameter) param).deleteValue(paramValue);
		} else {
			if (param.containsValue(paramValue))
				clearParameter(paramId);
		}
		stringConsistent = false;
	}
	/**
	 * Установить контекстного родителя (родителя в контексте выполнения)
	 * Вместе с родителем устанавливается и ассоциация
	 * @param parentId
	 */
	public final void setContextParentId(Assoc assoc, long parentId) {
		this.contextParentId = parentId;
		this.contextAssoc = assoc;
	}
	/**
	 * Возвращает значение одиночного парамтера
	 * @param paramId
	 * @return
	 */
	public final Object getValue(int paramId) {
		Parameter param = getParameter(paramId);
		if (param == null) 
			return null;
		return param.getValue();
	}
	/**
	 * Сравнивает только Id
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		return obj instanceof Item && ((Item) obj).getId() == getId();
	}
	/**
	 * Подготовка к сохранению:
	 * 1) Запись параметров в формате XML
	 * 2) Формирование нового ключевого параметра
	 * Вызывается перед сохранением айтема
	 */
	public final void prepareToSave() {
		if (!stringConsistent) {
			// Записать параметры в XML формате
			createXML();
			// Сформировать ключевой параметр
			if (itemType.hasKey()) {
				String[] paramNames = StringUtils.split(itemType.getKey(), ItemType.COMMON_DELIMITER);
				key = Strings.EMPTY;
				for (String paramName : paramNames) {
					int paramId = itemType.getParameter(paramName).getId();
					if (paramMap.get(paramId) != null) {
						key += ((SingleParameter) paramMap.get(paramId)).outputValue() + Strings.SPACE;
					}
				}
				key = key.trim();
			} else {
				key = itemType.getCaption();
			}
			if (key.length() > 99) key = key.substring(0, 98);
			if (itemType.isKeyUnique() && StringUtils.isBlank(keyUnique))
				keyUnique = Strings.translit(key);
		}
	}
	/**
	 * Вернуть версию айтема, которая была сразу после загрузки из БД
	 * (до установки и изменения параметров)
	 * @return
	 */
	public final Item getConsistentVersion() {
		if (stringConsistent)
			return this;
		return new Item(itemType, id, contextAssoc, contextParentId, ownerUserId, ownerGroupId, status,
				key, parametersXML,	oldKeyUnique, timeUpdated, areFilesProtected);
	}

	/**
	 * Возвращает старый уникальный ключ (который айетм имел при загрузке из БД)
	 * @return
	 */
	public final String getOldKeyUnique() {
		return oldKeyUnique;
	}
	/**
	 * Принудительно разобрать содержимое строки параметров и установить флаг о том,
	 * что айтем был обновлен (хотя он не был)
	 * @return
	 */
	public final void forceInitialInconsistent() {
		populateMap();
		stringConsistent = false;
	}
	/**
	 * Получить XML со всеми параметрами айтема
	 * После вызова этого метода айтем находится в согласованном состоянии
	 * @return
	 */
	public final String outputValues() {
		prepareToSave();
		return parametersXML;
	}

	/**
	 * Получить все параметры айтема
	 * @return
	 */
	public final Collection<Parameter> getAllParameters() {
		populateMap();
		return paramMap.values();
	}
	/**
	 * @return
	 */
	public final long getId() {
		return id;
	}
	/**
	 * Установить новый ID, использовать только в крайних случаях
	 * @param id
	 */
	public final void setId(long id) {
		this.id = id;
		setFilesPath();
	}
	/**
	 * @return
	 */
	public final ItemType getItemType() {
		return itemType;
	}
	/**
	 * @return
	 */
	public final String getTypeName()	{
		return itemType.getName();
	}
	/**
	 * @return
	 */
	public final int getTypeId() {
		return itemType.getTypeId();
	}
	/**
	 * @return
	 */
	public final byte getOwnerGroupId() {
		return ownerGroupId;
	}
	/**
	 * @return
	 */
	public final int getOwnerUserId() {
		return ownerUserId;
	}

	/**
	 * @return
	 */
	public final byte getStatus() {
		return status;
	}

	public final boolean isStatusNormal() {
		return status == STATUS_NORMAL;
	}

	public final boolean isStatusHidden() {
		return status == STATUS_NIDDEN;
	}

	public final boolean isStatusDeleted() {
		return status == STATUS_DELETED;
	}

	/**
	 * Проверяет, защищены ли файлы айтема от анонимного просмотра
	 * @return
	 */
	public boolean isFileProtected() {
		return areFilesProtected;
	}

	/**
	 * Нужно ли перемещать файлы айтема в защищенное хранилище или из него при
	 * сохранении айтема
	 * @return
	 */
	public boolean filesNeedMoving() {
		return areFilesInitiallyProtected != null && areFilesInitiallyProtected != areFilesProtected;
	}
	/**
	 * Устанавливает или снимает защиту на анонимный просмотр файлов айтема
	 * При установке и снятии защиты файлы айтема должны копироваться в другое место
	 * @param filesProtected
	 */
	public void setFilesProtected(boolean filesProtected) {
		this.areFilesProtected = areFilesProtected;
	}

	/**
	 * Является ли айтем персональным
	 * @return
	 */
	public final boolean isPersonal() {
		return ownerUserId != User.ANONYMOUS_ID;
	}
	/**
	 * @return
	 */
	public final Assoc getContextAssoc() {
		return contextAssoc;
	}
	/**
	 * @return
	 */
	public final long getContextParentId() {
		return contextParentId;
	}

	public final boolean hasNoParent() {
		return contextParentId == RootType.ID;
	}

	public final boolean hasParent() {
		return contextParentId != RootType.ID;
	}
	/**
	 * Возвращает название, которое уникально идентифицирует данный айтем для юзера в системе управления
	 * @return
	 */
	public final String getKey() {
		prepareToSave();
		return key;
	}
	/**
	 * Возвращает транслитерированный key
	 * @return
	 */
	public final String getKeyUnique() {
		return keyUnique;
	}
	/**
	 * Время создания или время последнего обновления айтема
	 * @return
	 */
	public final long getTimeUpdated() {
		return timeUpdated;
	}
	/**
	 * Установить уникальный текстовый ключ
	 * @param newKey
	 */
	public final void setKeyUnique(String newKey) {
		if (!StringUtils.isBlank(newKey))
			this.keyUnique = newKey;
	}
	/**
	 * Определяет, менялся ли айтем со времени его загрузки из БД
	 * @return
	 */
	public final boolean hasChanged() {
		if (stringConsistent)
			return false;
		for (Parameter param : paramMap.values()) {
			if (param.hasChanged())
				return true;
		}
		return false;
	}

	/**
	 * @see Object#toString()
	 */
	public final String toString() {
		return "Item - type: " + getItemType().getName() + ", id: " + getId();
	}
	/**
	 * Переписывает все параметры одного айтема в другой айтем в случае если тип айтемов совпадает
	 * Можно передавать список параметров, которые должны быть скопированы
	 * @param source
	 * @param destination
	 * @throws SQLException 
	 */
	public static void updateParamValues(Item source, Item destination, String...paramNamesToUpdate) {
		// Если тип айтемов не совпадает - ничего не делать
		try {
			Collection<ParameterDescription> paramsToCopy;
			// Если параметры переносятся из айтема-предка в айтем-потомок
			if (ItemTypeRegistry.getItemPredecessorsExt(destination.getTypeName()).contains(source.getTypeName()))
				paramsToCopy = source.itemType.getParameterList();
			// Если параметры переносятся из айтема-потомка в айтем-предок
			else if (ItemTypeRegistry.getItemPredecessorsExt(source.getTypeName()).contains(destination.getTypeName()))
				paramsToCopy = destination.itemType.getParameterList();
			else
				return;
			source.populateMap();
			HashSet<String> neededParams = null;
			// Если переданы параметры для копирования, то создать из них множество
			if (paramNamesToUpdate.length > 0)
				neededParams = new HashSet<>(Arrays.asList(paramNamesToUpdate));
			for (ParameterDescription paramDesc : paramsToCopy) {
				// Пропустить ненужные параметры
				if (neededParams != null && !neededParams.contains(paramDesc.getName()))
					continue;
				Parameter param = source.paramMap.get(paramDesc.getId());
				if (param != null) {
					// Одиночные параметры
					if (param instanceof SingleParameter) {
						destination.setValue(paramDesc.getId(), param.getValue());
					// Множественные параметры
					} else if (param instanceof MultipleParameter) {
						for (SingleParameter singleParam : ((MultipleParameter)param).getValues()) {
							destination.setValue(paramDesc.getId(), singleParam.getValue());
						}
					}
				}
			}
		} catch (Exception e) {
			// Ничего не делать
		}
	}
	/**
	 * Установить дополнительное значение в айтем (не параметр)
	 * @param name
	 * @param value
	 */
	public final void setExtra(String name, String value) {
		if (extras == null)
			extras = new HashMap<>();
		extras.put(name, value);
	}
	/**
	 * Извлечь дополнительное значение из айтема
	 * @param name
	 * @return
	 */
	public final String getExtra(String name) {
		if (extras == null)
			return null;
		return extras.get(name);
	}
	/**
	 * Получить все ключи дополнительных значений
	 * @return
	 */
	public final Collection<String> getExtraKeys() {
		if (extras == null)
			return new ArrayList<>(0);
		return extras.keySet();
	}
	/**
	 * Содержит ли айтем дополнительные значения
	 * @return
	 */
	public final boolean hasExtras() {
		return extras != null;
	}
	/**
	 * Возвращает значение одиночного парамтера
	 * @param paramName
	 * @return
	 */
	public final Object getValue(String paramName) {
		ParameterDescription param = itemType.getParameter(paramName);
		if (param == null) 
			return null;
		return getValue(param.getId());
	}
	/**
	 * Получить массив значений множественного параметра
	 * @param paramName
	 * @return
	 */
	public final Collection<SingleParameter> getParamValues(String paramName) {
		ParameterDescription paramDesc = itemType.getParameter(paramName);
		if (paramDesc == null) 
			return new ArrayList<>(0);
		Parameter param = getParameter(paramDesc.getId());
		if (param == null) 
			return new ArrayList<>(0);
		if (!param.isMultiple()) {
			ArrayList<SingleParameter> result = new ArrayList<>(1);
			result.add((SingleParameter) param);
			return result;
		}
		return ((MultipleParameter) param).getValues();
	}
	/**
	 * Проверяет, можно ли пользователь редактировать этот айтем
	 * @param user
	 * @return
	 */
	public final boolean isUserAllowed(User user) {
		if (!user.inGroup(getOwnerGroupId()))
			return false;
		if (isPersonal())
			return getOwnerUserId() == user.getUserId() || user.getRole(getOwnerGroupId()) == User.ADMIN;
		return true;
	}
	/**
	 * Получить все значения заданного параметра (как одиночного так и множественного) в виде массива
	 * @param paramName
	 * @return
	 */
	public final ArrayList<Object> getValues(String paramName) {
		ArrayList<Object> result = new ArrayList<>();
		Collection<SingleParameter> multipleValues = getParamValues(paramName);
		for (SingleParameter sp : multipleValues) {
			if (!sp.isEmpty())
				result.add(sp.getValue());
		}
		return result;
	}
	/**
	 * Получить все значения заданного параметра (как одиночного так и множественного) в виде массива
	 * Тип параметра - строковый
	 * @param paramName
	 * @return
	 */
	public final ArrayList<String> getStringValues(String paramName) {
		ArrayList<String> result = new ArrayList<>();
		Collection<SingleParameter> multipleValues = getParamValues(paramName);
		for (SingleParameter sp : multipleValues) {
			if (!sp.isEmpty())
				result.add((String)sp.getValue());
		}
		return result;
	}
	/**
	 * Получить все значения заданного параметра (как одиночного так и множественного) в виде массива
	 * Тип параметра - целочисленный длинный
	 * @param paramName
	 * @return
	 */
	public final ArrayList<Long> getLongValues(String paramName) {
		ArrayList<Long> result = new ArrayList<>();
		Collection<SingleParameter> multipleValues = getParamValues(paramName);
		for (SingleParameter sp : multipleValues) {
			if (!sp.isEmpty())
				result.add((Long)sp.getValue());
		}
		return result;
	}
	/**
	 * Получить все значения заданного параметра (как одиночного так и множественного) в виде массива
	 * Тип параметра - байт
	 * @param paramName
	 * @return
	 */
	public final ArrayList<Byte> getByteValues(String paramName) {
		ArrayList<Byte> result = new ArrayList<>();
		Collection<SingleParameter> multipleValues = getParamValues(paramName);
		for (SingleParameter sp : multipleValues) {
			if (!sp.isEmpty())
				result.add((Byte)sp.getValue());
		}
		return result;
	}
	/**
	 * Получить все значения заданного параметра (как одиночного так и множественного) в виде массива
	 * Тип параметра - integer
	 * @param paramName
	 * @return
	 */
	public final ArrayList<Integer> getIntValues(String paramName) {
		ArrayList<Integer> result = new ArrayList<>();
		Collection<SingleParameter> multipleValues = getParamValues(paramName);
		for (SingleParameter sp : multipleValues) {
			if (!sp.isEmpty())
				result.add((Integer)sp.getValue());
		}
		return result;
	}
	/**
	 * Получить все значения заданного параметра (как одиночного так и множественного) в виде массива
	 * Тип параметра - файл
	 * @param paramName
	 * @return
	 */
	public final ArrayList<File> getFileValues(String paramName, String filesRepositoryPath) {
		ArrayList<File> result = new ArrayList<>();
		Collection<SingleParameter> multipleValues = getParamValues(paramName);
		for (SingleParameter sp : multipleValues) {
			if (!sp.isEmpty())
				result.add(new File(getParamFileName(filesRepositoryPath, id, sp.outputValue())));
		}
		return result;
	}
	/**
	 * Получить все значения заданного параметра (как одиночного так и множественного) в виде массива
	 * Тип параметра - целочисленный длинный
	 * @param paramName
	 * @return
	 */
	public final ArrayList<String> outputValues(String paramName) {
		ArrayList<String> result = new ArrayList<>();
		Collection<SingleParameter> multipleValues = getParamValues(paramName);
		for (SingleParameter sp : multipleValues) {
			if (!sp.isEmpty())
				result.add(sp.outputValue());
		}
		return result;
	}
	/**
	 * Вернуть значение строкового параметра
	 * @param paramName
	 * @return
	 */
	public final String getStringValue(String paramName) {
		return (String) getValue(paramName);
	}
	/**
	 * Вернуть значение целочисленного параметра
	 * @param paramName
	 * @return
	 */
	public final Long getLongValue(String paramName) {
		return (Long) getValue(paramName);
	}
	/**
	 * Вернуть значение целочисленного параметра
	 * @param paramName
	 * @return
	 */
	public final Integer getIntValue(String paramName) {
		return (Integer) getValue(paramName);
	}
	/**
	 * Вернуть значение параметра с плавающей точкой
	 * @param paramName
	 * @return
	 */
	public final Double getDoubleValue(String paramName) {
		return (Double) getValue(paramName);
	}
	/**
	 * Вернуть значение целочисленного параметра
	 * @param paramName
	 * @return
	 */
	public final Byte getByteValue(String paramName) {
		return (Byte) getValue(paramName);
	}
	/**
	 * Вернуть значение одиночного параметра типа файл в виде файла, а не в виде строки
	 * @param paramName
	 * @param filesRepositoryPath - директория, в которой хранятся файлы айтемов
	 * @return
	 */
	public final File getFileValue(String paramName, String filesRepositoryPath) {
		String fileName = ((SingleParameter)getParameterByName(paramName)).outputValue();
		return new File(getParamFileName(filesRepositoryPath, id, fileName));
	}
	/**
	 * Возвращает значение одиночного парамтера.
	 * Если параметр не задан - возвращается значение по умолчанию
	 * @param paramName
	 * @param defaultValue
	 * @return
	 */
	public final Object getValue(String paramName, Object defaultValue) {
		Object value = getValue(paramName);
		return value == null ? defaultValue : value;
	}
	/**
	 * Возвращает значение одиночного парамтера.
	 * Если параметр не задан - возвращается значение по умолчанию
	 * @param paramName
	 * @param defaultValue
	 * @return
	 */
	public final String getStringValue(String paramName, String defaultValue) {
		return (String) getValue(paramName, defaultValue);
	}
	/**
	 * Возвращает значение одиночного парамтера.
	 * Если параметр не задан - возвращается значение по умолчанию
	 * @param paramName
	 * @param defaultValue
	 * @return
	 */
	public final long getLongValue(String paramName, long defaultValue) {
		return (Long) getValue(paramName, defaultValue);
	}
	/**
	 * Возвращает значение одиночного парамтера.
	 * Если параметр не задан - возвращается значение по умолчанию
	 * @param paramName
	 * @param defaultValue
	 * @return
	 */
	public final int getIntValue(String paramName, int defaultValue) {
		return (Integer) getValue(paramName, defaultValue);
	}
	/**
	 * Возвращает значение одиночного парамтера.
	 * Если параметр не задан - возвращается значение по умолчанию
	 * @param paramName
	 * @param defaultValue
	 * @return
	 */
	public final double getDoubleValue(String paramName, double defaultValue) {
		return (Double) getValue(paramName, defaultValue);
	}
	/**
	 * Возвращает значение одиночного парамтера.
	 * Если параметр не задан - возвращается значение по умолчанию
	 * @param paramName
	 * @param defaultValue
	 * @return
	 */
	public final byte getByteValue(String paramName, byte defaultValue) {
		return (Byte) getValue(paramName, defaultValue);
	}

	private static String getParamFileName(String basePath, long itemId, String fileName) {
		return basePath + '/' + itemId + '/' + fileName;
	}

	private void setFilesPath() {
		if (!isNew()) {
			this.filesPath = createItemFilesPath(id);
		}
	}

	/**
	 * Получить отсносительный путь (без общей для всех айтемов части) к директории файлов айтема
	 * @return
	 */
	public String getRelativeFilesPath() {
		return filesPath;
	}

	/**
	 * Создает путь к файлам айтема на базе ID айтема
	 * @param itemId
	 * @return
	 */
	public static String createItemFilesPath(long itemId) {
		String idStr = itemId + "";
		StringBuilder sb = new StringBuilder();
		int index = 0;
		while (index < idStr.length()) {
			sb.append(idStr.substring(index, Math.min(index + DIR_NAME_LENGTH, idStr.length()))).append(File.separator);
			index += DIR_NAME_LENGTH;
		}
		sb.setCharAt(sb.length() - 1, FINAL_DIR_CHAR);
		return sb.toString();
	}
}