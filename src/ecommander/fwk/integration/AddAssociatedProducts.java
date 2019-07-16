package ecommander.fwk.integration;

import ecommander.fwk.IntegrateBase;
import ecommander.model.Compare;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.commandunits.CreateAssocDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;
import extra._generated.ItemNames;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AddAssociatedProducts extends IntegrateBase implements CatalogConst{
	int toProcess = 0;
	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		info.setOperation("Загрузка разделов каталога");
		LinkedList<Item> sections = new LinkedList<>();
		ItemQuery q = new ItemQuery(SECTION_ITEM);
		q.addParameterCriteria(ItemNames.section.ASSOC_CODES,"-","!=", null, Compare.SOME);
		sections.addAll(q.loadItems());
		info.setToProcess(toProcess);
		info.setProcessed(0);
		for(Item section : sections){
			String assocCodes = section.getStringValue(ItemNames.section.ASSOC_CODES);
			List<String> codes = Arrays.asList(assocCodes.split("[\\n,\\,,\\;, \\s]"));
			q = new ItemQuery(PRODUCT_ITEM);
			q.addParameterCriteria(CODE_PARAM, codes, "=", null, Compare.SOME);
			LinkedList<Item> loadedProducts = new LinkedList<>();
			loadedProducts.addAll(q.loadItems());
			toProcess += loadedProducts.size();
			info.setToProcess(toProcess);
			Item product;
			while((product = loadedProducts.poll())!=null){
				CreateAssocDBUnit createAssocDBUnit = CreateAssocDBUnit.childExistsSoft(product, section, ItemTypeRegistry.getAssocId("catalog_link"));
				executeAndCommitCommandUnits(createAssocDBUnit);
				info.increaseProcessed();
			}
		}
		setOperation("Создание фильтров");
		CreateParametersAndFiltersCommand createFilters = new CreateParametersAndFiltersCommand(this);
		createFilters.setSections(sections);
		createFilters.integrate();
		setOperation("Переиндексация");
		info.indexsationStarted();
		LuceneIndexMapper.getSingleton().reindexAll();

	}

	@Override
	protected void terminate() throws Exception {

	}
}
