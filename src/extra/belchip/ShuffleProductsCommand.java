package extra.belchip;

import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import extra._generated.ItemNames;

public class ShuffleProductsCommand extends Command implements ItemNames.product_ {

	private static final String[] SORTING = new String[] {NAME, CODE, PRICE, NAME_EXTRA};
	
	@Override
	public ResultPE execute() throws Exception {
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
