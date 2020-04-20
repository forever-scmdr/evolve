package ecommander.fwk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Массив с уникальными элементами
 * Created by E on 19/4/2018.
 */
public class UniqueArrayList<T> implements Collection<T> {

	private ArrayList<T> array = new ArrayList<T>();
	private HashSet<T> set = new HashSet<T>();

	public UniqueArrayList(Collection<T> src) {
		addAll(src);
	}

	public UniqueArrayList() {

	}


	public boolean add(T t) {
		if (set.add(t)) {
			array.add(t);
			return true;
		}
		return false;
	}

	@Override
	public boolean remove(Object o) {
		if (set.contains(o)) {
			set.remove(o);
			array.remove(o);
			return true;
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return set.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		if (c.size() == 0)
			return false;
		boolean result = false;
		for (T t : c) {
			result |= add(t);
		}
		return result;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean result = set.removeAll(c);
		array.removeAll(c);
		return result;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean result = set.retainAll(c);
		array.retainAll(c);
		return result;
	}

	public int size() {
		return array.size();
	}

	public T get(int index) {
		return array.get(index);
	}

	public boolean isEmpty() {
		return array.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return set.contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		return array.iterator();
	}

	@Override
	public Object[] toArray() {
		return array.toArray();
	}

	@Override
	public <T1> T1[] toArray(T1[] a) {
		return array.toArray(a);
	}

	public void clear() {
		array = new ArrayList<T>();
		set = new HashSet<T>();
	}

	public T remove(int index) {
		T element = array.remove(index);
		set.remove(element);
		return element;
	}


}
