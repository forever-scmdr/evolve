package ecommander.fwk;

import org.apache.commons.lang3.StringUtils;

/**
 * Класс для пары значений (иногда надо)
 * @author E
 *
 * @param <L>
 * @param <R>
 */
public class Pair<L, R> {

	private L left;
	private R right;

	public Pair(L left, R right) {
		this.left = left;
		this.right = right;
	}

	public L getLeft() {
		return left;
	}

	public R getRight() {
		return right;
	}

	public boolean hasLeft() {
		return left != null && StringUtils.isNotBlank(left.toString());
	}
	
	public boolean hasRigth() {
		return right != null && StringUtils.isNotBlank(right.toString());
	}
	
	public void setLeft(L left) {
		this.left = left;
	}
	
	public void setRight(R right) {
		this.right = right;
	}
	
	@Override
	public int hashCode() {
		return left.hashCode() ^ right.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Pair))
			return false;
		@SuppressWarnings("unchecked")
		Pair<L, R> pairo = (Pair<L, R>) o;
		return this.left.equals(pairo.getLeft()) && this.right.equals(pairo.getRight());
	}

	@Override
	public String toString() {
		return left + " : " + right;
	}

}
