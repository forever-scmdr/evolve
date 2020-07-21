package ecommander.model;

import ecommander.fwk.Strings;
import ecommander.model.datatypes.DataType;
import ecommander.model.datatypes.FormatDataType;
import org.apache.commons.lang3.StringUtils;

public final class ParameterDescription {

	public enum TextIndex {
		fulltext, // Параметр индексируется и подвергается анализу
		filter, // Параметр индексируется, но анализу не подвергается
		none // Параметр не индексируется
	}

	private String name = Strings.EMPTY;
	private int paramId = 0; // ID параметра
	private DataType type = null; // Тип параметра
	private boolean isMultiple = false; // Одиночный или множественный параметр (может иметь одно значение или несколько)
	private int ownerItemId = -1; // ID айтема, которому принадлежит параметр
	private String domainName = Strings.EMPTY; // Название домена
	private String caption = Strings.EMPTY; // Символьное обозначение для юзера на русском языке
	private String description = Strings.EMPTY; // Описание на русском
	private String format = Strings.EMPTY; // Формат вывода параметра (сейчас только для числовых параметров, например ##.##)
	private Object formatter = null;
	private boolean isHidden = false; // параметр по умолчанию скрыт при редактировании в системе управления
	private boolean isVirtual = false; // параметр не доступен для редактирования пользователем, только для специальных операций
	private String defaultValue = null; // Значение по умолчанию в виде строки

	private ComputedDescription computed = null; // содержит описание вычисляемого параметра, если оно есть

	private TextIndex textIndex = TextIndex.none; // Индексировать ли этот параметр для полнотекстового поиска
	private float textIndexBoost = -1f; // Увеличение веса параметра в поисковой выдаче
	private String textIndexParamName; // Название параметра, в котором сохраняется значение этого параметра при полнотекстовом индексировании
	private String textIndexParser = null; // Парсер для разбора текста
	private String textIndexItem = null;    // название айтема-предшественника в случае если при поиске должен находиться не
											// сам айтем а его предшественник

	private boolean needsDBIndex = true; // нужно ли сохранять в индекс базы данных значение этого параметра
	
	public ParameterDescription(String name, int paramId, String type, boolean isMultiple, int parentItemId, String domainName, String caption,
	                            String description, String format, boolean isVirtual, boolean isHidden, String defaultValue, ComputedDescription.Func computedFunc) {
		super();
		this.name = name;
		this.paramId = paramId;
		this.type = DataTypeRegistry.getType(DataType.Type.get(type));
		this.isMultiple = isMultiple;
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
		if (StringUtils.isNotBlank(defaultValue))
			this.defaultValue = defaultValue;
		if (computedFunc != null)
			computed = new ComputedDescription(computedFunc);
		
		if (this.type.isFile() || this.type.isBigText() || this.isVirtual())
			needsDBIndex = false;
	}
	/**
	 * Установить каким образом будет индексироваться это поле при полнотекстовом поиске
	 * @param indexType
	 * @param paramName
	 * @param boost
	 */
	public void setFulltextSearch(TextIndex indexType, String paramName, float boost, String parser, String item) {
		this.textIndex = indexType;
		if (!StringUtils.isEmpty(paramName))
			this.textIndexParamName = paramName;
		else
			this.textIndexParamName = name;
		if (boost >= 1f)
			this.textIndexBoost = boost;
		if (!StringUtils.isBlank(parser))
			this.textIndexParser = parser;
		if (!StringUtils.isBlank(item))
			this.textIndexItem = item;
	}
	/**
	 * Создает параметр используя собственный тип (себя)
	 * @return
	 */
	Parameter createParameter(Item item) {
		if (isMultiple()) return new MultipleParameter(this, item);
		return new SingleParameter(this, item);
	}
	/**
	 * Создает одиночный параметр (нужно для множественных параметров
	 * @return
	 */
	SingleParameter createSingleParameter(Item item) {
		return new SingleParameter(this, item);
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

	public DataType getDataType() {
		return type;
	}
	
	public DataType.Type getType() {
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
		return isMultiple;
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
	 * Есть ли значение по умолчанию
	 * @return
	 */
	public boolean hasDefaultValue() {
		return defaultValue != null;
	}

	/**
	 * Проверить, есть ли вычисляемое значение
	 * @return
	 */
	public boolean isComputed() {
		return computed != null;
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
	 * Получить название айтема, который должен находиться при полнотекстовом поиске, в случае если
	 * это не сам айтем, а его предок
	 * @return
	 */
	public String getFulltextItem() {
		return textIndexItem;
	}

	/**
	 * Проверить, надо ли искать айтем-предшественник вместо айтема-владельца параметра при полнотекстовом
	 * поиске
	 * @return
	 */
	public boolean isFulltextOwnByPredecessor() {
		return StringUtils.isNotBlank(textIndexItem);
	}

	/**
	 * Получить значение по умолчанию в виде строки, так, как оно написано в XML файле
	 * @return
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Получть описание вычисляемого значения
	 * @return
	 */
	public ComputedDescription getComputed() {
		return computed;
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
		return getName() + ": " + type;
	}
}