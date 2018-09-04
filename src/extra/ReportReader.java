package extra;

import ecommander.fwk.ExcelPriceList;

import java.io.File;
import java.nio.file.Path;

/**
 * Created by E on 30/8/2018.
 */
public class ReportReader extends ExcelPriceList {
	public ReportReader(String fileName, String... mandatoryCols) {
		super(fileName, mandatoryCols);
	}

	public ReportReader(File file, String... mandatoryCols) {
		super(file, mandatoryCols);
	}

	public ReportReader(Path path, String... mandatoryCols) {
		super(path, mandatoryCols);
	}

	@Override
	protected void processRow() throws Exception {

	}

	@Override
	protected void processSheet() throws Exception {

	}

	public static void main(String[] args) {
		String[] names = {
				"ВФ 1Н - 4 сталь", "ФН 6 - 1М сталь", "ВН 1 1/2Н - 6П, УХЛ2", "ВН 6Н - 1ПЕ чугун", "ВН 1 1/2Н - 6П, УХЛ2",
				"ВФ 3/4Н - 4П фл. сталь", " РТБ-139.03.00.000", "ВН 3/4Н - 4П сталь", "Фильтрующий элемент к ФН11/2-2",
				" ВН2Н-6 фл", " ВН2Н-6ПЕ фл.сталь исп.У2 =24В", "С4Н-4-45", "Фильтроэлемент к ФН2-6 -4", "ВН1 1/2Н-1 в сборе с ВФ3/4Н-4 ",
				"Катушка к ВН2 1/2Н-0,5", "Комплект ЗИП РТИ и мембран для РС3/4-6-240-570 ", "РС3/4-6-240-570", "ВН2Н-1К(фл.)",
				"Фильтроэлемент к ФН3-1", "Катушка к ВН2 1/2Н-0,5"
		};

		System.out.println();
	}
}
