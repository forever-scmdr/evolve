package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.persistence.itemquery.ItemQuery;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

/**
 * Created by user on 03.05.2019.
 */
public class CreateGoogleSearchFeedCSV extends IntegrateBase implements CatalogConst {
	private int lineNumber = 0;
	private int position = 0;
	private LinkedList<Item> sections = new LinkedList<>();
	private static final String FILE_NAME = "search.csv";
	private static String host;
	private static final char SEP = ';';
	private static final String CAT = "CATEGORY_PAGE";
	private static final String PROD = "SINGLE_PRODUCT";


	@Override
	protected void integrate() throws Exception {
		setOperation("Создание файла search.csv");
		setProcessed(0);
		setLineNumber(lineNumber);
		ItemQuery q = new ItemQuery(SECTION_ITEM);
		q.setParentId(ItemQuery.loadSingleItemByName(CATALOG_ITEM).getId(),false);

		sections.addAll(q.loadItems());
		Path outputPath = Paths.get(AppContext.getContextPath(), FILE_NAME);
		host = getUrlBase();
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath.toString()), "UTF8"))){
			Item section;
			while ((section = sections.poll()) != null){
				processSection(section, writer, "");
			}
		}catch (Exception e){
			addError(e.toString()+" says: "+e.getMessage(), lineNumber, position);
		}
	}

	private void processSection(Item section, BufferedWriter writer, String parents) throws Exception {
		StringBuilder sb = new StringBuilder();
		String parentString = parents +SEP+ section.getKeyUnique();
		sb.append(host).append('/').append(section.getKeyUnique()).append(',').append(CAT);
		sb.append(parentString);
		writer.write(sb.toString());
		writer.newLine();
		info.setLineNumber(++lineNumber);
		info.increaseProcessed();

		int pageNumber = 0;
		ItemQuery query = new ItemQuery(PRODUCT_ITEM);
		query.setParentId(section.getId(), false);
		query.setLimit(500, ++pageNumber);

		LinkedList<Item> productQueue = new LinkedList<>();


		productQueue.addAll(query.loadItems());

		while (productQueue.size() > 0) {
			Item product;
			while ((product = productQueue.poll()) != null) {
				sb = new StringBuilder();
				sb.append(host).append('/').append(product.getKeyUnique()).append(',').append(PROD);
				sb.append(parentString).append(SEP).append(product.getKeyUnique());

				writer.write(sb.toString());
				writer.newLine();
				info.setLineNumber(++lineNumber);
				info.increaseProcessed();

			}
			query.setLimit(500, ++pageNumber);
			productQueue.addAll(query.loadItems());
		}
		ItemQuery q = new ItemQuery(SECTION_ITEM);
		q.setParentId(section.getId(), false);
		for(Item subSection : q.loadItems()){
			processSection(subSection, writer,parentString);
		}
		parentString = "";
	}


	@Override
	protected void terminate() throws Exception {

	}

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

}
