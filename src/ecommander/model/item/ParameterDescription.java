package ecommander.model.item;

import org.apache.commons.lang3.StringUtils;

import ecommander.common.Strings;
import ecommander.model.datatypes.DataType;
import ecommander.model.datatypes.DataType.Type;
import ecommander.model.datatypes.DataTypeRegistry;
import ecommander.model.datatypes.FormatDataType;

public final class ParameterDescription {
	
	public static enum Quantifier {
		single, multiple;
	}
	
	public static enum TextIndex {
		fulltext, // Параметр индексируется и подвергается анализу
		filter, // Параметр индексируется, но анализу не подвергается
		none // Параметр не индексируется
	}

	private static final int USER_PARAM_MOCK_ID = -1;
	private static final int USER_GROUP_PARAM_MOCK_ID = -2;
	
	// Специальный параметр - ID пользователя, используется при поиске айтемов, принадлежащих определенному пользователю
	public static final ParameterDescription USER = new ParameterDescription("user", USER_PARAM_MOCK_ID, "long",
			Quantifier.single, ItemType.SERVICE_ITEM_ID, null, null, null, null, false, true);
	// Специальный параметр - ID группы, используется при поиске айтемов, принадлежащих определенной группе
	public static final ParameterDescription GROUP = new ParameterDescription("group", USER_GROUP_PARAM_MOCK_ID,
			"integer", Quantifier.single, ItemType.SERVICE_ITEM_ID, null, null, null, null, false, true);
	
	private String name = Strings.EMPTY;
	private int paramId = 0; // ID параметра
	private DataType type = null; // Тип параметра
	private Quantifier quantifier = Quantifier.single; // Одиночный или множественный параметр (может иметь одно значение или несколько)
	private int ownerItemId = -1; // ID айтема, которому принадлежит параметр
	private String domainName = Strings.EMPTY; // Название домена
	private String caption = Strings.EMPTY; // Символьное обозначение для юзера на русском языке
	private String description = Strings.EMPTY; // Описание на русском
	private String format = Strings.EMPTY; // Формат вывода параметра (сейчас только для числовых параметров, например ##.##)
	private Object formatter = null;
	private boolean isHidden = false; // параметр по умолчанию скрыт при редактировании в системе управления
	private boolean isVirtual = false; // параметр не доступен для редактирования пользователем, только для специальных операций
	
	private TextIndex textIndex = TextIndex.none; // Индексировать ли этот параметр для полнотекстового поиска
	private float textIndexBoost = -1f; // Увеличение веса параметра в поисковой выдаче
	private String textIndexParamName; // Название параметра, в котором сохраняется значение этого параметра при полнотекстовом индексировании
	private String textIndexParser = null; // Парсер для разбора текста
	
	private boolean needsDBIndex = true; // нужно ли сохранять в индекс базы данных значение этого параметра
	
	public ParameterDescription(String name, int paramId, String type, Quantifier quantifier, int parentItemId, String domainName, String caption,
			String description, String format, boolean isVirtual, boolean isHidden) {
		super();
		this.name = name;
		this.paramId = paramId;
		this.type = DataTypeRegistry.getType(Type.fromString(type));
		this.quantifier = quantifier;
		this.ownerItemId = parentItemId;
		if (domainName != null)
			this.domainName = domainName;
		if (caption != null)
			this.caption = caption;
		if (description != null)
			this.description = description;
		if (format != null)
			this.format = format;
		if (!StringUtils.isBlank(format) && !this.type.isFile())
			formatter = ((FormatDataType)this.type).createFormatter(format);
		this.isVirtual = isVirtual;
		this.isHidden = isHidden;
		
		if (this.type.isFile() || this.type.isBigText() || this.isVirtual())
			needsDBIndex = false;
	}
	/**
	 * Установить каким образом будет индексироваться это поле при полнотекстовом поиске
	 * @param indexType
	 * @param paramName
	 * @param boost
	 */
	public void setFulltextSearch(TextIndex indexType, String paramName, float boost, String parser) {
		this.textIndex = indexType;
		if (!StringUtils.isEmpty(paramName))
			this.textIndexParamName = paramName;
		else
			this.textIndexParamName = name;
		if (boost >= 1f)
			this.textIndexBoost = boost;
		if (!StringUtils.isBlank(parser))
			this.textIndexParser = parser;
	}
	/**
	 * Создает параметр используя собственный тип (себя)
	 * @return
	 */
	public Parameter createParameter() {
		if (isMultiple()) return new MultipleParameter(this);
		return new SingleParameter(this);
	}
	/**
	 * Создает одиночный параметр (нужно для множественных параметров
	 * @return
	 */
	public SingleParameter createSingleParameter() {
		return new SingleParameter(this);
	}

