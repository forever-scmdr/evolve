package ecommander.pages.elements.variables;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;

import ecommander.model.item.Item;
import ecommander.pages.elements.ExecutablePagePE;
/**
 * Неитерируемая версия переменной страничного айтема
 * Метод output выводит не одно, а все значения айтема через запятую
 * @author E
 *
 */
public class StaticItemVariablePE extends ItemVariablePE {

	public StaticItemVariablePE(String varId, String itemPageId) {
		super(varId, itemPageId);
	}

	private StaticItemVariablePE(StaticItemVariablePE source, ExecutablePagePE parentPage) {
		super(source, parentPage);
	}
	
	@Override
	public String output() {
		if (isMultiple())
			return StringUtils.join(outputArray(), ',');
		Item item = getPageItem().getAllFoundItemIterator().getNextItem();
		if (item != null)
			return outputItem(item);
		return "";
	}

	@Override
	protected VariablePE createVarClone(ExecutablePagePE parentPage) {
		return new StaticItemVariablePE(this, parentPage);
	}
	
	@Override
	public String writeInAnUrlFormat() throws UnsupportedEncodingException {
		if (!isMultiple())
			return super.writeInAnUrlFormat();
		return VariablePE.writeMultipleVariableInAnUrlFormat(this);
	}
}
