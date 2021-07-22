package ecommander.fwk;

import java.util.Iterator;

/**
 * Created by E on 7/2/2019.
 */
public class IteratorCurrent<T> implements Iterator<T> {

	private Iterator<T> iterator;
	private T current;

	public IteratorCurrent(Iterator<T> iterator) {
		this.iterator = iterator;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public T next() {
		current = iterator.next();
		return current;
	}

	public T getCurrent() {
		return current;
	}
}
