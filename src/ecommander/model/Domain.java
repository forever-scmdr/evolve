package ecommander.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import ecommander.controllers.AppContext;

/**
 * Домен
 * Совокупность значений, имеющий строковый тип
 * Каждый домен имеет название и тип отображения
 * @author EEEE
 *
 */
public class Domain {
	private String name;
	private String viewType;
	private String format = null;
	private ArrayList<String> values = null;
	
	public Domain(String name, String viewType) {
		this.name = name;
		this.viewType = viewType;
		this.values = new ArrayList<String>();
	}
	
	public Domain(String name, String viewType, String format) {
		this.name = name;
		this.viewType = viewType;
		this.format = format;
		this.values = new ArrayList<String>();
	}
	
	public Domain(String name, String viewType, String format, ArrayList<String> values) {
		this.name = name;
		this.viewType = viewType;
		this.format = format;
		if (values != null) this.values = values;
		else this.values = new ArrayList<String>();
	}
	
	public void setValues(ArrayList<String> values) {
		if (values != null) this.values = values;
	}
	
	public boolean addValue(String value) {
		if (value == null || values.contains(value)) 
			return false;
		values.add(value);
		return true;
	}
	
	public void removeValue(String value) {
		values.remove(value);
	}
	
	public boolean hasFormat() {
		return !StringUtils.isBlank(format);
	}
	
	public String getName() {
		return name;
	}

	public String getViewType() {
		return viewType;
	}

	public String getFormat() {
		return format;
	}

	public ArrayList<String> getValues() {
		return values;
	}
	/**
	 * Создает и возвращает форматтер
	 * @return
	 */
	public DecimalFormat getFormatter() {
		DecimalFormat formatter = null;
		if (!StringUtils.isBlank(format)) {
			formatter = (DecimalFormat)NumberFormat.getNumberInstance(AppContext.getCurrentLocale());
			formatter.applyPattern(format);
		}
		return formatter;
	}
}
