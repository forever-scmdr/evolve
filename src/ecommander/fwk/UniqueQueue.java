package ecommander.fwk;

import java.util.*;

/**
 * Очередь с уникальными элементами
 * Created by E on 18/4/2018.
 */
public class UniqueQueue<T> implements Queue<T> {

	private HashSet<T> set = new HashSet<T>();
	private LinkedList<T> list = new LinkedList<T>();

	public UniqueQueue() {

	}

	public UniqueQueue(Collection<T> c) {
		set.addAll(c);
		list.addAll(set);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return set.contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		throw new IllegalStateException("Impossible to create iterator");
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T1> T1[] toArray(T1[] a) {
		return list.toArray(a);
	}

	@Override
	public boolean add(T t) {
		if (!set.contains(t)) {
			set.add(t);
			list.add(t);
			return true;
		}
		return false;
	}

	@Override
	public boolean remove(Object o) {
		if (set.contains(o)) {
			set.remove(o);
			list.remove(o);
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
		boolean added = false;
		for (T t : c) {
			added |= add(t);
		}
		return added;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean removed = false;
		for (Object o : c) {
			removed |= remove(o);
		}
		return removed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		HashSet<T> newSet = new HashSet<T>();
		LinkedList<T> newList = new LinkedList<T>();
		boolean removed = false;
		for (T t : list) {
			if (!c.contains(t)) {
				newSet.add(t);
				newList.add(t);
			} else {
				removed = true;
			}
		}
		list = newList;
		set = newSet;
		return removed;
	}

	@Override
	public void clear() {
		list = new LinkedList<T>();
		set = new HashSet<T>();
	}

	@Override
	public boolean offer(T t) {
		return add(t);
	}

	@Override
	public T remove() {
		T el = list.remove();
		set.remove(el);
		return el;
	}

	@Override
	public T poll() {
		if (list.isEmpty())
			return null;
		return remove();
	}

	@Override
	public T element() {
		return list.element();
	}

	@Override
	public T peek() {
		return list.peek();
	}
}
