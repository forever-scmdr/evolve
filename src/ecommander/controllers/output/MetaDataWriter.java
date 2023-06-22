package ecommander.controllers.output;

import java.util.ArrayList;

public abstract class MetaDataWriter {
	
	protected ArrayList<MetaDataWriter> additional;
	
	protected MetaDataWriter() {
		additional = new ArrayList<MetaDataWriter>();
	}
	/**
	 * Вывести краткое содержание объекта
	 * @param xml
	 */
	public abstract XmlDocumentBuilder write(XmlDocumentBuilder xml);
	/**
	 * Добавить часть, которая будет выводиться в дополнение к свойствам выводимого объекта и будет относиться к нему
	 * @param xmlPart
	 */
	public final void addSubwriter(MetaDataWriter part) {
		additional.add(part);
	}
	/**
	 * Удалить все дополнительные части
	 */
	public final void clearSubwriters() {
		additional = new ArrayList<MetaDataWriter>();
	}
	/**
	 * Вывести содержимое дополнительных элементов
	 * @param xml
	 */
	protected final void writeAdditional(XmlDocumentBuilder xml) {
		for (MetaDataWriter writer : additional) {
			writer.write(xml);
		}
	}
}