	public String getCaption() {
		return caption;
	}

	public String getDescription() {
		return description;
	}

	public String getDomainName() {
		return domainName;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return paramId;
	}
	
	public Quantifier getQuantifier() {
		return quantifier;
	}

	public DataType getDataType() {
		return type;
	}
	
	public Type getType() {
		return type.getType();
	}
	
	public Object getFormatter() {
		return formatter;
	}

	public String getFormat() {
		return format;
	}
	/**
	 * Вернуть название айтема, которому принадлежит параметр
	 * @return
	 */
	public int getOwnerItemId() {
		return ownerItemId;
	}
	/**
	 * Множественный или одиночный айтем
	 * @return
	 */
	public boolean isMultiple() {
		return quantifier == Quantifier.multiple;
	}
	/**
	 * Нужно ли скрывать параметр при редактировании в системе управления
	 * @return
	 */
	public boolean isHidden() {
		return isHidden;
	}
	/**
	 * Является ли параметр виртуальным, т. е. недоступным для редактирования пользователем и не хранимым в индексе айтема
	 * @return
	 */
	public boolean isVirtual() {
		return isVirtual;
	}
	/**
	 * Есть ли определенный формат у парамтера
	 * @return
	 */
	public boolean hasFormat() {
		return !StringUtils.isBlank(format);
	}
	/**
	 * Есть ли определенный домен у парамтера
	 * @return
	 */
	public boolean hasDomain() {
		return !StringUtils.isBlank(domainName);
	}
	/**
	 * Нужна ли отедльная индексация этого параметра для полнотекстового поиска
	 * @return
	 */
	public boolean isUsedInFulltexSearch() {
		return textIndex != TextIndex.none;
	}
	/**
	 * Должен ли осуществляться полнотекстовый поиск по этому параметру
	 * @return
	 */
	public boolean isFulltextSearchable() {
		return textIndex == TextIndex.fulltext;
	}
	/**
	 * Должна ли осуществляться фильтрация по этому параметру при полнотекстовом поиске
	 * Т.е. должно ли проверяться полное совпадение этого параметра с соответствующим параметром фильтра при полнотекством поиске
	 * @return
	 */
	public boolean isFulltextFilterable() {
		return textIndex == TextIndex.filter;
	}
	/**
	 * Является ли параметр более важным при полнотекстовом индексировании
	 * @return
	 */
	public boolean isFulltextBoosted() {
		return textIndexBoost > 1f;
	}
	/**
	 * Получить коэффициент увеличения важности параметра при полнотекстовом поиске
	 * @return
	 */
	public float getFulltextBoost() {
		return textIndexBoost;
	}
	/**
	 * Нужно ли разбирать значение параметра для сохранения в 
	 * @return
	 */
	public boolean needFulltextParsing() {
		return textIndexParser != null;
	}
	
	public boolean needsDBIndex() {
		return needsDBIndex;
	}
	/**
	 * Получить парсер для полнотекстовой индексации
	 * @return
	 */
	public String getFulltextParser() {
		return textIndexParser;
	}
	/**
	 * Вернуть название параметра, который создается в полнотекстовом индексе и по которому надо осуществлять поиск потом
	 * @return
	 */
	public String getFulltextIndexParameter() {
		return textIndexParamName;
	}
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "ParameterDescription - name: " + getName() + ", type: " + type + ", isMultiple:" + isMultiple();
	}
}