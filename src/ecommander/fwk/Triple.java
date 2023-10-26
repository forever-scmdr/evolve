package ecommander.fwk;

import org.apache.commons.lang3.StringUtils;

/**
 * Класс для пары значений (иногда надо)
 * @author E
 *
 * @param <L>
 * @param <R>
 */
public class Triple<L, M, R> {

	private L left;
	private M medium;
	private R right;

	public Triple(L left, M medium, R right) {
		this.left = left;
		this.medium = medium;
		this.right = right;
	}

	public L getLeft() {
		return left;
	}

	public M getMedium() {
		return medium;
	}

	public R getRight() {
		return right;
	}

	public boolean hasLeft() {
		return left != null && StringUtils.isNotBlank(left.toString());
	}

	public boolean hasMedium() {
		return medium != null && StringUtils.isNotBlank(medium.toString());
	}

	public boolean hasRigth() {
		return right != null && StringUtils.isNotBlank(right.toString());
	}

	public void setLeft(L left) {
		this.left = left;
	}

	public void setMedium(M medium) {
		this.medium = medium;
	}

	public void setRight(R right) {
		this.right = right;
	}

	@Override
	public int hashCode() {
		return (left == null ? 0 : left.hashCode()) ^ (medium == null ? 0 : medium.hashCode()) ^ (right == null ? 0 : right.hashCode());
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Triple))
			return false;
		@SuppressWarnings("unchecked")
        Triple<L, M, R> pairo = (Triple<L, M, R>) o;
		return this.left.equals(pairo.getLeft()) && this.medium.equals(pairo.getMedium()) && this.right.equals(pairo.getRight());
	}

	@Override
	public String toString() {
		return left + " : " + medium + " : " + right;
	}

}
