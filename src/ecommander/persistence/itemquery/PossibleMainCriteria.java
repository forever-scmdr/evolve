package ecommander.persistence.itemquery;

interface PossibleMainCriteria {
	void setMain();
	boolean isMain();
	String getTableName();
	String getSelectedColumnName();
	String getParentColumnName(); // Название колонки, которая хранит ID предка айтема
}
