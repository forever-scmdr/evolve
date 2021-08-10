package extra.belchip;

import ecommander.pages.Command;
import ecommander.pages.ResultPE;

public class ShuffleProductsCommand extends Command {

	private static final String[] SORTING = new String[] {"name", "code", "price", "new", "mark"};
	
	@Override
	public ResultPE execute() throws Exception {
		long nanotime = System.nanoTime()/100;
		
		long page = 1+nanotime%4;
		String sorting = SORTING[(int)(nanotime/10%SORTING.length)];
		//String direction = nanotime/100%2 > 0? "ASCENDING" : "DESCEDING";
		
		setPageVariable("page", String.valueOf(page));
		setPageVariable("sorting", sorting);
		//setPageVariable("dir", direction);
		return null;
	}

}
