package extra.belchip;

import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.stream.IntStream;

public class ShuffleProductsCommand extends Command implements ItemNames.product_ {

	private static final String[] SORTING = new String[] {NAME, CODE, PRICE, NAME_EXTRA};
	
	@Override
	public ResultPE execute() throws Exception {
		StringBuilder sb = new StringBuilder();
		LinkedHashSet<Integer> pos = new LinkedHashSet<>();
		while (pos.size() < 20) {
			pos.add((int)(Math.random() * 100) + 1);
		}
		setPageVariable("pos", StringUtils.join(pos, ":"));

		int rndSort = (int)(Math.random() * 100);
		int rndDir = (int)(Math.random() * 100);

		//int page = 1 + nanotime % 4;
		String sorting = SORTING[rndSort % SORTING.length];
		String direction = rndDir % 2 == 0 ? "ASC" : "DESC";

		//setPageVariable("page", String.valueOf(page));
		setPageVariable("sorting", sorting);
		setPageVariable("dir", direction);
		return null;
	}

}
